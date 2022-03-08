/*
 *  Copyright (c) 2004-2021, University of Oslo
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

public final class GeneralSettingTableInfo {

    private GeneralSettingTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "GeneralSetting";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends CoreColumns {
        public static final String ENCRYPT_DB = "encryptDB";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String RESERVED_VALUES = "reservedValues";
        public static final String SMS_GATEWAY = "smsGateway";
        public static final String SMS_RESULT_SENDER = "smsResultSender";
        public static final String MATOMO_ID = "matomoID";
        public static final String MATOMO_URL = "matomoURL";
        public static final String ALLOW_SCREEN_CAPTURE = "allowScreenCapture";
        public static final String MESSAGE_OF_THE_DAY = "messageOfTheDay";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    ENCRYPT_DB,
                    LAST_UPDATED,
                    RESERVED_VALUES,
                    SMS_GATEWAY,
                    SMS_RESULT_SENDER,
                    MATOMO_ID,
                    MATOMO_URL,
                    ALLOW_SCREEN_CAPTURE,
                    MESSAGE_OF_THE_DAY
            );
        }
    }
}