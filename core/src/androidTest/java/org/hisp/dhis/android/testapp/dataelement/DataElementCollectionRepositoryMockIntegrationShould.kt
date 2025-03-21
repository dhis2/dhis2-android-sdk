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
package org.hisp.dhis.android.testapp.dataelement

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class DataElementCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val dataElements = d2.dataElementModule().dataElements().blockingGet()
        assertThat(dataElements.size).isEqualTo(10)
    }

    @Test
    fun filter_by_value_type() {
        val dataElements = d2.dataElementModule().dataElements()
            .byValueType().eq(ValueType.TEXT)
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(2)
    }

    @Test
    fun filter_by_zero_is_significant() {
        val dataElements = d2.dataElementModule().dataElements()
            .byZeroIsSignificant().isFalse
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(8)
    }

    @Test
    fun filter_by_aggregation_type() {
        val dataElements = d2.dataElementModule().dataElements()
            .byAggregationType().eq("AVERAGE")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(2)
    }

    @Test
    fun filter_by_form_name() {
        val dataElements = d2.dataElementModule().dataElements()
            .byFormName().eq("ANC Visit")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(2)
    }

    @Test
    fun filter_by_domain_type() {
        val dataElements = d2.dataElementModule().dataElements()
            .byDomainType().eq("TRACKER")
            .blockingGet()
        assertThat(dataElements.size).isEqualTo(10)
    }

    @Test
    fun filter_by_display_form_name() {
        val dataElements = d2.dataElementModule().dataElements()
            .byDisplayFormName().eq("ANC Visit")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(2)
    }

    @Test
    fun filter_by_option_set() {
        val dataElements = d2.dataElementModule().dataElements()
            .byOptionSetUid().eq("VQ2lai3OfVG")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(1)
    }

    @Test
    fun filter_by_category_combo() {
        val dataElements = d2.dataElementModule().dataElements()
            .byCategoryComboUid().eq("m2jTvAj5kkm")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(6)
    }

    @Test
    fun filter_by_field_mask() {
        val dataElements = d2.dataElementModule().dataElements()
            .byFieldMask().eq("XXXXX")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(1)
    }

    @Test
    fun filter_by_field_color() {
        val dataElements = d2.dataElementModule().dataElements()
            .byColor().eq("#600")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(1)
    }

    @Test
    fun filter_by_field_icon() {
        val dataElements = d2.dataElementModule().dataElements()
            .byIcon().eq("data-element-icon-2")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(1)
    }

    @Test
    fun include_legends_as_children() {
        val dataElementWithLegendSets = d2.dataElementModule()
            .dataElements()
            .withLegendSets()
            .blockingGet()[4]

        val legendSets = dataElementWithLegendSets.legendSets()
        assertThat(legendSets!!.size).isEqualTo(1)
        assertThat(legendSets[0].uid()).isEqualTo("TiOkbpGEud4")
    }

    @Test
    fun include_attributeValues_as_children() {
        val dataElementWithAttributeValues =
            d2.dataElementModule().dataElements()
                .withAttributes()
                .blockingGet()[4]

        val attributeValues = dataElementWithAttributeValues.attributeValues()
        assertThat(attributeValues!!.size).isEqualTo(2)
        assertThat(attributeValues[0].attribute().uid()).isEqualTo("b0vcadVrn08")
        assertThat(attributeValues[0].value()).isEqualTo("Direct 2")
        assertThat(attributeValues[1].attribute().uid()).isEqualTo("qXS2NDUEAOS")
        assertThat(attributeValues[1].value()).isEqualTo("Direct")
    }
}
