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
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableWithStyleStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramTableInfo
import org.hisp.dhis.android.core.program.ProgramType
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("MagicNumber")
internal class ProgramStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : ProgramStore,
    IdentifiableObjectStoreImpl<Program>(
        databaseAdapter,
        ProgramTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> Program.create(cursor) },
    ) {

    override fun getUidsByProgramType(programType: ProgramType): List<String> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(ProgramTableInfo.Columns.PROGRAM_TYPE, programType.toString()).build()
        return selectStringColumnsWhereClause(IdentifiableColumns.UID, whereClause)
    }

    companion object {
        private val BINDER: StatementBinder<Program> = object : NameableWithStyleStatementBinder<Program>() {
            override fun bindToStatement(o: Program, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(13, o.version())
                w.bind(14, o.onlyEnrollOnce())
                w.bind(15, o.enrollmentDateLabel())
                w.bind(16, o.displayIncidentDate())
                w.bind(17, o.incidentDateLabel())
                w.bind(18, o.registration())
                w.bind(19, o.selectEnrollmentDatesInFuture())
                w.bind(20, o.dataEntryMethod())
                w.bind(21, o.ignoreOverdueEvents())
                w.bind(22, o.selectIncidentDatesInFuture())
                w.bind(23, o.useFirstStageDuringRegistration())
                w.bind(24, o.displayFrontPageList())
                w.bind(25, o.programType())
                w.bind(26, getUidOrNull(o.relatedProgram()))
                w.bind(27, getUidOrNull(o.trackedEntityType()))
                w.bind(28, o.categoryComboUid())
                w.bind(29, o.access().data().write())
                w.bind(30, o.expiryDays())
                w.bind(31, o.completeEventsExpiryDays())
                w.bind(32, o.expiryPeriodType())
                w.bind(33, o.minAttributesRequiredToSearch())
                w.bind(34, o.maxTeiCountToReturn())
                w.bind(35, o.featureType())
                w.bind(36, o.accessLevel())
                w.bind(37, o.enrollmentLabel())
                w.bind(38, o.followUpLabel())
                w.bind(39, o.orgUnitLabel())
                w.bind(40, o.relationshipLabel())
                w.bind(41, o.noteLabel())
                w.bind(42, o.trackedEntityAttributeLabel())
                w.bind(43, o.programStageLabel())
                w.bind(44, o.eventLabel())
            }
        }
    }
}
