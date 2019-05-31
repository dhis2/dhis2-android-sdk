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

package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class ObjectStyleStoreImpl extends ObjectWithoutUidStoreImpl<ObjectStyle>
        implements ObjectStyleStore {

    private static final StatementBinder<ObjectStyle> BINDER = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.uid());
        sqLiteBind(sqLiteStatement, 2, o.objectTable());
        sqLiteBind(sqLiteStatement, 3, o.color());
        sqLiteBind(sqLiteStatement, 4, o.icon());
    };

    private static final WhereStatementBinder<ObjectStyle> WHERE_UPDATE_BINDER
            = (o, sqLiteStatement) -> sqLiteBind(sqLiteStatement, 5, o.uid());

    private static final WhereStatementBinder<ObjectStyle> WHERE_DELETE_BINDER
            = (o, sqLiteStatement) -> sqLiteBind(sqLiteStatement, 1, o.uid());

    private ObjectStyleStoreImpl(DatabaseAdapter databaseAdapter,
                                 SQLStatementBuilder builder,
                                 StatementBinder<ObjectStyle> binder,
                                 WhereStatementBinder<ObjectStyle> whereUpdateBinder,
                                 WhereStatementBinder<ObjectStyle> whereDeleteBinder,
                                 CursorModelFactory<ObjectStyle> modelFactory) {
        super(databaseAdapter, builder, binder, whereUpdateBinder, whereDeleteBinder, modelFactory);
    }

    @Override
    public <O extends ObjectWithStyle<?, ?> & ObjectWithUidInterface> ObjectStyle getStyle(O objectWithStyle,
                                                                                           TableInfo tableInfo) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(ObjectStyleTableInfo.Columns.OBJECT_TABLE, tableInfo.name())
                .appendKeyStringValue(ObjectStyleTableInfo.Columns.UID, objectWithStyle.uid())
                .build();
        return selectOneWhere(whereClause);
    }

    public static ObjectStyleStore create(DatabaseAdapter databaseAdapter) {

        BaseModel.Columns columns = ObjectStyleTableInfo.TABLE_INFO.columns();

        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(
                ObjectStyleTableInfo.TABLE_INFO.name(), columns);

        return new ObjectStyleStoreImpl(databaseAdapter, statementBuilder, BINDER, WHERE_UPDATE_BINDER,
                WHERE_DELETE_BINDER, ObjectStyle::create);
    }
}