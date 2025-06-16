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
package org.hisp.dhis.android.core.dataset.internal

import com.google.common.truth.Truth
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCollectionRepository
import org.junit.Before
import java.text.SimpleDateFormat

class DataSetCompleteRegistrationPostCallRealIntegrationShould : BaseRealIntegrationTest() {
    private lateinit var dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore

    @Before
    override fun setUp() {
        super.setUp()
        dataSetCompleteRegistrationStore = DataSetCompleteRegistrationStoreImpl(d2.databaseAdapter())
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun upload_data_set_complete_registrations_with_to_post_state() {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()
        d2.aggregatedModule().data().blockingDownload()

        val dataSetCompleteRegistration = getTestDataSetCompleteRegistrationWith(State.TO_POST, "2018")

        val repository: DataSetCompleteRegistrationCollectionRepository =
            d2.dataSetModule().dataSetCompleteRegistrations()

        repository.value(
            dataSetCompleteRegistration.period(),
            dataSetCompleteRegistration.organisationUnit(),
            dataSetCompleteRegistration.dataSet(),
            dataSetCompleteRegistration.attributeOptionCombo(),
        ).blockingSet()

        repository.blockingUpload()

        /*int importCountTotal = dataValueImportSummary.importCount().imported() +
                dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();*/
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun upload_data_set_complete_registrations_with_to_update_state() = runTest {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()
        d2.aggregatedModule().data().blockingDownload()

        val dataSetCompleteRegistration = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE, "2018")

        Truth.assertThat(insertToPostDataSetCompleteRegistration(dataSetCompleteRegistration)).isTrue()

        d2.dataSetModule().dataSetCompleteRegistrations().blockingUpload()

        /*int importCountTotal = dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();*/
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun update_and_delete_different_data_set_complete_registrations() = runTest {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()
        d2.aggregatedModule().data().blockingDownload()

        val toDeleteDataSetCompleteRegistration = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE, "2019")

        val dataSetCompleteRegistration = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE, "2018")

        val repository = d2.dataSetModule().dataSetCompleteRegistrations()
        repository.value(
            toDeleteDataSetCompleteRegistration.period(),
            toDeleteDataSetCompleteRegistration.organisationUnit(),
            toDeleteDataSetCompleteRegistration.dataSet(),
            toDeleteDataSetCompleteRegistration.attributeOptionCombo(),
        ).blockingSet()
        repository.value(
            dataSetCompleteRegistration.period(),
            dataSetCompleteRegistration.organisationUnit(),
            dataSetCompleteRegistration.dataSet(),
            dataSetCompleteRegistration.attributeOptionCombo(),
        ).blockingSet()
        dataSetCompleteRegistrationStore.setDeleted(toDeleteDataSetCompleteRegistration)
        dataSetCompleteRegistrationStore.setState(toDeleteDataSetCompleteRegistration, State.TO_UPDATE)

        repository.blockingUpload()

        /*int importCountTotal = dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();
        assertThat(importCountTotal == 1).isTrue();
        assertThat(dataValueImportSummary.importCount().deleted() == 1).isTrue();*/
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun delete_data_set_complete_registrations_with_to_delete_state() = runTest {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()
        d2.aggregatedModule().data().blockingDownload()

        val dataSetCompleteRegistration = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE, "2018")

        val repository = d2.dataSetModule().dataSetCompleteRegistrations()
        repository.value(
            dataSetCompleteRegistration.period(),
            dataSetCompleteRegistration.organisationUnit(),
            dataSetCompleteRegistration.dataSet(),
            dataSetCompleteRegistration.attributeOptionCombo(),
        ).blockingSet()
        dataSetCompleteRegistrationStore.setDeleted(dataSetCompleteRegistration)
        dataSetCompleteRegistrationStore.setState(dataSetCompleteRegistration, State.TO_UPDATE)

        repository.blockingUpload()

        // assertThat(dataValueImportSummary.importCount().deleted() == 1).isTrue();
    }

    private suspend fun insertToPostDataSetCompleteRegistration(
        dataSetCompleteRegistration: DataSetCompleteRegistration,
    ): Boolean {
        return (dataSetCompleteRegistrationStore.insert(dataSetCompleteRegistration) > 0)
    }

    @Throws(Exception::class)
    private fun getTestDataSetCompleteRegistrationWith(state: State, period: String): DataSetCompleteRegistration {
        return DataSetCompleteRegistration.builder()
            .period(period)
            .dataSet("BfMAe6Itzgt")
            .attributeOptionCombo("HllvX50cXC0")
            .organisationUnit("DiszpKrYNg8")
            .date(dateFormat.parse("2010-03-02"))
            .storedBy("android")
            .syncState(state)
            .build()
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    }
}
