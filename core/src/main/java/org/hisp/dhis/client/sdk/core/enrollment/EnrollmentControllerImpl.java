/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.client.sdk.core.enrollment;

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.event.EventStore;
import org.hisp.dhis.client.sdk.core.event.EventController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.List;

public final class EnrollmentControllerImpl implements EnrollmentController {
    private final EnrollmentApiClient enrollmentApiClient;
    private final SystemInfoController systemInfoController;

    private final LastUpdatedPreferences lastUpdatedPreferences;
    private final TransactionManager transactionManager;

    private final EventController eventController;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;
    private final StateStore stateStore;

    public EnrollmentControllerImpl(EnrollmentApiClient apiClient,
                                    SystemInfoController systemInfoController,
                                    LastUpdatedPreferences preferences,
                                    TransactionManager transactionManager,
                                    EventController eventController, EnrollmentStore enrollmentStore,
                                    EventStore eventStore, StateStore stateStore) {
        this.enrollmentApiClient = apiClient;
        this.systemInfoController = systemInfoController;
        this.lastUpdatedPreferences = preferences;
        this.transactionManager = transactionManager;
        this.eventController = eventController;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
        this.stateStore = stateStore;
    }

    private List<Enrollment> getEnrollmentsDataFromServer(TrackedEntityInstance
                                                                  trackedEntityInstance) throws
            ApiException {
//        if (trackedEntityInstance == null) {
//            return new ArrayList<>();
//        }
//
//        DateTime lastUpdated = lastUpdatedPreferences
//                .get(ResourceType.ENROLLMENTS, trackedEntityInstance
// .getTrackedEntityInstanceUid());
//        DateTime serverDateTime = systemInfoController.getSystemInfo().getServerDate();
//
//        List<Enrollment> existingUpdatedAndPersistedEnrollments = updateEnrollments
//                (trackedEntityInstance, lastUpdated);
//
//        for (Enrollment enrollment : existingUpdatedAndPersistedEnrollments) {
//            enrollment.setTrackedEntityInstance(trackedEntityInstance);
//        }
//
//        for (Enrollment enrollment : existingUpdatedAndPersistedEnrollments) {
//            try {
//                eventController.getEventsDataFromServer(enrollment);
//            } catch (ApiException e) {
//                // can't throw this exception up because we want to
//                // continue loading enrollments.. todo: let the user know?
//                e.printStackTrace();
//            }
//        }
//
//        saveResourceDataFromServer(ResourceType.ENROLLMENTS,
//                trackedEntityInstance.getTrackedEntityInstanceUid(), enrollmentStore,
//                existingUpdatedAndPersistedEnrollments, enrollmentStore.query
//                        (trackedEntityInstance), serverDateTime);
//
//
//        return existingUpdatedAndPersistedEnrollments;
        return null;
    }

    private Enrollment getEnrollmentDataFromServer(String uid, boolean getEvents) throws
            ApiException {
//        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.ENROLLMENT, uid);
//        DateTime serverDateTime = systemInfoController.getSystemInfo().getServerDate();
//
//        Enrollment updatedEnrollment = enrollmentApiClient.getFullEnrollment(uid, lastUpdated);
//        //todo: if the updatedEnrollment is deleted on the server, delete it also locally
//        //todo: be sure to check if the enrollment has ever been on the server, or if it is still
//        // pending first time registration pull
//
//        Enrollment persistedEnrollment = enrollmentStore.queryByUid(uid);
//        if (updatedEnrollment.getUId() == null) {
//            //either the uid provided was invalid, or the enrollment has not been updated since
//            // lastUpdated
//            return persistedEnrollment;
//        }
//        if (persistedEnrollment != null) {
//            updatedEnrollment.setId(persistedEnrollment.getId());
//            if (updatedEnrollment.getLastUpdated().isAfter(persistedEnrollment.getLastUpdated()
// )) {
//                DbOperationImpl.with(enrollmentStore).update(updatedEnrollment).execute();
//            }
//        } else {
//            DbOperationImpl.with(enrollmentStore).insert(updatedEnrollment).execute();
//        }
//        lastUpdatedPreferences.save(ResourceType.ENROLLMENT, serverDateTime, uid);
//        if (getEvents) {
//            eventController.getEventsDataFromServer(updatedEnrollment);
//        }
//        return updatedEnrollment;
        return null;
    }

