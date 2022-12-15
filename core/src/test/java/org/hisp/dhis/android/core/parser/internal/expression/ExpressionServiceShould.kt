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
package org.hisp.dhis.android.core.parser.internal.expression

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DataElementObject
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DataElementOperandObject
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class ExpressionServiceShould {
    private val dataElementId1 = "sK2wroysTNW"
    private val dataElementId2 = "lZGmxYbs97q"
    private val categoryOptionComboId1 = "tpghB93ks57"
    private val categoryOptionComboId2 = "zDhUuAYrxNC"
    private val constantId = "e19hj1w7yKP"
    private val orgunitGroupId = "RAL7YE4KJ58"
    private val days = "[days]"

    private val dataElementStore: IdentifiableObjectStore<DataElement> = mock()
    private val dataElement1: DataElement = mock()
    private val dataElement2: DataElement = mock()

    private val categoryOptionComboStore: CategoryOptionComboStore = mock()
    private val categoryOptionCombo1: CategoryOptionCombo = mock()
    private val categoryOptionCombo2: CategoryOptionCombo = mock()

    private val organisationUnitGroupStore: IdentifiableObjectStore<OrganisationUnitGroup> = mock()
    private val organisationUnitGroup: OrganisationUnitGroup = mock()

    private val programStageStore: IdentifiableObjectStore<ProgramStage> = mock()

    private val constant: Constant = mock()

    private lateinit var constantMap: Map<String, Constant>
    private lateinit var service: ExpressionService

    @Before
    fun setUp() {
        service = ExpressionService(
            dataElementStore,
            categoryOptionComboStore,
            organisationUnitGroupStore,
            programStageStore
        )
        constantMap = mapOf(constantId to constant)

        whenever(dataElementStore.selectByUid(dataElementId1)).thenReturn(dataElement1)
        whenever(dataElementStore.selectByUid(dataElementId2)).thenReturn(dataElement2)
        whenever(categoryOptionComboStore.selectByUid(categoryOptionComboId1)).thenReturn(categoryOptionCombo1)
        whenever(categoryOptionComboStore.selectByUid(categoryOptionComboId2)).thenReturn(categoryOptionCombo2)
        whenever(organisationUnitGroupStore.selectByUid(orgunitGroupId)).thenReturn(organisationUnitGroup)
    }

    @Test
    fun evaluate_dataelements() {
        val expression = deOperand(dataElementId1, categoryOptionComboId1) + " + " + deOperand(
            dataElementId2,
            categoryOptionComboId2
        )
        val valueMap: Map<DimensionalItemObject, Double> = mapOf(
            DataElementOperandObject(dataElementId1, categoryOptionComboId1) to 5.0,
            DataElementOperandObject(dataElementId2, categoryOptionComboId2) to 3.0
        )

        val result = service.getExpressionValue(
            expression,
            valueMap,
            constantMap,
            emptyMap(),
            10,
            MissingValueStrategy.NEVER_SKIP
        ) as Double?

        assertThat(result).isEqualTo(8.0)
    }

    @Test
    fun evaluate_constants() {
        val expression = de(dataElementId1) + " + " + constant(constantId)
        val valueMap: Map<DimensionalItemObject, Double> = mapOf(
            DataElementOperandObject(dataElementId1, null) to 5.0
        )
        whenever(constant.value()).thenReturn(4.0)

        val result = service.getExpressionValue(
            expression, valueMap, constantMap, emptyMap(),
            10, MissingValueStrategy.NEVER_SKIP
        ) as Double?

        assertThat(result).isEqualTo(9.0)
    }

    @Test
    fun evaluate_without_coc() {
        val expression = de(dataElementId1) + " + " + de(dataElementId2)
        val valueMap: Map<DimensionalItemObject, Double> = mapOf(
            DataElementOperandObject(dataElementId1, null) to 5.0,
            DataElementOperandObject(dataElementId2, null) to 3.0
        )
        val result = service.getExpressionValue(
            expression,
            valueMap,
            constantMap,
            emptyMap(),
            10,
            MissingValueStrategy.NEVER_SKIP
        ) as Double?

        assertThat(result).isEqualTo(8.0)
    }

    @Test
    fun evaluate_days() {
        val expression = de(dataElementId1) + " + " + days
        val valueMap: Map<DimensionalItemObject, Double> = mapOf(
            DataElementOperandObject(dataElementId1, null) to 5.0
        )
        val result = service.getExpressionValue(
            expression,
            valueMap,
            constantMap,
            emptyMap(),
            10,
            MissingValueStrategy.NEVER_SKIP
        ) as Double?

        assertThat(result).isEqualTo(15.0)
    }

    @Test
    fun evaluate_orgunit_groups() {
        val expression = de(dataElementId1) + " + " + oug(orgunitGroupId)
        val valueMap: Map<DimensionalItemObject, Double> = mapOf(
            DataElementOperandObject(dataElementId1, null) to 5.0
        )
        val orgunitMap: Map<String, Int> = mapOf(
            orgunitGroupId to 20
        )
        val result = service.getExpressionValue(
            expression,
            valueMap,
            constantMap,
            orgunitMap,
            10,
            MissingValueStrategy.NEVER_SKIP
        ) as Double?

        assertThat(result).isEqualTo(25.0)
    }

    @Test
    fun evaluate_missing_strategies_with_some_missing_values() {
        val expression = de(dataElementId1) + " + " + de(dataElementId2)
        val valueMap: Map<DimensionalItemObject, Double> = mapOf(
            DataElementOperandObject(dataElementId1, null) to 5.0
        )

        mapOf(
            MissingValueStrategy.NEVER_SKIP to 5.0,
            MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING to null,
            MissingValueStrategy.SKIP_IF_ALL_VALUES_MISSING to 5.0
        ).forEach { (strategy, expected) ->
            val result = service.getExpressionValue(
                expression, valueMap,
                constantMap, emptyMap(), 10, strategy
            ) as Double?

            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun evaluate_missing_strategies_with_all_missing_values() {
        val expression = de(dataElementId1) + " + " + de(dataElementId2)
        val valueMap: Map<DimensionalItemObject, Double> = emptyMap()

        mapOf(
            MissingValueStrategy.NEVER_SKIP to 0.0,
            MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING to null,
            MissingValueStrategy.SKIP_IF_ALL_VALUES_MISSING to null
        ).forEach { (strategy, expected) ->
            val result = service.getExpressionValue(
                expression, valueMap,
                constantMap, emptyMap(), 10, strategy
            ) as Double?

            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun evaluate_null_expression() {
        assertThat(service.getExpressionValue(null)).isNull()
        assertThat(service.getExpressionDescription(null, emptyMap())).isEqualTo("")
        assertThat(service.getDataElementOperands(null)).isEmpty()
        assertThat(service.regenerateExpression(null, emptyMap(), constantMap, emptyMap(), 10)).isEqualTo("")
    }

    @Test
    fun evaluate_number_comparison() {
        assertThat(service.getExpressionValue("5.0 < 8.0") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("5.0 < 5.0") as Boolean?).isFalse()
        assertThat(service.getExpressionValue("5.0 <= 8.0") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("5.0 <= 5.0") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("5.0 == 8.0") as Boolean?).isFalse()
        assertThat(service.getExpressionValue("5.0 == 5.0") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("5.0 != 8.0") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("5.0 != 5.0") as Boolean?).isFalse()
    }

    @Test
    fun evaluate_logical_operators() {
        assertThat(service.getExpressionValue("true && true") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("true and true") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("true || false") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("true or false") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("5.0 == 8.0 && 4.0 == 4.0") as Boolean?).isFalse()
        assertThat(service.getExpressionValue("5.0 == 5.0 && 4.0 == 4.0") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("5.0 != 8.0 || 5.0 == 8.0") as Boolean?).isTrue()
        assertThat(service.getExpressionValue("5.0 != 5.0 || 8.0 != 8.0") as Boolean?).isFalse()
    }

    @Test
    fun evaluate_functions() {
        assertThat(service.getExpressionValue("firstNonNull(4 , 'two', 6)")).isEqualTo(4.0)
        assertThat(service.getExpressionValue("firstNonNull('two' , 4, 6)")).isEqualTo("two")
        assertThat(service.getExpressionValue("greatest(5, 2, 7, 3)")).isEqualTo(7.0)
        assertThat(service.getExpressionValue("greatest(-5, -2, -7)")).isEqualTo(-2.0)
        assertThat(service.getExpressionValue("if(5 > 2, 5, 2)")).isEqualTo(5.0)
        assertThat(service.getExpressionValue("if(5 < 2, 5, 2)")).isEqualTo(2.0)
        assertThat(service.getExpressionValue("isNotNull(5)")).isEqualTo(true)
        assertThat(service.getExpressionValue("isNull(5)")).isEqualTo(false)
        assertThat(service.getExpressionValue("least(5, 2, 7, 3)")).isEqualTo(2.0)
        assertThat(service.getExpressionValue("least(-5, -2, -7)")).isEqualTo(-7.0)
        assertThat(service.getExpressionValue("log(100)") as Double).isAtLeast(4.6)
        assertThat(service.getExpressionValue("log(100)") as Double).isAtMost(4.7)
        assertThat(service.getExpressionValue("log10(100)")).isEqualTo(2.0)
    }

    @Test
    fun evaluate_divide_by_zero() {
        assertThat(service.getExpressionValue("4 / 0")).isEqualTo(null)
    }

    @Test
    fun get_dataelement_ids() {
        val expression = de(dataElementId1) + " + " + de(dataElementId2)
        val dataElementOperands = service.getDataElementOperands(expression)
        assertThat(dataElementOperands.size).isEqualTo(2)

        for (deo in dataElementOperands) {
            if (deo.dataElement()!!.uid() != dataElementId1 && deo.dataElement()!!.uid() != dataElementId2) {
                fail("Should not reach this point")
            }
            assertThat(deo.categoryOptionCombo()).isNull()
        }
    }

    @Test
    fun get_dataelement_operands_ids() {
        val expression = deOperand(dataElementId1, categoryOptionComboId1) + " + " + deOperand(
            dataElementId2,
            categoryOptionComboId2
        )
        val dataElementOperands = service.getDataElementOperands(expression)
        assertThat(dataElementOperands.size).isEqualTo(2)

        for (deo in dataElementOperands) {
            if (deo.dataElement()!!.uid() == dataElementId1) {
                assertThat(deo.categoryOptionCombo()!!.uid()).isEqualTo(categoryOptionComboId1)
            } else if (deo.dataElement()!!.uid() == dataElementId2) {
                assertThat(deo.categoryOptionCombo()!!.uid()).isEqualTo(categoryOptionComboId2)
            } else {
                fail("Should not reach this point")
            }
        }
    }

    @Test
    fun get_dataelement_ids_in_empty_expression() {
        val expression = days + " + " + constant(constantId) + " + " + oug(constantId)
        val dataElementOperands: Set<DataElementOperand> = service.getDataElementOperands(expression)
        assertThat(dataElementOperands).isEmpty()
    }

    @Test
    fun get_description_when_all_items_exist() {
        whenever(dataElement1.displayName()).thenReturn("Data Element 1")
        whenever(dataElement2.displayName()).thenReturn("Data Element 2")
        whenever(categoryOptionCombo1.displayName()).thenReturn("COC 1")
        whenever(organisationUnitGroup.displayName()).thenReturn("Org Unit Group")
        whenever(constant.displayName()).thenReturn("Constant")

        val expression = deOperand(dataElementId1, categoryOptionComboId1) + " + " +
            de(dataElementId2) + " * " +
            oug(orgunitGroupId) + " + " +
            constant(constantId)
        val description = service.getExpressionDescription(expression, constantMap)

        assertThat(description).isEqualTo("Data Element 1 (COC 1) + Data Element 2 * Org Unit Group + Constant")
    }

    @Test
    fun get_description_with_missing_items() {
        whenever(dataElement1.displayName()).thenReturn("Data Element 1")

        val expression = de(dataElementId1) + " + " + de("atGmxEbs97n")
        val description = service.getExpressionDescription(expression, emptyMap())

        assertThat(description).isEqualTo("Data Element 1 + " + de("atGmxEbs97n"))
    }

    @Test
    fun regenerate_expression() {
        val expression = deOperand(dataElementId1, categoryOptionComboId1) + " + " +
            de(dataElementId2) + " / " +
            constant(constantId) + " * " +
            oug(orgunitGroupId) + " - " +
            days
        val valueMap: Map<DimensionalItemObject, Double> = mapOf(
            DataElementOperandObject(dataElementId1, categoryOptionComboId1) to 5.0,
            DataElementObject(dataElementId2) to 3.0
        )
        val orgunitMap: Map<String, Int> = mapOf(
            orgunitGroupId to 20
        )
        whenever(constant.value()).thenReturn(3.14)

        val regeneratedExpression: Any = service.regenerateExpression(
            expression, valueMap, constantMap,
            orgunitMap, 10
        )

        assertThat(regeneratedExpression).isEqualTo("5.0 + 3.0 / 3.14 * 20 - 10.0")
    }

    @Test
    fun regenerate_expression_with_missing_items() {
        val expression = deOperand(dataElementId1, categoryOptionComboId1) + " + " + de(dataElementId2)
        val valueMap: Map<DimensionalItemObject, Double> = mapOf(
            DataElementOperandObject(dataElementId1, categoryOptionComboId1) to 5.0
        )

        val regeneratedExpression: Any =
            service.regenerateExpression(expression, valueMap, constantMap, emptyMap(), 10)

        assertThat(regeneratedExpression).isEqualTo("5.0 + " + de(dataElementId2))
    }

    private fun constant(uid: String): String {
        return "C{$uid}"
    }

    private fun de(uid: String): String {
        return "#{$uid}"
    }

    private fun deOperand(de: String, coc: String): String {
        return "#{$de.$coc}"
    }

    private fun oug(uid: String): String {
        return "OUG{$uid}"
    }
}
