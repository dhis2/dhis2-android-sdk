/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUploadCollectionRepository;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class DataSetCompleteRegistrationPostCallRealIntegrationShould extends AbsStoreTestCase {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private D2 d2;

    private DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;

    @Before
    public void setUp() throws IOException {

        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        dataSetCompleteRegistrationStore = DataSetCompleteRegistrationStoreImpl.create(databaseAdapter());
    }

    @Test
    public void stub() {}

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void upload_data_set_complete_registrations_with_to_post_state() throws Exception {
        d2.userModule().logIn("android", "Android123").call();
        d2.syncMetaData().call();
        d2.aggregatedModule().data().download().call();

        DataSetCompleteRegistration dataValueModel
                = getTestDataSetCompleteRegistrationWith(State.TO_POST);

        ReadWriteWithUploadCollectionRepository<DataSetCompleteRegistration> repository
                = d2.dataSetModule().dataSetCompleteRegistrations;
        repository.add(dataValueModel);

        ImportSummary importSummary = repository.upload().call();

        int importCountTotal = importSummary.importCount().imported() +
                importSummary.importCount().updated() +
                importSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void upload_data_set_complete_registrations_with_to_update_state() throws Exception {
        d2.userModule().logIn("android", "Android123").call();
        d2.syncMetaData().call();
        d2.aggregatedModule().data().download().call();

        DataSetCompleteRegistration dataSetCompleteRegistration
                = getTestDataSetCompleteRegistrationWith(State.TO_UPDATE);

        assertThat(insertToPostDataSetCompleteRegistration(dataSetCompleteRegistration)).isTrue();

        ImportSummary importSummary = d2.dataSetModule().dataSetCompleteRegistrations.upload().call();

        int importCountTotal = importSummary.importCount().updated() +
                importSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();
    }


    private boolean insertToPostDataSetCompleteRegistration(DataSetCompleteRegistration dataSetCompleteRegistration){

        return (dataSetCompleteRegistrationStore.insert(dataSetCompleteRegistration) > 0);
    }

    private DataSetCompleteRegistration getTestDataSetCompleteRegistrationWith(State state) throws Exception {

        return DataSetCompleteRegistration.builder()
                        .period("2018")
                        .dataSet("BfMAe6Itzgt")
                        .attributeOptionCombo("HllvX50cXC0")
                        .organisationUnit("DiszpKrYNg8")
                        .date(dateFormat.parse("2010-03-02"))
                        .storedBy("android")
                        .state(state)
                        .build();
    }

}
