/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
package org.hisp.dhis.android.core.domain.aggregated.data;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.modules.Downloader;
import org.hisp.dhis.android.core.arch.modules.QueryDownloader;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCallFactory;
import org.hisp.dhis.android.core.dataset.DataSetStore;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueQuery;
import org.hisp.dhis.android.core.period.PeriodStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class AggregatedDataModule {

    private final GenericCallData genericCallData;
    private final APICallExecutor apiCallExecutor;
    private final D2CallExecutor d2CallExecutor;
    private final Downloader<SystemInfo> systemInfoDownloader;
    private final QueryDownloader<DataValue, DataValueQuery> dataValueDownloader;

    @Inject
    AggregatedDataModule(GenericCallData genericCallData,
                         APICallExecutor apiCallExecutor,
                         D2CallExecutor d2CallExecutor,
                         Downloader<SystemInfo> systemInfoDownloader,
                         QueryDownloader<DataValue, DataValueQuery> dataValueDownloader) {
        this.genericCallData = genericCallData;
        this.apiCallExecutor = apiCallExecutor;
        this.d2CallExecutor = d2CallExecutor;
        this.systemInfoDownloader = systemInfoDownloader;
        this.dataValueDownloader = dataValueDownloader;
    }

    public Call<Unit> download() {
        return new AggregatedDataCall(
                d2CallExecutor,
                systemInfoDownloader,
                dataValueDownloader,
                new DataSetCompleteRegistrationCallFactory(genericCallData, apiCallExecutor),
                DataSetStore.create(genericCallData.databaseAdapter()),
                PeriodStore.create(genericCallData.databaseAdapter()),
                UserOrganisationUnitLinkStore.create(genericCallData.databaseAdapter()));
    }
}
