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

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.NameableWithStyleColumns;

public final class ProgramTableInfo {

    private ProgramTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "Program";
        }

        @Override
        public Columns columns() {
            return new Columns();
        }
    };

    public static class Columns extends NameableWithStyleColumns {
        public static final String ACCESS_DATA_WRITE = "accessDataWrite";
        public static final String VERSION = "version";
        public static final String ONLY_ENROLL_ONCE = "onlyEnrollOnce";
        public static final String ENROLLMENT_DATE_LABEL = "enrollmentDateLabel";
        public static final String DISPLAY_INCIDENT_DATE = "displayIncidentDate";
        public static final String INCIDENT_DATE_LABEL = "incidentDateLabel";
        public static final String REGISTRATION = "registration";
        public static final String SELECT_ENROLLMENT_DATES_IN_FUTURE = "selectEnrollmentDatesInFuture";
        public static final String DATA_ENTRY_METHOD = "dataEntryMethod";
        public static final String IGNORE_OVERDUE_EVENTS = "ignoreOverdueEvents";
        public static final String SELECT_INCIDENT_DATES_IN_FUTURE = "selectIncidentDatesInFuture";
        public static final String USE_FIRST_STAGE_DURING_REGISTRATION = "useFirstStageDuringRegistration";
        public static final String DISPLAY_FRONT_PAGE_LIST = "displayFrontPageList";
        public static final String PROGRAM_TYPE = "programType";
        public static final String RELATED_PROGRAM = "relatedProgram";
        public static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
        public static final String CATEGORY_COMBO = "categoryCombo";
        public static final String EXPIRY_DAYS = "expiryDays";
        public static final String COMPLETE_EVENTS_EXPIRY_DAYS = "completeEventsExpiryDays";
        public static final String EXPIRY_PERIOD_TYPE = "expiryPeriodType";
        public static final String MIN_ATTRIBUTES_REQUIRED_TO_SEARCH = "minAttributesRequiredToSearch";
        public static final String MAX_TEI_COUNT_TO_RETURN = "maxTeiCountToReturn";
        public static final String FEATURE_TYPE = "featureType";
        public static final String ACCESS_LEVEL = "accessLevel";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    VERSION,
                    ONLY_ENROLL_ONCE,
                    ENROLLMENT_DATE_LABEL,
                    DISPLAY_INCIDENT_DATE,
                    INCIDENT_DATE_LABEL,
                    REGISTRATION,
                    SELECT_ENROLLMENT_DATES_IN_FUTURE,
                    DATA_ENTRY_METHOD,
                    IGNORE_OVERDUE_EVENTS,
                    SELECT_INCIDENT_DATES_IN_FUTURE,
                    USE_FIRST_STAGE_DURING_REGISTRATION,
                    DISPLAY_FRONT_PAGE_LIST,
                    PROGRAM_TYPE,
                    RELATED_PROGRAM,
                    TRACKED_ENTITY_TYPE,
                    CATEGORY_COMBO,
                    ACCESS_DATA_WRITE,
                    EXPIRY_DAYS,
                    COMPLETE_EVENTS_EXPIRY_DAYS,
                    EXPIRY_PERIOD_TYPE,
                    MIN_ATTRIBUTES_REQUIRED_TO_SEARCH,
                    MAX_TEI_COUNT_TO_RETURN,
                    FEATURE_TYPE,
                    ACCESS_LEVEL
            );
        }
    }
}
