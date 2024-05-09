/*
 *  Copyright (c) 2004-2023, University of Oslo
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
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableWithStyleStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("MagicNumber")
internal class ProgramStageStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : ProgramStageStore,
    IdentifiableObjectStoreImpl<ProgramStage>(
        databaseAdapter,
        ProgramStageTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> ProgramStage.create(cursor) },
    ) {
    companion object {
        private val BINDER: StatementBinder<ProgramStage> =
            object : IdentifiableWithStyleStatementBinder<ProgramStage>() {
                override fun bindToStatement(o: ProgramStage, w: StatementWrapper) {
                    super.bindToStatement(o, w)
                    w.bind(9, o.description())
                    w.bind(10, o.displayDescription())
                    w.bind(11, o.executionDateLabel())
                    w.bind(12, o.dueDateLabel())
                    w.bind(13, o.allowGenerateNextVisit())
                    w.bind(14, o.validCompleteOnly())
                    w.bind(15, o.reportDateToUse())
                    w.bind(16, o.openAfterEnrollment())
                    w.bind(17, o.repeatable())
                    w.bind(18, o.formType()!!.name)
                    w.bind(19, o.displayGenerateEventBox())
                    w.bind(20, o.generatedByEnrollmentDate())
                    w.bind(21, o.autoGenerateEvent())
                    w.bind(22, o.sortOrder())
                    w.bind(23, o.hideDueDate())
                    w.bind(24, o.blockEntryForm())
                    w.bind(25, o.minDaysFromStart())
                    w.bind(26, o.standardInterval())
                    w.bind(27, getUidOrNull(o.program()))
                    w.bind(28, o.periodType())
                    w.bind(29, o.access().data().write())
                    w.bind(30, o.remindCompleted())
                    w.bind(31, o.featureType())
                    w.bind(32, o.enableUserAssignment())
                    w.bind(33, o.validationStrategy())
                    w.bind(34, o.programStageLabel())
                    w.bind(35, o.eventLabel())
                }
            }
        val CHILD_PROJECTION = SingleParentChildProjection(
            ProgramStageTableInfo.TABLE_INFO,
            ProgramStageTableInfo.Columns.PROGRAM,
        )
    }
}
