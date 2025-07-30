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

import androidx.room.Query
import org.hisp.dhis.android.persistence.common.daos.ObjectDao
import org.hisp.dhis.android.processor.GenerateDaoQueries

@GenerateDaoQueries(tableName = "TrackedEntityAttributeReservedValueTableInfo.TABLE_NAME")
internal interface TrackedEntityAttributeReservedValueDaoTemp : ObjectDao<TrackedEntityAttributeReservedValueDB> {

    @Query(
        """
        DELETE FROM ${TrackedEntityAttributeReservedValueTableInfo.TABLE_NAME}
        WHERE (date(${TrackedEntityAttributeReservedValueTableInfo.Columns.EXPIRY_DATE}) < date(:serverDateAsString) 
            AND ${TrackedEntityAttributeReservedValueTableInfo.Columns.EXPIRY_DATE} IS NOT NULL)
        OR (date(${TrackedEntityAttributeReservedValueTableInfo.Columns.TEMPORAL_VALIDITY_DATE}) < date(:serverDateAsString) 
            AND ${TrackedEntityAttributeReservedValueTableInfo.Columns.TEMPORAL_VALIDITY_DATE} IS NOT NULL)
        """
    )
    suspend fun deleteExpired(serverDateAsString: String): Int

    @Query(
        """
        DELETE FROM ${TrackedEntityAttributeReservedValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityAttributeReservedValueTableInfo.Columns.OWNER_UID} = :ownerUid
        AND ${TrackedEntityAttributeReservedValueTableInfo.Columns.PATTERN} != :pattern
        """
    )
    suspend fun deleteIfOutdatedPattern(ownerUid: String, pattern: String)
}
