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
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

class DataValueConflictCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun find_all() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(3)
    }

    @Test
    fun filter_by_conflict() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byConflict()
            .eq("conflict")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_element() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byDataElement()
            .eq("g9eOBujte1U")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_period() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byPeriod()
            .eq("202101")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_organisation_unit() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byOrganisationUnitUid()
            .eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_category_option_combo() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byCategoryOptionCombo()
            .eq("Gmbgme7z9BF")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_attribute_option_combo() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byAttributeOptionCombo()
            .eq("bRowv6yZOF2")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_value() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byValue()
            .eq("5")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_created() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byCreated()
            .eq(DateUtils.DATE_FORMAT.parse("021-06-02T12:38:53.743"))
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(3)
    }

    @Test
    fun filter_by_state() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byStatus().eq(ImportStatus.SUCCESS)
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_display_description() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byDisplayDescription()
            .eq("display_description_other")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_error_code() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byErrorCode().isNull
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(3)
    }

    @Test
    fun filter_by_dataset() {
        val dataValues = d2.dataValueModule().dataValueConflicts()
            .byDataSet("lyLU2wR22tC")
            .blockingGet()

        assertThat(dataValues.size).isEqualTo(1)
    }
}
