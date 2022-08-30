/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo.Columns;

import java.util.Date;

public final class TrackedEntityAttributeReservedValueStore
        extends ObjectWithoutUidStoreImpl<TrackedEntityAttributeReservedValue>
        implements TrackedEntityAttributeReservedValueStoreInterface {

    private static final StatementBinder<TrackedEntityAttributeReservedValue> BINDER = (o, w) -> {
        w.bind(1, o.ownerObject());
        w.bind(2, o.ownerUid());
        w.bind(3, o.key());
        w.bind(4, o.value());
        w.bind(5, o.created());
        w.bind(6, o.expiryDate());
        w.bind(7, o.organisationUnit());
        w.bind(8, o.temporalValidityDate());
        w.bind(9, o.pattern());
    };

    private static final WhereStatementBinder<TrackedEntityAttributeReservedValue> WHERE_UPDATE_BINDER = (o, w) -> {
        w.bind(10, o.ownerUid());
        w.bind(11, o.value());
        w.bind(12, o.organisationUnit());
    };

    private static final WhereStatementBinder<TrackedEntityAttributeReservedValue> WHERE_DELETE_BINDER = (o, w) -> {
        w.bind(1, o.ownerUid());
        w.bind(2, o.value());
        w.bind(3, o.organisationUnit());
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
    public void deleteIfOutdatedPattern(@NonNull String ownerUid, @NonNull String pattern) {
        String deleteWhereClause = new WhereClauseBuilder()
                .appendKeyStringValue(Columns.OWNER_UID, ownerUid)
                .appendNotKeyStringValue(Columns.PATTERN, pattern)
                .build();

        super.deleteWhere(deleteWhereClause);
    }

    @Override
    public TrackedEntityAttributeReservedValue popOne(@NonNull String ownerUid, @Nullable String organisationUnitUid) {
        return popOneWhere(where(ownerUid, organisationUnitUid, null));
    }

    @Override
    public int count(@NonNull String ownerUid, @Nullable String organisationUnitUid, @Nullable String pattern) {
        return countWhere(where(ownerUid, organisationUnitUid, pattern));
    }

    private String where(@NonNull String ownerUid,
                         @Nullable String organisationUnit,
                         @Nullable String pattern) {
        WhereClauseBuilder builder = new WhereClauseBuilder()
                .appendKeyStringValue(Columns.OWNER_UID, ownerUid);

        if (organisationUnit != null) {
            builder.appendKeyStringValue(Columns.ORGANISATION_UNIT, organisationUnit);
        }
        if (pattern != null) {
            builder.appendKeyStringValue(Columns.PATTERN, pattern);
        }

        return builder.build();
    }

    public static TrackedEntityAttributeReservedValueStoreInterface create(DatabaseAdapter databaseAdapter) {

        SQLStatementBuilderImpl statementBuilder =
                new SQLStatementBuilderImpl(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO);

        return new TrackedEntityAttributeReservedValueStore(databaseAdapter, statementBuilder,
                BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER);
    }
}