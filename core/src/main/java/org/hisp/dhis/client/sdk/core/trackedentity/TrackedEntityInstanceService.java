/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;
import org.hisp.dhis.client.sdk.core.relationship.RelationshipStore;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.relationship.Relationship;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.util.List;

public final class TrackedEntityInstanceService implements ITrackedEntityInstanceService {

    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final RelationshipStore relationshipStore;
    private final StateStore stateStore;

    public TrackedEntityInstanceService(TrackedEntityInstanceStore trackedEntityInstanceStore,
                                        RelationshipStore relationshipStore, StateStore
                                                stateStore) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.relationshipStore = relationshipStore;
        this.stateStore = stateStore;
    }

    @Override
    public TrackedEntityInstance get(String uid) {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceStore.queryByUid(uid);
        Action action = stateStore.queryActionForModel(trackedEntityInstance);

        if (!Action.TO_DELETE.equals(action)) {
            return trackedEntityInstance;
        }

        return null;
    }

    @Override
    public TrackedEntityInstance create(TrackedEntity trackedEntity, OrganisationUnit
            organisationUnit) {
        Preconditions.isNull(trackedEntity, "Tracked entity must not be null");
        Preconditions.isNull(organisationUnit, "Organisation unit must not be null");

        TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setOrgUnit(organisationUnit.getUId());
        trackedEntityInstance.setTrackedEntity(trackedEntity.getUId());
        trackedEntityInstance.setTrackedEntityInstanceUid(CodeGenerator.generateCode());
        save(trackedEntityInstance);

        return trackedEntityInstance;
    }

    @Override
    public boolean addRelationship(TrackedEntityInstance trackedEntityInstanceA,
                                   TrackedEntityInstance trackedEntityInstanceB, RelationshipType
                                               relationshipType) {
        Preconditions.isNull(trackedEntityInstanceA, "Tracked entity instance A must not be null");
        Preconditions.isNull(trackedEntityInstanceB, "Tracked entity instance B must not be null");
        Preconditions.isNull(relationshipType, "Relationship type must not be null");

        List<Relationship> existingRelationships = trackedEntityInstanceA.getRelationships();
        for (Relationship existingRelationship : existingRelationships) {
            if (existingRelationship.getTrackedEntityInstanceB().getTrackedEntityInstanceUid()
                    .equals(trackedEntityInstanceB.getTrackedEntityInstanceUid()) &&
                    relationshipType.getUId().equals(existingRelationship.getRelationship())) {
                return false;
            }
        }
        Relationship relationship = new Relationship();
        relationship.setTrackedEntityInstanceA(trackedEntityInstanceA);
        relationship.setTrackedEntityInstanceB(trackedEntityInstanceB);
        relationship.setRelationship(relationship.getRelationship());
        relationshipStore.insert(relationship);
        trackedEntityInstanceA.getRelationships().add(relationship);
        trackedEntityInstanceB.getRelationships().add(relationship);
        save(trackedEntityInstanceA);
        save(trackedEntityInstanceB);
        return true;
    }

    @Override
    public boolean removeRelationship(Relationship relationship) {
        Preconditions.isNull(relationship, "Relationship must not be null");
        relationshipStore.delete(relationship);
        relationship.getTrackedEntityInstanceA().getRelationships().remove(relationship);
        relationship.getTrackedEntityInstanceB().getRelationships().remove(relationship);
        save(relationship.getTrackedEntityInstanceA());
        save(relationship.getTrackedEntityInstanceB());
        return true;
    }

    @Override
    public TrackedEntityInstance get(long id) {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceStore.queryById(id);
        Action action = stateStore.queryActionForModel(trackedEntityInstance);

        if (!Action.TO_DELETE.equals(action)) {
            return trackedEntityInstance;
        }

        return null;
    }

    @Override
    public List<TrackedEntityInstance> list() {
        return stateStore.queryModelsWithActions(TrackedEntityInstance.class, Action.SYNCED,
                Action.TO_UPDATE, Action.TO_POST);
    }

    @Override
    public boolean remove(TrackedEntityInstance object) {
        Preconditions.isNull(object, "trackedEntityInstance argument must not be null");
        if (!trackedEntityInstanceStore.delete(object)) {
            return false;
        }
        return stateStore.deleteActionForModel(object);
    }

    @Override
    public boolean save(TrackedEntityInstance object) {
        Preconditions.isNull(object, "Tracked entity instance must not be null");

        if (!trackedEntityInstanceStore.save(object)) {
            return false;
        }

        // TODO check if object was created earlier (then set correct flag)
        Action action = stateStore.queryActionForModel(object);

        if (action == null || Action.TO_POST.equals(action)) {
            return stateStore.saveActionForModel(object, Action.TO_POST);
        } else {
            return stateStore.saveActionForModel(object, Action.TO_UPDATE);
        }

    }
}
