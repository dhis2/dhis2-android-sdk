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

package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

public final class EventFilterTableInfo {

    private EventFilterTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "EventFilter";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends IdentifiableColumns {
        public final static String PROGRAM = "program";
        public final static String PROGRAM_STAGE = "programStage";
        public final static String DESCRIPTION = "description";
        public final static String FOLLOW_UP = "followUp";
        public final static String ORGANISATION_UNIT = "organisationUnit";
        public final static String OU_MODE = "ouMode";
        public final static String ASSIGNED_USER_MODE = "assignedUserMode";
        public final static String ORDER = "orderProperty";
        public final static String DISPLAY_COLUMN_ORDER = "displayColumnOrder";
        public final static String EVENTS = "events";
        public final static String EVENT_STATUS = "eventStatus";
        public final static String EVENT_DATE = "eventDate";
        public final static String DUE_DATE = "dueDate";
        public final static String LAST_UPDATED_DATE = "lastUpdatedDate";
        public final static String COMPLETED_DATE = "completedDate";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    PROGRAM,
                    PROGRAM_STAGE,
                    DESCRIPTION,
                    FOLLOW_UP,
                    ORGANISATION_UNIT,
                    OU_MODE,
                    ASSIGNED_USER_MODE,
                    ORDER,
                    DISPLAY_COLUMN_ORDER,
                    EVENTS,
                    EVENT_STATUS,
                    EVENT_DATE,
                    DUE_DATE,
                    LAST_UPDATED_DATE,
                    COMPLETED_DATE
            );
        }
    }
}
