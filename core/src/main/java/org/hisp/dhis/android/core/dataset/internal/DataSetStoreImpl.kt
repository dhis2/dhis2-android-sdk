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
package org.hisp.dhis.android.core.dataset.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableWithStyleStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetTableInfo
import org.koin.core.annotation.Singleton

@Suppress("MagicNumber")
internal class DataSetStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : DataSetStore,
    IdentifiableObjectStoreImpl<DataSet>(
        databaseAdapter,
        DataSetTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> DataSet.create(cursor) },
    ) {

    companion object {
        private val BINDER: StatementBinder<DataSet> = object : NameableWithStyleStatementBinder<DataSet>() {
            override fun bindToStatement(o: DataSet, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(13, o.periodType())
                w.bind(14, getUidOrNull(o.categoryCombo()))
                w.bind(15, o.mobile())
                w.bind(16, o.version())
                w.bind(17, o.expiryDays())
                w.bind(18, o.timelyDays())
                w.bind(19, o.notifyCompletingUser())
                w.bind(20, o.openFuturePeriods())
                w.bind(21, o.fieldCombinationRequired())
                w.bind(22, o.validCompleteOnly())
                w.bind(23, o.noValueRequiresComment())
                w.bind(24, o.skipOffline())
                w.bind(25, o.dataElementDecoration())
                w.bind(26, o.renderAsTabs())
                w.bind(27, o.renderHorizontally())
                w.bind(28, o.access().data().write())
                w.bind(29, getUidOrNull(o.workflow()))
                w.bind(30, o.displayOptions()?.customText()?.header())
                w.bind(31, o.displayOptions()?.customText()?.subHeader())
                w.bind(32, o.displayOptions()?.customText()?.align()?.name)
                w.bind(33, o.displayOptions()?.tabsDirection()?.name)
            }
        }
    }
}
