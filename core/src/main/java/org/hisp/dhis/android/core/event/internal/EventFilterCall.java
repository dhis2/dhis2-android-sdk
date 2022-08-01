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

package org.hisp.dhis.android.core.event.internal;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader;
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.common.internal.DataAccessFields;
import org.hisp.dhis.android.core.event.EventFilter;
import org.hisp.dhis.android.core.systeminfo.DHISVersion;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public final class EventFilterCall implements UidsCall<EventFilter> {

    private static final int MAX_UID_LIST_SIZE = 50;

    private final EventFilterService service;
    private final Handler<EventFilter> handler;
    private final APIDownloader apiDownloader;
    private final DHISVersionManager versionManager;

    @Inject
    EventFilterCall(EventFilterService service,
                    Handler<EventFilter> handler,
                    APIDownloader apiDownloader,
                    DHISVersionManager versionManager) {
        this.service = service;
        this.handler = handler;
        this.apiDownloader = apiDownloader;
        this.versionManager = versionManager;
    }

    @Override
    public Single<List<EventFilter>> download(Set<String> programUids) {
        if (versionManager.isGreaterThan(DHISVersion.V2_31)) {
            String accessDataReadFilter = "access." + DataAccessFields.read.eq(true).generateString();
            return apiDownloader.downloadPartitioned(programUids, MAX_UID_LIST_SIZE, handler, partitionUids ->
                    service.getEventFilters(
                            EventFilterFields.programUid.in(partitionUids),
                            accessDataReadFilter,
                            EventFilterFields.allFields,
                            Boolean.FALSE));
        } else {
            return Single.just(Lists.newArrayList());
        }
    }
}