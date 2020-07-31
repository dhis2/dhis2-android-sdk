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

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance;
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;
import io.reactivex.Single;

@Reusable
final class TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory {

    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final RelationshipStore relationshipStore;
    private final TrackedEntityInstanceService service;
    private final TrackedEntityInstancePersistenceCallFactory persistenceCallFactory;

    @Inject
    TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory(
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull RelationshipStore relationshipStore,
            @NonNull TrackedEntityInstanceService service,
            @NonNull TrackedEntityInstancePersistenceCallFactory persistenceCallFactory) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.relationshipStore = relationshipStore;
        this.service = service;
        this.persistenceCallFactory = persistenceCallFactory;
    }

    Single<List<TrackedEntityInstance>> downloadAndPersist() {
        return Single.just(Collections.emptyList()).flatMap(emptyMap -> {

            List<String> relationships = trackedEntityInstanceStore.queryMissingRelationshipsUids();

            if (relationships.isEmpty()) {
                return Single.just(Collections.emptyList());
            } else {
                List<Single<Payload<TrackedEntityInstance>>> singles = new ArrayList<>();
                List<String> failedTeis = new ArrayList<>();
                for (String uid : relationships) {
                    Single<Payload<TrackedEntityInstance>> single =
                            service.getTrackedEntityInstance(uid, TrackedEntityInstanceFields.asRelationshipFields,
                            true, true)
                            .onErrorResumeNext((err) -> {
                                failedTeis.add(uid);
                                return Single.just(Payload.emptyPayload());
                            });

                    singles.add(single);
                }

                return Single.merge(singles)
                        .collect((Callable<List<TrackedEntityInstance>>) ArrayList::new,
                                (teis, payload) -> teis.addAll(payload.items()))
                        .doAfterSuccess(teis -> persistenceCallFactory.persistRelationships(teis).blockingAwait())
                        .doAfterSuccess(teis -> cleanFailedRelationships(failedTeis));
            }
        });

    }

    private void cleanFailedRelationships(List<String> failedTeis) {
        List<Relationship> corruptedRelationships = new ArrayList<>();
        for (String uid : failedTeis) {
            RelationshipItem item = RelationshipItem.builder()
                    .trackedEntityInstance(RelationshipItemTrackedEntityInstance.builder()
                            .trackedEntityInstance(uid).build())
                    .build();
            corruptedRelationships.addAll(relationshipStore.getRelationshipsByItem(item));
        }

        for (Relationship r : corruptedRelationships) {
            relationshipStore.deleteById(r);
        }
    }
}
