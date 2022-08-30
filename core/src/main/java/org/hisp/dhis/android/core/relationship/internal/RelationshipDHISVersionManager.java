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

package org.hisp.dhis.android.core.relationship.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.relationship.BaseRelationship;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class RelationshipDHISVersionManager {

    private final IdentifiableObjectStore<RelationshipType> relationshipTypeStore;

    @Inject
    public RelationshipDHISVersionManager(IdentifiableObjectStore<RelationshipType> relationshipTypeStore) {
        this.relationshipTypeStore = relationshipTypeStore;
    }

    public List<Relationship> getOwnedRelationships(Collection<Relationship> relationships, String elementUid) {
        List<Relationship> ownedRelationships = new ArrayList<>();
        for (Relationship relationship : relationships) {
            RelationshipItem fromItem = relationship.from();
            if (isBidirectional(relationship) ||
                    fromItem != null && fromItem.elementUid() != null && fromItem.elementUid().equals(elementUid)) {
                ownedRelationships.add(relationship);
            }
        }
        return ownedRelationships;
    }

    private boolean isBidirectional(Relationship relationship) {
        if (relationship.relationshipType() == null) {
            return false;
        } else {
            RelationshipType relationshipType = relationshipTypeStore.selectByUid(relationship.relationshipType());
            return relationshipType != null && relationshipType.bidirectional();
        }
    }

    public TrackedEntityInstance getRelativeTei(Relationship relationship, String teiUid) {
        return getRelativeTEI230(relationship, teiUid);
    }

    public TrackedEntityInstance getRelativeTEI230(BaseRelationship baseRelationship, String teiUid) {
        String fromTEIUid = RelationshipHelper.getTeiUid(baseRelationship.from());
        String toTEIUid = RelationshipHelper.getTeiUid(baseRelationship.to());

        if (fromTEIUid == null || toTEIUid == null) {
            return null;
        }

        String relatedTEIUid = teiUid.equals(fromTEIUid) ? toTEIUid : fromTEIUid;

        return TrackedEntityInstanceInternalAccessor.insertRelationships(
                TrackedEntityInstance.builder(), Collections.emptyList())
                .uid(relatedTEIUid)
                .deleted(false)
                .build();
    }

    public RelationshipItem getRelatedRelationshipItem(BaseRelationship baseRelationship, String parentUid) {
        String fromUid = baseRelationship.from() == null ? null : baseRelationship.from().elementUid();
        String toUid = baseRelationship.to() == null ? null : baseRelationship.to().elementUid();

        if (fromUid == null || toUid == null) {
            return null;
        }

        return parentUid.equals(fromUid) ? baseRelationship.to() : baseRelationship.from();
    }

    public void saveRelativesIfNotExist(Collection<Relationship> relationships,
                                        String parentUid,
                                        RelationshipItemRelatives relatives,
                                        RelationshipHandler relationshipHandler) {
        for (BaseRelationship relationship : relationships) {
            RelationshipItem item = getRelatedRelationshipItem(relationship, parentUid);
            if (item != null && !relationshipHandler.doesRelationshipItemExist(item)) {
                switch (item.elementType()) {
                    case RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE:
                        relatives.addTrackedEntityInstance(item.elementUid());
                        break;
                    case RelationshipItemTableInfo.Columns.ENROLLMENT:
                        relatives.addEnrollment(item.elementUid());
                        break;
                    case RelationshipItemTableInfo.Columns.EVENT:
                        relatives.addEvent(item.elementUid());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}