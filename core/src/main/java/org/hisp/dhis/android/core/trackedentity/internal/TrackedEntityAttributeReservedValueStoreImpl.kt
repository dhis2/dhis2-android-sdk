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
import java.util.Date
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo.Columns

@Suppress("MagicNumber")
internal class TrackedEntityAttributeReservedValueStoreImpl(
    databaseAdapter: DatabaseAdapter
) : TrackedEntityAttributeReservedValueStore,
    ObjectWithoutUidStoreImpl<TrackedEntityAttributeReservedValue>(
        databaseAdapter,
        TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO,
        BINDER,
        WHERE_UPDATE_BINDER,
        WHERE_DELETE_BINDER,
        { cursor: Cursor -> TrackedEntityAttributeReservedValue.create(cursor) }
    ) {

    override fun deleteExpired(serverDate: Date) {
        val serverDateStr = "date('${DateUtils.DATE_FORMAT.format(serverDate)}')"
        super.deleteWhere(
            "${Columns.EXPIRY_DATE} < $serverDateStr OR " +
                "( ${Columns.TEMPORAL_VALIDITY_DATE} < $serverDateStr AND " +
                "${Columns.TEMPORAL_VALIDITY_DATE} IS NOT NULL );"
        )
    }

    override fun deleteIfOutdatedPattern(ownerUid: String, pattern: String) {
        val deleteWhereClause = WhereClauseBuilder()
            .appendKeyStringValue(Columns.OWNER_UID, ownerUid)
            .appendNotKeyStringValue(Columns.PATTERN, pattern)
            .build()
        super.deleteWhere(deleteWhereClause)
    }

    override fun popOne(ownerUid: String, organisationUnitUid: String?): TrackedEntityAttributeReservedValue? {
        return popOneWhere(where(ownerUid, organisationUnitUid, null))
    }

    override fun count(ownerUid: String, organisationUnitUid: String?, pattern: String?): Int {
        return countWhere(where(ownerUid, organisationUnitUid, pattern))
    }

    private fun where(
        ownerUid: String,
        organisationUnit: String?,
        pattern: String?
    ): String {
        val builder = WhereClauseBuilder()
            .appendKeyStringValue(Columns.OWNER_UID, ownerUid)
        if (organisationUnit != null) {
            builder.appendKeyStringValue(Columns.ORGANISATION_UNIT, organisationUnit)
        }
        if (pattern != null) {
            builder.appendKeyStringValue(Columns.PATTERN, pattern)
        }
        return builder.build()
    }

    companion object {
        private val BINDER = StatementBinder { o: TrackedEntityAttributeReservedValue, w: StatementWrapper ->
            w.bind(1, o.ownerObject())
            w.bind(2, o.ownerUid())
            w.bind(3, o.key())
            w.bind(4, o.value())
            w.bind(5, o.created())
            w.bind(6, o.expiryDate())
            w.bind(7, o.organisationUnit())
            w.bind(8, o.temporalValidityDate())
            w.bind(9, o.pattern())
        }
        private val WHERE_UPDATE_BINDER =
            WhereStatementBinder { o: TrackedEntityAttributeReservedValue, w: StatementWrapper ->
                w.bind(10, o.ownerUid())
                w.bind(11, o.value())
                w.bind(12, o.organisationUnit())
            }
        private val WHERE_DELETE_BINDER =
            WhereStatementBinder { o: TrackedEntityAttributeReservedValue, w: StatementWrapper ->
                w.bind(1, o.ownerUid())
                w.bind(2, o.value())
                w.bind(3, o.organisationUnit())
            }
    }
}
