/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.corejava;

import android.net.Uri;

import org.hisp.dhis.android.sdk.corejava.common.PushableDataController;
import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.core.api.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.core.models.ResourceType;
import org.hisp.dhis.android.sdk.core.api.utils.DbUtils;
import org.hisp.dhis.android.sdk.core.api.utils.NetworkUtils;
import org.hisp.dhis.android.sdk.models.common.base.IStore;
import org.hisp.dhis.android.sdk.models.common.faileditem.FailedItemType;
import org.hisp.dhis.android.sdk.models.common.faileditem.IFailedItemStore;
import org.hisp.dhis.android.sdk.models.common.importsummary.ImportSummary;
import org.hisp.dhis.android.sdk.models.common.meta.DbOperation;
import org.hisp.dhis.android.sdk.models.common.meta.IDbOperation;
import org.hisp.dhis.android.sdk.models.common.state.Action;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.enrollment.IEnrollmentStore;
import org.hisp.dhis.android.sdk.models.relationship.IRelationshipStore;
import org.hisp.dhis.android.sdk.models.relationship.Relationship;
import org.hisp.dhis.android.sdk.models.trackedentity.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.android.sdk.models.trackedentity.ITrackedEntityInstanceStore;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit.client.Header;
import retrofit.client.Response;

import static org.hisp.dhis.android.sdk.core.api.utils.NetworkUtils.unwrapResponse;

public final class TrackedEntityInstanceController extends PushableDataController implements ITrackedEntityInstanceController {

    private final static String TRACKEDENTITYINSTANCES = "trackedEntityInstances";

    private final IDhisApi mDhisApi;
    private final IEnrollmentController enrollmentController;
    private final ITrackedEntityInstanceStore trackedEntityInstanceStore;
    private final IStateStore stateStore;
    private final IFailedItemStore failedItemStore;
    private final IRelationshipStore relationshipStore;
    private final ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private final IEnrollmentStore enrollmentStore;

    public TrackedEntityInstanceController(IDhisApi dhisApi,
                                           IEnrollmentController enrollmentController,
                                           ITrackedEntityInstanceStore trackedEntityInstanceStore,
                                           IStateStore stateStore, IFailedItemStore failedItemStore,
                                           IRelationshipStore relationshipStore,
                                           ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore,
                                           IEnrollmentStore enrollmentStore) {
        mDhisApi = dhisApi;
        this.enrollmentController = enrollmentController;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.stateStore = stateStore;
        this.failedItemStore = failedItemStore;
        this.relationshipStore = relationshipStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.enrollmentStore = enrollmentStore;
    }

