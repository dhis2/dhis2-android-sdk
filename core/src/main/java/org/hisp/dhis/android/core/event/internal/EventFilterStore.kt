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
package org.hisp.dhis.android.core.event.internal

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DateFilterPeriodColumnAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory.objectWithUidStore
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.event.EventFilterTableInfo

@Suppress("MagicNumber")
internal object EventFilterStore {
    private val BINDER = object : IdentifiableStatementBinder<EventFilter>() {
        override fun bindToStatement(o: EventFilter, w: StatementWrapper) {
            super.bindToStatement(o, w)
            w.bind(7, o.program())
            w.bind(8, o.programStage())
            w.bind(9, o.description())
            w.bind(10, o.eventQueryCriteria()?.followUp())
            w.bind(11, o.eventQueryCriteria()?.organisationUnit())
            w.bind(12, o.eventQueryCriteria()?.ouMode())
            w.bind(13, o.eventQueryCriteria()?.assignedUserMode())
            w.bind(14, o.eventQueryCriteria()?.order())
            w.bind(15, StringListColumnAdapter.serialize(o.eventQueryCriteria()?.displayColumnOrder()))
            w.bind(16, StringListColumnAdapter.serialize(o.eventQueryCriteria()?.events()))
            w.bind(17, o.eventQueryCriteria()?.eventStatus())
            w.bind(18, DateFilterPeriodColumnAdapter.serialize(o.eventQueryCriteria()?.eventDate()))
            w.bind(19, DateFilterPeriodColumnAdapter.serialize(o.eventQueryCriteria()?.dueDate()))
            w.bind(20, DateFilterPeriodColumnAdapter.serialize(o.eventQueryCriteria()?.lastUpdatedDate()))
            w.bind(21, DateFilterPeriodColumnAdapter.serialize(o.eventQueryCriteria()?.completedDate()))
        }
    }

    @JvmStatic
    fun create(databaseAdapter: DatabaseAdapter): IdentifiableObjectStore<EventFilter> {
        return objectWithUidStore(
            databaseAdapter, EventFilterTableInfo.TABLE_INFO, BINDER
        ) { EventFilter.create(it) }
    }
}
