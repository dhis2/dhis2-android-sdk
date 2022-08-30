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
package org.hisp.dhis.android.core.parser.internal.service.utils

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DataElementObject
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DataElementOperandObject
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject
import org.hisp.dhis.android.core.parser.internal.service.utils.ExpressionHelper.getValueMap
import org.junit.Test

class ExpressionHelperShould {

    private val dataElement1 = "sK2wroysTNW"
    private val dataElement2 = "lZGmxYbs97q"
    private val coc1 = "tpghB93ks57"
    private val coc2 = "zDhUuAYrxNC"
    private val cocDefault = "HllvX50cXC0"

    @Test
    fun map_data_elements_only() {
        val dataValues: List<DataValue> = listOf(
            dv(dataElement1, cocDefault, "4"),
            dv(dataElement2, cocDefault, "6")
        )
        val valueMap = getValueMap(dataValues)
        Truth.assertThat(valueMap.keys.size).isEqualTo(4)
        assertContainsEntry(valueMap, dataElement1, 4.0)
        assertContainsEntry(valueMap, dataElement1, cocDefault, 4.0)
        assertContainsEntry(valueMap, dataElement2, 6.0)
        assertContainsEntry(valueMap, dataElement2, cocDefault, 6.0)
    }

    @Test
    fun map_data_elements_and_operands() {
        val dataValues: List<DataValue> = listOf(
            dv(dataElement1, coc1, "4"),
            dv(dataElement1, coc2, "5"),
            dv(dataElement2, coc1, "6"),
            dv(dataElement2, coc2, "7")
        )
        val valueMap = getValueMap(dataValues)
        Truth.assertThat(valueMap.keys.size).isEqualTo(6)
        assertContainsEntry(valueMap, dataElement1, 9.0)
        assertContainsEntry(valueMap, dataElement1, coc1, 4.0)
        assertContainsEntry(valueMap, dataElement1, coc2, 5.0)
        assertContainsEntry(valueMap, dataElement2, 13.0)
        assertContainsEntry(valueMap, dataElement2, coc1, 6.0)
        assertContainsEntry(valueMap, dataElement2, coc2, 7.0)
    }

    @Test
    fun ignore_non_numeric_values() {
        val dataValues: List<DataValue> = listOf(
            dv(dataElement1, coc1, "4"),
            dv(dataElement1, coc2, "5"),
            dv(dataElement2, coc1, "text1"),
            dv(dataElement2, coc2, "text2")
        )
        val valueMap = getValueMap(dataValues)
        Truth.assertThat(valueMap.keys.size).isEqualTo(3)
        assertNotContainsEntry(valueMap, dataElement2, null)
        assertNotContainsEntry(valueMap, dataElement2, coc1)
        assertNotContainsEntry(valueMap, dataElement2, coc2)
    }

    private fun dv(dataElementId: String, categoryOptionComboId: String, value: String): DataValue {
        return DataValue.builder()
            .dataElement(dataElementId)
            .categoryOptionCombo(categoryOptionComboId)
            .value(value)
            .build()
    }

    private fun assertContainsEntry(
        valueMap: Map<DimensionalItemObject, Double>,
        dataElementId: String,
        value: Double
    ) {
        assertContainsEntry(valueMap, dataElementId, null, value)
    }

    private fun assertContainsEntry(
        valueMap: Map<DimensionalItemObject, Double>,
        dataElementId: String,
        categoryOptionComboId: String?,
        value: Double
    ) {
        val key =
            if (categoryOptionComboId == null) DataElementObject.create(dataElementId)
            else DataElementOperandObject.create(dataElementId, categoryOptionComboId)

        val entry = valueMap.entries.find { it.key == key }

        Truth.assertThat(entry).isNotNull()
        Truth.assertThat(entry!!.value).isEqualTo(value)
    }

    private fun assertNotContainsEntry(
        valueMap: Map<DimensionalItemObject, Double>,
        dataElementId: String,
        categoryOptionComboId: String?
    ) {
        val key =
            if (categoryOptionComboId == null) DataElementObject.create(dataElementId)
            else DataElementOperandObject.create(dataElementId, categoryOptionComboId)

        valueMap.entries.none { it.key == key }
    }
}
