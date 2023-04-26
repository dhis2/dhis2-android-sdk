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
package org.hisp.dhis.android.core.programstageworkinglist.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DateFilterPeriodColumnAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory.objectWithUidStore
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList

@Suppress("MagicNumber")
internal object ProgramStageWorkingListStore {
    private val BINDER: StatementBinder<ProgramStageWorkingList> =
        object : IdentifiableStatementBinder<ProgramStageWorkingList>() {
            override fun bindToStatement(o: ProgramStageWorkingList, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(7, o.description())
                w.bind(8, o.program()?.uid())
                w.bind(9, o.programStage()?.uid())
                w.bind(10, o.programStageQueryCriteria()?.eventStatus())
                w.bind(
                    11,
                    DateFilterPeriodColumnAdapter.serialize(o.programStageQueryCriteria()?.eventCreatedAt())
                )
                w.bind(12, DateFilterPeriodColumnAdapter.serialize(o.programStageQueryCriteria()?.eventOccurredAt()))
                w.bind(13, DateFilterPeriodColumnAdapter.serialize(o.programStageQueryCriteria()?.eventScheduledAt()))
                w.bind(14, o.programStageQueryCriteria()?.enrollmentStatus())
                w.bind(15, DateFilterPeriodColumnAdapter.serialize(o.programStageQueryCriteria()?.enrolledAt()))
                w.bind(
                    16,
                    DateFilterPeriodColumnAdapter.serialize(o.programStageQueryCriteria()?.enrollmentOccurredAt())
                )
                w.bind(17, o.programStageQueryCriteria()?.order())
                w.bind(18, StringListColumnAdapter.serialize(o.programStageQueryCriteria()?.displayColumnOrder()))
                w.bind(19, o.programStageQueryCriteria()?.orgUnit())
                w.bind(20, o.programStageQueryCriteria()?.ouMode())
                w.bind(21, o.programStageQueryCriteria()?.assignedUserMode())
            }
        }

    @JvmStatic
    fun create(databaseAdapter: DatabaseAdapter): IdentifiableObjectStore<ProgramStageWorkingList> {
        return objectWithUidStore(
            databaseAdapter,
            ProgramStageWorkingListTableInfo.TABLE_INFO,
            BINDER
        ) { cursor: Cursor -> ProgramStageWorkingList.create(cursor) }
    }
}
