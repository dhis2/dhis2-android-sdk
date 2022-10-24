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

package org.hisp.dhis.android.core.sms.domain.converter.internal;

import android.util.Log;

import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.smscompression.SMSConsts;
import org.hisp.dhis.smscompression.models.GeoPoint;
import org.hisp.dhis.smscompression.models.SMSDataValue;

import java.util.ArrayList;
import java.util.List;

final class ConverterUtils {

    private final static String TAG = ConverterUtils.class.getSimpleName();

    private ConverterUtils() {
        // no instances
    }

    static SMSConsts.SMSEventStatus convertEventStatus(EventStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case ACTIVE:
                return SMSConsts.SMSEventStatus.ACTIVE;
            case COMPLETED:
                return SMSConsts.SMSEventStatus.COMPLETED;
            case SCHEDULE:
                return SMSConsts.SMSEventStatus.SCHEDULE;
            case SKIPPED:
                return SMSConsts.SMSEventStatus.SKIPPED;
            case VISITED:
                return SMSConsts.SMSEventStatus.VISITED;
            case OVERDUE:
                return SMSConsts.SMSEventStatus.OVERDUE;
            default:
                return null;
        }
    }

    static SMSConsts.SMSEnrollmentStatus convertEnrollmentStatus(EnrollmentStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case ACTIVE:
                return SMSConsts.SMSEnrollmentStatus.ACTIVE;
            case CANCELLED:
                return SMSConsts.SMSEnrollmentStatus.CANCELLED;
            case COMPLETED:
                return SMSConsts.SMSEnrollmentStatus.COMPLETED;
            default:
                return null;
        }
    }

    static GeoPoint convertGeometryPoint(Geometry geometry) {
        if (!GeometryHelper.containsAPoint(geometry)) {
            return null;
        }

        try {
            List<Double> point = GeometryHelper.getPoint(geometry);
            return new GeoPoint(point.get(1).floatValue(), point.get(0).floatValue());
        } catch (D2Error d2Error) {
            Log.d(TAG, d2Error.errorDescription());
            return null;
        }
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    static List<SMSDataValue> convertDataValues(String catOptionCombo,
                                                List<TrackedEntityDataValue> trackedEntityDataValues) {
        ArrayList<SMSDataValue> dataValues = new ArrayList<>();
        if (trackedEntityDataValues == null) {
            return dataValues;
        }
        for (TrackedEntityDataValue tedv : trackedEntityDataValues) {
            String value = tedv.value() == null ? "" : tedv.value();
            dataValues.add(new SMSDataValue(catOptionCombo, tedv.dataElement(), value));
        }
        return dataValues;
    }
}