    public List<Enrollment> updateEnrollments(TrackedEntityInstance trackedEntityInstance,
                                              DateTime lastUpdated) {

//        List<Enrollment> allExistingEnrollments = enrollmentApiClient
//                .getBasicEnrollments(trackedEntityInstance.getTrackedEntityInstanceUid(), null);
//
//        // retrieve all enrollments, with all fields based on lastUpdated parameter
//        List<Enrollment> updatedEnrollments = enrollmentApiClient
//                .getFullEnrollments(trackedEntityInstance.getTrackedEntityInstanceUid(),
//                        lastUpdated);
//
//
//        // query enrollment store for all enrollments for the tracked entity instance
//        List<Enrollment> enrollmentsForTrackedEntityInstance = enrollmentStore.query
//                (trackedEntityInstance);
//
//        // get action map for all enrollments in state store
//        Map<Long, Action> actionMap = stateStore.queryActionsForModel(Enrollment.class);
//
//        // create a new list that filters out the enrollments that has Action.TO_POST (meaning it
//        // is saved locally), from enrollment store
//        List<Enrollment> filteredEnrollments = new ArrayList<>();
//
//        for (Enrollment enrollment : enrollmentsForTrackedEntityInstance) {
//            if (!(Action.TO_POST.equals(actionMap.get(enrollment.getId())))) {
//                filteredEnrollments.add(enrollment);
//            }
//        }
//
//        // List<Enrollment> existingUpdatedAndPersistedEnrollments =
//        //        modelUtils.merge(allExistingEnrollments, updatedEnrollments, enrollmentStore
//        // .query(trackedEntityInstance));
//
//        List<Enrollment> existingUpdatedAndPersistedEnrollments =
//                ModelUtils.merge(allExistingEnrollments, updatedEnrollments, filteredEnrollments);
//
//        return existingUpdatedAndPersistedEnrollments;
        return null;
    }

    private void saveResourceDataFromServer(ResourceType resourceType, String extraIdentifier,
                                            IdentifiableObjectStore<Enrollment> store,
                                            List<Enrollment> updatedItems,
                                            List<Enrollment> persistedItems, DateTime
                                                    serverDateTime) {
//        Queue<DbOperation> operations = new LinkedList<>();
//        operations.addAll(DbUtils.createOperations(store, persistedItems, updatedItems));
//        transactionManager.transact(operations);
//        lastUpdatedPreferences.save(resourceType, serverDateTime, extraIdentifier);
    }


    private void sendEnrollmentChanges() throws ApiException {
//        List<Enrollment> enrollments = getLocallyChangedEnrollments();
//        sendEnrollmentChanges(enrollments);
    }

    private List<Enrollment> getLocallyChangedEnrollments() {
//        List<Enrollment> toPost = stateStore.queryModelsWithActions(Enrollment.class, Action
//                .TO_POST);
//        List<Enrollment> toPut = stateStore.queryModelsWithActions(Enrollment.class, Action
//                .TO_UPDATE);
//        List<Enrollment> enrollments = new ArrayList<>();
//        enrollments.addAll(toPost);
//        enrollments.addAll(toPut);
//        return enrollments;
        return null;
    }

