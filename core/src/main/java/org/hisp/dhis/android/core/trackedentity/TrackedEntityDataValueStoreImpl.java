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

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class TrackedEntityDataValueStoreImpl extends ObjectWithoutUidStoreImpl<TrackedEntityDataValue>
        implements TrackedEntityDataValueStore {

    private static final StatementBinder<TrackedEntityDataValue> BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.event());
        sqLiteBind(sqLiteStatement, 2, o.created());
        sqLiteBind(sqLiteStatement, 3, o.lastUpdated());
        sqLiteBind(sqLiteStatement, 4, o.dataElement());
        sqLiteBind(sqLiteStatement, 5, o.storedBy());
        sqLiteBind(sqLiteStatement, 6, o.value());
        sqLiteBind(sqLiteStatement, 7, o.providedElsewhere());
    };

    private static final WhereStatementBinder<TrackedEntityDataValue> WHERE_UPDATE_BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 8, o.event());
        sqLiteBind(sqLiteStatement, 9, o.dataElement());
    };

    private static final WhereStatementBinder<TrackedEntityDataValue> WHERE_DELETE_BINDER
            = (o, sqLiteStatement) -> {
        sqLiteBind(sqLiteStatement, 1, o.event());
        sqLiteBind(sqLiteStatement, 2, o.dataElement());
    };

    static final SingleParentChildProjection CHILD_PROJECTION = new SingleParentChildProjection(
            TrackedEntityDataValueTableInfo.TABLE_INFO, TrackedEntityDataValueTableInfo.Columns.EVENT);

    private TrackedEntityDataValueStoreImpl(DatabaseAdapter databaseAdapter,
                                            SQLStatementBuilder builder,
                                            StatementBinder<TrackedEntityDataValue> binder,
                                            WhereStatementBinder<TrackedEntityDataValue> whereUpdateBinder,
                                            WhereStatementBinder<TrackedEntityDataValue> whereDeleteBinder,
                                            CursorModelFactory<TrackedEntityDataValue> modelFactory) {
        super(databaseAdapter, builder, binder, whereUpdateBinder, whereDeleteBinder, modelFactory);
    }

    @Override
    public int deleteByEventAndNotInDataElements(@NonNull String eventUid,
                                                 @NonNull List<String> dataElementUids) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
                .appendNotInKeyStringValues(TrackedEntityDataValueFields.DATA_ELEMENT, dataElementUids)
                .build();

        return databaseAdapter.delete(TrackedEntityDataValueTableInfo.TABLE_INFO.name(), whereClause, null);
    }

    @Override
    public List<TrackedEntityDataValue> queryTrackedEntityDataValuesByEventUid(@NonNull String eventUid) {
        WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid);

        return selectWhere(whereClauseBuilder.build());
    }

    @Override
    public Map<String, List<TrackedEntityDataValue>> querySingleEventsTrackedEntityDataValues() {

        String queryStatement = "SELECT TrackedEntityDataValue.* " +
                " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid)" +
                " WHERE Event.enrollment ISNULL AND (Event.state = 'TO_POST' OR Event.state = 'TO_UPDATE');";

        return queryTrackedEntityDataValues(queryStatement);
    }

    @Override
    public Map<String, List<TrackedEntityDataValue>> queryTrackerTrackedEntityDataValues() {

        String queryStatement = "SELECT TrackedEntityDataValue.* " +
                " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid) " +
                " WHERE Event.enrollment IS NOT NULL AND (Event.state = 'TO_POST' OR Event.state = 'TO_UPDATE');";

        return queryTrackedEntityDataValues(queryStatement);
    }

    private Map<String, List<TrackedEntityDataValue>> queryTrackedEntityDataValues(String queryStatement) {

        List<TrackedEntityDataValue> dataValueList = new ArrayList<>();
        Cursor cursor = databaseAdapter.query(queryStatement);
        addObjectsToCollection(cursor, dataValueList);

        Map<String, List<TrackedEntityDataValue>> dataValuesMap = new HashMap<>();
        for (TrackedEntityDataValue dataValue : dataValueList) {
            addDataValuesToMap(dataValuesMap, dataValue);
        }

        return dataValuesMap;
    }

    private void addDataValuesToMap(Map<String, List<TrackedEntityDataValue>> dataValuesMap,
                                    TrackedEntityDataValue dataValue) {
        if (dataValuesMap.get(dataValue.event()) == null) {
            dataValuesMap.put(dataValue.event(), new ArrayList<>());
        }

        dataValuesMap.get(dataValue.event()).add(dataValue);
    }

    public static TrackedEntityDataValueStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(
                TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
                TrackedEntityDataValueTableInfo.TABLE_INFO.columns());

        return new TrackedEntityDataValueStoreImpl(databaseAdapter, statementBuilder, BINDER, WHERE_UPDATE_BINDER,
                WHERE_DELETE_BINDER, TrackedEntityDataValue::create
        );
    }
}