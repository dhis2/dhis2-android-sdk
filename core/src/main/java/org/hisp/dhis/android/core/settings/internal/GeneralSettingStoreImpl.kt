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
package org.hisp.dhis.android.core.settings.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.settings.GeneralSettingTableInfo
import org.hisp.dhis.android.core.settings.GeneralSettings
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("MagicNumber")
internal class GeneralSettingStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : GeneralSettingStore,
    ObjectWithoutUidStoreImpl<GeneralSettings>(
        databaseAdapter,
        GeneralSettingTableInfo.TABLE_INFO,
        BINDER,
        WHERE_UPDATE_BINDER,
        WHERE_DELETE_BINDER,
        { cursor: Cursor -> GeneralSettings.create(cursor) },
    ) {

    companion object {
        private val BINDER = StatementBinder { o: GeneralSettings, w: StatementWrapper ->
            w.bind(1, o.encryptDB())
            w.bind(2, o.lastUpdated())
            w.bind(3, o.reservedValues())
            w.bind(4, o.smsGateway())
            w.bind(5, o.smsResultSender())
            w.bind(6, o.matomoID())
            w.bind(7, o.matomoURL())
            w.bind(8, o.allowScreenCapture())
            w.bind(9, o.messageOfTheDay())
            w.bind(10, StringListColumnAdapter.serialize(o.experimentalFeatures()))
        }

        private val WHERE_UPDATE_BINDER = WhereStatementBinder {
                _: GeneralSettings, _: StatementWrapper ->
        }

        private val WHERE_DELETE_BINDER = WhereStatementBinder {
                _: GeneralSettings, _: StatementWrapper ->
        }
    }
}
