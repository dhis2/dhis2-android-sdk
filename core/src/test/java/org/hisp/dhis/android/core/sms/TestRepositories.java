/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public class TestRepositories {

    public static String enrollmentUid = "jQK0XnMVFIK";
    public static String teiUid = "MmzaWDDruXW";
    public static String trackedEntityType = "nEenWmSyUEp";

    public static class TestWebApiRepository implements WebApiRepository {
        public Metadata metadata;

        public TestWebApiRepository() {
            metadata = new TestMetadata();
        }

        @Override
        public Single<Metadata> getMetadataIds(GetMetadataIdsConfig config) {
            return Single.fromCallable(() -> metadata);
        }
    }

    public static class TestLocalDbRepository implements LocalDbRepository {
        public static String userId = "AIK2aQOJIbj";
        private String gatewayNumber = null;
        private String confirmationSenderNumber = null;
        private Integer resultWaitingTimeout = 120;
        public Metadata metadata = new TestMetadata();

        public TestLocalDbRepository() {
            metadata.lastSyncDate = new Date();
        }

        @Override
        public Single<String> getUserName() {
            return Single.fromCallable(() -> userId);
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
        public Single<Integer> getWaitingResultTimeout() {
            return Single.fromCallable(() -> resultWaitingTimeout);
        }

        @Override
        public Completable setWaitingResultTimeout(Integer timeoutSeconds) {
            return Completable.fromAction(() -> resultWaitingTimeout = timeoutSeconds);
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
        public Single<Metadata> getMetadataIds() {
            return Single.fromCallable(() -> metadata);
        }

        @Override
        public Completable setMetadataIds(Metadata metadata) {
            return Completable.complete();
        }

        @Override
        public Single<Event> getTrackerEventToSubmit(String eventUid) {
            return null;
        }

        @Override
        public Single<Event> getSimpleEventToSubmit(String eventUid) {
            return null;
        }

        @Override
        public Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid) {
            return Single.just(TrackedEntityInstance.builder()
                    .uid(teiUid)
                    .trackedEntityType(trackedEntityType)
                    .trackedEntityAttributeValues(getTestValues())
                    .enrollments(Collections.singletonList(getTestEnrollment(enrollmentUid, teiUid)))
                    .build());
        }

        @Override
        public Completable updateEventSubmissionState(String eventUid, State state) {
            return Completable.complete();
        }

        @Override
        public Completable updateEnrollmentSubmissionState(String enrollmentUid, State state) {
            return Completable.complete();
        }

        @Override
        public Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
            return Completable.complete();
        }

        @Override
        public Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig() {
            return Single.just(new WebApiRepository.GetMetadataIdsConfig());
        }

        @Override
        public Completable setModuleEnabled(boolean enabled) {
            return Completable.complete();
        }

        @Override
        public Single<Boolean> isModuleEnabled() {
            return Single.just(true);
        }

        @Override
        public Single<Map<Integer, SubmissionType>> getOngoingSubmissions() {
            return null;
        }

        @Override
        public Completable addOngoingSubmission(Integer id, SubmissionType type) {
            return null;
        }

        @Override
        public Completable removeOngoingSubmission(Integer id) {
            return null;
        }

        @Override
        public Single<List<DataValue>> getDataValues(String orgUnit, String period, String attributeOptionComboUid) {
            return null;
        }

        @Override
        public Completable updateDataSetSubmissionState(String dataSet, String orgUnit, String period, String attributeOptionComboUid, State state) {
            return null;
        }
    }

    public static Enrollment getTestEnrollment(String enrollmentUid, String teiUid) {
        return Enrollment.builder()
                .uid(enrollmentUid)
                .created(new Date())
                .lastUpdated(new Date())
                .organisationUnit("DiszpKrYNg8")
                .program("IpHINAT79UW")
                .enrollmentDate(new Date())
                .trackedEntityInstance(teiUid)
                .id(341L).build();
    }

    public static ArrayList<TrackedEntityAttributeValue> getTestValues() {
        ArrayList<TrackedEntityAttributeValue> list = new ArrayList<>();
        list.add(getTestValue("w75KJ2mc4zz", "Anne"));
        list.add(getTestValue("zDhUuAYrxNC", "Anski"));
        list.add(getTestValue("cejWyOfXge6", "Female"));
        list.add(getTestValue("mLur0EGaw9A", "OU test"));
        return list;
    }

    private static TrackedEntityAttributeValue getTestValue(String attr, String value) {
        return TrackedEntityAttributeValue.builder()
                .value(value)
                .created(new Date())
                .lastUpdated(new Date())
                .trackedEntityAttribute(attr)
                .trackedEntityInstance("MmzaWDDruXW")
                .build();
    }

    public static class TestMetadata extends Metadata {
        Enrollment enrollment = getTestEnrollment(enrollmentUid, teiUid);

        public List<String> getUsers() {
            return Collections.singletonList(TestRepositories.TestLocalDbRepository.userId);
        }

        public List<String> getTrackedEntityTypes() {
            return Collections.singletonList(enrollment.trackedEntityInstance());
        }

        public List<String> getTrackedEntityAttributes() {
            ArrayList<String> attrs = new ArrayList<>();
            for (TrackedEntityAttributeValue item : getTestValues()) {
                attrs.add(item.trackedEntityAttribute());
            }
            return attrs;
        }

        public List<String> getPrograms() {
            return Collections.singletonList(enrollment.program());
        }

        public List<String> getOrganisationUnits() {
            return Collections.singletonList(enrollment.organisationUnit());
        }
    }
}
