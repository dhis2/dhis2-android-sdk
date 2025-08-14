/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.testapp.dataset

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.util.toJavaSimpleDate
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test
import java.text.ParseException

class DataSetCompleteRegistrationCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun find_all() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations().blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(3)
    }

    @Test
    fun filter_by_period() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations()
            .byPeriod()
            .eq("2016")
            .blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_set() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations()
            .byDataSetUid()
            .eq("lyLU2wR22tC")
            .blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(3)
    }

    @Test
    fun filter_by_organisation_unit() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations()
            .byOrganisationUnitUid()
            .eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(2)
    }

    @Test
    fun filter_by_attribute_option_combo() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations()
            .byAttributeOptionComboUid()
            .eq("bRowv6yZOF2").blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(3)
    }

    @Test
    @Throws(ParseException::class)
    fun filter_by_date_after() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations()
            .byDate()
            .after("2010-08-03".toJavaSimpleDate()!!)
            .blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(1)
    }

    @Test
    @Throws(ParseException::class)
    fun filter_by_date_before() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations()
            .byDate()
            .before("2010-08-03".toJavaSimpleDate()!!)
            .blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(2)
    }

    @Test
    fun filter_by_stored_by() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations()
            .byStoredBy()
            .eq("imported")
            .blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(2)
    }

    @Test
    fun filter_by_deleted() {
        val dataSetCompleteRegistrations = d2.dataSetModule().dataSetCompleteRegistrations()
            .byDeleted().isFalse
            .blockingGet()

        assertThat(dataSetCompleteRegistrations.size).isEqualTo(3)
    }
}
