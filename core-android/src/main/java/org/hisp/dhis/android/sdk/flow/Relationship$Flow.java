/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;

import org.hisp.dhis.android.sdk.common.meta.DbDhis;
import org.hisp.dhis.java.sdk.models.relationship.Relationship;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = Relationship$Flow.UNIQUE_RELATIONSHIP_GROUP, uniqueConflict = ConflictAction.FAIL)
})
public final class Relationship$Flow extends BaseModel$Flow {

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
                    @ForeignKeyReference(columnName = TRACKED_ENTITY_INSTANCE_A_KEY, columnType = long.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    TrackedEntityInstance$Flow trackedEntityInstanceA;

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_RELATIONSHIP_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = TRACKED_ENTITY_INSTANCE_B_KEY, columnType = long.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    TrackedEntityInstance$Flow trackedEntityInstanceB;

    @Column
    String displayName;

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public TrackedEntityInstance$Flow getTrackedEntityInstanceA() {
        return trackedEntityInstanceA;
    }

    public void setTrackedEntityInstanceA(TrackedEntityInstance$Flow trackedEntityInstanceA) {
        this.trackedEntityInstanceA = trackedEntityInstanceA;
    }

    public TrackedEntityInstance$Flow getTrackedEntityInstanceB() {
        return trackedEntityInstanceB;
    }

    public void setTrackedEntityInstanceB(TrackedEntityInstance$Flow trackedEntityInstanceB) {
        this.trackedEntityInstanceB = trackedEntityInstanceB;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Relationship$Flow() {
        // empty constructor
    }

    public static Relationship toModel(Relationship$Flow relationshipFlow) {
        if (relationshipFlow == null) {
            return null;
        }

        Relationship relationship = new Relationship();
        relationship.setId(relationshipFlow.getId());
        relationship.setRelationship(relationshipFlow.getRelationship());
        relationship.setTrackedEntityInstanceA(TrackedEntityInstance$Flow.toModel(relationshipFlow.getTrackedEntityInstanceA()));
        relationship.setTrackedEntityInstanceB(TrackedEntityInstance$Flow.toModel(relationshipFlow.getTrackedEntityInstanceB()));
        relationship.setDisplayName(relationshipFlow.getDisplayName());
        relationship.setTrackedEntityInstanceA(TrackedEntityInstance$Flow.toModel(relationshipFlow.getTrackedEntityInstanceA()));
        relationship.setTrackedEntityInstanceB(TrackedEntityInstance$Flow.toModel(relationshipFlow.getTrackedEntityInstanceB()));
        relationship.setDisplayName(relationshipFlow.getDisplayName());
        return relationship;
    }

    public static Relationship$Flow fromModel(Relationship relationship) {
        if (relationship == null) {
            return null;
        }

        Relationship$Flow relationshipFlow = new Relationship$Flow();
        relationshipFlow.setId(relationship.getId());
        relationshipFlow.setRelationship(relationship.getRelationship());
        relationshipFlow.setTrackedEntityInstanceA(TrackedEntityInstance$Flow.fromModel(relationship.getTrackedEntityInstanceA()));
        relationshipFlow.setTrackedEntityInstanceB(TrackedEntityInstance$Flow.fromModel(relationship.getTrackedEntityInstanceB()));
        relationshipFlow.setDisplayName(relationship.getDisplayName());
        relationshipFlow.setTrackedEntityInstanceA(TrackedEntityInstance$Flow.fromModel(relationship.getTrackedEntityInstanceA()));
        relationshipFlow.setTrackedEntityInstanceB(TrackedEntityInstance$Flow.fromModel(relationship.getTrackedEntityInstanceB()));
        relationshipFlow.setDisplayName(relationship.getDisplayName());
        return relationshipFlow;
    }

    public static List<Relationship> toModels(List<Relationship$Flow> relationshipFlows) {
        List<Relationship> relationships = new ArrayList<>();

        if (relationshipFlows != null && !relationshipFlows.isEmpty()) {
            for (Relationship$Flow relationshipFlow : relationshipFlows) {
                relationships.add(toModel(relationshipFlow));
            }
        }

        return relationships;
    }

    public static List<Relationship$Flow> fromModels(List<Relationship> relationships) {
        List<Relationship$Flow> relationshipFlows = new ArrayList<>();

        if (relationships != null && !relationships.isEmpty()) {
            for (Relationship relationship: relationships) {
                relationshipFlows.add(fromModel(relationship));
            }
        }

        return relationshipFlows;
    }
}
