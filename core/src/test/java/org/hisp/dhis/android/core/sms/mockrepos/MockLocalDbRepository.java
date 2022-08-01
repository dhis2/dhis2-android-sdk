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

package org.hisp.dhis.android.core.sms.mockrepos;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockMetadata;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.smscompression.models.SMSMetadata;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public class MockLocalDbRepository implements LocalDbRepository {
    private static final int RESULT_WAITING_TIMEOUT_DEFAULT = 120;

    private String gatewayNumber = "525525";
    private String confirmationSenderNumber = null;
    private Integer resultWaitingTimeout = RESULT_WAITING_TIMEOUT_DEFAULT;
    private SMSMetadata metadata = new MockMetadata();
    private WebApiRepository.GetMetadataIdsConfig metadataIdsConfig = new WebApiRepository.GetMetadataIdsConfig();
    private boolean moduleEnabled = true;
    private boolean waitingForResult = false;
    private final HashMap<Integer, SubmissionType> ongoingSubmissions = new HashMap<>();

    public MockLocalDbRepository() {
        metadata.lastSyncDate = new Date();
    }

    @Override
    public Single<String> getUserName() {
        return Single.fromCallable(() -> MockObjects.user);
    }

    @Override
    public Single<String> getGatewayNumber() {
        return Single.fromCallable(() -> gatewayNumber);
    }

    @Override
    public Completable setGatewayNumber(String number) {
        return Completable.fromAction(() -> gatewayNumber = number);
    }

    @Override
    public Completable deleteGatewayNumber() {
        return Completable.fromAction(() -> gatewayNumber = null);
    }

    @Override
    public Single<Integer> getWaitingResultTimeout() {
        return Single.fromCallable(() -> resultWaitingTimeout);
    }

    @Override
    public Completable setWaitingResultTimeout(Integer timeoutSeconds) {
        return Completable.fromAction(() -> resultWaitingTimeout = timeoutSeconds);
    }

    @Override
    public Completable deleteWaitingResultTimeout() {
        return Completable.fromAction(() -> resultWaitingTimeout = RESULT_WAITING_TIMEOUT_DEFAULT);
    }

    @Override
    public Single<String> getConfirmationSenderNumber() {
        return Single.fromCallable(() -> confirmationSenderNumber);
    }

    @Override
    public Completable setConfirmationSenderNumber(String number) {
        return Completable.fromAction(() -> confirmationSenderNumber = number);
    }

    @Override
    public Completable deleteConfirmationSenderNumber() {
        return Completable.fromAction(() -> confirmationSenderNumber = null);
    }

    @Override
    public Single<SMSMetadata> getMetadataIds() {
        return Single.fromCallable(() -> metadata);
    }

    @Override
    public Completable setMetadataIds(SMSMetadata metadata) {
        return Completable.fromAction(() -> this.metadata = metadata);
    }

    @Override
    public Single<Event> getTrackerEventToSubmit(String eventUid) {
        return Single.fromCallable(MockObjects::getTrackerEvent);
    }

    @Override
    public Single<Event> getSimpleEventToSubmit(String eventUid) {
        return Single.fromCallable(MockObjects::getSimpleEvent);
    }

    @Override
    public Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid) {
        if (enrollmentUid.equals(MockObjects.enrollmentUidWithNullEvents)) {
            return Single.fromCallable(MockObjects::getTEIEnrollmentWithoutEvents);
        }
        else if (enrollmentUid.equals(MockObjects.enrollmentUidWithoutEvents)) {
            return Single.fromCallable(MockObjects::getTEIEnrollmentWithEventEmpty);
        }
        else if (enrollmentUid.equals(MockObjects.enrollmentUidWithoutGeometry)) {
            return Single.fromCallable(MockObjects::getTEIEnrollmentWithoutGeometry);
        }
        else {
            return Single.fromCallable(MockObjects::getTEIEnrollment);
        }
    }

    @Override
    public Completable updateEventSubmissionState(String eventUid, State state) {
        return Completable.complete();
    }

    @Override
    public Completable updateEnrollmentSubmissionState(TrackedEntityInstance trackedEntityInstance, State state) {
        return Completable.complete();
    }

    @Override
    public Completable updateRelationshipSubmissionState(String relationshipUid, State state) {
        return Completable.complete();
    }

    @Override
    public Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
        return Completable.fromAction(() -> this.metadataIdsConfig = metadataIdsConfig);
    }

    @Override
    public Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig() {
        return Single.fromCallable(() -> metadataIdsConfig);
    }

    @Override
    public Completable setModuleEnabled(boolean enabled) {
        return Completable.fromAction(() -> this.moduleEnabled = enabled);
    }

    @Override
    public Single<Boolean> isModuleEnabled() {
        return Single.fromCallable(() -> moduleEnabled);
    }

    @Override
    public Completable setWaitingForResultEnabled(boolean enabled) {
        return Completable.fromAction(() -> waitingForResult = enabled);
    }

    @Override
    public Single<Boolean> getWaitingForResultEnabled() {
        return Single.fromCallable(() -> waitingForResult);
    }

    @Override
    public Single<Map<Integer, SubmissionType>> getOngoingSubmissions() {
        return Single.fromCallable(() -> ongoingSubmissions);
    }

    @Override
    public Single<Integer> generateNextSubmissionId() {
        return Single.fromCallable(() -> {
            int next = 0;
            do {
                if (!ongoingSubmissions.keySet().contains(next)) {
                    return next;
                }
                next++;
            } while (next <= 255);
            throw new LocalDbRepository.TooManySubmissionsException();
        });
    }

    @Override
    public Completable addOngoingSubmission(Integer id, SubmissionType type) {
        return Completable.fromAction(() -> ongoingSubmissions.put(id, type));
    }

    @Override
    public Completable removeOngoingSubmission(Integer id) {
        return Completable.fromAction(() -> ongoingSubmissions.remove(id));
    }

    @Override
    public Single<SMSDataValueSet> getDataValueSet(String dataSetUid, String orgUnit, String period, String attributeOptionComboUid) {
        if (dataSetUid.equals(MockObjects.dataSetEmptyListUid)) {
            return Single.fromCallable(MockObjects::getSMSDataValueSetEmptyList);
        } else {
            return Single.fromCallable(MockObjects::getSMSDataValueSet);
        }
    }

    @Override
    public Completable updateDataSetSubmissionState(String dataSet, String orgUnit, String period, String attributeOptionComboUid, State state) {
        return Completable.complete();
    }

    @Override
    public Single<Relationship> getRelationship(String relationshipUid) {
        return Single.fromCallable(MockObjects::getRelationship);
    }
}