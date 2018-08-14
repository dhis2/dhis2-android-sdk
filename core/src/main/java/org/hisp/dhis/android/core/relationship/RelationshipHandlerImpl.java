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
package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;

import java.util.List;

import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM;
import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.TO;

final class RelationshipHandlerImpl implements RelationshipHandler {

    private final IdentifiableObjectStore<Relationship> relationshipStore;
    private final RelationshipItemStoreInterface relationshipItemStore;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;

    private RelationshipHandlerImpl(
            IdentifiableObjectStore<Relationship> relationshipStore,
            RelationshipItemStoreInterface relationshipItemStore,
            TrackedEntityInstanceStore trackedEntityInstanceStore) {
        this.relationshipStore = relationshipStore;
        this.relationshipItemStore = relationshipItemStore;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
    }


    @Override
    public void handle(Relationship o) {
        String fromUid = RelationshipHelper.getTeiUid(o.from());
        String toUid = RelationshipHelper.getTeiUid(o.to());

        if (fromUid == null || toUid == null) {
            // TODO support events and enrollments
            throw new RuntimeException("Only TEI to TEI relationships are supported");
        }

        if (!this.trackedEntityInstanceStore.exists(fromUid) || !this.trackedEntityInstanceStore.exists(toUid)) {
            throw new RuntimeException("Trying to persist relationship for TEI not present in database");
        }

        String existingRelationshipUid = getExistingRelationshipUid(fromUid, toUid, o.relationshipType());

        if (existingRelationshipUid != null && !existingRelationshipUid.equals(o.uid())) {
            this.relationshipStore.delete(existingRelationshipUid);
        }

        this.relationshipStore.updateOrInsert(o);

        RelationshipItemModel fromItemModel = RelationshipItemModel.builder()
                .relationship(o.uid())
                .relationshipItemType(FROM)
                .trackedEntityInstance(fromUid)
                .build();

        RelationshipItemModel toItemModel = RelationshipItemModel.builder()
                .relationship(o.uid())
                .relationshipItemType(TO)
                .trackedEntityInstance(toUid)
                .build();

        this.relationshipItemStore.updateOrInsertWhere(fromItemModel);
        this.relationshipItemStore.updateOrInsertWhere(toItemModel);
    }

    public boolean doesTEIRelationshipExist(String fromUid, String toUid, String relationshipType) {
        return getExistingRelationshipUid(fromUid, toUid, relationshipType) != null;
    }

    private String getExistingRelationshipUid(String fromUid, String toUid, String relationshipType) {
        List<String> existingRelationshipUidsForPair =
                this.relationshipItemStore.getRelationshipsFromAndToTEI(fromUid, toUid);

        for (String existingRelationshipUid : existingRelationshipUidsForPair) {
            Relationship existingRelationship = this.relationshipStore.selectByUid(existingRelationshipUid,
                    Relationship.factory);

            if (existingRelationship != null && relationshipType.equals(existingRelationship.relationshipType())) {
                existingRelationship.uid();
            }
        }
        return null;
    }

    public static RelationshipHandler create(DatabaseAdapter databaseAdapter) {
        return new RelationshipHandlerImpl(
                RelationshipStore.create(databaseAdapter),
                RelationshipItemStore.create(databaseAdapter),
                new TrackedEntityInstanceStoreImpl(databaseAdapter)
        );
    }
}
