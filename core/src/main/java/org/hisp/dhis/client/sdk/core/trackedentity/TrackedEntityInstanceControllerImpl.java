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

package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperationImpl;
import org.hisp.dhis.client.sdk.core.common.persistence.Store;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentStore;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentController;
import org.hisp.dhis.client.sdk.core.relationship.RelationshipStore;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.relationship.Relationship;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TrackedEntityInstanceControllerImpl implements TrackedEntityInstanceController {
    private final TrackedEntityInstanceApiClient trackedEntityInstanceApiClient;
    private final SystemInfoApiClient systemInfoApiClient;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final LastUpdatedPreferences lastUpdatedPreferences;
    private final TransactionManager transactionManager;

    private final EnrollmentController enrollmentController;
    private final StateStore stateStore;
    private final RelationshipStore relationshipStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private final EnrollmentStore enrollmentStore;

    public TrackedEntityInstanceControllerImpl(TrackedEntityInstanceApiClient
                                                   trackedEntityInstanceApiClient,
                                               SystemInfoApiClient systemInfoApiClient,
                                               TrackedEntityInstanceStore trackedEntityInstanceStore,
                                               LastUpdatedPreferences lastUpdatedPreferences,
                                               TransactionManager transactionManager,
                                               EnrollmentController enrollmentController,
                                               StateStore stateStore,
                                               RelationshipStore relationshipStore,
                                               TrackedEntityAttributeValueStore
                                                   trackedEntityAttributeValueStore,
                                               EnrollmentStore enrollmentStore) {
        this.trackedEntityInstanceApiClient = trackedEntityInstanceApiClient;
        this.systemInfoApiClient = systemInfoApiClient;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.transactionManager = transactionManager;
        this.enrollmentController = enrollmentController;
        this.stateStore = stateStore;
        this.relationshipStore = relationshipStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.enrollmentStore = enrollmentStore;
    }

    public static Map<String, Relationship> toMap(List<Relationship> objects) {
        Map<String, Relationship> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (Relationship object : objects) {
                if (object.getTrackedEntityInstanceA() != null && object
                        .getTrackedEntityInstanceB() != null && object.getRelationship() != null) {
                    map.put(object.getTrackedEntityInstanceA().getTrackedEntityInstanceUid()
                            + object.getTrackedEntityInstanceB().getTrackedEntityInstanceUid()
                            + object.getRelationship(), object);
                }
            }
        }
        return map;
    }

    /**
     * Queries the server and returns a list of tracked entity instances based on the given
     * parameters
     * The returned tracked entity instances will only contain basic information. More
     * information, like
     * enrollments and events need to be loaded with either
     * getTrackedEntityInstancesDataFromServer(List),
     * or getTrackedEntityInstanceDataFromServer(String uid);
     *
     * @param organisationUnitUid
     * @param programUid
     * @param queryString
     * @param params
     */
    private List<TrackedEntityInstance> queryTrackedEntityInstancesDataFromServer(String organisationUnitUid, String programUid,
                                                                                  String queryString, TrackedEntityAttributeValue... params) {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        if (programUid != null) {
            QUERY_MAP_FULL.put("program", programUid);
        }

        List<TrackedEntityAttributeValue> valueParams = new LinkedList<>();
        if (params != null) {
            for (TrackedEntityAttributeValue trackedEntityAttributeValue : params) {
                if (trackedEntityAttributeValue != null &&
                        trackedEntityAttributeValue.getValue() != null) {
                    if (!trackedEntityAttributeValue.getValue().isEmpty()) {
                        valueParams.add(trackedEntityAttributeValue);
                        QUERY_MAP_FULL.put("filter", trackedEntityAttributeValue
                                .getTrackedEntityAttributeUId() + ":LIKE:" +
                                trackedEntityAttributeValue
                                .getValue());
                    }
                }
            }
        }

        //doesnt work with both attribute filter and query
        if (queryString != null && !queryString.isEmpty() && valueParams.isEmpty()) {
            QUERY_MAP_FULL.put("query", "LIKE:" + queryString);//todo: make a map where we can
            // use more than one of each key
        }
        List<TrackedEntityInstance> trackedEntityInstances
                = trackedEntityInstanceApiClient.getBasicTrackedEntityInstances
                (organisationUnitUid, null);
        return trackedEntityInstances;
    }

    /**
     * Loads a list of trackedEntityInstances from the server and stores to the local database.
     *
     * @param trackedEntityInstances
     * @param getEnrollments         set to true if you want to load enrollments with the
     *                               trackedEntityInstance
     */
    private void getTrackedEntityInstancesDataFromServer(List<TrackedEntityInstance>
                                                                 trackedEntityInstances, boolean
            getEnrollments) {
        if (trackedEntityInstances == null) {
            return;
        }
        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            try {
                getTrackedEntityInstanceDataFromServer(trackedEntityInstance
                        .getTrackedEntityInstanceUid(), getEnrollments);
            } catch (ApiException e) { //can't throw this further up because we want to continue
            // loading all the TEIs..
                e.printStackTrace();
            }
        }
    }

    private TrackedEntityInstance getTrackedEntityInstanceDataFromServer(String uid, boolean
            getEnrollments) {
        DateTime lastUpdated = DateTime.now();// lastUpdatedPreferences.get(ResourceType
        // .TRACKED_ENTITY_INSTANCE, uid);
        DateTime serverDateTime = systemInfoApiClient.getSystemInfo().getServerDate();

        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        TrackedEntityInstance updatedTrackedEntityInstance =
                trackedEntityInstanceApiClient.getFullTrackedEntityInstance(uid, null);
        TrackedEntityInstance persistedTrackedEntityInstance =
                trackedEntityInstanceStore.queryByUid(uid);

        if (persistedTrackedEntityInstance != null) {
            updatedTrackedEntityInstance.setId(persistedTrackedEntityInstance.getId());
            if (updatedTrackedEntityInstance.getLastUpdated().isAfter
                    (persistedTrackedEntityInstance.getLastUpdated())) {
                DbOperationImpl.with(trackedEntityInstanceStore).update(updatedTrackedEntityInstance)
                        .execute();
            }
        } else {
            DbOperationImpl.with(trackedEntityInstanceStore).insert(updatedTrackedEntityInstance)
                    .execute();
        }

        List<DbOperation> operations = new ArrayList<>();
        if (updatedTrackedEntityInstance.getAttributes() != null) {
            for (TrackedEntityAttributeValue value : updatedTrackedEntityInstance.getAttributes()) {
                if (value != null) {
                    value.setTrackedEntityInstance(updatedTrackedEntityInstance);
                    operations.add(DbOperationImpl.with(trackedEntityAttributeValueStore).save(value));
                }
            }
        }
        List<Relationship> updatedRelationships = updatedTrackedEntityInstance.getRelationships();
        List<Relationship> persistedRelationships = null;
        if (persistedTrackedEntityInstance != null) {
            persistedRelationships = persistedTrackedEntityInstance.getRelationships();
        }
        if (updatedRelationships != null) {
            operations.addAll(createOperations(relationshipStore, persistedRelationships,
                    updatedRelationships));
        }
        transactionManager.transact(operations);
        // lastUpdatedPreferences.save(ResourceType.TRACKED_ENTITY_INSTANCE, serverDateTime, uid);
        if (getEnrollments) {
            enrollmentController.sync(updatedTrackedEntityInstance);
        }
        return updatedTrackedEntityInstance;
    }

    /**
     * This utility method allows to determine which type of operation to apply to
     * each BaseIdentifiableObject$Flow depending on TimeStamp.
     *
     * @param oldModels List of models from local storage.
     * @param newModels List of models of distance instance of DHIS.
     */
    private List<DbOperationImpl> createOperations(Store<Relationship> modelStore,
                                                   List<Relationship> oldModels,
                                                   List<Relationship> newModels) {
        List<DbOperationImpl> ops = new ArrayList<>();

        Map<String, Relationship> newModelsMap = toMap(newModels);
        Map<String, Relationship> oldModelsMap = toMap(oldModels);

        // As we will go through map of persisted items, we will try to update existing data.
        // Also, during each iteration we will remove old model key from list of new models.
        // As the result, the list of remaining items in newModelsMap,
        // will contain only those items which were not inserted before.
        for (String oldModelKey : oldModelsMap.keySet()) {
            Relationship newModel = newModelsMap.get(oldModelKey);
            Relationship oldModel = oldModelsMap.get(oldModelKey);

            // if there is no particular model with given uid in list of
            // actual (up to date) items, it means it was removed on the server side,
            // or the item was created locally and has not yet been posted.
            if (newModel == null) {
                Action action = stateStore.queryActionForModel(oldModel);
                if (!Action.TO_UPDATE.equals(action) && !Action.TO_POST.equals(action)) {
                    ops.add(DbOperationImpl.with(modelStore)
                            .delete(oldModel));
                }

                // in case if there is no new model object,
                // we can jump to next iteration.
                continue;
            }

            newModel.setId(oldModel.getId());
            ops.add(DbOperationImpl.with(modelStore)
                    .update(newModel));

            // as we have processed given old (persisted) model,
            // we can remove it from map of new models.
            newModelsMap.remove(oldModelKey);
        }

        // Inserting new items.
        for (String newModelKey : newModelsMap.keySet()) {
            Relationship item = newModelsMap.get(newModelKey);
            ops.add(DbOperationImpl.with(modelStore)
                    .insert(item));
        }

        return ops;
    }

    private void sendTrackedEntityInstanceChanges(boolean sendEnrollments) {
        List<TrackedEntityInstance> trackedEntityInstances =
                getLocallyChangedTrackedEntityInstances();
        sendTrackedEntityInstancesChanges(trackedEntityInstances, sendEnrollments);
    }

    private List<TrackedEntityInstance> getLocallyChangedTrackedEntityInstances() {
        List<TrackedEntityInstance> toPost = stateStore.queryModelsWithActions
                (TrackedEntityInstance.class, Action.TO_POST);
        List<TrackedEntityInstance> toPut = stateStore.queryModelsWithActions
                (TrackedEntityInstance.class, Action.TO_UPDATE);
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();
        trackedEntityInstances.addAll(toPost);
        trackedEntityInstances.addAll(toPut);
        return trackedEntityInstances;
    }

    public void sendTrackedEntityInstancesChanges(List<TrackedEntityInstance>
                                                          trackedEntityInstances, boolean
            sendEnrollments) {
        if (trackedEntityInstances == null || trackedEntityInstances.isEmpty()) {
            return;
        }

        Map<Long, Action> actionMap = stateStore
                .queryActionsForModel(TrackedEntityInstance.class);

        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            sendTrackedEntityInstanceChanges(trackedEntityInstance, actionMap.get
                    (trackedEntityInstance.getId()), sendEnrollments);
        }
    }

    public void sendTrackedEntityInstanceChanges(TrackedEntityInstance trackedEntityInstance,
                                                 Action action, boolean sendEnrollments) {
        if (trackedEntityInstance == null) {
            return;
        }
        if (Action.TO_POST.equals(action)) {
            postTrackedEntityInstance(trackedEntityInstance);
        } else {
            putTrackedEntityInstance(trackedEntityInstance);
        }
        if (sendEnrollments) {
            List<Enrollment> enrollments = enrollmentStore.query(trackedEntityInstance);
            enrollmentController.sendEnrollmentChanges(enrollments);
        }
    }

    /**
     * Updates a TrackedEntityInstance on the server with a POST request
     *
     * @param trackedEntityInstance
     */
    public void postTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        try {
            ImportSummary importSummary = trackedEntityInstanceApiClient
                    .postTrackedEntityInstance(trackedEntityInstance);
            handleImportSummary(trackedEntityInstance, importSummary);
        } catch (ApiException apiException) {
//            handleTrackedEntityInstanceSendException(apiException, failedItemStore,
// trackedEntityInstance);
        }
    }

    /**
     * Registers a TrackedEntityInstance on the server with a PUT request
     *
     * @param trackedEntityInstance
     */
    public void putTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        try {
            ImportSummary importSummary = trackedEntityInstanceApiClient.putTrackedEntityInstance
                    (trackedEntityInstance);
            handleImportSummary(trackedEntityInstance, importSummary);
        } catch (ApiException apiException) {
//            handleTrackedEntityInstanceSendException(apiException, failedItemStore,
// trackedEntityInstance);
        }
    }

    /**
     * Handles an ImportSummary typically from a post or put request to server by {@link
     * #putTrackedEntityInstance(TrackedEntityInstance)} or
     * {@link #postTrackedEntityInstance(TrackedEntityInstance)}. Handling includes updating the
     * State for the object and
     * FailedItem for the object, and triggers fetching of lastUpdated and created timestamps
     *
     * @param trackedEntityInstance
     * @param importSummary
     */
    public void handleImportSummary(TrackedEntityInstance trackedEntityInstance, ImportSummary
            importSummary) {
        if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
                ImportSummary.Status.OK.equals(importSummary.getStatus())) {
            stateStore.saveActionForModel(trackedEntityInstance, Action.SYNCED);
//            clearFailedItem(FailedItemType.TRACKED_ENTITY_INSTANCE, failedItemStore,
// trackedEntityInstance.getId());
            updateTrackedEntityInstanceTimestamp(trackedEntityInstance);
        } else {
//            handleImportSummaryWithError(importSummary, failedItemStore, FailedItemType
// .TRACKED_ENTITY_INSTANCE, trackedEntityInstance.getId());
        }
    }

    /**
     * Updates the timestamps of created and lastUpdated for the given TrackedEntityInstance
     * based on the values
     * stored on the online server
     *
     * @param trackedEntityInstance
     */
    public void updateTrackedEntityInstanceTimestamp(TrackedEntityInstance trackedEntityInstance) {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "created,lastUpdated");
            TrackedEntityInstance updatedTrackedEntityInstance = trackedEntityInstanceApiClient
                    .getFullTrackedEntityInstance(trackedEntityInstance
                            .getTrackedEntityInstanceUid(), null);

            // merging updated timestamp to local trackedentityinstance model
            trackedEntityInstance.setCreated(updatedTrackedEntityInstance.getCreated());
            trackedEntityInstance.setLastUpdated(updatedTrackedEntityInstance.getLastUpdated());
            trackedEntityInstanceStore.save(trackedEntityInstance);
        } catch (ApiException apiException) {
            apiException.printStackTrace();
            trackedEntityInstance.setLastUpdated(new DateTime(1970, 1, 1, 0, 0));
            trackedEntityInstanceStore.save(trackedEntityInstance);
        }
    }

    @Override
    public void sync() throws ApiException {
    }

    @Override
    public void sync(List<TrackedEntityInstance> trackedEntityInstances, boolean getEnrollments) {
        getTrackedEntityInstancesDataFromServer(trackedEntityInstances, getEnrollments);
    }

    @Override
    public TrackedEntityInstance sync(String uid, boolean getEnrollments) throws ApiException {
        return getTrackedEntityInstanceDataFromServer(uid, getEnrollments);
    }

    @Override
    public List<TrackedEntityInstance> queryServerTrackedEntityInstances(String organisationUnitUid,
                                                                         String programUid,
                                                                         String queryString,
                                                                         TrackedEntityAttributeValue... params) throws ApiException {
        return queryTrackedEntityInstancesDataFromServer(organisationUnitUid, programUid,
                queryString, params);
    }
}
