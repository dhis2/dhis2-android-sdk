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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleaner;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;
import retrofit2.Call;

@Reusable
public final class TrackedEntityInstanceListDownloadAndPersistCallFactory {

    private final ForeignKeyCleaner foreignKeyCleaner;
    private final RelationshipDownloadAndPersistCallFactory relationshipDownloadAndPersistCallFactory;
    private final TrackedEntityInstancePersistenceCallFactory persistenceCallFactory;
    private final D2CallExecutor d2CallExecutor;
    private final APICallExecutor apiCallExecutor;
    private final DHISVersionManager versionManager;
    private final TrackedEntityInstanceService trackedEntityInstanceService;


    @Inject
    TrackedEntityInstanceListDownloadAndPersistCallFactory(
            ForeignKeyCleaner foreignKeyCleaner,
            RelationshipDownloadAndPersistCallFactory relationshipDownloadAndPersistCallFactory,
            TrackedEntityInstancePersistenceCallFactory persistenceCallFactory,
            D2CallExecutor d2CallExecutor,
            APICallExecutor apiCallExecutor,
            DHISVersionManager versionManager,
            TrackedEntityInstanceService trackedEntityInstanceService) {
        this.foreignKeyCleaner = foreignKeyCleaner;
        this.relationshipDownloadAndPersistCallFactory = relationshipDownloadAndPersistCallFactory;
        this.persistenceCallFactory = persistenceCallFactory;
        this.d2CallExecutor = d2CallExecutor;
        this.apiCallExecutor = apiCallExecutor;
        this.versionManager = versionManager;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
    }

    public Single<List<TrackedEntityInstance>> getCall(final Collection<String> trackedEntityInstanceUids,
                                                       final String program) {
        return Single.fromCallable(() -> downloadAndPersistBlocking(trackedEntityInstanceUids, program));
    }

    private List<TrackedEntityInstance> downloadAndPersistBlocking(final Collection<String> trackedEntityInstanceUids,
                                                                   final String program) throws D2Error {

        return d2CallExecutor.executeD2CallTransactionally(() -> {
            if (trackedEntityInstanceUids == null) {
                throw new IllegalArgumentException("UID list is null");
            }

            List<TrackedEntityInstance> teis = new ArrayList<>();

            for (String uid : trackedEntityInstanceUids) {
                List<TrackedEntityInstance> teiList;
                teiList = downloadGlassAware(uid, program);
                teis.addAll(teiList);
            }


            RelationshipItemRelatives relatives = new RelationshipItemRelatives();
            persistenceCallFactory.persistTEIs(teis, true, false, relatives).blockingGet();

            if (!versionManager.is2_29()) {
                relationshipDownloadAndPersistCallFactory.downloadAndPersist(relatives).blockingGet();
            }

            foreignKeyCleaner.cleanForeignKeyErrors();

            return teis;
        });
    }

    private List<TrackedEntityInstance> downloadGlassAware(String uid, String program) throws D2Error {
        try {
            TrackedEntityInstance tei = apiCallExecutor.executeObjectCallWithErrorCatcher(getTeiByProgram(uid, program),
                    new TrackedEntityInstanceCallErrorCatcher());

            return Collections.singletonList(tei);
        } catch (D2Error d2Error) {
            if (!d2Error.errorCode().equals(D2ErrorCode.OWNERSHIP_ACCESS_DENIED)) {
                return Collections.emptyList();
            }

            HttpMessageResponse breakGlassResponse = apiCallExecutor.executeObjectCall(
                    trackedEntityInstanceService.breakGlass(uid, program, "Android sync download"));

            if (!breakGlassResponse.httpStatusCode().equals(200)) {
                return Collections.emptyList();
            }

            return Collections.singletonList(apiCallExecutor.executeObjectCall(getTeiByProgram(uid, program)));
        }
    }

    private Call<TrackedEntityInstance> getTeiByProgram(String uid, String program) {
        return trackedEntityInstanceService.getTrackedEntityInstanceByProgram(uid, program,
                TrackedEntityInstanceFields.allFields, true, true);
    }
}
