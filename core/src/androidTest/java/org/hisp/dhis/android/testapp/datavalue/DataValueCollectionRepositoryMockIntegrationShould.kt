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
package org.hisp.dhis.android.testapp.datavalue

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class DataValueCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun find_all() {
        val dataValues = d2.dataValueModule().dataValues()
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(9)
    }

    @Test
    fun filter_by_data_element() {
        val dataValues = d2.dataValueModule().dataValues()
            .byDataElementUid()
            .eq("g9eOBujte1U")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(9)
    }

    @Test
    fun filter_by_period() {
        val dataValues = d2.dataValueModule().dataValues()
            .byPeriod()
            .eq("2018")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_organisation_unit() {
        val dataValues = d2.dataValueModule().dataValues()
            .byOrganisationUnitUid()
            .eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(7)
    }

    @Test
    fun filter_by_category_option_combo() {
        val dataValues = d2.dataValueModule().dataValues()
            .byCategoryOptionComboUid()
            .eq("Gmbgme7z9BF")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(8)
    }

    @Test
    fun filter_by_attribute_option_combo() {
        val dataValues = d2.dataValueModule().dataValues()
            .byAttributeOptionComboUid()
            .eq("bRowv6yZOF2")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(6)
    }

    @Test
    fun filter_by_value() {
        val dataValues = d2.dataValueModule().dataValues()
            .byValue()
            .eq("11")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_stored_by() {
        val dataValues = d2.dataValueModule().dataValues()
            .byStoredBy()
            .eq("android")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_created() {
        val dataValues = d2.dataValueModule().dataValues()
            .byCreated()
            .eq(DateUtils.DATE_FORMAT.parse("2010-02-11T00:00:00.000+0100"))
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_last_updated() {
        val dataValues = d2.dataValueModule().dataValues()
            .byLastUpdated()
            .eq(DateUtils.DATE_FORMAT.parse("2011-01-11T00:00:00.000+0000"))
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_comment() {
        val dataValues = d2.dataValueModule().dataValues()
            .byComment()
            .eq("Relevant comment")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_follow_up() {
        val dataValues = d2.dataValueModule().dataValues()
            .byFollowUp().isFalse
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(8)
    }

    @Test
    fun filter_by_state() {
        val dataValues = d2.dataValueModule().dataValues()
            .bySyncState().eq(State.SYNCED)
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(9)
    }

    @Test
    fun filter_by_deleted() {
        val dataValues = d2.dataValueModule().dataValues()
            .byDeleted().isFalse
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(9)
    }

    @Test
    fun filter_by_dataset() {
        val dataValues = d2.dataValueModule().dataValues()
            .byDataSetUid("TaMAefItzgt")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun return_data_value_object_repository() {
        val objectRepository = d2.dataValueModule().dataValues()
            .value(
                "2018",
                "DiszpKrYNg8",
                "g9eOBujte1U",
                "Gmbgme7z9BF",
                "bRowv6yZOF2",
            )

        assertThat(objectRepository.blockingExists()).isTrue()
        assertThat(objectRepository.blockingGet()!!.value()).isEqualTo("10")
    }
}
