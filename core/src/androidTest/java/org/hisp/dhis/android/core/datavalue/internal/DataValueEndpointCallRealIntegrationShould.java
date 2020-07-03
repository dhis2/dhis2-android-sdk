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

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Factory;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundle;
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundleKey;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Before;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getDataSets;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getOrgUnitUids;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getPeriodIds;

public class DataValueEndpointCallRealIntegrationShould extends BaseRealIntegrationTest {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private Callable<List<DataValue>> dataValueCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.forNewDatabase();
        dataValueCall = createCall();
    }

    private Callable<List<DataValue>> createCall() {
        APICallExecutor apiCallExecutor = APICallExecutorImpl.create(d2.databaseAdapter());
        Handler<DataValue> dataValueHandler =  new ObjectWithoutUidHandlerImpl<>(
                DataValueStore.create(d2.databaseAdapter()));
        AggregatedDataCallBundleKey key = AggregatedDataCallBundleKey.builder()
                .periodType(PeriodType.Daily)
                .futurePeriods(0)
                .pastPeriods(30)
                .lastUpdated(null)
                .build();
        AggregatedDataCallBundle bundle = AggregatedDataCallBundle.builder()
                .key(key)
                .dataSets(getDataSets())
                .periodIds(getPeriodIds())
                .rootOrganisationUnitUids(getOrgUnitUids())
                .build();
        return new DataValueEndpointCallFactory(getGenericCallData(d2), apiCallExecutor, dataValueHandler).create(
                DataValueQuery.create(bundle));
    }

    // @Test
    public void download_data_values() throws Exception {
        if (!d2.userModule().isLogged().blockingGet()) {
            d2.userModule().logIn(username, password, url).blockingGet();
        }

        /*  This test won't pass independently of the sync of metadata, as the foreign keys
            constraints won't be satisfied.
            To run the test, you will need to disable foreign key support in database in
            DbOpenHelper.java replacing 'foreign_keys = ON' with 'foreign_keys = OFF' and
            uncomment the @Test tag */

        dataValueCall.call();
    }
}
