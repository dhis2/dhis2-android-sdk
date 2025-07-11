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
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.dataset.Section
import org.hisp.dhis.android.core.dataset.SectionTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("MagicNumber")
internal class SectionStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : SectionStore,
    IdentifiableObjectStoreImpl<Section>(
        databaseAdapter,
        SectionTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> Section.create(cursor) },
    ) {
    companion object {
        private val BINDER: StatementBinder<Section> = object : IdentifiableStatementBinder<Section>() {
            override fun bindToStatement(o: Section, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(7, o.description())
                w.bind(8, o.sortOrder())
                w.bind(9, getUidOrNull(o.dataSet()))
                w.bind(10, o.showRowTotals())
                w.bind(11, o.showColumnTotals())
                w.bind(12, o.disableDataElementAutoGroup())
                w.bind(13, o.displayOptions()?.beforeSectionText())
                w.bind(14, o.displayOptions()?.afterSectionText())
                w.bind(15, o.displayOptions()?.pivotMode())
                w.bind(16, o.displayOptions()?.pivotedCategory())
            }
        }
    }
}
