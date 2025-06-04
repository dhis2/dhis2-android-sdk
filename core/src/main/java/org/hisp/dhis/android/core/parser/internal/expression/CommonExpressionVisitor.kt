/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.parser.internal.expression

import org.antlr.v4.runtime.ParserRuleContext
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.IndicatorContext
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.isZeroOrPositive
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemId
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorContext
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorExecutor
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLContext
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.antlr.AntlrExpressionVisitor
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

class CommonExpressionVisitor constructor(
    val scope: CommonExpressionVisitorScope,
) : AntlrExpressionVisitor() {

    /**
     * Map of ExprItem instances to call for each expression item
     */
    private val itemMap: Map<Int, ExpressionItem> = scope.itemMap

    /**
     * Method to call within the ExprItem instance
     */
    private val itemMethod: ExpressionItemMethod = scope.itemMethod

    /**
     * Constants to use in evaluating an expression.
     */
    val constantMap: Map<String, Constant> = scope.constantMap

    /**
     * Stores
     */
    val dataElementStore: IdentifiableObjectStore<DataElement>? = scope.dataElementStore
    val trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>? = scope.trackedAttributeStore
    val categoryOptionComboStore: IdentifiableObjectStore<CategoryOptionCombo>? = scope.categoryOptionComboStore
    val organisationUnitGroupStore: IdentifiableObjectStore<OrganisationUnitGroup>? = scope.organisationUnitGroupStore
    val programStageStore: IdentifiableObjectStore<ProgramStage>? = scope.programStageStore

    /**
     * Context
     */
    val programIndicatorContext: ProgramIndicatorContext? = scope.programIndicatorContext
    val programIndicatorExecutor: ProgramIndicatorExecutor? = scope.programIndicatorExecutor
    val programIndicatorSQLContext: ProgramIndicatorSQLContext? = scope.programIndicatorSQLContext
    val indicatorContext: IndicatorContext? = scope.indicatorContext

    /**
     * Used to collect the string replacements to build a description.
     */
    val itemDescriptions: MutableMap<String, String> = HashMap()

    /**
     * Used to collect the dimensional item ids in the expression.
     */
    var itemIds: MutableSet<DimensionalItemId> = HashSet()

    /**
     * Organisation unit group counts to use in evaluating an expression.
     */
    var orgUnitCountMap: Map<String, Int> = HashMap()

    /**
     * Count of days in period to use in evaluating an expression.
     */
    var days: Double? = null

    /**
     * Values to use for variables in evaluating an org.hisp.dhis.rules.parser.expression.
     */
    var itemValueMap: Map<String, Double> = HashMap()

    /**
     * Expression state
     */
    val state = ExpressionState()

    // -------------------------------------------------------------------------
    // Visitor methods
    // -------------------------------------------------------------------------
    override fun visitExpr(ctx: ExprContext): Any? {
        if (ctx.it != null) {
            val item = itemMap[ctx.it.type]
                ?: throw ParserExceptionWithoutContext(
                    "Item " + ctx.it.text + " not supported for this type of expression",
                )
            return itemMethod.apply(item, ctx, this)
        }
        return if (ctx.expr().size > 0) {
            // If there's an expr, visit the expr
            visit(ctx.expr(0))
        } else {
            // All others: visit first child.
            visit(ctx.getChild(0))
        }
    }

    // -------------------------------------------------------------------------
    // Logic for expression items
    // -------------------------------------------------------------------------
    /**
     * Visits a context while allowing null values (not replacing them
     * with 0 or ''), even if we would otherwise be replacing them.
     *
     * @param ctx any context
     * @return the value while allowing nulls
     */
    fun visitAllowingNulls(ctx: ParserRuleContext?): Any? {
        val savedReplaceNulls = state.replaceNulls
        state.replaceNulls = false
        val result = visit(ctx)
        state.replaceNulls = savedReplaceNulls
        return result
    }

    fun visitWithQueryMods(ctx: ParserRuleContext, queryMods: QueryMods): Any? {
        val savedQueryMods = state.queryMods
        state.queryMods = queryMods
        val result = visit(ctx)
        state.queryMods = savedQueryMods
        return result
    }

    /**
     * Handles nulls and missing values.
     *
     *
     * If we should replace nulls with the default value, then do so, and
     * remember how many items found, and how many of them had values, for
     * subsequent MissingValueStrategy analysis.
     *
     *
     * If we should not replace nulls with the default value, then don't,
     * as this is likely for some function that is testing for nulls, and
     * a missing value should not count towards the MissingValueStrategy.
     *
     * @param value the (possibly null) value
     * @return the value we should return.
     */
    fun handleNulls(value: Any?, valueType: ValueType?): Any? {
        if (state.replaceNulls) {
            state.itemsFound = state.itemsFound + 1
            if (value == null) {
                return if ((valueType ?: ValueType.NUMBER).isNumeric) {
                    ParserUtils.DOUBLE_VALUE_IF_NULL
                } else {
                    ParserUtils.TEXT_VALUE_IF_NULL
                }
            } else {
                state.itemValuesFound = state.itemValuesFound + 1
                if (isZeroOrPositive(value.toString())) {
                    state.itemZeroPosValuesFound = state.itemZeroPosValuesFound + 1
                }
            }
        }
        return value
    }

    /**
     * Regenerates an expression by visiting all the children of the
     * expression node (including any terminal nodes).
     *
     * @param ctx the expression context
     * @return the regenerated expression (as a String)
     */
    fun regenerateAllChildren(ctx: ExprContext): Any {
        return ctx.children.joinToString(separator = " ") { castStringVisit(it) }
    }

    companion object {
        /**
         * Default value for data type double.
         */
        const val DEFAULT_DOUBLE_VALUE = 1.0
    }
}
