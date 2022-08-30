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

package org.hisp.dhis.android.core.relationship;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

public class CreateRelationshipTypeUtils {
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    //RelationshipTypeModel attributes:
    private static final String TO_FROM_NAME = "cat of";
    private static final String FROM_TO_NAME = "owner of";

    /**
     * A method to createTrackedEntityAttribute ContentValues from a RelationshipType.
     * To be used by other tests that have RelationshipType as foreign key.
     *
     * @param id
     * @param uid
     * @return
     */
    public static ContentValues create(long id, String uid) {

        ContentValues relationshipType = new ContentValues();

        relationshipType.put(CoreColumns.ID, id);
        relationshipType.put(IdentifiableColumns.UID, uid);
        relationshipType.put(IdentifiableColumns.CODE, CODE);
        relationshipType.put(IdentifiableColumns.NAME, NAME);
        relationshipType.put(IdentifiableColumns.DISPLAY_NAME, DISPLAY_NAME);
        relationshipType.put(IdentifiableColumns.CREATED, DATE);
        relationshipType.put(IdentifiableColumns.LAST_UPDATED, DATE);
        relationshipType.put(RelationshipTypeTableInfo.Columns.TO_FROM_NAME, TO_FROM_NAME);
        relationshipType.put(RelationshipTypeTableInfo.Columns.FROM_TO_NAME, FROM_TO_NAME);
        relationshipType.put(RelationshipTypeTableInfo.Columns.BIDIRECTIONAL, 0);
        relationshipType.put(RelationshipTypeTableInfo.Columns.ACCESS_DATA_WRITE, 1);

        return relationshipType;
    }
}
