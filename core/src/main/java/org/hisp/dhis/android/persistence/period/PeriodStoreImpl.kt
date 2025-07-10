/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.persistence.period

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.PeriodStore
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.ObjectWithoutUidStoreImpl
import org.koin.core.annotation.Singleton
import java.util.Date

@Singleton
internal class PeriodStoreImpl(
    private val databaseAdapter: DatabaseAdapter
) : PeriodStore, ObjectWithoutUidStoreImpl<Period, PeriodDB>(
    { databaseAdapter.getCurrentDatabase()?.periodDao()!! },
    Period::toDB,
    SQLStatementBuilderImpl(PeriodTableInfo.TABLE_INFO),
) {
    override suspend fun selectByPeriodId(periodId: String?): Period? {
        return periodId?.let {
            val whereClause = WhereClauseBuilder()
                .appendKeyStringValue(PeriodTableInfo.Columns.PERIOD_ID, it)
                .build()
            selectOneWhere(whereClause)
        }
    }

    override suspend fun selectPeriodByTypeAndDate(periodType: PeriodType, date: Date): Period? {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(PeriodTableInfo.Columns.PERIOD_TYPE, periodType)
            .appendKeyLessThanOrEqStringValue(
                PeriodTableInfo.Columns.START_DATE,
                BaseIdentifiableObject.DATE_FORMAT.format(date),
            )
            .appendKeyGreaterOrEqStringValue(
                PeriodTableInfo.Columns.END_DATE,
                BaseIdentifiableObject.DATE_FORMAT.format(date),
            )
            .build()
        return selectOneWhere(whereClause)
    }

    override suspend fun oldestPeriodStartDate(): Date? {
        val period = selectOneOrderedBy(PeriodTableInfo.Columns.START_DATE, SQLOrderType.ASC)
        return period?.startDate()
    }
}
