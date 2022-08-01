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

import android.database.Cursor;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public final class TrackedEntityDataValueStoreImpl extends ObjectWithoutUidStoreImpl<TrackedEntityDataValue>
        implements TrackedEntityDataValueStore {

    private static final StatementBinder<TrackedEntityDataValue> BINDER = (o, w) -> {
        w.bind(1, o.event());
        w.bind(2, o.created());
        w.bind(3, o.lastUpdated());
        w.bind(4, o.dataElement());
        w.bind(5, o.storedBy());
        w.bind(6, o.value());
        w.bind(7, o.providedElsewhere());
    };

    private static final WhereStatementBinder<TrackedEntityDataValue> WHERE_UPDATE_BINDER = (o, w) -> {
        w.bind(8, o.event());
        w.bind(9, o.dataElement());
    };

    private static final WhereStatementBinder<TrackedEntityDataValue> WHERE_DELETE_BINDER = (o, w) -> {
        w.bind(1, o.event());
        w.bind(2, o.dataElement());
    };

    static final SingleParentChildProjection CHILD_PROJECTION = new SingleParentChildProjection(
            TrackedEntityDataValueTableInfo.TABLE_INFO, TrackedEntityDataValueTableInfo.Columns.EVENT);

    private TrackedEntityDataValueStoreImpl(DatabaseAdapter databaseAdapter,
                                            SQLStatementBuilderImpl builder,
                                            StatementBinder<TrackedEntityDataValue> binder,
                                            WhereStatementBinder<TrackedEntityDataValue> whereUpdateBinder,
                                            WhereStatementBinder<TrackedEntityDataValue> whereDeleteBinder,
                                            Function1<Cursor, TrackedEntityDataValue> objectFactory) {
        super(databaseAdapter, builder, binder, whereUpdateBinder, whereDeleteBinder, objectFactory);
    }

    @Override
    public boolean deleteByEventAndNotInDataElements(@NonNull String eventUid,
                                                     @NonNull List<String> dataElementUids) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
                .appendNotInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, dataElementUids)
                .build();

        return deleteWhere(whereClause);
    }

    @Override
    public boolean deleteByEvent(@NonNull String eventUid) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
                .build();

        return deleteWhere(whereClause);
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
                " WHERE Event.enrollment ISNULL " +
                "AND " + eventInUploadableState() + ";";

        return queryTrackedEntityDataValues(queryStatement);
    }

    @Override
    public void removeDeletedDataValuesByEvent(@NonNull String eventUid) {
        String deleteWhereQuery = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
                .appendIsNullValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE)
                .build();

        deleteWhere(deleteWhereQuery);
    }

    private String eventInUploadableState() {
        String states = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
                CollectionsHelper.withSingleQuotationMarksArray(
                        EnumHelper.asStringList(State.uploadableStatesIncludingError())));
        return "(Event." + EventTableInfo.Columns.AGGREGATED_SYNC_STATE + " IN (" + states + "))";
    }

    @Override
    public Map<String, List<TrackedEntityDataValue>> queryTrackerTrackedEntityDataValues() {

        String queryStatement = "SELECT TrackedEntityDataValue.* " +
                " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid) " +
                " WHERE Event.enrollment IS NOT NULL " +
                "AND " + eventInUploadableState() + ";";

        return queryTrackedEntityDataValues(queryStatement);
    }

    @Override
    public Map<String, List<TrackedEntityDataValue>> queryByUploadableEvents() {

        String queryStatement = "SELECT TrackedEntityDataValue.* " +
                " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid) " +
                " WHERE " + eventInUploadableState() + ";";

        return queryTrackedEntityDataValues(queryStatement);
    }

    private Map<String, List<TrackedEntityDataValue>> queryTrackedEntityDataValues(String queryStatement) {

        List<TrackedEntityDataValue> dataValueList = new ArrayList<>();
        Cursor cursor = getDatabaseAdapter().rawQuery(queryStatement);
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
        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(
                TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
                TrackedEntityDataValueTableInfo.TABLE_INFO.columns());

        return new TrackedEntityDataValueStoreImpl(databaseAdapter, statementBuilder, BINDER, WHERE_UPDATE_BINDER,
                WHERE_DELETE_BINDER, TrackedEntityDataValue::create
        );
    }
}