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

package org.hisp.dhis.client.sdk.core.relationship;

import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.relationship.Relationship;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.util.List;

public class RelationshipService implements IRelationshipService {
    private IRelationshipStore relationshipStore;
    private IStateStore stateStore;

    public RelationshipService(IRelationshipStore relationshipStore, IStateStore stateStore) {
        this.relationshipStore = relationshipStore;
        this.stateStore = stateStore;
    }
    @Override
    public List<Relationship> list(TrackedEntityInstance trackedEntityInstance) {
        Preconditions.isNull(trackedEntityInstance, "Object must not be null");

        Action action = stateStore.queryActionForModel(trackedEntityInstance);

        if (!Action.TO_DELETE.equals(action)) {
            return relationshipStore.query(trackedEntityInstance);
        }

        return null;
    }

    @Override
    public Relationship get(long id) {
        Relationship relationship = relationshipStore.queryById(id);

        if (relationship != null) {
            Action action = stateStore.queryActionForModel(relationship);

            if (!Action.TO_DELETE.equals(action)) {
                return relationship;
            }
        }
        return null;
    }

    @Override
    public List<Relationship> list() {
        return stateStore.queryModelsWithActions(Relationship.class,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public boolean remove(Relationship object) {
        Preconditions.isNull(object, "Object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            return false;
        }

        boolean status = false;
        switch (action) {
            case SYNCED:
            case TO_UPDATE: {
                status = stateStore.saveActionForModel(object, Action.TO_DELETE);
                break;
            }
            case TO_POST: {
                status = relationshipStore.delete(object);
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }
        }

        return status;
    }

    @Override
    public boolean save(Relationship object) {
        Preconditions.isNull(object, "Object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            boolean status = relationshipStore.save(object);

            if (status) {
                status = stateStore.saveActionForModel(object, Action.TO_POST);
            }

            return status;
        }

        boolean status = false;
        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                status = relationshipStore.save(object);
                break;
            }
            case SYNCED: {
                status = relationshipStore.save(object);

                if (status) {
                    status = stateStore.saveActionForModel(object, Action.TO_UPDATE);
                }
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }

        }

        return status;
    }
}
