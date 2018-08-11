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

package org.hisp.dhis.android.core.datavalue;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class DataValueStore {

    private DataValueStore() {}

    private static final StatementBinder<DataValueModel> BINDER = new StatementBinder<DataValueModel>() {
        @Override
        public void bindToStatement(@NonNull DataValueModel o, @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.dataElement());
            sqLiteBind(sqLiteStatement, 2, o.period());
            sqLiteBind(sqLiteStatement, 3, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 4, o.categoryOptionCombo());
            sqLiteBind(sqLiteStatement, 5, o.attributeOptionCombo());
            sqLiteBind(sqLiteStatement, 6, o.value());
            sqLiteBind(sqLiteStatement, 7, o.storedBy());
            sqLiteBind(sqLiteStatement, 8, o.created());
            sqLiteBind(sqLiteStatement, 9, o.lastUpdated());
            sqLiteBind(sqLiteStatement, 10, o.comment());
            sqLiteBind(sqLiteStatement, 11, o.followUp());
        }
    };

    private static final WhereStatementBinder<DataValueModel> WHERE_UPDATE_BINDER
            = new WhereStatementBinder<DataValueModel>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull DataValueModel o, @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 12, o.dataElement());
            sqLiteBind(sqLiteStatement, 13, o.period());
            sqLiteBind(sqLiteStatement, 14, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 15, o.categoryOptionCombo());
            sqLiteBind(sqLiteStatement, 16, o.attributeOptionCombo());
        }
    };

    public static ObjectWithoutUidStore<DataValueModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithoutUidStore(databaseAdapter, DataValueModel.TABLE,
                new DataValueModel.Columns(), BINDER, WHERE_UPDATE_BINDER);
    }
}