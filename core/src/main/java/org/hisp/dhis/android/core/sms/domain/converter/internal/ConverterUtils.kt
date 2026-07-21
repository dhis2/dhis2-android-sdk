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
package org.hisp.dhis.android.core.sms.domain.converter.internal

import android.util.Log
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.containsAPoint
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper.getPoint
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.smscompression.SMSConsts.SMSEnrollmentStatus
import org.hisp.dhis.smscompression.SMSConsts.SMSEventStatus
import org.hisp.dhis.smscompression.models.GeoPoint
import org.hisp.dhis.smscompression.models.SMSDataValue

internal object ConverterUtils {
    private val TAG: String = ConverterUtils::class.java.getSimpleName()

    @Suppress("ReturnCount")
    fun convertEventStatus(status: EventStatus?): SMSEventStatus? {
        if (status == null) {
            return null
        }
        return when (status) {
            EventStatus.ACTIVE -> SMSEventStatus.ACTIVE
            EventStatus.COMPLETED -> SMSEventStatus.COMPLETED
            EventStatus.SCHEDULE -> SMSEventStatus.SCHEDULE
            EventStatus.SKIPPED -> SMSEventStatus.SKIPPED
            EventStatus.VISITED -> SMSEventStatus.VISITED
            EventStatus.OVERDUE -> SMSEventStatus.OVERDUE
        }
    }

    @Suppress("ReturnCount")
    fun convertEnrollmentStatus(status: EnrollmentStatus?): SMSEnrollmentStatus? {
        if (status == null) {
            return null
        }
        return when (status) {
            EnrollmentStatus.ACTIVE -> SMSEnrollmentStatus.ACTIVE
            EnrollmentStatus.CANCELLED -> SMSEnrollmentStatus.CANCELLED
            EnrollmentStatus.COMPLETED -> SMSEnrollmentStatus.COMPLETED
        }
    }

    @Suppress("ReturnCount")
    fun convertGeometryPoint(geometry: Geometry?): GeoPoint? {
        if (geometry == null || !containsAPoint(geometry)) {
            return null
        }

        try {
            val point: List<Double?> = getPoint(geometry)
            return GeoPoint(point.get(1)!!.toFloat(), point.get(0)!!.toFloat())
        } catch (d2Error: D2Error) {
            Log.d(TAG, d2Error.errorDescription())
            return null
        }
    }

    fun convertDataValues(
        catOptionCombo: String?,
        trackedEntityDataValues: List<TrackedEntityDataValue>?,
    ): List<SMSDataValue> {
        val dataValues = mutableListOf<SMSDataValue>()
        if (trackedEntityDataValues == null) {
            return dataValues
        }
        for (tedv in trackedEntityDataValues) {
            val value = if (tedv.value() == null) "" else tedv.value()
            dataValues.add(SMSDataValue(catOptionCombo, tedv.dataElement(), value))
        }
        return dataValues
    }
}
