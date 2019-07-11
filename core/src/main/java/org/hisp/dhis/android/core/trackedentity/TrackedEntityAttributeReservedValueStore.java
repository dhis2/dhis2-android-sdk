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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo.Columns;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.sqLiteBind;

public final class TrackedEntityAttributeReservedValueStore
        extends ObjectWithoutUidStoreImpl<TrackedEntityAttributeReservedValue>
        implements TrackedEntityAttributeReservedValueStoreInterface {

    private static final StatementBinder<TrackedEntityAttributeReservedValue> BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.ownerObject());
        sqLiteBind(sqLiteStatement, 2, o.ownerUid());
        sqLiteBind(sqLiteStatement, 3, o.key());
        sqLiteBind(sqLiteStatement, 4, o.value());
        sqLiteBind(sqLiteStatement, 5, o.created());
        sqLiteBind(sqLiteStatement, 6, o.expiryDate());
        sqLiteBind(sqLiteStatement, 7, o.organisationUnit());
        sqLiteBind(sqLiteStatement, 8, o.temporalValidityDate());
    };

    private static final WhereStatementBinder<TrackedEntityAttributeReservedValue> WHERE_UPDATE_BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 9, o.ownerUid());
        sqLiteBind(sqLiteStatement, 10, o.value());
        sqLiteBind(sqLiteStatement, 11, o.organisationUnit());
    };

    private static final WhereStatementBinder<TrackedEntityAttributeReservedValue> WHERE_DELETE_BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.ownerUid());
        sqLiteBind(sqLiteStatement, 2, o.value());
        sqLiteBind(sqLiteStatement, 3, o.organisationUnit());
    };

    private TrackedEntityAttributeReservedValueStore(
            DatabaseAdapter databaseAdapter,
            SQLStatementBuilderImpl builder,
            StatementBinder<TrackedEntityAttributeReservedValue> binder,
            WhereStatementBinder<TrackedEntityAttributeReservedValue> whereUpdateBinder,
            WhereStatementBinder<TrackedEntityAttributeReservedValue> whereDeleteBinder) {
        super(databaseAdapter, builder, binder, whereUpdateBinder, whereDeleteBinder,
                TrackedEntityAttributeReservedValue::create);
    }

    @Override
    public void deleteExpired(@NonNull Date serverDate) throws RuntimeException {
        String serverDateStr = "date('" + BaseIdentifiableObject.DATE_FORMAT.format(serverDate) + "')";
        super.deleteWhere(Columns.EXPIRY_DATE + " < " + serverDateStr + " OR " +
                "( " + Columns.TEMPORAL_VALIDITY_DATE + " < " + serverDateStr + " AND " +
                Columns.TEMPORAL_VALIDITY_DATE + " IS NOT NULL );");
    }

    @Override
    public TrackedEntityAttributeReservedValue popOne(@NonNull String ownerUid,
                                                      @Nullable String organisationUnitUid) {
        String where = organisationUnitUid == null ? where(ownerUid) : where(ownerUid, organisationUnitUid);
        return popOneWhere(where);
    }

    @Override
    public int count(@NonNull String ownerUid, @NonNull String organisationUnitUid) {
        return countWhere(where(ownerUid, organisationUnitUid));
    }

    @Override
    public int count(@NonNull String ownerUid) {
        return countWhere(where(ownerUid));
    }

    private String where(@NonNull String ownerUid,
                         @NonNull String organisationUnitUid) {
        return new WhereClauseBuilder()
                .appendKeyStringValue(Columns.OWNER_UID, ownerUid)
                .appendKeyStringValue(Columns.ORGANISATION_UNIT, organisationUnitUid)
                .build();
    }

    private String where(@NonNull String ownerUid) {
        return new WhereClauseBuilder()
                .appendKeyStringValue(Columns.OWNER_UID, ownerUid)
                .build();
    }

    public static TrackedEntityAttributeReservedValueStoreInterface create(DatabaseAdapter databaseAdapter) {

        SQLStatementBuilderImpl statementBuilder =
                new SQLStatementBuilderImpl(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO);

        return new TrackedEntityAttributeReservedValueStore(databaseAdapter, statementBuilder,
                BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER);
    }
}