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

package org.hisp.dhis.android.core.relationship.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.internal.RelationshipDeleteWebResponse;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.isDeleted;

@Reusable
public final class RelationshipDeleteCall {

    private final RelationshipService relationshipService;

    private final RelationshipStore relationshipStore;

    private final APICallExecutor apiCallExecutor;

    private final DHISVersionManager dhisVersionManager;

    @Inject
    RelationshipDeleteCall(@NonNull RelationshipService relationshipService,
                           @NonNull RelationshipStore relationshipStore,
                           @NonNull APICallExecutor apiCallExecutor,
                           @NonNull DHISVersionManager dhisVersionManager) {
        this.relationshipService = relationshipService;
        this.relationshipStore = relationshipStore;
        this.apiCallExecutor = apiCallExecutor;
        this.dhisVersionManager = dhisVersionManager;
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    public List<TrackedEntityInstance> postDeletedRelationships(List<TrackedEntityInstance> trackedEntityInstances) {
        List<TrackedEntityInstance> withoutDeletedRelationships = new ArrayList<>(trackedEntityInstances.size());

        for (TrackedEntityInstance instance : trackedEntityInstances) {
            List<Relationship229Compatible> relationships = TrackedEntityInstanceInternalAccessor
                    .accessRelationships(instance);

            if (relationships == null || relationships.isEmpty()) {
                withoutDeletedRelationships.add(instance);
            } else {
                List<Relationship229Compatible> nonDeletedRelationships = new ArrayList<>();
                for (Relationship229Compatible relationship : relationships) {
                    if (isDeleted(relationship)) {
                        deleteRelationship(relationship).blockingAwait();
                    } else {
                        nonDeletedRelationships.add(relationship);
                    }
                }
                TrackedEntityInstance newInstance = TrackedEntityInstanceInternalAccessor
                        .insertRelationships(instance.toBuilder(), nonDeletedRelationships)
                        .build();
                withoutDeletedRelationships.add(newInstance);
            }
        }
        return withoutDeletedRelationships;
    }

    private Completable deleteRelationship(Relationship229Compatible relationship) {
        return Completable.fromCallable(() -> {
            if (dhisVersionManager.is2_29()) {
                String whereClause = new WhereClauseBuilder()
                        .appendKeyStringValue(CoreColumns.ID, relationship.id()).build();
                relationshipStore.deleteWhereIfExists(whereClause);
                return RelationshipDeleteWebResponse.empty();
            } else {
                RelationshipDeleteWebResponse httpResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                        relationshipService.deleteRelationship(relationship.uid()),
                        Collections.singletonList(404),
                        RelationshipDeleteWebResponse.class);

                ImportStatus status  = httpResponse.response() == null ? null : httpResponse.response().status();

                if (httpResponse.httpStatusCode() == 200 && ImportStatus.SUCCESS.equals(status) ||
                        httpResponse.httpStatusCode() == 404) {
                    relationshipStore.delete(relationship.uid());
                } else {
                    // TODO Implement better handling
                    // The relationship is marked as error, but there is no handling in the TEI. The TEI is being posted
                    relationshipStore.setState(relationship.uid(), State.ERROR);
                }
                return httpResponse;
            }
        });
    }

}