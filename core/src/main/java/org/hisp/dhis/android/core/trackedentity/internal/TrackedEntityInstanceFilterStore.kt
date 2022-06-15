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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DateFilterPeriodColumnAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableWithStyleStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory.objectWithUidStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterTableInfo

@Suppress("MagicNumber")
internal object TrackedEntityInstanceFilterStore {
    private val BINDER = object : IdentifiableWithStyleStatementBinder<TrackedEntityInstanceFilter>() {
        override fun bindToStatement(o: TrackedEntityInstanceFilter, w: StatementWrapper) {
            super.bindToStatement(o, w)
            w.bind(9, getUidOrNull(o.program()))
            w.bind(10, o.description())
            w.bind(11, o.sortOrder())
            w.bind(12, o.entityQueryCriteria().enrollmentStatus())
            w.bind(13, o.entityQueryCriteria().followUp())
            w.bind(14, o.entityQueryCriteria().organisationUnit())
            w.bind(15, o.entityQueryCriteria().ouMode())
            w.bind(16, o.entityQueryCriteria().assignedUserMode())
            w.bind(17, o.entityQueryCriteria().order())
            w.bind(18, StringListColumnAdapter.serialize(o.entityQueryCriteria().displayColumnOrder()))
            w.bind(19, o.entityQueryCriteria().eventStatus())
            w.bind(20, DateFilterPeriodColumnAdapter.serialize(o.entityQueryCriteria().eventDate()))
            w.bind(21, DateFilterPeriodColumnAdapter.serialize(o.entityQueryCriteria().lastUpdatedDate()))
            w.bind(22, o.entityQueryCriteria().programStage())
            w.bind(23, StringListColumnAdapter.serialize(o.entityQueryCriteria().trackedEntityInstances()))
            w.bind(24, DateFilterPeriodColumnAdapter.serialize(o.entityQueryCriteria().enrollmentIncidentDate()))
            w.bind(25, DateFilterPeriodColumnAdapter.serialize(o.entityQueryCriteria().enrollmentCreatedDate()))
            w.bind(26, o.entityQueryCriteria().trackedEntityType())
        }
    }

    @JvmStatic
    fun create(databaseAdapter: DatabaseAdapter): IdentifiableObjectStore<TrackedEntityInstanceFilter> {
        return objectWithUidStore(
            databaseAdapter, TrackedEntityInstanceFilterTableInfo.TABLE_INFO, BINDER
        ) { TrackedEntityInstanceFilter.create(it) }
    }
}
