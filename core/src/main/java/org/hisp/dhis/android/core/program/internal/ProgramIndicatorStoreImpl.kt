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
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("MagicNumber")
internal class ProgramIndicatorStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : ProgramIndicatorStore,
    IdentifiableObjectStoreImpl<ProgramIndicator>(
        databaseAdapter,
        ProgramIndicatorTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> ProgramIndicator.create(cursor) },
    ) {

    companion object {
        private val BINDER: StatementBinder<ProgramIndicator> = object : NameableStatementBinder<ProgramIndicator>() {
            override fun bindToStatement(
                o: ProgramIndicator,
                w: StatementWrapper,
            ) {
                super.bindToStatement(o, w)
                w.bind(11, o.displayInForm())
                w.bind(12, o.expression())
                w.bind(13, o.dimensionItem())
                w.bind(14, o.filter())
                w.bind(15, o.decimals())
                w.bind(16, o.aggregationType())
                w.bind(17, getUidOrNull(o.program()))
                w.bind(18, o.analyticsType())
            }
        }
    }

    override fun getForProgramStageSection(programStageSectionUid: String): List<ProgramIndicator> {
        val projection = LinkTableChildProjection(
            ProgramIndicatorTableInfo.TABLE_INFO,
            ProgramStageSectionProgramIndicatorLinkTableInfo.Columns.PROGRAM_STAGE_SECTION,
            ProgramStageSectionProgramIndicatorLinkTableInfo.Columns.PROGRAM_INDICATOR,
        )
        val sectionSqlBuilder = SQLStatementBuilderImpl(ProgramStageSectionProgramIndicatorLinkTableInfo.TABLE_INFO)
        val query = sectionSqlBuilder.selectChildrenWithLinkTable(
            projection,
            programStageSectionUid,
            null,
        )
        return selectRawQuery(query)
    }
}
