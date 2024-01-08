/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;

public final class RelationshipConstraintTableInfo {

    private RelationshipConstraintTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "RelationshipConstraint";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends CoreColumns {
        public static final String RELATIONSHIP_TYPE = "relationshipType";
        public static final String CONSTRAINT_TYPE = "constraintType";
        public static final String RELATIONSHIP_ENTITY = "relationshipEntity";
        public static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
        public static final String PROGRAM = "program";
        public static final String PROGRAM_STAGE = "programStage";
        public static final String TRACKER_DATA_VIEW_ATTRIBUTES = "trackerDataViewAttributes";
        public static final String TRACKER_DATA_VIEW_DATA_ELEMENTS = "trackerDataViewDataElements";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    RELATIONSHIP_TYPE,
                    CONSTRAINT_TYPE,
                    RELATIONSHIP_ENTITY,
                    TRACKED_ENTITY_TYPE,
                    PROGRAM,
                    PROGRAM_STAGE,
                    TRACKER_DATA_VIEW_ATTRIBUTES,
                    TRACKER_DATA_VIEW_DATA_ELEMENTS
            );
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{
                    RELATIONSHIP_TYPE,
                    CONSTRAINT_TYPE
            };
        }
    }
}
