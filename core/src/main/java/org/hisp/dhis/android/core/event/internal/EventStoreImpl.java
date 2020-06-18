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

package org.hisp.dhis.android.core.event.internal;

import android.database.Cursor;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStoreImpl;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.event.EventTableInfo.Columns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventStoreImpl extends IdentifiableDeletableDataObjectStoreImpl<Event> implements EventStore {

    private static final String QUERY_SINGLE_EVENTS = "SELECT Event.* FROM Event WHERE Event.enrollment IS NULL";

    private static final StatementBinder<Event> BINDER = (o, w) -> {
        w.bind(1, o.uid());
        w.bind(2, o.enrollment());
        w.bind(3, o.created());
        w.bind(4, o.lastUpdated());
        w.bind(5, o.createdAtClient());
        w.bind(6, o.lastUpdatedAtClient());
        w.bind(7, o.status());
        w.bind(8, o.geometry() == null ? null : o.geometry().type());
        w.bind(9, o.geometry() == null ? null : o.geometry().coordinates());
        w.bind(10, o.program());
        w.bind(11, o.programStage());
        w.bind(12, o.organisationUnit());
        w.bind(13, o.eventDate());
        w.bind(14, o.completedDate());
        w.bind(15, o.dueDate());
        w.bind(16, o.state());
        w.bind(17, o.attributeOptionCombo());
        w.bind(18, o.deleted());
        w.bind(19, o.assignedUser());
    };

    private EventStoreImpl(DatabaseAdapter databaseAdapter,
                           SQLStatementBuilderImpl builder,
                           StatementBinder<Event> binder,
                           ObjectFactory<Event> objectFactory) {
        super(databaseAdapter, builder, binder, objectFactory);
    }

    @Override
    public Map<String, List<Event>> queryEventsAttachedToEnrollmentToPost() {
        String eventsAttachedToEnrollmentsQuery = new WhereClauseBuilder()
                .appendIsNotNullValue(Columns.ENROLLMENT)
                .appendInKeyStringValues(Columns.STATE, EnumHelper.asStringList(State.uploadableStates())).build();

        List<Event> eventList = selectWhere(eventsAttachedToEnrollmentsQuery);

        Map<String, List<Event>> eventsMap = new HashMap<>();
        for (Event event : eventList) {
            addEventsToMap(eventsMap, event);
        }

        return eventsMap;
    }

    @Override
    public List<Event> querySingleEventsToPost() {
        String states = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
                CollectionsHelper.withSingleQuotationMarksArray(EnumHelper.asStringList(State.uploadableStates())));
        String singleEventsToPostQuery = QUERY_SINGLE_EVENTS +
                " AND (Event.state IN (" + states + "))";
        return eventListFromQuery(singleEventsToPostQuery);
    }

    @Override
    public List<Event> querySingleEvents() {
        return eventListFromQuery(QUERY_SINGLE_EVENTS);
    }

    @Override
    public List<Event> queryOrderedForEnrollmentAndProgramStage(String enrollmentUid, String programStageUid,
                                                                Boolean includeDeleted) {
        WhereClauseBuilder whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(Columns.ENROLLMENT, enrollmentUid)
                .appendKeyStringValue(Columns.PROGRAM_STAGE, programStageUid);

        if (!includeDeleted) {
            whereClause.appendIsNullOrValue(Columns.DELETED, "0");
        }

        String query = "SELECT * FROM " + EventTableInfo.TABLE_INFO.name() + " " +
                "WHERE " + whereClause.build() +
                "ORDER BY " + Columns.EVENT_DATE + ", " + Columns.LAST_UPDATED;

        return eventListFromQuery(query);
    }

    @Override
    public Integer countEventsForEnrollment(String enrollmentUid, Boolean includeDeleted) {
        WhereClauseBuilder whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(Columns.ENROLLMENT, enrollmentUid);

        if (!includeDeleted) {
            whereClause.appendIsNullOrValue(Columns.DELETED, "0");
        }

        String query = "SELECT * FROM " + EventTableInfo.TABLE_INFO.name() + " " +
                "WHERE " + whereClause.build();

        List<Event> events = eventListFromQuery(query);
        return events.size();
    }

    @Override
    public int countTeisWhereEvents(String whereClause) {
        String whereStatement = whereClause == null ? "" : " WHERE " + whereClause;
        String query = "SELECT COUNT(DISTINCT a." + EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE + ") " +
                "FROM " + EnrollmentTableInfo.TABLE_INFO.name() + " a " +
                "INNER JOIN " +
                "(SELECT DISTINCT " + Columns.ENROLLMENT +
                    " FROM " + EventTableInfo.TABLE_INFO.name() + whereStatement + ") b " +
                "ON a." + IdentifiableColumns.UID + " = b." + Columns.ENROLLMENT;

        return processCount(databaseAdapter.rawQuery(query));
    }

    @Override
    public List<String> queryMissingRelationshipsUids() {
        String whereRelationshipsClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataColumns.STATE, State.RELATIONSHIP)
                .appendIsNullValue(EventTableInfo.Columns.ORGANISATION_UNIT)
                .build();

        return selectUidsWhere(whereRelationshipsClause);
    }

    private List<Event> eventListFromQuery(String query) {
        List<Event> eventList = new ArrayList<>();
        Cursor cursor = databaseAdapter.rawQuery(query);
        addObjectsToCollection(cursor, eventList);
        return eventList;
    }

    private void addEventsToMap(Map<String, List<Event>> eventsMap, Event event) {
        if (eventsMap.get(event.enrollment()) == null) {
            eventsMap.put(event.enrollment(), new ArrayList<>());
        }

        eventsMap.get(event.enrollment()).add(event);
    }

    public static EventStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(
                EventTableInfo.TABLE_INFO.name(),
                EventTableInfo.TABLE_INFO.columns());

        return new EventStoreImpl(
                databaseAdapter,
                statementBuilder,
                BINDER,
                Event::create
        );
    }
}