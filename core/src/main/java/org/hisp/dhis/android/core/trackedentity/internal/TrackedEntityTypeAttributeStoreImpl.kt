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
package org.hisp.dhis.android.core.trackedentity.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStoreImpl
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttributeTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityTypeAttributeStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : TrackedEntityTypeAttributeStore,
    LinkStoreImpl<TrackedEntityTypeAttribute>(
        databaseAdapter,
        TrackedEntityTypeAttributeTableInfo.TABLE_INFO,
        TrackedEntityTypeAttributeTableInfo.Columns.TRACKED_ENTITY_TYPE,
        BINDER,
        { cursor: Cursor -> TrackedEntityTypeAttribute.create(cursor) },
    ) {
    companion object {
        private val BINDER = StatementBinder { o: TrackedEntityTypeAttribute, w: StatementWrapper ->
            w.bind(1, o.trackedEntityType().uid())
            w.bind(2, o.trackedEntityAttribute()!!.uid())
            w.bind(3, o.displayInList())
            w.bind(4, o.mandatory())
            w.bind(5, o.searchable())
            w.bind(6, o.sortOrder())
        }
    }

    override fun getTrackedEntityTypeAttributeForTrackedEntityType(
        trackedEntityTypeUid: String,
    ): List<TrackedEntityTypeAttribute> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(
                TrackedEntityTypeAttributeTableInfo.Columns.TRACKED_ENTITY_TYPE,
                trackedEntityTypeUid,
            ).build()
        val selectStatement = builder.selectWhere(whereClause)
        return selectRawQuery(selectStatement)
    }
}
