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

package org.hisp.dhis.android.core.period.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodTableInfo;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Date;

public final class PeriodStoreImpl extends ObjectWithoutUidStoreImpl<Period> implements PeriodStore {

    private static final StatementBinder<Period> BINDER = (o, w) -> {
        w.bind(1, o.periodId());
        w.bind(2, o.periodType());
        w.bind(3, o.startDate());
        w.bind(4, o.endDate());
    };

    private static final WhereStatementBinder<Period> WHERE_UPDATE_BINDER
            = (o, w) -> w.bind(5, o.periodId());

    private static final WhereStatementBinder<Period> WHERE_DELETE_BINDER
            = (o, w) -> w.bind(1, o.periodId());

    private PeriodStoreImpl(DatabaseAdapter databaseAdapter,
                            SQLStatementBuilderImpl builder) {
        super(databaseAdapter, builder, BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER, Period::create);
    }

    @Override
    public Period selectByPeriodId(String periodId) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(PeriodTableInfo.Columns.PERIOD_ID, periodId)
                .build();

        return selectOneWhere(whereClause);
    }

    @Override
    public Period selectPeriodByTypeAndDate(PeriodType periodType, Date date) {

        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(PeriodTableInfo.Columns.PERIOD_TYPE, periodType)
                .appendKeyLessThanOrEqStringValue(PeriodTableInfo.Columns.START_DATE,
                        BaseIdentifiableObject.DATE_FORMAT.format(date))
                .appendKeyGreaterOrEqStringValue(PeriodTableInfo.Columns.END_DATE,
                        BaseIdentifiableObject.DATE_FORMAT.format(date))
                .build();

        return selectOneWhere(whereClause);
    }

    @Override
    public Date getOldestPeriodStartDate() {

        Period period = selectOneOrderedBy(PeriodTableInfo.Columns.START_DATE, SQLOrderType.ASC);

        return period.startDate();
    }

    public static PeriodStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(
                PeriodTableInfo.TABLE_INFO.name(), PeriodTableInfo.TABLE_INFO.columns());

        return new PeriodStoreImpl(databaseAdapter, statementBuilder);
    }
}