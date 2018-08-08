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

package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.arch.db.TableInfo;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

@SuppressWarnings("PMD.UseVarargs")
public final class StoreFactory {

    private StoreFactory() {}

    public static <I extends Model & IdentifiableObject> IdentifiableObjectStore<I>
    identifiableStore(DatabaseAdapter databaseAdapter, String tableName, String[] columns,
                      StatementBinder<I> binder) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(tableName, columns, new String[]{});
        SQLStatementWrapper statements = new SQLStatementWrapper(statementBuilder, databaseAdapter);
        return new IdentifiableObjectStoreImpl<>(databaseAdapter, statements, statementBuilder, binder);
    }

    public static <I extends Model & IdentifiableObject> IdentifiableObjectStore<I>
    identifiableStore(DatabaseAdapter databaseAdapter, TableInfo tableInfo, StatementBinder<I> binder) {
        return identifiableStore(databaseAdapter, tableInfo.name(), tableInfo.columns().all(), binder);
    }

    static <I extends BaseModel> ObjectStore<I>
    objectStore(DatabaseAdapter databaseAdapter, String tableName, String[] columns, StatementBinder<I> binder) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(tableName, columns, new String[]{});
        return new ObjectStoreImpl<>(databaseAdapter, databaseAdapter.compileStatement(
                statementBuilder.insert()), statementBuilder, binder);
    }

    public static <I extends BaseModel> ObjectWithoutUidStore<I> objectWithoutUidStore(
            DatabaseAdapter databaseAdapter, String tableName, BaseModel.Columns columns, StatementBinder<I> binder,
            WhereStatementBinder<I> whereBinder) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(tableName, columns.all(), columns.whereUpdate());
        return new ObjectWithoutUidStoreImpl<>(databaseAdapter,
                databaseAdapter.compileStatement(statementBuilder.insert()),
                databaseAdapter.compileStatement(statementBuilder.updateWhere()),
                statementBuilder, binder, whereBinder);
    }

    public static <I extends BaseModel> ObjectWithoutUidStore<I> objectWithoutUidStore(
            DatabaseAdapter databaseAdapter, TableInfo tableInfo, StatementBinder<I> binder,
            WhereStatementBinder<I> whereBinder) {
        return objectWithoutUidStore(databaseAdapter, tableInfo.name(), tableInfo.columns(), binder, whereBinder);
    }

    public static <I extends BaseModel> LinkModelStore<I> linkModelStore(
            DatabaseAdapter databaseAdapter, String tableName, BaseModel.Columns columns,
            String masterColumn, StatementBinder<I> binder, WhereStatementBinder<I> whereBinder) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(tableName, columns.all(), columns.whereUpdate());
        return new LinkModelStoreImpl<>(databaseAdapter,
                databaseAdapter.compileStatement(statementBuilder.insert()),
                databaseAdapter.compileStatement(statementBuilder.updateWhere()),
                statementBuilder, masterColumn, binder, whereBinder);
    }
}