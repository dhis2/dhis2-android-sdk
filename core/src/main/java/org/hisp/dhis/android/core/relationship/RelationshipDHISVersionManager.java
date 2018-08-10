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

import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RelationshipDHISVersionManager {

    private final DHISVersionManager versionManager;

    public RelationshipDHISVersionManager(DHISVersionManager versionManager) {
        this.versionManager = versionManager;
    }

    public List<Relationship229Compatible> toServer(List<Relationship> storedRelationships) {
        List<Relationship229Compatible> transformedRelationships = new ArrayList<>();
        for (Relationship relationship : storedRelationships) {
            transformedRelationships.add(toServer(relationship));
        }
        return transformedRelationships;
    }

    private Relationship229Compatible toServer(Relationship relationship) {
        Relationship229Compatible.Builder builder = Relationship229Compatible.builder()
                .displayName(relationship.displayName());

        if (versionManager.is2_29()) {
            return builder
                    .uid(relationship.relationshipType())
                    .trackedEntityInstanceA(relationship.from().trackedEntityInstance().trackedEntityInstance())
                    .trackedEntityInstanceB(relationship.to().trackedEntityInstance().trackedEntityInstance())
                    .build();
        } else {
            return builder
                    .relationshipType(relationship.relationshipType())
                    .from(relationship.from())
                    .to(relationship.to())
                    .build();
        }
    }

    public Relationship fromServer(Relationship229Compatible relationship229Compatible) {
        Relationship.Builder builder = Relationship.builder()
                .displayName(relationship229Compatible.displayName());

        if (versionManager.is2_29()) {
            return builder
                    .relationshipType(relationship229Compatible.uid())
                    .from(RelationshipItem.teiItem(relationship229Compatible.trackedEntityInstanceA()))
                    .to(RelationshipItem.teiItem(relationship229Compatible.trackedEntityInstanceB()))
                    .build();
        } else {
            return builder
                    .relationshipType(relationship229Compatible.relationshipType())
                    .from(relationship229Compatible.from())
                    .to(relationship229Compatible.to())
                    .build();
        }
    }

    public TrackedEntityInstance getRelativeTei(Relationship229Compatible relationship229Compatible, String teiUid) {
        if (versionManager.is2_29()) {
            return relationship229Compatible.relative();
        } else {
            String fromTEIUid = getTEIUidFromRelationshipItem(relationship229Compatible.from());
            String toTEIUid = getTEIUidFromRelationshipItem(relationship229Compatible.to());

            if (fromTEIUid == null || toTEIUid == null) {
                return null;
            }

            String relatedTEIUid = teiUid.equals(fromTEIUid) ? toTEIUid : fromTEIUid;

            return TrackedEntityInstance.create(relatedTEIUid, null, null,
                    null, null, null, null, null,
                    null, false, null, Collections.<Relationship229Compatible>emptyList(), null);
        }
    }

    private String getTEIUidFromRelationshipItem(RelationshipItem item) {
        if (item != null && item.trackedEntityInstance() != null) {
            return item.trackedEntityInstance().trackedEntityInstance();
        }
        return null;
    }
}