    @Override
    public void sendEnrollmentChanges(List<Enrollment> enrollments) throws ApiException {
//        if (enrollments == null || enrollments.isEmpty()) {
//            return;
//        }
//
//        Map<Long, Action> actionMap = stateStore
//                .queryActionsForModel(Enrollment.class);
//
//
//        for (int i = 0; i < enrollments.size(); i++) {/* workaround for not attempting to upload
//        enrollments with local tei reference*/
//            Enrollment enrollment = enrollments.get(i);
//            Action trackedEntityInstanceAction = null;
//            TrackedEntityInstance trackedEntityInstance = enrollment.getTrackedEntityInstance();
//            if (trackedEntityInstance == null) {
//                trackedEntityInstanceAction = stateStore.queryActionForModel
// (trackedEntityInstance);
//            }
//
//            //we avoid trying to send enrollments whose trackedEntityInstances that have not yet
//            // been posted to server
//            if (Action.TO_POST.equals(trackedEntityInstanceAction)) {
//                enrollments.remove(i);
//                i--;
//            }
//        }
//        for (Enrollment enrollment : enrollments) {
//            sendEnrollmentChanges(enrollment, actionMap.get(enrollment.getId()));
//        }
//    }
//
//    private void sendEnrollmentChanges(Enrollment enrollment, Action action) throws ApiException {
//        if (enrollment == null) {
//            return;
//        }
//        Action trackedEntityInstanceAction = null;
//        TrackedEntityInstance trackedEntityInstance = enrollment.getTrackedEntityInstance();
//        if (trackedEntityInstance == null) {
//            trackedEntityInstanceAction = stateStore.queryActionForModel(trackedEntityInstance);
//        }
//
//        //we avoid trying to send enrollments whose trackedEntityInstances that have not yet been
//        // posted to server
//        if (Action.TO_POST.equals(trackedEntityInstanceAction)) {
//            return;
//        }
//
//        if (Action.TO_POST.equals(action)) {
//            postEnrollment(enrollment);
//        } else {
//            putEnrollment(enrollment);
//        }
//
//        List<Event> events = eventStore.query(enrollment);
//        eventController.sendEventChanges(events);
    }

    private void postEnrollment(Enrollment enrollment) throws ApiException {
//        try {
//            ImportSummary importSummary = enrollmentApiClient.postEnrollment(enrollment);
////            handleImportSummaryWithError(importSummary, failedItemStore, FailedItemType
//// .ENROLLMENT, enrollment.getId());
//
//            if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
//                    ImportSummary.Status.OK.equals(importSummary.getStatus())) {
//
//                stateStore.saveActionForModel(enrollment, Action.SYNCED);
//                enrollmentStore.save(enrollment);
////                clearFailedItem(FailedItemType.ENROLLMENT, failedItemStore, enrollment.getId());
//                UpdateEnrollmentTimestamp(enrollment);
//            }
//        } catch (ApiException apiException) {
////            handleEnrollmentSendException(apiException, failedItemStore, enrollment);
//        }
    }

    private void putEnrollment(Enrollment enrollment) throws ApiException {
//        try {
//            ImportSummary importSummary = enrollmentApiClient.putEnrollment(enrollment);
////            handleImportSummaryWithError(importSummary, failedItemStore, FailedItemType
//// .ENROLLMENT, enrollment.getId());
//
//            if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
//                    ImportSummary.Status.OK.equals(importSummary.getStatus())) {
//
//                stateStore.saveActionForModel(enrollment, Action.SYNCED);
//                enrollmentStore.save(enrollment);
////                clearFailedItem(FailedItemType.ENROLLMENT, failedItemStore, enrollment.getId());
//                UpdateEnrollmentTimestamp(enrollment);
//            }
//        } catch (ApiException apiException) {
////            handleEnrollmentSendException(apiException, failedItemStore, enrollment);
//        }
    }

    private void UpdateEnrollmentTimestamp(Enrollment enrollment) throws ApiException {
//        try {
//            Enrollment updatedEnrollment = enrollmentApiClient
//                    .getBasicEnrollment(enrollment.getUId(), null);
//
//            // merging updated timestamp to local enrollment model
//            enrollment.setCreated(updatedEnrollment.getCreated());
//            enrollment.setLastUpdated(updatedEnrollment.getLastUpdated());
//            enrollmentStore.save(enrollment);
//        } catch (ApiException apiException) {
//            // NetworkUtils.handleApiException(apiException);
//        }
    }

    @Override
    public void sync() throws ApiException {
//        sendEnrollmentChanges();
    }

    @Override
    public List<Enrollment> sync(TrackedEntityInstance trackedEntityInstance) throws ApiException {
//        return getEnrollmentsDataFromServer(trackedEntityInstance);
        return null;
    }

    @Override
    public Enrollment sync(String uid, boolean getEvents) throws ApiException {
//        return getEnrollmentDataFromServer(uid, getEvents);
        return null;
    }
}
