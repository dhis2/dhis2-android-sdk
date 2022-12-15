/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
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
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.parser.expression.antlr.ExpressionParser

internal class ExpressionService @Inject constructor(
    private val dataElementStore: IdentifiableObjectStore<DataElement>,
    private val categoryOptionComboStore: CategoryOptionComboStore,
    private val organisationUnitGroupStore: IdentifiableObjectStore<OrganisationUnitGroup>,
    private val programStageStore: IdentifiableObjectStore<ProgramStage>
) {
    private val validationRuleExpressionItems: Map<Int, ExpressionItem> = getValidationRuleExpressionItems()

    private fun getValidationRuleExpressionItems(): Map<Int, ExpressionItem> {
        return COMMON_EXPRESSION_ITEMS + (
            ExpressionParser.HASH_BRACE to DimItemDataElementAndOperand()
            )
    }

    fun getDimensionalItemIds(expression: String?): Set<DimensionalItemId> {
        return if (expression == null) {
            emptySet()
        } else {
            // TODO REVIEW
            val itemIds: MutableSet<DimensionalItemId> = HashSet()
            val visitor = newVisitor(ITEM_GET_IDS, emptyMap())
            visitor.itemIds = itemIds
            visit(expression, visitor)
            itemIds
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
            val visitor = newVisitor(ITEM_GET_DESCRIPTIONS, constantMap)
            visit(expression, visitor)
            val itemDescriptions = visitor.itemDescriptions
            var description: String = expression
            for ((key, value) in itemDescriptions) {
                description = description.replace(key, value)
            }
            description
        }
    }

    fun getExpressionValue(expression: String?): Any? {
        return getExpressionValue(
            expression, emptyMap(), emptyMap(), emptyMap(), 0, MissingValueStrategy.NEVER_SKIP
        )
    }

    @Suppress("LongParameterList", "ComplexMethod", "ReturnCount")
    fun getExpressionValue(
        expression: String?,
        valueMap: Map<DimensionalItemObject, Double>,
        constantMap: Map<String, Constant>,
        orgUnitCountMap: Map<String, Int>,
        days: Int?,
        missingValueStrategy: MissingValueStrategy
    ): Any? {
        return expression?.let {
            val visitor = newVisitor(
                ITEM_EVALUATE,
                constantMap
            )
            val itemValueMap = valueMap.map { it.key.dimensionItem to it.value }.toMap()

            visitor.itemValueMap = itemValueMap
            visitor.orgUnitCountMap = orgUnitCountMap
            if (days != null) {
                visitor.days = days.toDouble()
            }
            val value = visit(expression, visitor)
            val itemsFound = visitor.state.itemsFound
            val itemValuesFound = visitor.state.itemValuesFound

            when (missingValueStrategy) {
                MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING -> {
                    if (itemValuesFound < itemsFound) {
                        return null
                    }
                    if (itemsFound != 0 && itemValuesFound == 0) {
                        return null
                    }
                    if (value == null) {
                        // TODO Handle other ParseType
                        return 0.0
                    }
                }
                MissingValueStrategy.SKIP_IF_ALL_VALUES_MISSING -> {
                    if (itemsFound != 0 && itemValuesFound == 0) {
                        return null
                    }
                    if (value == null) {
                        return 0.0
                    }
                }
                MissingValueStrategy.NEVER_SKIP -> if (value == null) {
                    return 0.0
                }
            }

            if (value is Double && value.isNaN()) {
                null
            } else {
                value
            }
        }
    }

    fun regenerateExpression(
        expression: String?,
        valueMap: Map<DimensionalItemObject, Double>,
        constantMap: Map<String, Constant>,
        orgUnitCountMap: Map<String, Int>,
        days: Int?
    ): String {
        return if (expression == null) {
            ""
        } else {
            val visitor = newVisitor(
                ITEM_REGENERATE,
                constantMap
            )

            val itemValueMap = valueMap.map { it.key.dimensionItem to it.value }.toMap()
            visitor.itemValueMap = itemValueMap
            visitor.orgUnitCountMap = orgUnitCountMap
            visitor.setExpressionLiteral(RegenerateLiteral())
            if (days != null) {
                visitor.days = days.toDouble()
            }
            visit(expression, visitor) as String
        }
    }
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    /**
     * Creates a new ExpressionItemsVisitor object.
     */
    private fun newVisitor(
        // ParseType parseType,
        itemMethod: ExpressionItemMethod,
        // List<Period> samplePeriods,
        constantMap: Map<String, Constant>
    ): CommonExpressionVisitor {
        return CommonExpressionVisitor(
            CommonExpressionVisitorScope.Expression(
                itemMap = validationRuleExpressionItems,
                itemMethod = itemMethod,
                constantMap = constantMap,
                dataElementStore = dataElementStore,
                categoryOptionComboStore = categoryOptionComboStore,
                organisationUnitGroupStore = organisationUnitGroupStore,
                programStageStore = programStageStore
            )
        )
    }
}
