/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.domain.aggregated.data.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.dataset.DataSetElement
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AggregatedDataSyncHashHelperShould {
    private val dataElementStore: DataElementStore = mock()
    private val hashHelper = AggregatedDataSyncHashHelper(dataElementStore)

    private val dataSetElement1 = dataSetElement("de1", null)
    private val dataSetElement2 = dataSetElement("de2", "overrideCombo")
    private val dataSet = DataSetSamples.dataSet.toBuilder()
        .dataSetElements(listOf(dataSetElement1, dataSetElement2))
        .build()

    @Test
    fun return_same_hash_regardless_of_data_set_elements_order() = runTest {
        givenDataElements(dataElement("de1", "combo1"), dataElement("de2", "combo2"))

        val reordered = dataSet.toBuilder().dataSetElements(listOf(dataSetElement2, dataSetElement1)).build()

        assertThat(hashHelper.getDataSetDataElementsHash(reordered))
            .isEqualTo(hashHelper.getDataSetDataElementsHash(dataSet))
    }

    @Test
    fun change_hash_if_data_set_category_combo_changes() = runTest {
        givenDataElements(dataElement("de1", "combo1"), dataElement("de2", "combo2"))

        val changed = dataSet.toBuilder().categoryCombo(ObjectWithUid.create("otherComboUid")).build()

        assertThat(hashHelper.getDataSetDataElementsHash(changed))
            .isNotEqualTo(hashHelper.getDataSetDataElementsHash(dataSet))
    }

    @Test
    fun change_hash_if_data_element_category_combo_changes() = runTest {
        givenDataElements(dataElement("de1", "combo1"), dataElement("de2", "combo2"))
        val hash = hashHelper.getDataSetDataElementsHash(dataSet)

        givenDataElements(dataElement("de1", "otherCombo"), dataElement("de2", "combo2"))

        assertThat(hashHelper.getDataSetDataElementsHash(dataSet)).isNotEqualTo(hash)
    }

    @Test
    fun change_hash_if_data_set_element_category_combo_override_changes() = runTest {
        givenDataElements(dataElement("de1", "combo1"), dataElement("de2", "combo2"))

        val changed = dataSet.toBuilder()
            .dataSetElements(listOf(dataSetElement1, dataSetElement("de2", "otherOverrideCombo")))
            .build()

        assertThat(hashHelper.getDataSetDataElementsHash(changed))
            .isNotEqualTo(hashHelper.getDataSetDataElementsHash(dataSet))
    }

    @Test
    fun keep_hash_if_overridden_data_element_category_combo_changes() = runTest {
        givenDataElements(dataElement("de1", "combo1"), dataElement("de2", "combo2"))
        val hash = hashHelper.getDataSetDataElementsHash(dataSet)

        givenDataElements(dataElement("de1", "combo1"), dataElement("de2", "otherCombo"))

        assertThat(hashHelper.getDataSetDataElementsHash(dataSet)).isEqualTo(hash)
    }

    @Test
    fun change_hash_if_data_element_is_added() = runTest {
        givenDataElements(dataElement("de1", "combo1"), dataElement("de2", "combo2"), dataElement("de3", "combo1"))

        val changed = dataSet.toBuilder()
            .dataSetElements(listOf(dataSetElement1, dataSetElement2, dataSetElement("de3", null)))
            .build()

        assertThat(hashHelper.getDataSetDataElementsHash(changed))
            .isNotEqualTo(hashHelper.getDataSetDataElementsHash(dataSet))
    }

    private suspend fun givenDataElements(vararg dataElements: DataElement) {
        whenever(dataElementStore.selectByUids(any())).doReturn(dataElements.toList())
    }

    private fun dataSetElement(dataElementUid: String, categoryComboUid: String?): DataSetElement {
        return DataSetElement.builder()
            .dataSet(ObjectWithUid.create(DataSetSamples.dataSet.uid()))
            .dataElement(ObjectWithUid.create(dataElementUid))
            .categoryCombo(categoryComboUid?.let { ObjectWithUid.create(it) })
            .build()
    }

    private fun dataElement(uid: String, categoryComboUid: String): DataElement {
        return DataElement.builder()
            .uid(uid)
            .categoryCombo(ObjectWithUid.create(categoryComboUid))
            .build()
    }
}
