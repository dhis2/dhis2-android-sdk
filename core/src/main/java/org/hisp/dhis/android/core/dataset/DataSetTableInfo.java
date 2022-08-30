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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.NameableWithStyleColumns;

public final class DataSetTableInfo {

    private DataSetTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "DataSet";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends NameableWithStyleColumns {

        public static final String PERIOD_TYPE = "periodType";
        public static final String CATEGORY_COMBO = "categoryCombo";
        public static final String MOBILE = "mobile";
        public static final String VERSION = "version";
        public static final String EXPIRY_DAYS = "expiryDays";
        public static final String TIMELY_DAYS = "timelyDays";
        public static final String NOTIFY_COMPLETING_USER = "notifyCompletingUser";
        public static final String OPEN_FUTURE_PERIODS = "openFuturePeriods";
        public static final String FIELD_COMBINATION_REQUIRED = "fieldCombinationRequired";
        public static final String VALID_COMPLETE_ONLY = "validCompleteOnly";
        public static final String NO_VALUE_REQUIRES_COMMENT = "noValueRequiresComment";
        public static final String SKIP_OFFLINE = "skipOffline";
        public static final String DATA_ELEMENT_DECORATION = "dataElementDecoration";
        public static final String RENDER_AS_TABS = "renderAsTabs";
        public static final String RENDER_HORIZONTALLY = "renderHorizontally";
        public static final String ACCESS_DATA_WRITE = "accessDataWrite";
        public static final String WORKFLOW = "workflow";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    PERIOD_TYPE,
                    CATEGORY_COMBO,
                    MOBILE,
                    VERSION,
                    EXPIRY_DAYS,
                    TIMELY_DAYS,
                    NOTIFY_COMPLETING_USER,
                    OPEN_FUTURE_PERIODS,
                    FIELD_COMBINATION_REQUIRED,
                    VALID_COMPLETE_ONLY,
                    NO_VALUE_REQUIRES_COMMENT,
                    SKIP_OFFLINE,
                    DATA_ELEMENT_DECORATION,
                    RENDER_AS_TABS,
                    RENDER_HORIZONTALLY,
                    ACCESS_DATA_WRITE,
                    WORKFLOW
            );
        }
    }
}
