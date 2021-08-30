/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.program.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundaryTableInfo
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo

@Suppress("MagicNumber")
internal class AnalyticsPeriodBoundaryStore private constructor(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilderImpl,
    binder: StatementBinder<AnalyticsPeriodBoundary>,
    whereUpdateBinder: WhereStatementBinder<AnalyticsPeriodBoundary>,
    whereDeleteBinder: WhereStatementBinder<AnalyticsPeriodBoundary>
) : ObjectWithoutUidStoreImpl<AnalyticsPeriodBoundary>(
    databaseAdapter,
    builder,
    binder,
    whereUpdateBinder,
    whereDeleteBinder,
    { cursor: Cursor -> AnalyticsPeriodBoundary.create(cursor) }) {
    companion object {
        val CHILD_PROJECTION = SingleParentChildProjection(
            AnalyticsPeriodBoundaryTableInfo.TABLE_INFO, AnalyticsPeriodBoundaryTableInfo.Columns.PROGRAM_INDICATOR
        )

        private val BINDER = StatementBinder { o: AnalyticsPeriodBoundary, w: StatementWrapper ->
            w.bind(1, o.programIndicator())
            w.bind(2, o.boundaryTarget())
            w.bind(3, o.analyticsPeriodBoundaryType())
            w.bind(4, o.offsetPeriods())
            w.bind(5, o.offsetPeriodType())
        }
        private val WHERE_UPDATE_BINDER = WhereStatementBinder { o: AnalyticsPeriodBoundary, w: StatementWrapper ->
            w.bind(6, o.programIndicator())
            w.bind(7, o.boundaryTarget())
            w.bind(8, o.analyticsPeriodBoundaryType())
            w.bind(9, o.offsetPeriodType())
        }
        private val WHERE_DELETE_BINDER = WhereStatementBinder { o: AnalyticsPeriodBoundary, w: StatementWrapper ->
            w.bind(1, o.programIndicator())
            w.bind(2, o.boundaryTarget())
            w.bind(3, o.analyticsPeriodBoundaryType())
            w.bind(4, o.offsetPeriodType())
        }

        @JvmStatic
        fun create(databaseAdapter: DatabaseAdapter): ObjectWithoutUidStore<AnalyticsPeriodBoundary> {
            val statementBuilder = SQLStatementBuilderImpl(AnalyticsPeriodBoundaryTableInfo.TABLE_INFO)
            return AnalyticsPeriodBoundaryStore(
                databaseAdapter,
                statementBuilder,
                BINDER,
                WHERE_UPDATE_BINDER,
                WHERE_DELETE_BINDER
            )
        }
    }
}
