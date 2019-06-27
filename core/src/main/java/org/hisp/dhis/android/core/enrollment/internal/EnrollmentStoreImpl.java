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

package org.hisp.dhis.android.core.enrollment.internal;

import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorModelFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.statementwrapper.internal.SQLStatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectWithStateStoreImpl;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection;
import org.hisp.dhis.android.core.arch.helpers.CoordinateHelper;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.sqLiteBind;

public final class EnrollmentStoreImpl
        extends IdentifiableObjectWithStateStoreImpl<Enrollment> implements EnrollmentStore {

    private static final StatementBinder<Enrollment> BINDER = (o, sqLiteStatement) -> {
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
    };


    static final SingleParentChildProjection CHILD_PROJECTION = new SingleParentChildProjection(
            EnrollmentTableInfo.TABLE_INFO, EnrollmentFields.TRACKED_ENTITY_INSTANCE);

    private EnrollmentStoreImpl(DatabaseAdapter databaseAdapter,
                                SQLStatementWrapper statementWrapper,
                                SQLStatementBuilderImpl builder,
                                StatementBinder<Enrollment> binder,
                                CursorModelFactory<Enrollment> modelFactory) {
        super(databaseAdapter, statementWrapper, builder, binder, modelFactory);
    }

    @Override
    public Map<String, List<Enrollment>> queryEnrollmentsToPost() {
        String enrollmentsToPostQuery = new WhereClauseBuilder()
                .appendInKeyStringValues(BaseDataModel.Columns.STATE, Arrays.asList(
                        State.TO_POST.name(),
                        State.TO_UPDATE.name(),
                        State.TO_DELETE.name())).build();

        List<Enrollment> enrollmentList = selectWhere(enrollmentsToPostQuery);

        Map<String, List<Enrollment>> enrollmentMap = new HashMap<>();
        for (Enrollment enrollment : enrollmentList) {
            addEnrollmentToMap(enrollmentMap, enrollment);
        }

        return enrollmentMap;
    }

    private void addEnrollmentToMap(Map<String, List<Enrollment>> enrollmentMap, Enrollment enrollment) {
        if (enrollmentMap.get(enrollment.trackedEntityInstance()) == null) {
            enrollmentMap.put(enrollment.trackedEntityInstance(), new ArrayList<>());
        }

        enrollmentMap.get(enrollment.trackedEntityInstance()).add(enrollment);
    }

    public static EnrollmentStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(
                EnrollmentTableInfo.TABLE_INFO.name(),
                EnrollmentTableInfo.TABLE_INFO.columns());
        SQLStatementWrapper statementWrapper = new SQLStatementWrapper(statementBuilder, databaseAdapter);

        return new EnrollmentStoreImpl(
                databaseAdapter,
                statementWrapper,
                statementBuilder,
                BINDER,
                Enrollment::create
        );
    }
}