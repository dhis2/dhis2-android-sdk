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

import android.database.Cursor;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class TrackedEntityAttributeValueStoreImpl
        extends ObjectWithoutUidStoreImpl<TrackedEntityAttributeValue> implements TrackedEntityAttributeValueStore {

    private static final StatementBinder<TrackedEntityAttributeValue> BINDER =
            (o, sqLiteStatement) -> {
                sqLiteBind(sqLiteStatement, 1, o.value());
                sqLiteBind(sqLiteStatement, 2, o.created());
                sqLiteBind(sqLiteStatement, 3, o.lastUpdated());
                sqLiteBind(sqLiteStatement, 4, o.trackedEntityAttribute());
                sqLiteBind(sqLiteStatement, 5, o.trackedEntityInstance());
            };

    private static final WhereStatementBinder<TrackedEntityAttributeValue> WHERE_UPDATE_BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 6, o.trackedEntityAttribute());
        sqLiteBind(sqLiteStatement, 7, o.trackedEntityInstance());
    };


    private static final WhereStatementBinder<TrackedEntityAttributeValue> WHERE_DELETE_BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.trackedEntityAttribute());
        sqLiteBind(sqLiteStatement, 2, o.trackedEntityInstance());
    };

    static final SingleParentChildProjection CHILD_PROJECTION = new SingleParentChildProjection(
            TrackedEntityAttributeValueTableInfo.TABLE_INFO,
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE);

    private TrackedEntityAttributeValueStoreImpl(DatabaseAdapter databaseAdapter,
                                SQLStatementBuilder builder) {
        super(databaseAdapter, builder, BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER,
                TrackedEntityAttributeValue::create);
    }

    @Override
    public Map<String, List<TrackedEntityAttributeValue>> queryTrackedEntityAttributeValueToPost() {
        String toPostQuery =
                "SELECT TrackedEntityAttributeValue.* " +
                "FROM (TrackedEntityAttributeValue INNER JOIN TrackedEntityInstance " +
                "ON TrackedEntityAttributeValue.trackedEntityInstance = TrackedEntityInstance.uid) " +
                "WHERE TrackedEntityInstance.state = 'TO_POST' " +
                "OR TrackedEntityInstance.state = 'TO_UPDATE';";

        List<TrackedEntityAttributeValue> valueList = trackedEntityAttributeValueListFromQuery(toPostQuery);

        Map<String, List<TrackedEntityAttributeValue>> valueMap = new HashMap<>();
        for (TrackedEntityAttributeValue value : valueList) {
            addTrackedEntityAttributeValueToMap(valueMap, value);
        }

        return valueMap;
    }

    @Override
    public List<TrackedEntityAttributeValue> queryByTrackedEntityInstance(String trackedEntityInstanceUid) {
        String selectByTrackedEntityInstanceQuery = new WhereClauseBuilder().appendKeyStringValue(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid).build();

        return selectWhere(selectByTrackedEntityInstanceQuery);
    }

    @Override
    public void deleteByInstanceAndNotInAttributes(@NonNull String trackedEntityInstanceUid,
                                                   @NonNull List<String> trackedEntityAttributeUids) {
        String deleteWhereQuery = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                        trackedEntityInstanceUid)
                .appendNotInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                        trackedEntityAttributeUids)
                .build();

        deleteWhere(deleteWhereQuery);
    }

    private List<TrackedEntityAttributeValue> trackedEntityAttributeValueListFromQuery(String query) {
        List<TrackedEntityAttributeValue> trackedEntityAttributeValueList = new ArrayList<>();
        Cursor cursor = databaseAdapter.query(query);
        addObjectsToCollection(cursor, trackedEntityAttributeValueList);
        return trackedEntityAttributeValueList;
    }

    private void addTrackedEntityAttributeValueToMap(Map<String, List<TrackedEntityAttributeValue>> valueMap,
                                    TrackedEntityAttributeValue trackedEntityAttributeValue) {
        if (valueMap.get(trackedEntityAttributeValue.trackedEntityInstance()) == null) {
            valueMap.put(trackedEntityAttributeValue.trackedEntityInstance(),
                    new ArrayList<>());
        }

        valueMap.get(trackedEntityAttributeValue.trackedEntityInstance()).add(trackedEntityAttributeValue);
    }

    public static TrackedEntityAttributeValueStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(
                TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(),
                TrackedEntityAttributeValueTableInfo.TABLE_INFO.columns());

        return new TrackedEntityAttributeValueStoreImpl(
                databaseAdapter,
                statementBuilder
        );
    }
}