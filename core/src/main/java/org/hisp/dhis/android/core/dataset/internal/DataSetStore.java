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

package org.hisp.dhis.android.core.dataset.internal;

import android.database.sqlite.SQLiteStatement;

import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;

import androidx.annotation.NonNull;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class DataSetStore {

    private DataSetStore() {}

    private static StatementBinder<DataSet> BINDER = new NameableStatementBinder<DataSet>() {
        @Override
        public void bindToStatement(@NonNull DataSet o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 11, o.periodType());
            sqLiteBind(sqLiteStatement, 12, UidsHelper.getUidOrNull(o.categoryCombo()));
            sqLiteBind(sqLiteStatement, 13, o.mobile());
            sqLiteBind(sqLiteStatement, 14, o.version());
            sqLiteBind(sqLiteStatement, 15, o.expiryDays());
            sqLiteBind(sqLiteStatement, 16, o.timelyDays());
            sqLiteBind(sqLiteStatement, 17, o.notifyCompletingUser());
            sqLiteBind(sqLiteStatement, 18, o.openFuturePeriods());
            sqLiteBind(sqLiteStatement, 19, o.fieldCombinationRequired());
            sqLiteBind(sqLiteStatement, 20, o.validCompleteOnly());
            sqLiteBind(sqLiteStatement, 21, o.noValueRequiresComment());
            sqLiteBind(sqLiteStatement, 22, o.skipOffline());
            sqLiteBind(sqLiteStatement, 23, o.dataElementDecoration());
            sqLiteBind(sqLiteStatement, 24, o.renderAsTabs());
            sqLiteBind(sqLiteStatement, 25, o.renderHorizontally());
            sqLiteBind(sqLiteStatement, 26, AccessHelper.getAccessDataWrite(o.access()));
            sqLiteBind(sqLiteStatement, 27, UidsHelper.getUidOrNull(o.workflow()));
        }
    };

    public static IdentifiableObjectStore<DataSet> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter, DataSetTableInfo.TABLE_INFO, BINDER, DataSet::create);
    }
}