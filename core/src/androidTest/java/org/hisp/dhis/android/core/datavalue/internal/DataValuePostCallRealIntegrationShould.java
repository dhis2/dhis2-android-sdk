/*
 * Copyright (c) 2004-2019, University of Oslo
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

package org.hisp.dhis.android.core.datavalue.internal;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore;
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.junit.Before;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class DataValuePostCallRealIntegrationShould extends BaseRealIntegrationTest {

    private D2 d2;

    private DataValueStore dataValueStore;

    @Before
    public void setUp() throws IOException {

        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        dataValueStore = DataValueStore.create(databaseAdapter());
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void dataValuesWithToPostState_shouldBeUploaded() throws Exception {

        d2.syncMetaData().call();
        d2.aggregatedModule().data().download().asObservable().subscribe();

        DataValue dataValue = getTestDataValueWith(State.TO_POST, 1);

        assertThat(insertToPostDataValue(dataValue)).isTrue();

        DataValueImportSummary dataValueImportSummary = d2.dataValueModule().dataValues.upload().call();

        int importCountTotal = dataValueImportSummary.importCount().imported() +
                dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void dataValuesWithToUpdateState_shouldBeUploaded() throws Exception {

        d2.syncMetaData().call();
        d2.aggregatedModule().data().download().asObservable().subscribe();

        DataValue dataValue = getTestDataValueWith(State.TO_UPDATE,2);

        assertThat(insertToPostDataValue(dataValue)).isTrue();

        DataValueImportSummary dataValueImportSummary = d2.dataValueModule().dataValues.upload().call();

        int importCountTotal = dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();
    }


    private boolean insertToPostDataValue(DataValue dataValue){

        return (dataValueStore.insert(dataValue) > 0);
    }

    private DataValue getTestDataValueWith(State state, int value) {

        DataValue dataValue =
                DataValue.builder()
                        .dataElement("WUg3MYWQ7pt")
                        .categoryOptionCombo("bjDvmb4bfuf")
                        .attributeOptionCombo("nvLjum6Xbv5")
                        .period("2018")
                        .organisationUnit("DiszpKrYNg8")
                        .value(String.valueOf(value))
                        .state(state)
                        .build();

        return dataValue;
    }
}