    /**
     * Queries the server and returns a list of tracked entity instances based on the given parameters
     * The returned tracked entity instances will only contain basic information. More information, like
     * enrollments and events need to be loaded with either getTrackedEntityInstancesDataFromServer(List),
     * or getTrackedEntityInstanceDataFromServer(String uid);
     *
     * @param organisationUnitUid
     * @param programUid
     * @param queryString
     * @param params
     * @return
     * @throws APIException
     */
    private List<TrackedEntityInstance> queryTrackedEntityInstancesDataFromServer(String organisationUnitUid,
                                                                                  String programUid,
                                                                                  String queryString,
                                                                                  TrackedEntityAttributeValue... params) throws APIException {
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
                                .getTrackedEntityAttributeUId() + ":LIKE:" + trackedEntityAttributeValue
                                .getValue());
                    }
                }
            }
        }

        //doesnt work with both attribute filter and query
        if (queryString != null && !queryString.isEmpty() && valueParams.isEmpty()) {
            QUERY_MAP_FULL.put("query", "LIKE:" + queryString);//todo: make a map where we can use more than one of each key
        }
        List<TrackedEntityInstance> trackedEntityInstances = NetworkUtils.unwrapResponse(mDhisApi
                .getTrackedEntityInstances(organisationUnitUid,
                        QUERY_MAP_FULL), TRACKEDENTITYINSTANCES);
        return trackedEntityInstances;
    }

    /**
     * Loads a list of trackedEntityInstances from the server and stores to the local database.
     *
     * @param trackedEntityInstances
     * @param getEnrollments         set to true if you want to load enrollments with the trackedEntityInstance
     */
    private void getTrackedEntityInstancesDataFromServer(List<TrackedEntityInstance> trackedEntityInstances, boolean getEnrollments) {
        if (trackedEntityInstances == null) {
            return;
        }
        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            try {
                getTrackedEntityInstanceDataFromServer(trackedEntityInstance.getTrackedEntityInstanceUid(), getEnrollments);
            } catch (APIException e) { //can't throw this further up because we want to continue loading all the TEIs..
                e.printStackTrace();
            }
        }
    }

    private TrackedEntityInstance getTrackedEntityInstanceDataFromServer(String uid, boolean getEnrollments) throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.TRACKEDENTITYINSTANCE, uid);
        DateTime serverDateTime = mDhisApi.getSystemInfo()
                .getServerDate();

        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        TrackedEntityInstance updatedTrackedEntityInstance = mDhisApi.getTrackedEntityInstance(uid, QUERY_MAP_FULL);
        TrackedEntityInstance persistedTrackedEntityInstance = trackedEntityInstanceStore.query(uid);
        if (persistedTrackedEntityInstance != null) {
            updatedTrackedEntityInstance.setId(persistedTrackedEntityInstance.getId());
            if (updatedTrackedEntityInstance.getLastUpdated().isAfter(persistedTrackedEntityInstance.getLastUpdated())) {
                DbOperation.with(trackedEntityInstanceStore).update(updatedTrackedEntityInstance).execute();
            }
        } else {
            DbOperation.with(trackedEntityInstanceStore).insert(updatedTrackedEntityInstance).execute();
        }

        List<IDbOperation> operations = new ArrayList<>();
        if (updatedTrackedEntityInstance.getAttributes() != null) {
            for (TrackedEntityAttributeValue value : updatedTrackedEntityInstance.getAttributes()) {
                if (value != null) {
                    value.setTrackedEntityInstance(updatedTrackedEntityInstance);
                    operations.add(DbOperation.with(trackedEntityAttributeValueStore).save(value));
                }
            }
        }
        List<Relationship> updatedRelationships = updatedTrackedEntityInstance.getRelationships();
        List<Relationship> persistedRelationships = null;
        if (persistedTrackedEntityInstance != null) {
            persistedRelationships = persistedTrackedEntityInstance.getRelationships();
        }
        if (updatedRelationships != null) {
            operations.addAll(createOperations(relationshipStore, persistedRelationships, updatedRelationships));
        }
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.TRACKEDENTITYINSTANCE, uid, serverDateTime);
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
    private List<DbOperation> createOperations(IStore<Relationship> modelStore,
                                               List<Relationship> oldModels,
                                               List<Relationship> newModels) {
        List<DbOperation> ops = new ArrayList<>();

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
                    ops.add(DbOperation.with(modelStore)
                            .delete(oldModel));
                }

                // in case if there is no new model object,
                // we can jump to next iteration.
                continue;
            }

            newModel.setId(oldModel.getId());
            ops.add(DbOperation.with(modelStore)
                    .update(newModel));

            // as we have processed given old (persisted) model,
            // we can remove it from map of new models.
            newModelsMap.remove(oldModelKey);
        }

        // Inserting new items.
        for (String newModelKey : newModelsMap.keySet()) {
            Relationship item = newModelsMap.get(newModelKey);
            ops.add(DbOperation.with(modelStore)
                    .insert(item));
        }

        return ops;
    }

    public static Map<String, Relationship> toMap(List<Relationship> objects) {
        Map<String, Relationship> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (Relationship object : objects) {
                if (object.getTrackedEntityInstanceA() != null && object.getTrackedEntityInstanceB() != null && object.getRelationship() != null) {
                    map.put(object.getTrackedEntityInstanceA().getTrackedEntityInstanceUid()
                            + object.getTrackedEntityInstanceB().getTrackedEntityInstanceUid()
                            + object.getRelationship(), object);
                }
            }
        }
        return map;
    }

    private void sendTrackedEntityInstanceChanges(boolean sendEnrollments) throws APIException {
        List<TrackedEntityInstance> trackedEntityInstances = getLocallyChangedTrackedEntityInstances();
        sendTrackedEntityInstanceChanges(trackedEntityInstances, sendEnrollments);
    }

    private List<TrackedEntityInstance> getLocallyChangedTrackedEntityInstances() {
        List<TrackedEntityInstance> toPost = stateStore.filterModelsByAction(TrackedEntityInstance.class, Action.TO_POST);
        List<TrackedEntityInstance> toPut = stateStore.filterModelsByAction(TrackedEntityInstance.class, Action.TO_UPDATE);
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();
        trackedEntityInstances.addAll(toPost);
        trackedEntityInstances.addAll(toPut);
        return trackedEntityInstances;
    }

    private void sendTrackedEntityInstanceChanges(List<TrackedEntityInstance> trackedEntityInstances, boolean sendEnrollments) throws APIException {
        if (trackedEntityInstances == null || trackedEntityInstances.isEmpty()) {
            return;
        }

        Map<Long, Action> actionMap = stateStore
                .queryActionsForModel(TrackedEntityInstance.class);

        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            sendTrackedEntityInstanceChanges(trackedEntityInstance, actionMap.get(trackedEntityInstance.getId()), sendEnrollments);
        }
    }

    private void sendTrackedEntityInstanceChanges(TrackedEntityInstance trackedEntityInstance, Action action, boolean sendEnrollments) throws APIException {
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
            enrollmentController.sendEnrollmentChanges(enrollments, sendEnrollments);
        }
    }

    private void postTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) throws APIException {
        try {
            Response response = mDhisApi.postTrackedEntityInstance(trackedEntityInstance);
            if (response.getStatus() == 200) {
                ImportSummary importSummary = getImportSummary(response);
                handleImportSummary(importSummary, failedItemStore, FailedItemType.TRACKED_ENTITY_INSTANCE, trackedEntityInstance.getId());
                if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.Status.OK.equals(importSummary.getStatus())) {

                    // also, we will need to find UUID of newly created trackedentityinstance,
                    // which is contained inside of HTTP Location header
                    Header header = NetworkUtils.findLocationHeader(response.getHeaders());
                    // parse the value of header as URI and extract the id
                    String trackedEntityInstanceUid = Uri.parse(header.getValue()).getLastPathSegment();
                    // set UUID, change state and save trackedentityinstance
                    String oldUid = trackedEntityInstance.getTrackedEntityInstanceUid();
                    trackedEntityInstance.setTrackedEntityInstanceUid(trackedEntityInstanceUid);
                    stateStore.saveActionForModel(trackedEntityInstance, Action.SYNCED);
                    trackedEntityInstanceStore.save(trackedEntityInstance);
                    clearFailedItem(FailedItemType.TRACKED_ENTITY_INSTANCE, failedItemStore, trackedEntityInstance.getId());
                    UpdateTrackedEntityInstanceTimestamp(trackedEntityInstance);
                }
            }
        } catch (APIException apiException) {
            handleTrackedEntityInstanceSendException(apiException, failedItemStore, trackedEntityInstance);
        }
    }

    private void putTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) throws APIException {
        try {
            Response response = mDhisApi.putTrackedEntityInstance(trackedEntityInstance.getTrackedEntityInstanceUid(), trackedEntityInstance);
            if (response.getStatus() == 200) {
                ImportSummary importSummary = getImportSummary(response);
                handleImportSummary(importSummary, failedItemStore, FailedItemType.TRACKED_ENTITY_INSTANCE, trackedEntityInstance.getId());
                if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.Status.OK.equals(importSummary.getStatus())) {
                    stateStore.saveActionForModel(trackedEntityInstance, Action.SYNCED);
                    trackedEntityInstanceStore.save(trackedEntityInstance);
                    clearFailedItem(FailedItemType.TRACKED_ENTITY_INSTANCE, failedItemStore, trackedEntityInstance.getId());
                    UpdateTrackedEntityInstanceTimestamp(trackedEntityInstance);
                }
            }
        } catch (APIException apiException) {
            handleTrackedEntityInstanceSendException(apiException, failedItemStore, trackedEntityInstance);
        }
    }

    private void UpdateTrackedEntityInstanceTimestamp(TrackedEntityInstance trackedEntityInstance) throws APIException {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "created,lastUpdated");
            TrackedEntityInstance updatedTrackedEntityInstance = mDhisApi
                    .getTrackedEntityInstance(trackedEntityInstance.getTrackedEntityInstanceUid(), QUERY_PARAMS);

            // merging updated timestamp to local trackedentityinstance model
            trackedEntityInstance.setCreated(updatedTrackedEntityInstance.getCreated());
            trackedEntityInstance.setLastUpdated(updatedTrackedEntityInstance.getLastUpdated());
            trackedEntityInstanceStore.save(trackedEntityInstance);
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException);
        }
    }

    @Override
    public void sync() throws APIException {
    }

    @Override
    public void sync(List<TrackedEntityInstance> trackedEntityInstances, boolean getEnrollments) {
        getTrackedEntityInstancesDataFromServer(trackedEntityInstances, getEnrollments);
    }

    @Override
    public TrackedEntityInstance sync(String uid, boolean getEnrollments) throws APIException {
        return getTrackedEntityInstanceDataFromServer(uid, getEnrollments);
    }

    @Override
    public List<TrackedEntityInstance> queryServerTrackedEntityInstances(String organisationUnitUid,
                                                                         String programUid,
                                                                         String queryString,
                                                                         TrackedEntityAttributeValue... params) throws APIException {
        return queryTrackedEntityInstancesDataFromServer(organisationUnitUid, programUid, queryString, params);
    }
}
