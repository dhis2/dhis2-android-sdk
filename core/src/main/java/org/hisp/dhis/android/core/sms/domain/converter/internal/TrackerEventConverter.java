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

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.smscompression.models.SMSSubmission;
import org.hisp.dhis.smscompression.models.TrackerEventSMSSubmission;

import io.reactivex.Completable;
import io.reactivex.Single;

public class TrackerEventConverter extends Converter<Event> {
    private final String eventUid;

    public TrackerEventConverter(LocalDbRepository localDbRepository,
                                 DHISVersionManager dhisVersionManager,
                                 String eventUid) {
        super(localDbRepository, dhisVersionManager);
        this.eventUid = eventUid;
    }

    @Override
    public Single<? extends SMSSubmission> convert(@NonNull Event e, String user, int submissionId) {
        return Single.fromCallable(() -> {
            TrackerEventSMSSubmission subm = new TrackerEventSMSSubmission();

            subm.setSubmissionID(submissionId);
            subm.setUserID(user);

            subm.setEvent(e.uid());
            subm.setEventDate(e.eventDate());
            subm.setEventStatus(ConverterUtils.convertEventStatus(e.status()));
            subm.setProgramStage(e.programStage());
            subm.setDueDate(e.dueDate());
            subm.setAttributeOptionCombo(e.attributeOptionCombo());
            subm.setOrgUnit(e.organisationUnit());
            subm.setEnrollment(e.enrollment());
            subm.setValues(ConverterUtils.convertDataValues(e.attributeOptionCombo(), e.trackedEntityDataValues()));

            if (GeometryHelper.containsAPoint(e.geometry())) {
                subm.setCoordinates(ConverterUtils.convertGeometryPoint(e.geometry()));
            }

            return subm;
        });
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateEventSubmissionState(eventUid, state);
    }

    @Override
    Single<Event> readItemFromDb() {
        return getLocalDbRepository().getTrackerEventToSubmit(eventUid);
    }
}
