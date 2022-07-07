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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableWithStyleColumns;

public final class TrackedEntityInstanceFilterTableInfo {

    private TrackedEntityInstanceFilterTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "TrackedEntityInstanceFilter";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends IdentifiableWithStyleColumns {
        public final static String PROGRAM = "program";
        public final static String DESCRIPTION = "description";
        public final static String SORT_ORDER = "sortOrder";
        public final static String ENROLLMENT_STATUS = "enrollmentStatus";
        public final static String FOLLOW_UP = "followUp";
        public final static String ORGANISATION_UNIT = "organisationUnit";
        public final static String OU_MODE = "ouMode";
        public final static String ASSIGNED_USER_MODE = "assignedUserMode";
        public final static String ORDER = "orderProperty";
        public final static String DISPLAY_COLUMN_ORDER = "displayColumnOrder";
        public final static String EVENT_STATUS = "eventStatus";
        public final static String EVENT_DATE = "eventDate";
        public final static String LAST_UPDATED_DATE = "lastUpdatedDate";
        public final static String PROGRAM_STAGE = "programStage";
        public final static String TRACKED_ENTITY_INSTANCES = "trackedEntityInstances";
        public final static String ENROLLMENT_INCIDENT_DATE = "enrollmentIncidentDate";
        public final static String ENROLLMENT_CREATED_DATE = "enrollmentCreatedDate";
        public final static String TRACKED_ENTITY_TYPE = "trackedEntityType";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    PROGRAM,
                    DESCRIPTION,
                    SORT_ORDER,
                    ENROLLMENT_STATUS,
                    FOLLOW_UP,
                    ORGANISATION_UNIT,
                    OU_MODE,
                    ASSIGNED_USER_MODE,
                    ORDER,
                    DISPLAY_COLUMN_ORDER,
                    EVENT_STATUS,
                    EVENT_DATE,
                    LAST_UPDATED_DATE,
                    PROGRAM_STAGE,
                    TRACKED_ENTITY_INSTANCES,
                    ENROLLMENT_INCIDENT_DATE,
                    ENROLLMENT_CREATED_DATE,
                    TRACKED_ENTITY_TYPE
            );
        }
    }
}