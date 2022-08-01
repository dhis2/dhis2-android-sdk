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
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableWithStyleColumns;

public final class ProgramStageTableInfo {

    private ProgramStageTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "ProgramStage";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends IdentifiableWithStyleColumns {
        public static final String DESCRIPTION = "description";
        public static final String DISPLAY_DESCRIPTION = "displayDescription";
        public static final String EXECUTION_DATE_LABEL = "executionDateLabel";
        public static final String DUE_DATE_LABEL = "dueDateLabel";
        public static final String ALLOW_GENERATE_NEXT_VISIT = "allowGenerateNextVisit";
        public static final String VALID_COMPLETE_ONLY = "validCompleteOnly";
        public static final String REPORT_DATE_TO_USE = "reportDateToUse";
        public static final String OPEN_AFTER_ENROLLMENT = "openAfterEnrollment";
        public static final String REPEATABLE = "repeatable";
        public static final String FEATURE_TYPE = "featureType";
        public static final String FORM_TYPE = "formType";
        public static final String DISPLAY_GENERATE_EVENT_BOX = "displayGenerateEventBox";
        public static final String GENERATED_BY_ENROLMENT_DATE = "generatedByEnrollmentDate";
        public static final String AUTO_GENERATE_EVENT = "autoGenerateEvent";
        public static final String SORT_ORDER = "sortOrder";
        public static final String HIDE_DUE_DATE = "hideDueDate";
        public static final String BLOCK_ENTRY_FORM = "blockEntryForm";
        public static final String MIN_DAYS_FROM_START = "minDaysFromStart";
        public static final String STANDARD_INTERVAL = "standardInterval";
        public static final String PERIOD_TYPE = "periodType";
        public static final String PROGRAM = "program";
        public static final String REMIND_COMPLETED = "remindCompleted";
        public static final String ACCESS_DATA_WRITE = "accessDataWrite";
        public static final String ENABLE_USER_ASSIGNMENT = "enableUserAssignment";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    DESCRIPTION,
                    DISPLAY_DESCRIPTION,
                    EXECUTION_DATE_LABEL,
                    DUE_DATE_LABEL,
                    ALLOW_GENERATE_NEXT_VISIT,
                    VALID_COMPLETE_ONLY,
                    REPORT_DATE_TO_USE,
                    OPEN_AFTER_ENROLLMENT,
                    REPEATABLE,
                    FORM_TYPE,
                    DISPLAY_GENERATE_EVENT_BOX,
                    GENERATED_BY_ENROLMENT_DATE,
                    AUTO_GENERATE_EVENT,
                    SORT_ORDER,
                    HIDE_DUE_DATE,
                    BLOCK_ENTRY_FORM,
                    MIN_DAYS_FROM_START,
                    STANDARD_INTERVAL,
                    PROGRAM,
                    PERIOD_TYPE,
                    ACCESS_DATA_WRITE,
                    REMIND_COMPLETED,
                    FEATURE_TYPE,
                    ENABLE_USER_ASSIGNMENT
            );
        }
    }
}