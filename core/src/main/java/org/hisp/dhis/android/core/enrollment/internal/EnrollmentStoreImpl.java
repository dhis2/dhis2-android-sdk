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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStoreImpl;
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.event.EventTableInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EnrollmentStoreImpl
        extends IdentifiableDeletableDataObjectStoreImpl<Enrollment> implements EnrollmentStore {

    private static final StatementBinder<Enrollment> BINDER = (o, w) -> {
        w.bind(1, o.uid());
        w.bind(2, o.created());
        w.bind(3, o.lastUpdated());
        w.bind(4, o.createdAtClient());
        w.bind(5, o.lastUpdatedAtClient());
        w.bind(6, o.organisationUnit());
        w.bind(7, o.program());
        w.bind(8, o.enrollmentDate());
        w.bind(9, o.incidentDate());
        w.bind(10, o.completedDate());
        w.bind(11, o.followUp());
        w.bind(12, o.status());
        w.bind(13, o.trackedEntityInstance());
        w.bind(14, o.geometry() == null ? null : o.geometry().type());
        w.bind(15, o.geometry() == null ? null : o.geometry().coordinates());
        w.bind(16, o.state());
        w.bind(17, o.deleted());
    };

    private EnrollmentStoreImpl(DatabaseAdapter databaseAdapter,
                                SQLStatementBuilderImpl builder,
                                StatementBinder<Enrollment> binder,
                                ObjectFactory<Enrollment> objectFactory) {
        super(databaseAdapter, builder, binder, objectFactory);
    }

    @Override
    public Map<String, List<Enrollment>> queryEnrollmentsToPost() {
        String enrollmentsToPostQuery = new WhereClauseBuilder()
                .appendInKeyStringValues(DataColumns.STATE,
                        EnumHelper.asStringList(State.uploadableStatesIncludingError())).build();

        List<Enrollment> enrollmentList = selectWhere(enrollmentsToPostQuery);

        Map<String, List<Enrollment>> enrollmentMap = new HashMap<>();
        for (Enrollment enrollment : enrollmentList) {
            addEnrollmentToMap(enrollmentMap, enrollment);
        }

        return enrollmentMap;
    }

    @Override
    public List<String> queryMissingRelationshipsUids() {
        String whereRelationshipsClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataColumns.STATE, State.RELATIONSHIP)
                .appendIsNullValue(EventTableInfo.Columns.ORGANISATION_UNIT)
                .build();

        return selectUidsWhere(whereRelationshipsClause);
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

        return new EnrollmentStoreImpl(
                databaseAdapter,
                statementBuilder,
                BINDER,
                Enrollment::create
        );
    }
}