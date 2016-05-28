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

package org.hisp.dhis.client.sdk.android.api.persistence.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.relationship.Relationship;

@Table(database = DbDhis.class, uniqueColumnGroups = {
        @UniqueGroup(
                groupNumber = RelationshipFlow.UNIQUE_RELATIONSHIP_GROUP,
                uniqueConflict = ConflictAction.FAIL)
})
public final class RelationshipFlow extends BaseModelFlow {
    public static final Mapper<Relationship, RelationshipFlow> MAPPER = new RelationshipMapper();

    static final int UNIQUE_RELATIONSHIP_GROUP = 93;
    static final String TRACKED_ENTITY_INSTANCE_A_KEY = "trackedEntityInstanceA";
    static final String TRACKED_ENTITY_INSTANCE_B_KEY = "trackedEntityInstanceB";

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_RELATIONSHIP_GROUP})
    String relationship;

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_RELATIONSHIP_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = TRACKED_ENTITY_INSTANCE_A_KEY, columnType = String.class,
                            foreignKeyColumnName = "trackedEntityInstanceUid"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    TrackedEntityInstanceFlow trackedEntityInstanceA;

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_RELATIONSHIP_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = TRACKED_ENTITY_INSTANCE_B_KEY, columnType = String.class,
                            foreignKeyColumnName = "trackedEntityInstanceUid"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    TrackedEntityInstanceFlow trackedEntityInstanceB;

    @Column
    String displayName;

    public RelationshipFlow() {
        // empty constructor
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public TrackedEntityInstanceFlow getTrackedEntityInstanceA() {
        return trackedEntityInstanceA;
    }

    public void setTrackedEntityInstanceA(TrackedEntityInstanceFlow trackedEntityInstanceA) {
        this.trackedEntityInstanceA = trackedEntityInstanceA;
    }

    public TrackedEntityInstanceFlow getTrackedEntityInstanceB() {
        return trackedEntityInstanceB;
    }

    public void setTrackedEntityInstanceB(TrackedEntityInstanceFlow trackedEntityInstanceB) {
        this.trackedEntityInstanceB = trackedEntityInstanceB;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private static class RelationshipMapper extends AbsMapper<Relationship, RelationshipFlow> {

        @Override
        public RelationshipFlow mapToDatabaseEntity(Relationship relationship) {
            if (relationship == null) {
                return null;
            }

            RelationshipFlow relationshipFlow = new RelationshipFlow();
            relationshipFlow.setId(relationship.getId());
            relationshipFlow.setRelationship(relationship.getRelationship());
            relationshipFlow.setTrackedEntityInstanceA(TrackedEntityInstanceFlow.MAPPER
                    .mapToDatabaseEntity(relationship.getTrackedEntityInstanceA()));
            relationshipFlow.setTrackedEntityInstanceB(TrackedEntityInstanceFlow.MAPPER
                    .mapToDatabaseEntity(relationship.getTrackedEntityInstanceB()));
            relationshipFlow.setDisplayName(relationship.getDisplayName());
            return relationshipFlow;
        }

        @Override
        public Relationship mapToModel(RelationshipFlow relationshipFlow) {
            if (relationshipFlow == null) {
                return null;
            }

            Relationship relationship = new Relationship();
            relationship.setId(relationshipFlow.getId());
            relationship.setRelationship(relationshipFlow.getRelationship());
            relationship.setTrackedEntityInstanceA(TrackedEntityInstanceFlow.MAPPER
                    .mapToModel(relationshipFlow.getTrackedEntityInstanceA()));
            relationship.setTrackedEntityInstanceB(TrackedEntityInstanceFlow.MAPPER
                    .mapToModel(relationshipFlow.getTrackedEntityInstanceB()));
            relationship.setDisplayName(relationshipFlow.getDisplayName());
            return relationship;
        }

        @Override
        public Class<Relationship> getModelTypeClass() {
            return Relationship.class;
        }

        @Override
        public Class<RelationshipFlow> getDatabaseEntityTypeClass() {
            return RelationshipFlow.class;
        }
    }
}
