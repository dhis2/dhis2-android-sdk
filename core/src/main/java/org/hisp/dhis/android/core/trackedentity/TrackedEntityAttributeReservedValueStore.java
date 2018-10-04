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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class TrackedEntityAttributeReservedValueStore
        extends ObjectWithoutUidStoreImpl<TrackedEntityAttributeReservedValueModel>
        implements TrackedEntityAttributeReservedValueStoreInterface {

    private TrackedEntityAttributeReservedValueStore(DatabaseAdapter databaseAdapter,
                                                     SQLiteStatement insertStatement,
                                                     SQLiteStatement updateWhereStatement,
                                                     SQLStatementBuilder builder,
                                                     StatementBinder<TrackedEntityAttributeReservedValueModel> binder,
                                                     WhereStatementBinder<TrackedEntityAttributeReservedValueModel>
                                                             whereBinder) {
        super(databaseAdapter, insertStatement, updateWhereStatement, builder, binder, whereBinder);
    }

    @Override
    public void deleteExpired(@NonNull Date serverDate) throws RuntimeException {
        String serverDateStr = "date('" + BaseIdentifiableObject.DATE_FORMAT.format(serverDate) + "')";
        super.deleteWhereClause(Columns.EXPIRY_DATE + " < " + serverDateStr + " OR " +
                "( " + Columns.TEMPORAL_VALIDITY_DATE + " < " + serverDateStr + " AND " +
                Columns.TEMPORAL_VALIDITY_DATE + " IS NOT NULL );");
    }

    @Override
    public TrackedEntityAttributeReservedValueModel popOne(@NonNull String ownerUid,
                                                           @NonNull String organisationUnitUid) {
        return popOneWhere(where(ownerUid, organisationUnitUid));
    }

    @Override
    public int count(@NonNull String ownerUid, @NonNull String organisationUnitUid) {
        return countWhere(where(ownerUid, organisationUnitUid));
    }

    private String where(@NonNull String ownerUid,
                         @NonNull String organisationUnitUid) {
        return Columns.OWNER_UID + "='" + ownerUid + "' AND " +
                Columns.ORGANISATION_UNIT + "='" + organisationUnitUid + "'";
    }

    private static final StatementBinder<TrackedEntityAttributeReservedValueModel> BINDER
            = new StatementBinder<TrackedEntityAttributeReservedValueModel>() {
        @Override
        public void bindToStatement(@NonNull TrackedEntityAttributeReservedValueModel o,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.ownerObject());
            sqLiteBind(sqLiteStatement, 2, o.ownerUid());
            sqLiteBind(sqLiteStatement, 3, o.key());
            sqLiteBind(sqLiteStatement, 4, o.value());
            sqLiteBind(sqLiteStatement, 5, o.created());
            sqLiteBind(sqLiteStatement, 6, o.expiryDate());
            sqLiteBind(sqLiteStatement, 7, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 8, o.temporalValidityDate());
        }
    };

    private static final WhereStatementBinder<TrackedEntityAttributeReservedValueModel> WHERE_UPDATE_BINDER
            = new WhereStatementBinder<TrackedEntityAttributeReservedValueModel>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull TrackedEntityAttributeReservedValueModel o,
                                               @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 9, o.ownerUid());
            sqLiteBind(sqLiteStatement, 10, o.value());
            sqLiteBind(sqLiteStatement, 11, o.organisationUnit());
        }
    };


    public static TrackedEntityAttributeReservedValueStoreInterface
    create(DatabaseAdapter databaseAdapter) {
        BaseModel.Columns columns = new TrackedEntityAttributeReservedValueModel.Columns();

        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(
                TrackedEntityAttributeReservedValueModel.TABLE, columns);

        return new TrackedEntityAttributeReservedValueStore(
                databaseAdapter,
                databaseAdapter.compileStatement(statementBuilder.insert()),
                databaseAdapter.compileStatement(statementBuilder.updateWhere()),
                statementBuilder,
                BINDER,
                WHERE_UPDATE_BINDER);
    }
}