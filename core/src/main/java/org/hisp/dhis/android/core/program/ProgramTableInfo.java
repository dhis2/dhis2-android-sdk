/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.utils.Utils;

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

    static class Columns extends BaseNameableObjectModel.Columns {
        public static final String ACCESS_DATA_WRITE = "accessDataWrite";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    ProgramFields.VERSION,
                    ProgramFields.ONLY_ENROLL_ONCE,
                    ProgramFields.ENROLLMENT_DATE_LABEL,
                    ProgramFields.DISPLAY_INCIDENT_DATE,
                    ProgramFields.INCIDENT_DATE_LABEL,
                    ProgramFields.REGISTRATION,
                    ProgramFields.SELECT_ENROLLMENT_DATES_IN_FUTURE,
                    ProgramFields.DATA_ENTRY_METHOD,
                    ProgramFields.IGNORE_OVERDUE_EVENTS,
                    ProgramFields.RELATIONSHIP_FROM_A,
                    ProgramFields.SELECT_INCIDENT_DATES_IN_FUTURE,
                    ProgramFields.CAPTURE_COORDINATES,
                    ProgramFields.USE_FIRST_STAGE_DURING_REGISTRATION,
                    ProgramFields.DISPLAY_FRONT_PAGE_LIST,
                    ProgramFields.PROGRAM_TYPE,
                    ProgramFields.RELATIONSHIP_TYPE,
                    ProgramFields.RELATIONSHIP_TEXT,
                    ProgramFields.RELATED_PROGRAM,
                    ProgramFields.TRACKED_ENTITY_TYPE,
                    ProgramFields.CATEGORY_COMBO,
                    ACCESS_DATA_WRITE,
                    ProgramFields.EXPIRY_DAYS,
                    ProgramFields.COMPLETE_EVENTS_EXPIRY_DAYS,
                    ProgramFields.EXPIRY_PERIOD_TYPE,
                    ProgramFields.MIN_ATTRIBUTES_REQUIRED_TO_SEARCH,
                    ProgramFields.MAX_TEI_COUNT_TO_RETURN,
                    ProgramFields.FEATURE_TYPE
            );
        }
    }
}
