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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DateFilterPeriodColumnAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringSetColumnAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.common.tableinfo.ItemFilterTableInfo
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter

@Suppress("MagicNumber")
internal class AttributeValueFilterStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : AttributeValueFilterStore,
    ObjectWithoutUidStoreImpl<AttributeValueFilter>(
        databaseAdapter,
        ItemFilterTableInfo.TABLE_INFO,
        BINDER,
        WHERE_UPDATE_BINDER,
        WHERE_DELETE_BINDER,
        { AttributeValueFilter.create(it) },
    ) {

    companion object {
        private val BINDER = StatementBinder { o: AttributeValueFilter, w: StatementWrapper ->
            w.bindNull(1)
            w.bindNull(2)
            w.bind(3, o.trackedEntityInstanceFilter())
            w.bind(4, o.attribute())
            w.bindNull(5)
            w.bind(6, o.sw())
            w.bind(7, o.ew())
            w.bind(8, o.le())
            w.bind(9, o.ge())
            w.bind(10, o.gt())
            w.bind(11, o.lt())
            w.bind(12, o.eq())
            w.bind(13, StringSetColumnAdapter.serialize(o.`in`()))
            w.bind(14, o.like())
            w.bind(15, DateFilterPeriodColumnAdapter.serialize(o.dateFilter()))
        }

        private val WHERE_UPDATE_BINDER = WhereStatementBinder { _: AttributeValueFilter, _ -> }
        private val WHERE_DELETE_BINDER = WhereStatementBinder { _: AttributeValueFilter, _ -> }

        val CHILD_PROJECTION = SingleParentChildProjection(
            ItemFilterTableInfo.TABLE_INFO,
            ItemFilterTableInfo.Columns.TRACKED_ENTITY_INSTANCE_FILTER,
        )
    }
}
