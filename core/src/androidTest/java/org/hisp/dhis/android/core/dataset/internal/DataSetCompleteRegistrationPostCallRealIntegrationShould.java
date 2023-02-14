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

package org.hisp.dhis.android.core.dataset.internal;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCollectionRepository;
import org.junit.Before;

import java.text.SimpleDateFormat;

public class DataSetCompleteRegistrationPostCallRealIntegrationShould extends BaseRealIntegrationTest {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;

    @Before
    public void setUp() {
        super.setUp();
        dataSetCompleteRegistrationStore = DataSetCompleteRegistrationStoreImpl.create(d2.databaseAdapter());
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void upload_data_set_complete_registrations_with_to_post_state() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();
        d2.metadataModule().blockingDownload();
        d2.aggregatedModule().data().blockingDownload();

        DataSetCompleteRegistration dataSetCompleteRegistration
                = getTestDataSetCompleteRegistrationWith(State.TO_POST, "2018");

        DataSetCompleteRegistrationCollectionRepository repository
                = d2.dataSetModule().dataSetCompleteRegistrations();
        repository.value(dataSetCompleteRegistration.period(),
                dataSetCompleteRegistration.organisationUnit(),
                dataSetCompleteRegistration.dataSet(),
                dataSetCompleteRegistration.attributeOptionCombo()).blockingSet();

        repository.blockingUpload();

        /*int importCountTotal = dataValueImportSummary.importCount().imported() +
                dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();*/
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void upload_data_set_complete_registrations_with_to_update_state() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();
        d2.metadataModule().blockingDownload();
        d2.aggregatedModule().data().blockingDownload();

        DataSetCompleteRegistration dataSetCompleteRegistration
                = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE, "2018");

        assertThat(insertToPostDataSetCompleteRegistration(dataSetCompleteRegistration)).isTrue();

        d2.dataSetModule().dataSetCompleteRegistrations().blockingUpload();

        /*int importCountTotal = dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();*/
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void update_and_delete_different_data_set_complete_registrations() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();
        d2.metadataModule().blockingDownload();
        d2.aggregatedModule().data().blockingDownload();

        DataSetCompleteRegistration toDeleteDataSetCompleteRegistration
                = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE, "2019");

        DataSetCompleteRegistration dataSetCompleteRegistration
                = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE, "2018");

        DataSetCompleteRegistrationCollectionRepository repository = d2.dataSetModule().dataSetCompleteRegistrations();
        repository.value(toDeleteDataSetCompleteRegistration.period(),
                toDeleteDataSetCompleteRegistration.organisationUnit(),
                toDeleteDataSetCompleteRegistration.dataSet(),
                toDeleteDataSetCompleteRegistration.attributeOptionCombo()).blockingSet();
        repository.value(dataSetCompleteRegistration.period(),
                dataSetCompleteRegistration.organisationUnit(),
                dataSetCompleteRegistration.dataSet(),
                dataSetCompleteRegistration.attributeOptionCombo()).blockingSet();
        dataSetCompleteRegistrationStore.setDeleted(toDeleteDataSetCompleteRegistration);
        dataSetCompleteRegistrationStore.setState(toDeleteDataSetCompleteRegistration, State.TO_UPDATE);

        repository.blockingUpload();

        /*int importCountTotal = dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();
        assertThat(importCountTotal == 1).isTrue();
        assertThat(dataValueImportSummary.importCount().deleted() == 1).isTrue();*/
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void delete_data_set_complete_registrations_with_to_delete_state() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();
        d2.metadataModule().blockingDownload();
        d2.aggregatedModule().data().blockingDownload();

        DataSetCompleteRegistration dataSetCompleteRegistration
                = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE, "2018");

        DataSetCompleteRegistrationCollectionRepository repository
                = d2.dataSetModule().dataSetCompleteRegistrations();
        repository.value(dataSetCompleteRegistration.period(),
                dataSetCompleteRegistration.organisationUnit(),
                dataSetCompleteRegistration.dataSet(),
                dataSetCompleteRegistration.attributeOptionCombo()).blockingSet();
        dataSetCompleteRegistrationStore.setDeleted(dataSetCompleteRegistration);
        dataSetCompleteRegistrationStore.setState(dataSetCompleteRegistration, State.TO_UPDATE);

        repository.blockingUpload();

        // assertThat(dataValueImportSummary.importCount().deleted() == 1).isTrue();
    }

    private boolean insertToPostDataSetCompleteRegistration(DataSetCompleteRegistration dataSetCompleteRegistration){

        return (dataSetCompleteRegistrationStore.insert(dataSetCompleteRegistration) > 0);
    }

    private DataSetCompleteRegistration getTestDataSetCompleteRegistrationWith(State state, String period)
            throws Exception {

        return DataSetCompleteRegistration.builder()
                        .period(period)
                        .dataSet("BfMAe6Itzgt")
                        .attributeOptionCombo("HllvX50cXC0")
                        .organisationUnit("DiszpKrYNg8")
                        .date(dateFormat.parse("2010-03-02"))
                        .storedBy("android")
                        .syncState(state)
                        .build();
    }
}