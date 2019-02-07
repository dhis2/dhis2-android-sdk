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

package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory {

    private final APICallExecutor apiCallExecutor;
    private final D2CallExecutor d2CallExecutor;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityInstanceService service;
    private final TrackedEntityInstanceRelationshipPersistenceCallFactory persistenceCallFactory;

    @Inject
    TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory(
            @NonNull APICallExecutor apiCallExecutor,
            @NonNull D2CallExecutor d2CallExecutor,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull TrackedEntityInstanceService service,
            @NonNull TrackedEntityInstanceRelationshipPersistenceCallFactory persistenceCallFactory) {
        this.apiCallExecutor = apiCallExecutor;
        this.d2CallExecutor = d2CallExecutor;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.service = service;
        this.persistenceCallFactory = persistenceCallFactory;
    }

    public Callable<List<TrackedEntityInstance>> getCall() {
        return new Callable<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() throws Exception {
                return downloadAndPersist();
            }
        };
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private List<TrackedEntityInstance> downloadAndPersist() throws D2Error {
        List<String> relationships = trackedEntityInstanceStore.queryMissingRelationshipsUids();

        List<TrackedEntityInstance> teis = new ArrayList<>();
        if (!relationships.isEmpty()) {

            for (String uid : relationships) {
                try {
                    retrofit2.Call<Payload<TrackedEntityInstance>> teiCall =
                            service.getTrackedEntityInstance(uid, TrackedEntityInstanceFields.allFields, true);

                    teis.addAll(apiCallExecutor.executePayloadCall(teiCall));
                } catch (D2Error ignored) {
                    // Ignore
                }
            }

            d2CallExecutor.executeD2Call(persistenceCallFactory.getCall(teis));
        }

        return teis;
    }
}
