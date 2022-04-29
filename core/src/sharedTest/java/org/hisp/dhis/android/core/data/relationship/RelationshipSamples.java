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

package org.hisp.dhis.android.core.data.relationship;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.RelationshipItem;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.NAME;

public class RelationshipSamples {

    protected static String UID = "uid";

    protected static String FROM_UID = "fromUid";

    protected static String TO_UID = "toUid";

    protected static String TYPE = "type";

    protected static RelationshipItem fromItem = RelationshipHelper.teiItem(FROM_UID);

    protected static RelationshipItem toItem = RelationshipHelper.teiItem(TO_UID);

    protected RelationshipItem eventItem = RelationshipHelper.eventItem(TO_UID);

    protected static State STATE = State.SYNCED;

    protected static Boolean DELETED = false;

    private static Relationship.Builder commonBuilder = Relationship
            .builder()
            .created(CREATED)
            .lastUpdated(LAST_UPDATED)
            .name(NAME)
            .syncState(STATE)
            .deleted(DELETED);

    protected Relationship get230() {
        return commonBuilder
                .uid(UID)
                .relationshipType(TYPE)
                .from(fromItem)
                .to(toItem)
                .build();
    }

    public static Relationship get230(String uid, String fromUid, String toUid) {
        return get230(uid, RelationshipHelper.teiItem(fromUid), RelationshipHelper.teiItem(toUid));
    }

    public static Relationship get230(String uid, RelationshipItem from, RelationshipItem to) {
        return commonBuilder
                .uid(uid)
                .relationshipType(TYPE)
                .from(from)
                .to(to)
                .build();
    }

    public static Relationship getRelationshipToInsertOnDB() {
        return commonBuilder
                .id(1L)
                .uid(UID)
                .relationshipType(TYPE)
                .build();
    }
}