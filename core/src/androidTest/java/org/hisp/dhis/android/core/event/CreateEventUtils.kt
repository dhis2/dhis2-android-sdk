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
package org.hisp.dhis.android.core.event

import android.content.ContentValues
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo.Columns

object CreateEventUtils {
    private const val ENROLLMENT_UID = "test_enrollment"
    private val STATUS = EventStatus.ACTIVE
    private val GEOMETRY_TYPE = FeatureType.POINT
    private const val GEOMETRY_COORDINATES = "[59.345231, 10.832152]"

    // timestamp
    private const val DATE = "2017-01-12T11:31:00.000"

    fun create(
        uid: String,
        program: String,
        programStage: String,
        orgUnit: String,
        enrollmentUid: String?,
    ): ContentValues {
        val event = ContentValues()
        event.put(EventTableInfo.Columns.UID, uid)

        if (enrollmentUid == null) {
            event.putNull(Columns.ENROLLMENT)
        }

        event.put(Columns.ENROLLMENT, enrollmentUid)
        event.put(Columns.CREATED, DATE)
        event.put(Columns.LAST_UPDATED, DATE)
        event.put(Columns.STATUS, STATUS.name)
        event.put(Columns.GEOMETRY_TYPE, GEOMETRY_TYPE.featureType)
        event.put(Columns.GEOMETRY_COORDINATES, GEOMETRY_COORDINATES)
        event.put(Columns.PROGRAM, program)
        event.put(Columns.PROGRAM_STAGE, programStage)
        event.put(Columns.ORGANISATION_UNIT, orgUnit)
        event.put(Columns.EVENT_DATE, DATE)
        event.put(Columns.COMPLETED_DATE, DATE)
        event.put(Columns.DUE_DATE, DATE)
        event.put(Columns.SYNC_STATE, State.TO_POST.name)
        return event
    }
}
