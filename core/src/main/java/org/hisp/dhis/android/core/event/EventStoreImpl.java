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

package org.hisp.dhis.android.core.event;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.IdentifiableObjectWithStateStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.SQLStatementWrapper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.EventModel.Columns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public class EventStoreImpl extends IdentifiableObjectWithStateStoreImpl<Event> implements EventStore {

    private EventStoreImpl(DatabaseAdapter databaseAdapter,
                           SQLStatementWrapper statementWrapper,
                           SQLStatementBuilder builder,
                           StatementBinder<Event> binder,
                           CursorModelFactory<Event> modelFactory) {
        super(databaseAdapter, statementWrapper, builder, binder, modelFactory);
    }

    private static final String QUERY_SINGLE_EVENTS = "SELECT Event.* FROM Event WHERE Event.enrollment ISNULL";

    @Override
            public Map<String, List<  Event>> queryEventsAttachedToEnrollmentToPost() {
             String eventsAttachedToEnrollmentsQuery = "SELECT Event.* FROM " +
                     "(Event INNER JOIN Enrollment ON Event.enrollment = Enrollment.uid " +
            "  INNER JOIN TrackedEntityInstance ON Enrollment.trackedEntityInstance = TrackedEntityInstance.uid)" +
            "WHERE TrackedEntityInstance.state = 'TO_POST' OR TrackedEntityInstance.state = 'TO_UPDATE' " +
            "      OR Enrollment.state = 'TO_POST' OR Enrollment.state = 'TO_UPDATE' OREvent.state = 'TO_POST' " +
            "OR Event.state = 'TO_UPDATE' OR Event.state = 'TO_DELETE';";

        List<Event> eventList = eventListFromQuery(eventsAttachedToEnrollmentsQuery);

        Map<String, List<Event>> eventsMap = new HashMap<>();
        for (Event event : eventList) {
            addEventsToMap(eventsMap, event);
        }

        return eventsMap;
    }

    @Override
    public List<Event> querySingleEventsToPost() {
        String singleEventsToPostQuery = QUERY_SINGLE_EVENTS +
                " AND (Event.state = 'TO_POST' OR Event.state = 'TO_UPDATE')";
        return eventListFromQuery(singleEventsToPostQuery);
    }

    @Override
    public List<Event> querySingleEvents() {
        return eventListFromQuery(QUERY_SINGLE_EVENTS);
    }

    @Override
    public List<Event> queryOrderedForEnrollmentAndProgramStage(String enrollmentUid, String programStageUid) {
        String byEnrollmentAndProgramStageQuery = "SELECT Event.* FROM Event " +
                "WHERE Event.enrollment = '" + enrollmentUid + "' " +
                "AND Event.programStage = '" + programStageUid + "' " +
                "ORDER BY Event." + Columns.EVENT_DATE + ", Event." + Columns.LAST_UPDATED;

        return eventListFromQuery(byEnrollmentAndProgramStageQuery);
    }

    private List<Event> eventListFromQuery(String query) {
        List<Event> eventList = new ArrayList<>();
        Cursor cursor = databaseAdapter.query(query);
        addObjectsToCollection(cursor, eventList);
        return eventList;
    }

    private void addEventsToMap(Map<String, List<Event>> eventsMap, Event event) {
        if (eventsMap.get(event.enrollment()) == null) {
            eventsMap.put(event.enrollment(), new ArrayList<Event>());
        }

        eventsMap.get(event.enrollment()).add(event);
    }

    private static final StatementBinder<Event> BINDER = new StatementBinder<Event>() {
        @Override
        public void bindToStatement(@NonNull Event o, @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.uid());
            sqLiteBind(sqLiteStatement, 2, o.enrollment());
            sqLiteBind(sqLiteStatement, 3, o.created());
            sqLiteBind(sqLiteStatement, 4, o.lastUpdated());
            sqLiteBind(sqLiteStatement, 5, o.createdAtClient());
            sqLiteBind(sqLiteStatement, 6, o.lastUpdatedAtClient());
            sqLiteBind(sqLiteStatement, 7, o.status());
            sqLiteBind(sqLiteStatement, 8, o.latitude());
            sqLiteBind(sqLiteStatement, 9, o.longitude());
            sqLiteBind(sqLiteStatement, 10, o.program());
            sqLiteBind(sqLiteStatement, 11, o.programStage());
            sqLiteBind(sqLiteStatement, 12, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 13, o.eventDate());
            sqLiteBind(sqLiteStatement, 14, o.completedDate());
            sqLiteBind(sqLiteStatement, 15, o.dueDate());
            sqLiteBind(sqLiteStatement, 16, o.state());
            sqLiteBind(sqLiteStatement, 17, o.attributeOptionCombo());
            sqLiteBind(sqLiteStatement, 18, o.trackedEntityInstance());
        }
    };

    private static final CursorModelFactory<Event> FACTORY = new CursorModelFactory<Event>() {
        @Override
        public Event fromCursor(Cursor cursor) {
            return Event.create(cursor);
        }
    };

    public static EventStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(
                EventTableInfo.TABLE_INFO.name(),
                EventTableInfo.TABLE_INFO.columns());
        SQLStatementWrapper statementWrapper = new SQLStatementWrapper(statementBuilder, databaseAdapter);

        return new EventStoreImpl(
                databaseAdapter,
                statementWrapper,
                statementBuilder,
                BINDER,
                FACTORY
        );
    }
}