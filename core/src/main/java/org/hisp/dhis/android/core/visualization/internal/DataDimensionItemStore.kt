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
package org.hisp.dhis.android.core.visualization.internal

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory
import org.hisp.dhis.android.core.arch.helpers.UidsHelper
import org.hisp.dhis.android.core.visualization.DataDimensionItem
import org.hisp.dhis.android.core.visualization.DataDimensionItemTableInfo

@Suppress("MagicNumber")
internal object DataDimensionItemStore {
    private val BINDER = StatementBinder { o: DataDimensionItem, w: StatementWrapper ->
        w.bind(1, o.visualization())
        w.bind(2, o.dataDimensionItemType())
        w.bind(3, UidsHelper.getUidOrNull(o.indicator()))
        w.bind(4, UidsHelper.getUidOrNull(o.dataElement()))
        w.bind(5, UidsHelper.getUidOrNull(o.dataElementOperand()))
        w.bind(6, UidsHelper.getUidOrNull(o.reportingRate()))
        w.bind(7, UidsHelper.getUidOrNull(o.programIndicator()))
        w.bind(8, UidsHelper.getUidOrNull(o.programDataElement()))
        w.bind(9, UidsHelper.getUidOrNull(o.programAttribute()))
        w.bind(10, UidsHelper.getUidOrNull(o.validationRule()))
    }

    fun create(databaseAdapter: DatabaseAdapter): LinkStore<DataDimensionItem> {
        return StoreFactory.linkStore(
            databaseAdapter,
            DataDimensionItemTableInfo.TABLE_INFO,
            DataDimensionItemTableInfo.Columns.VISUALIZATION,
            BINDER
        ) { DataDimensionItem.create(it) }
    }
}
