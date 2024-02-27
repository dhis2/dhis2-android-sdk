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
package org.hisp.dhis.android.core.settings

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreColumns

object GeneralSettingTableInfo {
    val TABLE_INFO: TableInfo = object : TableInfo() {
        override fun name(): String {
            return "GeneralSetting"
        }

        override fun columns(): CoreColumns {
            return Columns()
        }
    }

    class Columns : CoreColumns() {
        override fun all(): Array<String> {
            return CollectionsHelper.appendInNewArray(
                super.all(),
                ENCRYPT_DB,
                LAST_UPDATED,
                RESERVED_VALUES,
                SMS_GATEWAY,
                SMS_RESULT_SENDER,
                MATOMO_ID,
                MATOMO_URL,
                ALLOW_SCREEN_CAPTURE,
                MESSAGE_OF_THE_DAY,
                EXPERIMENTAL_FEATURES,
                BYPASS_DHIS2_VERSION_CHECK,
            )
        }

        companion object {
            const val ENCRYPT_DB = "encryptDB"
            const val LAST_UPDATED = "lastUpdated"
            const val RESERVED_VALUES = "reservedValues"
            const val SMS_GATEWAY = "smsGateway"
            const val SMS_RESULT_SENDER = "smsResultSender"
            const val MATOMO_ID = "matomoID"
            const val MATOMO_URL = "matomoURL"
            const val ALLOW_SCREEN_CAPTURE = "allowScreenCapture"
            const val MESSAGE_OF_THE_DAY = "messageOfTheDay"
            const val EXPERIMENTAL_FEATURES = "experimentalFeatures"
            const val BYPASS_DHIS2_VERSION_CHECK = "bypassDHIS2VersionCheck"
        }
    }
}