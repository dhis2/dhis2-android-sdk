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
package org.hisp.dhis.android.core.datavalue.internal

import io.reactivex.Single
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloaderImpl
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl
import org.hisp.dhis.android.core.data.datavalue.DataValueUtils
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundle
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundleKey
import org.hisp.dhis.android.core.period.PeriodType

class DataValueEndpointCallRealIntegrationShould : BaseRealIntegrationTest() {

    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */

    private fun download(): Single<List<DataValue>> {
        val dataValueHandler: Handler<DataValue> = ObjectWithoutUidHandlerImpl(
            DataValueStore.create(d2.databaseAdapter())
        )

        val key = AggregatedDataCallBundleKey(
            periodType = PeriodType.Daily,
            futurePeriods = 0,
            pastPeriods = 30,
            lastUpdated = null
        )

        val bundle = AggregatedDataCallBundle(
            key = key,
            dataSets = DataValueUtils.dataSets,
            periodIds = DataValueUtils.periodIds,
            rootOrganisationUnitUids = DataValueUtils.orgUnitUids,
            allOrganisationUnitUidsSet = emptySet()
        )

        val resourceHandler = getGenericCallData(d2).resourceHandler()
        val apiDownloader: APIDownloader = APIDownloaderImpl(resourceHandler)
        val dataValueService = d2.retrofit().create(DataValueService::class.java)

        return DataValueCall(dataValueService, dataValueHandler, apiDownloader)
            .download(DataValueQuery.create(bundle))
    }

    // @Test
    @Throws(Exception::class)
    fun download_data_values() {
        if (!d2.userModule().isLogged.blockingGet()) {
            d2.userModule().logIn(username, password, url).blockingGet()
        }

        /*  This test won't pass independently of the sync of metadata, as the foreign keys
            constraints won't be satisfied.
            To run the test, you will need to disable foreign key support in database in
            DbOpenHelper.java replacing 'foreign_keys = ON' with 'foreign_keys = OFF' and
            uncomment the @Test tag */download().blockingGet()
    }
}
