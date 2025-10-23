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
import org.hisp.dhis.android.persistence.program.ProgramTrackedEntityAttributeTableInfo
import org.hisp.dhis.android.processor.GenerateDaoQueries

@GenerateDaoQueries(tableName = "TrackedEntityAttributeValueTableInfo.TABLE_NAME")
internal interface TrackedEntityAttributeValueDaoAux : ObjectDao<TrackedEntityAttributeValueDB> {
    @Query(
        """UPDATE TrackedEntityAttributeValue 
        SET ${TrackedEntityAttributeValueTableInfo.Columns.SYNC_STATE} = :state 
        WHERE ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} = :uid""",
    )
    fun setSyncStateByInstance(state: String, uid: String)

    @Query(
        """
        DELETE FROM ${TrackedEntityAttributeValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} = :trackedEntityInstanceUid
          AND ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} 
            NOT IN (:trackedEntityAttributeUids)
    """,
    )
    fun deleteByInstanceAndNotInAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>,
    ): Int

    @Query(
        """
        DELETE FROM ${TrackedEntityAttributeValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} = :trackedEntityInstanceUid
          AND ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} 
            NOT IN (:trackedEntityAttributeUids)
          AND ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} IN (
              SELECT ${ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE}
              FROM ${ProgramTrackedEntityAttributeTableInfo.TABLE_NAME}
              WHERE ${ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM} = :programUid 
          )
    """,
    )
    fun deleteByInstanceAndNotInProgramAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>,
        programUid: String,
    ): Int

    @Query(
        """
        DELETE FROM ${TrackedEntityAttributeValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} = :trackedEntityInstanceUid
          AND ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} 
            NOT IN (:trackedEntityAttributeUids)
          AND ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} IN (
              SELECT ${ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE}
              FROM ${ProgramTrackedEntityAttributeTableInfo.TABLE_NAME}
              WHERE ${ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM} IN (:programUids)
              UNION ALL
              SELECT ${TrackedEntityTypeAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE}
              FROM ${TrackedEntityTypeAttributeTableInfo.TABLE_NAME}
              WHERE ${TrackedEntityTypeAttributeTableInfo.Columns.TRACKED_ENTITY_TYPE} = :teiTypeUid
          )
    """,
    )
    fun deleteByInstanceAndNotInAccessibleAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>,
        programUids: List<String>,
        teiTypeUid: String,
    ): Int

    @Query(
        """
        DELETE FROM ${TrackedEntityAttributeValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} = :trackedEntityInstanceUid
          AND ${TrackedEntityAttributeValueTableInfo.Columns.VALUE} IS NULL
    """,
    )
    fun removeDeletedAttributeValuesByInstance(
        trackedEntityInstanceUid: String,
    ): Int
}
