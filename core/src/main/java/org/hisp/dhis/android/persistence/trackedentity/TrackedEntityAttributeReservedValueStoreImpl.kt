/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.persistence.trackedentity

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueStore
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeReservedValueTableInfo.Columns
import org.koin.core.annotation.Singleton
import java.util.Date

@Singleton
internal class TrackedEntityAttributeReservedValueStoreImpl(
    val dao: TrackedEntityAttributeReservedValueDao,
) : TrackedEntityAttributeReservedValueStore,
    ObjectWithoutUidStoreImpl<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueDB>(
        dao,
        TrackedEntityAttributeReservedValue::toDB,
        SQLStatementBuilderImpl(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO),
    ) {

    override suspend fun deleteExpired(serverDate: Date) {
        val serverDateStr = "date('${DateUtils.DATE_FORMAT.format(serverDate)}')"
        super.deleteWhere(
            "${Columns.EXPIRY_DATE} < $serverDateStr OR " +
                "( ${Columns.TEMPORAL_VALIDITY_DATE} < $serverDateStr AND " +
                "${Columns.TEMPORAL_VALIDITY_DATE} IS NOT NULL );",
        )
    }

    override suspend fun deleteIfOutdatedPattern(ownerUid: String, pattern: String) {
        val deleteWhereClause = WhereClauseBuilder()
            .appendKeyStringValue(Columns.OWNER_UID, ownerUid)
            .appendNotKeyStringValue(Columns.PATTERN, pattern)
            .build()
        super.deleteWhere(deleteWhereClause)
    }

    override suspend fun popOne(ownerUid: String, organisationUnitUid: String?): TrackedEntityAttributeReservedValue? {
        return selectOneWhere(where(ownerUid, organisationUnitUid, null))?.also {
            deleteWhereIfExists(it)
        }
    }

    override suspend fun count(ownerUid: String, organisationUnitUid: String?, pattern: String?): Int {
        return countWhere(where(ownerUid, organisationUnitUid, pattern))
    }

    private fun where(
        ownerUid: String,
        organisationUnit: String?,
        pattern: String?,
    ): String {
        val builder = WhereClauseBuilder().appendKeyStringValue(Columns.OWNER_UID, ownerUid)
        if (organisationUnit != null) {
            builder.appendKeyStringValue(Columns.ORGANISATION_UNIT, organisationUnit)
        }
        if (pattern != null) {
            builder.appendKeyStringValue(Columns.PATTERN, pattern)
        }
        return builder.build()
    }
}
