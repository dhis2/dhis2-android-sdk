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

package org.hisp.dhis.android.core.arch.db.stores.internal;

import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorExecutorImpl;
import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorModelFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.statementwrapper.internal.SQLStatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection;
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public final class StoreFactory {

    private StoreFactory() {}

    public static <I extends Model & ObjectWithUidInterface> IdentifiableObjectStore<I> objectWithUidStore(
            DatabaseAdapter databaseAdapter, TableInfo tableInfo, StatementBinder<I> binder,
            CursorModelFactory<I> modelFactory) {
        SQLStatementBuilder statementBuilder =
                new SQLStatementBuilderImpl(tableInfo.name(), tableInfo.columns().all(), new String[]{});
        SQLStatementWrapper statements = new SQLStatementWrapper(statementBuilder, databaseAdapter);
        return new IdentifiableObjectStoreImpl<>(databaseAdapter, statements, statementBuilder, binder, modelFactory);
    }

    public static <I extends Model> ObjectStore<I> objectStore(DatabaseAdapter databaseAdapter,
                                                               TableInfo tableInfo,
                                                               StatementBinder<I> binder,
                                                               CursorModelFactory<I> modelFactory) {
        SQLStatementBuilder statementBuilder =
                new SQLStatementBuilderImpl(tableInfo.name(), tableInfo.columns().all(), new String[]{});
        return new ObjectStoreImpl<>(databaseAdapter, databaseAdapter.compileStatement(statementBuilder.insert()),
                statementBuilder, binder, modelFactory);
    }

    public static <I extends Model> ObjectWithoutUidStore<I> objectWithoutUidStore(
            DatabaseAdapter databaseAdapter, TableInfo tableInfo, StatementBinder<I> binder,
            WhereStatementBinder<I> whereUpdateBinder,
            WhereStatementBinder<I> whereDeleteBinder, CursorModelFactory<I> modelFactory) {
        SQLStatementBuilder statementBuilder =
                new SQLStatementBuilderImpl(tableInfo.name(), tableInfo.columns().all(), tableInfo.columns().whereUpdate());
        return new ObjectWithoutUidStoreImpl<>(databaseAdapter, statementBuilder, binder, whereUpdateBinder,
                whereDeleteBinder, modelFactory);
    }

    public static <I extends Model> LinkModelStore<I> linkModelStore(
            DatabaseAdapter databaseAdapter, TableInfo tableInfo, String masterColumn, StatementBinder<I> binder,
            CursorModelFactory<I> modelFactory) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilderImpl(tableInfo.name(), tableInfo.columns().all(),
                tableInfo.columns().whereUpdate());
        return new LinkModelStoreImpl<>(databaseAdapter, databaseAdapter.compileStatement(statementBuilder.insert()),
                statementBuilder, masterColumn, binder, modelFactory);
    }

    public static <P extends ObjectWithUidInterface,
            C extends ObjectWithUidInterface> LinkModelChildStore<P, C> linkModelChildStore(
                    DatabaseAdapter databaseAdapter,
                    TableInfo linkTableInfo,
                    LinkTableChildProjection linkTableChildProjection,
                    CursorModelFactory<C> childFactory) {
        return new LinkModelChildStoreImpl<>(
                linkTableChildProjection,
                databaseAdapter,
                new SQLStatementBuilderImpl(linkTableInfo),
                new CursorExecutorImpl<>(childFactory));
    }

    public static <P extends ObjectWithUidInterface, C> SingleParentChildStore<P, C> singleParentChildStore(
                    DatabaseAdapter databaseAdapter,
                    SingleParentChildProjection childProjection,
                    CursorModelFactory<C> childFactory) {
        return new SingleParentChildStoreImpl<>(
                childProjection,
                databaseAdapter,
                new SQLStatementBuilderImpl(childProjection.childTableInfo),
                new CursorExecutorImpl<>(childFactory));
    }

    public static <P extends ObjectWithUidInterface> ObjectWithUidChildStore<P> objectWithUidChildStore(
                    DatabaseAdapter databaseAdapter,
                    TableInfo linkTableInfo,
                    LinkTableChildProjection childProjection) {
        return new ObjectWithUidChildStoreImpl<>(
                childProjection,
                databaseAdapter,
                new SQLStatementBuilderImpl(linkTableInfo));
    }
}