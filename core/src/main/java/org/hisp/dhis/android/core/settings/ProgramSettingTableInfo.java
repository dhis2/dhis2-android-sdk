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

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

public final class ProgramSettingTableInfo {

    private ProgramSettingTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "ProgramSetting";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends CoreColumns {
        public static final String UID = IdentifiableColumns.UID;
        public static final String NAME = IdentifiableColumns.NAME;
        public static final String LAST_UPDATED = IdentifiableColumns.LAST_UPDATED;
        public static final String TEI_DOWNLOAD = "teiDownload";
        public static final String TEI_DB_TRIMMING = "teiDBTrimming";
        public static final String EVENTS_DOWNLOAD = "eventsDownload";
        public static final String EVENTS_DB_TRIMMING = "eventsDBTrimming";
        public static final String UPDATE_DOWNLOAD = "updateDownload";
        public static final String UPDATE_DB_TRIMMING = "updateDBTrimming";
        public static final String SETTING_DOWNLOAD = "settingDownload";
        public static final String SETTING_DB_TRIMMING = "settingDBTrimming";
        public static final String ENROLLMENT_DOWNLOAD = "enrollmentDownload";
        public static final String ENROLLMENT_DB_TRIMMING = "enrollmentDBTrimming";
        public static final String EVENT_DATE_DOWNLOAD = "eventDateDownload";
        public static final String EVENT_DATE_DB_TRIMMING = "eventDateDBTrimming";
        public static final String ENROLLMENT_DATE_DOWNLOAD = "enrollmentDateDownload";
        public static final String ENROLLMENT_DATE_DB_TRIMMING = "enrollmentDateDBTrimming";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    UID,
                    NAME,
                    LAST_UPDATED,
                    TEI_DOWNLOAD,
                    TEI_DB_TRIMMING,
                    EVENTS_DOWNLOAD,
                    EVENTS_DB_TRIMMING,
                    UPDATE_DOWNLOAD,
                    UPDATE_DB_TRIMMING,
                    SETTING_DOWNLOAD,
                    SETTING_DB_TRIMMING,
                    ENROLLMENT_DOWNLOAD,
                    ENROLLMENT_DB_TRIMMING,
                    EVENT_DATE_DOWNLOAD,
                    EVENT_DATE_DB_TRIMMING,
                    ENROLLMENT_DATE_DOWNLOAD,
                    ENROLLMENT_DATE_DB_TRIMMING
            );
        }
    }
}