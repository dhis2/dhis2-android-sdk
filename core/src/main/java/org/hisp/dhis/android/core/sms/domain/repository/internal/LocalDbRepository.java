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

package org.hisp.dhis.android.core.sms.domain.repository.internal;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.smscompression.models.SMSMetadata;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface LocalDbRepository {

    Single<String> getUserName();

    Single<String> getGatewayNumber();

    Completable setGatewayNumber(String number);

    Completable deleteGatewayNumber();

    Single<Integer> getWaitingResultTimeout();

    Completable setWaitingResultTimeout(Integer timeoutSeconds);

    Completable deleteWaitingResultTimeout();

    Single<String> getConfirmationSenderNumber();

    Completable setConfirmationSenderNumber(String number);

    Completable deleteConfirmationSenderNumber();

    Single<SMSMetadata> getMetadataIds();

    Completable setMetadataIds(SMSMetadata metadata);

    Single<Event> getTrackerEventToSubmit(String eventUid);

    Single<Event> getSimpleEventToSubmit(String eventUid);

    Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid);

    Completable updateEventSubmissionState(String eventUid, State state);

    Completable updateEnrollmentSubmissionState(TrackedEntityInstance tei, State state);

    Completable updateRelationshipSubmissionState(String relationshipUid, State state);

    Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig);

    Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig();

    Completable setModuleEnabled(boolean enabled);

    Single<Boolean> isModuleEnabled();

    Completable setWaitingForResultEnabled(boolean enabled);

    Single<Boolean> getWaitingForResultEnabled();

    Single<Map<Integer, SubmissionType>> getOngoingSubmissions();

    Single<Integer> generateNextSubmissionId();

    Completable addOngoingSubmission(Integer id, SubmissionType type);

    Completable removeOngoingSubmission(Integer id);

    Single<SMSDataValueSet> getDataValueSet(String dataSet,
                                            String orgUnit,
                                            String period,
                                            String attributeOptionComboUid);

    Completable updateDataSetSubmissionState(String dataSet,
                                             String orgUnit,
                                             String period,
                                             String attributeOptionComboUid,
                                             State state);

    Single<Relationship> getRelationship(String relationshipUid);

    class TooManySubmissionsException extends IllegalStateException {
        public TooManySubmissionsException() {
            super("Too many ongoing submissions at the same time >255");
        }
    }
}
