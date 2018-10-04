/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.dataset;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.NameableStatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class DataSetStore {

    private DataSetStore() {}

    private static StatementBinder<DataSetModel> BINDER = new NameableStatementBinder<DataSetModel>() {
        @Override
        public void bindToStatement(@NonNull DataSetModel o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 11, o.periodType());
            sqLiteBind(sqLiteStatement, 12, o.categoryCombo());
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
            sqLiteBind(sqLiteStatement, 26, o.accessDataWrite());
        }
    };

    private static final CursorModelFactory<DataSetModel> FACTORY = new CursorModelFactory<DataSetModel>() {
        @Override
        public DataSetModel fromCursor(Cursor cursor) {
            return DataSetModel.create(cursor);
        }
    };

    public static IdentifiableObjectStore<DataSetModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter, DataSetModel.TABLE,
                new DataSetModel.Columns().all(), BINDER, FACTORY);
    }
}