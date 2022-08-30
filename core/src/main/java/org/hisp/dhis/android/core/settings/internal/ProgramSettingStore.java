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

package org.hisp.dhis.android.core.settings.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.settings.ProgramSetting;
import org.hisp.dhis.android.core.settings.ProgramSettingTableInfo;

final class ProgramSettingStore {

    private static final StatementBinder<ProgramSetting> BINDER = (o, w) -> {
        w.bind(1, o.uid());
        w.bind(2, o.name());
        w.bind(3, o.lastUpdated());
        w.bind(4, o.teiDownload());
        w.bind(5, o.teiDBTrimming());
        w.bind(6, o.eventsDownload());
        w.bind(7, o.eventsDBTrimming());
        w.bind(8, o.updateDownload());
        w.bind(9, o.updateDBTrimming());
        w.bind(10, o.settingDownload());
        w.bind(11, o.settingDBTrimming());
        w.bind(12, o.enrollmentDownload());
        w.bind(13, o.enrollmentDBTrimming());
        w.bind(14, o.eventDateDownload());
        w.bind(15, o.eventDateDBTrimming());
        w.bind(16, o.enrollmentDateDownload());
        w.bind(17, o.enrollmentDateDBTrimming());
    };


    private static final WhereStatementBinder<ProgramSetting> WHERE_UPDATE_BINDER =
            (o, w) -> w.bind(18, o.uid());

    private static final WhereStatementBinder<ProgramSetting> WHERE_DELETE_BINDER =
            (o, w) -> w.bind(1, o.uid());


    private ProgramSettingStore() {}

    public static ObjectWithoutUidStore<ProgramSetting> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithoutUidStore(databaseAdapter, ProgramSettingTableInfo.TABLE_INFO, BINDER,
                WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER, ProgramSetting::create);
    }
}