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

package org.hisp.dhis.android.core.trackedentity;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.IdentifiableObjectWithStateStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.SQLStatementWrapper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class TrackedEntityInstanceStoreImpl extends IdentifiableObjectWithStateStoreImpl<TrackedEntityInstance>
        implements TrackedEntityInstanceStore {

    public TrackedEntityInstanceStoreImpl(DatabaseAdapter databaseAdapter,
                                          SQLStatementWrapper statementWrapper,
                                          SQLStatementBuilder builder) {
        super(databaseAdapter, statementWrapper, builder, BINDER, FACTORY);
    }

    @Override
    public List<TrackedEntityInstance> queryTrackedEntityInstancesToPost() {
        String whereToPostClause = new WhereClauseBuilder()
                .appendOrKeyStringValue(BaseDataModel.Columns.STATE, State.TO_POST)
                .appendOrKeyStringValue(BaseDataModel.Columns.STATE, State.TO_UPDATE)
                .appendOrKeyStringValue(BaseDataModel.Columns.STATE, State.TO_DELETE)
                .build();

        return selectWhereClause(whereToPostClause);
    }

    @Override
    public List<String> querySyncedTrackedEntityInstanceUids() {
        String whereSyncedClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseDataModel.Columns.STATE, State.SYNCED)
                .build();

        return selectUidsWhere(whereSyncedClause);
    }

    @Override
    public List<String> queryMissingRelationshipsUids() {
        String whereRelationshipsClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseDataModel.Columns.STATE, State.RELATIONSHIP)
                .appendIsNullValue(TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT)
                .build();

        return selectUidsWhere(whereRelationshipsClause);
    }

    private static final StatementBinder<TrackedEntityInstance> BINDER = new StatementBinder<TrackedEntityInstance>() {
        @Override
        public void bindToStatement(@NonNull TrackedEntityInstance o, @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.uid());
            sqLiteBind(sqLiteStatement, 2, o.created());
            sqLiteBind(sqLiteStatement, 3, o.lastUpdated());
            sqLiteBind(sqLiteStatement, 4, o.createdAtClient());
            sqLiteBind(sqLiteStatement, 5, o.lastUpdatedAtClient());
            sqLiteBind(sqLiteStatement, 6, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 7, o.trackedEntityType());
            sqLiteBind(sqLiteStatement, 8, o.coordinates());
            sqLiteBind(sqLiteStatement, 9, o.featureType());
            sqLiteBind(sqLiteStatement, 10, o.state());
        }
    };

    private static final CursorModelFactory<TrackedEntityInstance> FACTORY =
            new CursorModelFactory<TrackedEntityInstance>() {
        @Override
        public TrackedEntityInstance fromCursor(Cursor cursor) {
            return TrackedEntityInstance.create(cursor);
        }
    };

    public static TrackedEntityInstanceStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(
                TrackedEntityInstanceTableInfo.TABLE_INFO.name(),
                TrackedEntityInstanceTableInfo.TABLE_INFO.columns());
        SQLStatementWrapper statementWrapper = new SQLStatementWrapper(statementBuilder, databaseAdapter);

        return new TrackedEntityInstanceStoreImpl(
                databaseAdapter,
                statementWrapper,
                statementBuilder
        );
    }
}