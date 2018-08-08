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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class DataSetDataElementLinkStore {

    private DataSetDataElementLinkStore() {}

    static final StatementBinder<DataSetDataElementLinkModel> BINDER
            = new StatementBinder<DataSetDataElementLinkModel>() {
        @Override
        public void bindToStatement(@NonNull DataSetDataElementLinkModel o, @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.dataSet());
            sqLiteBind(sqLiteStatement, 2, o.dataElement());
            sqLiteBind(sqLiteStatement, 3, o.categoryCombo());
        }
    };

    static final WhereStatementBinder<DataSetDataElementLinkModel> WHERE_UPDATE_BINDER
            = new WhereStatementBinder<DataSetDataElementLinkModel>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull DataSetDataElementLinkModel o,
                                               @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 4, o.dataSet());
            sqLiteBind(sqLiteStatement, 5, o.dataElement());
        }
    };

    public static ObjectWithoutUidStore<DataSetDataElementLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithoutUidStore(databaseAdapter, DataSetDataElementLinkModel.TABLE,
                new DataSetDataElementLinkModel.Columns(), BINDER, WHERE_UPDATE_BINDER);
    }
}