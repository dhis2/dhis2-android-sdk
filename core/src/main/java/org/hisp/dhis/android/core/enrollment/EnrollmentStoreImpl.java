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

package org.hisp.dhis.android.core.enrollment;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CoordinateHelper;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.IdentifiableObjectWithStateStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.SQLStatementWrapper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public class EnrollmentStoreImpl extends IdentifiableObjectWithStateStoreImpl<Enrollment> implements EnrollmentStore {

    private EnrollmentStoreImpl(DatabaseAdapter databaseAdapter,
                                SQLStatementWrapper statementWrapper,
                                SQLStatementBuilder builder,
                                StatementBinder<Enrollment> binder,
                                CursorModelFactory<Enrollment> modelFactory) {
        super(databaseAdapter, statementWrapper, builder, binder, modelFactory);
    }

    @Override
    public Map<String, List<Enrollment>> queryEnrollmentsToPost() {
        String enrollmentsToPostQuery = "SELECT Enrollment.* FROM " +
                "(Enrollment INNER JOIN TrackedEntityInstance " +
                "ON Enrollment.trackedEntityInstance = TrackedEntityInstance.uid) " +
                "WHERE TrackedEntityInstance.state = 'TO_POST' " +
                "OR TrackedEntityInstance.state = 'TO_UPDATE' " +
                "OR TrackedEntityInstance.state = 'TO_DELETE' " +
                "OR Enrollment.state = 'TO_POST' " +
                "OR Enrollment.state = 'TO_UPDATE' " +
                "OR Enrollment.state = 'TO_DELETE';";

        List<Enrollment> enrollmentList = enrollmentListFromQuery(enrollmentsToPostQuery);

        Map<String, List<Enrollment>> enrollmentMap = new HashMap<>();
        for (Enrollment enrollment : enrollmentList) {
            addEnrollmentToMap(enrollmentMap, enrollment);
        }

        return enrollmentMap;
    }

    private List<Enrollment> enrollmentListFromQuery(String query) {
        List<Enrollment> enrollmentList = new ArrayList<>();
        Cursor cursor = databaseAdapter.query(query);
        addObjectsToCollection(cursor, enrollmentList);
        return enrollmentList;
    }

    private void addEnrollmentToMap(Map<String, List<Enrollment>> enrollmentMap, Enrollment enrollment) {
        if (enrollmentMap.get(enrollment.trackedEntityInstance()) == null) {
            enrollmentMap.put(enrollment.trackedEntityInstance(), new ArrayList<Enrollment>());
        }

        enrollmentMap.get(enrollment.trackedEntityInstance()).add(enrollment);
    }

    private static final StatementBinder<Enrollment> BINDER = new StatementBinder<Enrollment>() {
        @Override
        public void bindToStatement(@NonNull Enrollment o, @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.uid());
            sqLiteBind(sqLiteStatement, 2, o.created());
            sqLiteBind(sqLiteStatement, 3, o.lastUpdated());
            sqLiteBind(sqLiteStatement, 4, o.createdAtClient());
            sqLiteBind(sqLiteStatement, 5, o.lastUpdatedAtClient());
            sqLiteBind(sqLiteStatement, 6, o.organisationUnit());
            sqLiteBind(sqLiteStatement, 7, o.program());
            sqLiteBind(sqLiteStatement, 8, o.enrollmentDate());
            sqLiteBind(sqLiteStatement, 9, o.incidentDate());
            sqLiteBind(sqLiteStatement, 10, o.followUp());
            sqLiteBind(sqLiteStatement, 11, o.status());
            sqLiteBind(sqLiteStatement, 12, o.trackedEntityInstance());
            sqLiteBind(sqLiteStatement, 13, CoordinateHelper.getLatitude(o.coordinate()));
            sqLiteBind(sqLiteStatement, 14, CoordinateHelper.getLongitude(o.coordinate()));
            sqLiteBind(sqLiteStatement, 15, o.state());
        }
    };

    private static final CursorModelFactory<Enrollment> FACTORY = new CursorModelFactory<Enrollment>() {
        @Override
        public Enrollment fromCursor(Cursor cursor) {
            return Enrollment.create(cursor);
        }
    };

    public static EnrollmentStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(
                EnrollmentTableInfo.TABLE_INFO.name(),
                EnrollmentTableInfo.TABLE_INFO.columns());
        SQLStatementWrapper statementWrapper = new SQLStatementWrapper(statementBuilder, databaseAdapter);

        return new EnrollmentStoreImpl(
                databaseAdapter,
                statementWrapper,
                statementBuilder,
                BINDER,
                FACTORY
        );
    }
}