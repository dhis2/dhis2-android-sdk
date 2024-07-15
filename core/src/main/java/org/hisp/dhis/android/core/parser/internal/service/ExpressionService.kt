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
package org.hisp.dhis.android.core.parser.internal.service

import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStore
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitorScope
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser.visit
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItemMethod
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.COMMON_EXPRESSION_ITEMS
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.ITEM_EVALUATE
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.ITEM_GET_DESCRIPTIONS
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.ITEM_GET_IDS
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.ITEM_REGENERATE
import org.hisp.dhis.android.core.parser.internal.expression.literal.RegenerateLiteral
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimItemDataElementAndOperand
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemId
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.antlr.ParserException
import org.hisp.dhis.parser.expression.antlr.ExpressionParser
import org.koin.core.annotation.Singleton

@Singleton
internal class ExpressionService(
    private val dataElementStore: DataElementStore,
    private val categoryOptionComboStore: CategoryOptionComboStore,
    private val organisationUnitGroupStore: OrganisationUnitGroupStore,
    private val programStageStore: ProgramStageStore,
) {
    private val validationRuleExpressionItems: Map<Int, ExpressionItem> = getValidationRuleExpressionItems()

    private fun getValidationRuleExpressionItems(): Map<Int, ExpressionItem> {
        return COMMON_EXPRESSION_ITEMS + (
            ExpressionParser.HASH_BRACE to DimItemDataElementAndOperand()
            )
    }

    private fun getDimensionalItemIds(expression: String?): Set<DimensionalItemId> {
        return if (expression == null) {
            emptySet()
        } else {
            try {
                val visitor = newVisitor(ITEM_GET_IDS, emptyMap())
                visit(expression, visitor)
                visitor.itemIds
            } catch (e: ParserException) {
                emptySet()
            }
        }
    }

    fun getDataElementOperands(expression: String?): Set<DataElementOperand> {
        val dimensionalItemIds = getDimensionalItemIds(expression)

        return dimensionalItemIds
            .filter { it.isDataElementOrOperand }
            .map {
                DataElementOperand.builder()
                    .dataElement(ObjectWithUid.create(it.id0))
                    .categoryOptionCombo(it.id1?.let { id -> ObjectWithUid.create(id) })
                    .build()
            }
            .toSet()
    }

    fun getExpressionDescription(expression: String?, constantMap: Map<String, Constant>): String {
        return if (expression == null) {
            ""
        } else {
            try {
                val visitor = newVisitor(ITEM_GET_DESCRIPTIONS, constantMap)
                visit(expression, visitor)
                visitor.itemDescriptions.entries.fold(expression) { acc, (key, value) ->
                    acc.replace(key, value)
                }
            } catch (e: ParserException) {
                ""
            }
        }
    }

    fun getExpressionValue(expression: String?): Any? {
        return getExpressionValue(
            expression,
            ExpressionServiceContext(),
            MissingValueStrategy.NEVER_SKIP,
        )
    }

    fun getExpressionValue(
        expression: String?,
        context: ExpressionServiceContext,
        missingValueStrategy: MissingValueStrategy,
        ignoreParseErrors: Boolean = true,
    ): Any? {
        return expression?.let {
            val visitor = newVisitor(
                ITEM_EVALUATE,
                context.constantMap,
            )
            val itemValueMap = context.valueMap.map { it.key.dimensionItem to it.value }.toMap()

            visitor.itemValueMap = itemValueMap
            visitor.orgUnitCountMap = context.orgUnitCountMap
            visitor.days = context.days?.toDouble()

            val value = try {
                visit(expression, visitor)
            } catch (e: ParserException) {
                if (ignoreParseErrors) {
                    null
                } else {
                    throw e
                }
            }

            val itemsFound = visitor.state.itemsFound
            val itemValuesFound = visitor.state.itemValuesFound

            when (missingValueStrategy) {
                MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING -> {
                    if (itemValuesFound < itemsFound) {
                        null
                    } else {
                        getHandledValue(value)
                    }
                }

                MissingValueStrategy.SKIP_IF_ALL_VALUES_MISSING -> {
                    if (itemsFound != 0 && itemValuesFound == 0) {
                        null
                    } else {
                        getHandledValue(value)
                    }
                }

                MissingValueStrategy.NEVER_SKIP -> getHandledValue(value)
            }
        }
    }

    fun regenerateExpression(
        expression: String?,
        context: ExpressionServiceContext,
    ): String {
        return if (expression == null) {
            ""
        } else {
            try {
                val visitor = newVisitor(
                    ITEM_REGENERATE,
                    context.constantMap,
                )

                val itemValueMap = context.valueMap.map { it.key.dimensionItem to it.value }.toMap()
                visitor.itemValueMap = itemValueMap
                visitor.orgUnitCountMap = context.orgUnitCountMap
                visitor.setExpressionLiteral(RegenerateLiteral())
                visitor.days = context.days?.toDouble()
                visit(expression, visitor) as String
            } catch (e: ParserException) {
                ""
            }
        }
    }
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private fun getHandledValue(value: Any?): Any? {
        return if (value == null) {
            0.0
        } else if (value is Double && value.isNaN()) {
            null
        } else {
            value
        }
    }

    /**
     * Creates a new ExpressionItemsVisitor object.
     */
    private fun newVisitor(
        // ParseType parseType,
        itemMethod: ExpressionItemMethod,
        // List<Period> samplePeriods,
        constantMap: Map<String, Constant>,
    ): CommonExpressionVisitor {
        return CommonExpressionVisitor(
            CommonExpressionVisitorScope.Expression(
                itemMap = validationRuleExpressionItems,
                itemMethod = itemMethod,
                constantMap = constantMap,
                dataElementStore = dataElementStore,
                categoryOptionComboStore = categoryOptionComboStore,
                organisationUnitGroupStore = organisationUnitGroupStore,
                programStageStore = programStageStore,
            ),
        )
    }
}
