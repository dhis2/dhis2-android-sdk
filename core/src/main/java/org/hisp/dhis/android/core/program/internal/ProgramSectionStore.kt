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
package org.hisp.dhis.android.core.program.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableWithStyleStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory.objectWithUidStore
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.program.ProgramSection
import org.hisp.dhis.android.core.program.ProgramSectionTableInfo

@Suppress("MagicNumber")
internal object ProgramSectionStore {

    private val BINDER: StatementBinder<ProgramSection> =
        object : IdentifiableWithStyleStatementBinder<ProgramSection>() {
            override fun bindToStatement(o: ProgramSection, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(9, o.description())
                w.bind(10, getUidOrNull(o.program()))
                w.bind(11, o.sortOrder())
                w.bind(12, o.formName())
                w.bind(13, o.renderType()?.desktop()?.type())
                w.bind(14, o.renderType()?.mobile()?.type())
            }
        }

    val CHILD_PROJECTION = SingleParentChildProjection(
        ProgramSectionTableInfo.TABLE_INFO, ProgramSectionTableInfo.Columns.PROGRAM
    )

    @JvmStatic
    fun create(databaseAdapter: DatabaseAdapter): IdentifiableObjectStore<ProgramSection> {
        return objectWithUidStore(
            databaseAdapter,
            ProgramSectionTableInfo.TABLE_INFO,
            BINDER
        ) { cursor: Cursor -> ProgramSection.create(cursor) }
    }
}
