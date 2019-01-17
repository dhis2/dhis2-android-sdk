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
package org.hisp.dhis.android.core.resource;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.SQLStatementWrapper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public class ResourceStoreImpl extends ObjectWithoutUidStoreImpl<Resource> implements ResourceStore {
    public ResourceStoreImpl(DatabaseAdapter databaseAdapter,
                             SQLStatementWrapper statementWrapper,
                             SQLStatementBuilder builder) {
        super(databaseAdapter, statementWrapper.insert, statementWrapper.update, builder, BINDER,
                WHERE_UPDATE_BINDER, FACTORY);
    }

    private static final StatementBinder<Resource> BINDER = new StatementBinder<Resource>() {
        @Override
        public void bindToStatement(@NonNull Resource resource,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, resource.resourceType());
            sqLiteBind(sqLiteStatement, 2, resource.lastSynced());
        }
    };

    private static final WhereStatementBinder<Resource> WHERE_UPDATE_BINDER
            = new WhereStatementBinder<Resource>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull Resource resource,
                                               @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 3, resource.resourceType());
        }
    };

    private static final CursorModelFactory<Resource> FACTORY =
            new CursorModelFactory<Resource>() {
        @Override
        public Resource fromCursor(Cursor cursor) {
            return Resource.create(cursor);
        }
    };

    @Override
    public String getLastUpdated(ResourceModel.Type type) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(ResourceTableInfo.Columns.RESOURCE_TYPE, type.name()).build();

        Date lastUpdated = selectWhereClause(whereClause).get(0).lastSynced();
        return BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated);
    }

    public static ResourceStore create(DatabaseAdapter databaseAdapter) {

        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(ResourceTableInfo.TABLE_INFO.name(),
                ResourceTableInfo.TABLE_INFO.columns());

        SQLStatementWrapper statementWrapper = new SQLStatementWrapper(statementBuilder, databaseAdapter);

        return new ResourceStoreImpl(databaseAdapter, statementWrapper, statementBuilder);
    }
}