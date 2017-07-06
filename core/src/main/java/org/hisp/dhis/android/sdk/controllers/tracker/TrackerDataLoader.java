/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.controllers.tracker;

import static org.hisp.dhis.android.sdk.utils.NetworkUtils.unwrapResponse;
import static org.hisp.dhis.client.sdk.utils.StringUtils.isEmpty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.ApiEndpointContainer;
import org.hisp.dhis.android.sdk.controllers.LoadingController;
import org.hisp.dhis.android.sdk.controllers.ResourceController;
import org.hisp.dhis.android.sdk.controllers.SyncStrategy;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.controllers.wrappers.EventsWrapper;
import org.hisp.dhis.android.sdk.events.LoadingMessageEvent;
import org.hisp.dhis.android.sdk.events.OnTeiDownloadedEvent;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.DbUtils;
import org.hisp.dhis.android.sdk.utils.UiUtils;
import org.hisp.dhis.android.sdk.utils.Utils;
import org.hisp.dhis.android.sdk.utils.api.ProgramType;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Simen Skogly Russnes on 24.08.15.
 */
final class TrackerDataLoader extends ResourceController {

    public static final String CLASS_TAG = TrackerDataLoader.class.getSimpleName();

    private TrackerDataLoader() {
    }

    /**
     * Loads datavalue items that is scheduled to be loaded but has not yet been.
     */
    static void updateDataValueDataItems(Context context, DhisApi dhisApi,
            SyncStrategy syncStrategy) throws APIException {
        if (dhisApi == null) {
            return;
        }
        SystemInfo serverSystemInfo = dhisApi.getSystemInfo();
        DateTime serverDateTime = serverSystemInfo.getServerDate();
        List<OrganisationUnit> assignedOrganisationUnits =
                MetaDataController.getAssignedOrganisationUnits();
        Hashtable<String, List<Program>> programsForOrganisationUnits = new Hashtable<>();
        List<TrackedEntityInstance> trackedEntityInstances = MetaDataController.getTrackedEntityInstancesFromServer();
        if(syncStrategy.equals(SyncStrategy.DOWNLOAD_ALL)) {
            TrackerController.updateTrackedEntityInstances(dhisApi, trackedEntityInstances,
                    serverDateTime);
        }
        //check if events is updated on server
        //List<Enrollment> activeEnrollments = TrackerController.getActiveEnrollments();
        //updateEventsForEnrollments(context, dhisApi, activeEnrollments, serverDateTime);

        if (LoadingController.isLoadFlagEnabled(context, ResourceType.EVENTS)) {
            for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
                if (organisationUnit.getId() == null
                        || organisationUnit.getId().length() == Utils.randomUUID.length()) {
                    continue;
                }

                List<Program> programsForOrgUnit = new ArrayList<>();
                List<Program> programsForOrgUnitFromDB =
                        MetaDataController.getProgramsForOrganisationUnit
                                (organisationUnit.getId(),
                                        ProgramType.WITHOUT_REGISTRATION, ProgramType.WITH_REGISTRATION);
                if (programsForOrgUnitFromDB != null) {
                    programsForOrgUnit.addAll(programsForOrgUnitFromDB);
                }

                programsForOrganisationUnits.put(organisationUnit.getId(), programsForOrgUnit);
            }

            for (final OrganisationUnit organisationUnit : assignedOrganisationUnits) {
                if (organisationUnit.getId() == null
                        || organisationUnit.getId().length() == Utils.randomUUID.length()) {
                    continue;
                }

                for (final Program program : programsForOrganisationUnits.get(
                        organisationUnit.getId())) {
                    if (program.getUid() == null
                            || program.getUid().length() == Utils.randomUUID.length()) {
                        continue;
                    }

                    if (shouldLoad(serverDateTime, ResourceType.EVENTS,
                            organisationUnit.getId() + program.getUid())) {
                        UiUtils.postProgressMessage(
                                context.getString(R.string.loading_events) + ": "
                                        + organisationUnit.getLabel() + ": " + program.getName(),
                                LoadingMessageEvent.EventType.DATA);
                        try {
                            getEventsDataFromServer(dhisApi, syncStrategy, organisationUnit.getId(),
                                    program.getUid(), serverDateTime);
                        } catch (APIException e) {
                            e.printStackTrace();
                            //todo: could probably do something prettier here. This catch is done
                            // to prevent
                            // stopping loading of the following program/orgUnit as throwing and
                            // exception would exit the loop..
                        }
                    }
                }
            }
        }
        UiUtils.postProgressMessage("", LoadingMessageEvent.EventType.FINISH);
    }

    static void updateEventsForEnrollments(Context context, DhisApi dhisApi,
            List<Enrollment> enrollments, DateTime serverDateTime) {
        DateTime lastUpdated = DateTimeManager.getInstance().getLastUpdated(ResourceType.EVENTS);
        String delimiter = ";";
        boolean failed = false;
        String trackedEntityInstanceQueryParams = "trackedEntityInstance";
        Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        Map<String, List<Enrollment>> programToEnrollmentMap = mapActiveEnrollmentsByProgram(
                enrollments);
        List<Event> eventsFromServer = new ArrayList<>();
        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("lastUpdated", lastUpdated.toString());
        }

        if (programToEnrollmentMap.keySet().size() > 0) {
            StringBuilder sb = new StringBuilder();
            Set<String> programUids = programToEnrollmentMap.keySet();

            for (String programUid : programUids) {
                List<Enrollment> enrollmentsForProgram = programToEnrollmentMap.get(programUid);
                for (Enrollment enrollment : enrollmentsForProgram) {
                    sb.append(enrollment.getTrackedEntityInstance() + delimiter);
                }
                QUERY_MAP_FULL.put(trackedEntityInstanceQueryParams, sb.toString());
                try {
                    List<Event> eventsForTrackedEntityInstance =
                            dhisApi.getEventsForTrackedEntityInstance(programUid, QUERY_MAP_FULL);
                    if (eventsForTrackedEntityInstance != null) {
                        eventsFromServer.addAll(
                                dhisApi.getEventsForTrackedEntityInstance(programUid,
                                        QUERY_MAP_FULL));
                    }
                } catch (APIException apiException) {
                    apiException.printStackTrace();
                    failed = true;
                }
            }
        }

        if (!failed) {
            saveResourceDataFromServer(ResourceType.EVENTS, dhisApi, eventsFromServer, null,
                    serverDateTime);
            DateTimeManager.getInstance().setLastUpdated(ResourceType.EVENTS, serverDateTime);
        }

    }


    /**
     * Loads datavalue items that is scheduled to be loaded but has not yet been.
     */
    static void deleteRemotelyDeletedTrackedEntityInstances(Context context, DhisApi dhisApi,
            Hashtable<String, List<Program>> myProgramsByOrganisationUnit) {
            for (String organisationUnitUid : myProgramsByOrganisationUnit.keySet()) {

                    UiUtils.postProgressMessage(
                            context.getString(R.string.sync_deleted_tracked_entities) + ": "
                                    + organisationUnitUid,
                            LoadingMessageEvent.EventType.REMOVE_DATA);

                List<TrackedEntityInstance> localTrackedEntityInstances =
                        TrackerController.getTrackedEntityInstances(organisationUnitUid);

                HashMap<String, List<TrackedEntityInstance>> mapTrackedEntityInstances =
                        groupTrackedEntityInstancesByTrackedEntity(localTrackedEntityInstances);

                for (String trackedEntity : mapTrackedEntityInstances.keySet()) {
                    List<TrackedEntityInstance> trackedEntityInstanceList =
                            mapTrackedEntityInstances.get(trackedEntity);

                    try {
                        deleteRemotelyDeletedTrackedEntityInstances(dhisApi, organisationUnitUid,
                                trackedEntity, trackedEntityInstanceList);
                    } catch (APIException e) {
                        e.printStackTrace();
                        //todo: could probably do something prettier here. This catch is done to
                        // prevent
                        // stopping loading of the following program/orgUnit as throwing and
                        // exception would exit the loop..
                    }
                }
            }

        deleteRemotelyDeletedEnrollments(context, dhisApi, myProgramsByOrganisationUnit);
    }

    private static void deleteRemotelyDeletedEnrollments(Context context, DhisApi dhisApi,
            Hashtable<String, List<Program>> myProgramsByOrganisationUnit) {
        for (String organisationUnitUid : myProgramsByOrganisationUnit.keySet()) {

            UiUtils.postProgressMessage(
                    context.getString(R.string.sync_deleted_enrollments) + ": "
                            + organisationUnitUid, LoadingMessageEvent.EventType.REMOVE_DATA);

            List<TrackedEntityInstance> localTrackedEntityInstances =
                    TrackerController.getTrackedEntityInstances(organisationUnitUid);

            HashMap<String, List<TrackedEntityInstance>> mapTrackedEntityInstances =
                    groupTrackedEntityInstancesByTrackedEntity(localTrackedEntityInstances);

            for (String trackedEntity : mapTrackedEntityInstances.keySet()) {
                List<TrackedEntityInstance> trackedEntityInstanceList =
                        mapTrackedEntityInstances.get(trackedEntity);

                ArrayList<Enrollment> enrollments = new ArrayList<>();
                for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstanceList) {
                    enrollments.addAll(TrackerController.getEnrollments(trackedEntityInstance));
                }

                try {
                    deleteRemotelyDeletedEnrollments(dhisApi, organisationUnitUid,
                            trackedEntity, enrollments);
                } catch (APIException e) {
                    e.printStackTrace();
                    //todo: could probably do something prettier here. This catch is done to
                    // prevent
                    // stopping loading of the following program/orgUnit as throwing and
                    // exception would exit the loop..
                }
            }
        }
        deleteRemotelyDeletedRelationships(context, dhisApi, myProgramsByOrganisationUnit);
    }

    private static void deleteRemotelyDeletedRelationships(Context context, DhisApi dhisApi,
            Hashtable<String, List<Program>> myProgramsByOrganisationUnit) {

        for (String organisationUnitUid : myProgramsByOrganisationUnit.keySet()) {
            for (Program program : myProgramsByOrganisationUnit.get(organisationUnitUid)) {
                if (program.getUid() == null
                        || program.getUid().length() == Utils.randomUUID.length()) {
                    continue;
                }

                UiUtils.postProgressMessage(
                        context.getString(R.string.sync_deleted_relations) + ": "
                                + organisationUnitUid + ": " + program.getName(),
                        LoadingMessageEvent.EventType.REMOVE_DATA);

                List<TrackedEntityInstance> localTrackedEntityInstances =
                        TrackerController.getTrackedEntityInstances();
                try {
                    for (TrackedEntityInstance trackedEntityInstance :
                            localTrackedEntityInstances) {
                        if (trackedEntityInstance.getRelationships() != null
                                && trackedEntityInstance.getRelationships().size() > 0) {
                            refreshRelationshipsByTrackedEntityInstance(dhisApi, trackedEntityInstance.getUid());
                        }
                    }
                } catch (APIException e) {
                    e.printStackTrace();
                    //todo: could probably do something prettier here. This catch is done to
                    // prevent
                    // stopping loading of the following program/orgUnit as throwing and
                    // exception would exit the loop..
                }
            }
        }
    }

    @NonNull
    private static HashMap<String, List<TrackedEntityInstance>>
    groupTrackedEntityInstancesByTrackedEntity(
            List<TrackedEntityInstance> localTrackedEntityInstances) {
        HashMap<String, List<TrackedEntityInstance>> mapTrackedEntityInstances =
                new HashMap<>();
        for (TrackedEntityInstance localTrackedEntityInstance :
                localTrackedEntityInstances) {
            String trackedEntityUid = localTrackedEntityInstance.getTrackedEntity();
            if (mapTrackedEntityInstances.containsKey(trackedEntityUid)) {
                mapTrackedEntityInstances.get(trackedEntityUid).add(localTrackedEntityInstance);
            } else {
                List<TrackedEntityInstance> trackedEntityInstancesList = new ArrayList<>();
                trackedEntityInstancesList.add(localTrackedEntityInstance);
                mapTrackedEntityInstances.put(trackedEntityUid,
                        trackedEntityInstancesList);
            }
        }
        return mapTrackedEntityInstances;
    }

    static void deleteRemotelyDeletedData(Context context, DhisApi dhisApi) throws APIException {
        Hashtable<String, List<Program>> myProgramsByOrganisationUnit = new Hashtable<>();

        myProgramsByOrganisationUnit =
                MetaDataController.getAssignedProgramsByOrganisationUnit();
        for (String organisationUnitUid : myProgramsByOrganisationUnit.keySet()) {
            for (Program program : myProgramsByOrganisationUnit.get(organisationUnitUid)) {
                if (program.getUid() == null
                        || program.getUid().length() == Utils.randomUUID.length()) {
                    continue;
                }

                UiUtils.postProgressMessage(
                        context.getString(R.string.sync_deleted_events) + ": "
                                + organisationUnitUid + ": " + program.getName(),
                        LoadingMessageEvent.EventType.REMOVE_DATA);

                try {
                    deleteRemotelyDeletedEvents(dhisApi, organisationUnitUid, program.getUid());
                } catch (APIException e) {
                    e.printStackTrace();
                    //todo: could probably do something prettier here. This catch is done to
                    // prevent
                    // stopping loading of the following program/orgUnit as throwing and
                    // exception would exit the loop..
                }
            }
        }

        deleteRemotelyDeletedTrackedEntityInstances(context, dhisApi, myProgramsByOrganisationUnit);
    }

    private static void deleteRemotelyDeletedEnrollments(DhisApi dhisApi,
            String organisationUnitUid, String trackedEntityUid,
            ArrayList<Enrollment> enrollments) {
        Log.d(CLASS_TAG, "getTrackedEntityInstances");
        final Map<String, String> map = new HashMap<>();
        map.put("fields", "enrollment");
        map.put("trackedEntity", trackedEntityUid);

        if (enrollments.size() == 0) {
            return;
        }

        List<Enrollment> remoteEnrollments = new ArrayList<>();
        Map<String, List<Enrollment>> enrollmentList = dhisApi.getEnrollmentsByOrgUnit(organisationUnitUid,
                map);
        for (String enrollmentKey : enrollmentList.keySet()) {
            remoteEnrollments.addAll(
                    enrollmentList.get(enrollmentKey));
        }
        List<Enrollment> localEnrollmentToBeRemoved = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            boolean isRemoved = true;
            for (Enrollment remoteEnrollment : remoteEnrollments) {
                if (remoteEnrollment.getUid().equals(enrollment.getUid())) {
                    isRemoved = false;
                    break;
                }
            }
            if (isRemoved) {
                localEnrollmentToBeRemoved.add(enrollment);
            }
        }

        ResourceController.removeTrackedEntityEnrollments(localEnrollmentToBeRemoved);
    }

    private static void deleteRemotelyDeletedTrackedEntityInstances(DhisApi dhisApi,
            String organisationUnitUid, String trackedEntityUid,
            List<TrackedEntityInstance> localTrackedEntityInstances) {
        Log.d(CLASS_TAG, "getTrackedEntityInstances");
        final Map<String, String> map = new HashMap<>();
        map.put("fields", "trackedEntityInstance,[attributes]");
        map.put("trackedEntity", trackedEntityUid);

        if (localTrackedEntityInstances.size() == 0) {
            return;
        }

        List<TrackedEntityInstance> remoteTrackedEntityInstances = new ArrayList<>();
        Map<String, List<TrackedEntityInstance>> trackedEntityInstancesList =
                dhisApi.getTrackedEntityInstances(organisationUnitUid,
                        map);
        for (String trackedEntityInstanceKey : trackedEntityInstancesList.keySet()) {
            remoteTrackedEntityInstances.addAll(
                    trackedEntityInstancesList.get(trackedEntityInstanceKey));
        }
        List<TrackedEntityInstance> localTrackedEntityInstancesToBeRemoved =
                new ArrayList<>();
        for (TrackedEntityInstance trackedEntityInstance : localTrackedEntityInstances) {
            boolean isRemoved = true;
            for (TrackedEntityInstance remoteTrackedEntityInstance : remoteTrackedEntityInstances) {
                if (remoteTrackedEntityInstance.getUid().equals(trackedEntityInstance.getUid())) {
                    if(remoteTrackedEntityInstance.getAttributes().size()>1) {
                        isRemoved = false;
                        break;
                    }
                }
            }
            if (isRemoved) {
                localTrackedEntityInstancesToBeRemoved.add(trackedEntityInstance);
            }
        }

        ResourceController.removeTrackedEntityInstances(localTrackedEntityInstancesToBeRemoved);
    }

    static void deleteRemotelyDeletedEvents(DhisApi dhisApi, String organisationUnitUid,
            String programUid) throws APIException {
        Log.d(CLASS_TAG, "getEventsDataFromServer");
        final Map<String, String> map = new HashMap<>();
        map.put("fields", "[event]");
        map.put("skipPaging", "true");

        List<Event> localEvents = TrackerController.getAllConflictingAndNotConflictingEvents(organisationUnitUid, programUid,
                true);
        if (localEvents.size() == 0) {
            return;
        }

        JsonNode response = dhisApi.getEventUids(programUid, organisationUnitUid,
                map);

        List<Event> remoteEvents = EventsWrapper.getEvents(response);
        List<Event> eventsToBeRemoved = new ArrayList<>();
        for (Event localEvent : localEvents) {
            boolean isRemoved = true;
            for (Event remoteEvent : remoteEvents) {
                if (remoteEvent.getEvent().equals(localEvent.getEvent())) {
                    isRemoved = false;
                    break;
                }
            }
            if (isRemoved) {
                eventsToBeRemoved.add(localEvent);
            }
        }

        ResourceController.removeEvents(eventsToBeRemoved);
    }

    static Map<String, List<Enrollment>> mapActiveEnrollmentsByProgram(List<Enrollment>
            enrollments) {
        Map<String, List<Enrollment>> programToEnrollmentMap = new HashMap<>();

        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getProgram() != null
                    // if enrollment exists, is active and have a trackedEntityInstance
                    && Enrollment.ACTIVE.equals(enrollment.getStatus())
                    && !isEmpty(enrollment.getTrackedEntityInstance())) {

                if (!programToEnrollmentMap.containsKey(enrollment.getProgram().getUid())) {
                    List<Enrollment> enrollmentForProgram = new ArrayList<>();
                    enrollmentForProgram.add(enrollment);
                    programToEnrollmentMap.put(enrollment.getProgram().getUid(),
                            enrollmentForProgram);
                } else {
                    programToEnrollmentMap.get(enrollment.getProgram().getUid()).add(
                            enrollment); // adding enrollment to list
                }
            }
        }
        return programToEnrollmentMap;
    }

    static void updateEnrollments(Context context, DhisApi dhisApi, List<Enrollment> enrollments) {
        DateTime lastUpdated = DateTimeManager.getInstance().getLastUpdated(
                ResourceType.ENROLLMENTS);
    }

    static void updateEvents(Context context, DhisApi dhisApi, List<Event> events) {
        DateTime lastUpdated = DateTimeManager.getInstance().getLastUpdated(ResourceType.EVENTS);

        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        if (events != null && events.size() > 0) {
            QUERY_MAP_FULL.put("program", "");
        }
    }

    static void getEventsDataFromServer(DhisApi dhisApi, SyncStrategy syncStrategy,
            String organisationUnitUid, String programUid, DateTime serverDateTime)
            throws APIException {
        if (dhisApi == null) {
            return;
        }
        Log.d(CLASS_TAG, "getEventsDataFromServer");

        DateTime lastUpdated = null;

        if (syncStrategy == SyncStrategy.DOWNLOAD_ONLY_NEW) {
            DateTimeManager.getInstance()
                    .getLastUpdated(ResourceType.EVENTS, organisationUnitUid + programUid);
        }

        final Map<String, String> map = new HashMap<>();
        map.put("fields", "[:all]");
        if (lastUpdated != null) {
            map.put("lastUpdated", lastUpdated.toString());
        }
        List<Event> events = dhisApi.getEvents(programUid, organisationUnitUid, 50,
                map);
        saveResourceDataFromServer(ResourceType.EVENTS, organisationUnitUid + programUid, dhisApi,
                events, null, serverDateTime);
    }

    static TrackedEntityInstance queryTrackedEntityInstanceDataFromServer(DhisApi dhisApi,
            String trackedEntityInstanceUid) throws APIException {
        final Map<String, String> map = new HashMap<>();
        if (dhisApi == null) {
            return null;
        }
        TrackedEntityInstance trackedEntityInstance = dhisApi
                .getTrackedEntityInstance(trackedEntityInstanceUid, map);
        return trackedEntityInstance;
    }

    static List<TrackedEntityInstance> queryTrackedEntityInstanceDataFromServer(DhisApi dhisApi,
            String organisationUnitUid,
            String programUid,
            String queryString,
            TrackedEntityAttributeValue... params) throws APIException {
        if (dhisApi == null) {
            return null;
        }
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        if (programUid != null) {
            QUERY_MAP_FULL.put("program", programUid);
        }
        List<TrackedEntityAttributeValue> valueParams = new LinkedList<>();
        if (params != null) {
            for (TrackedEntityAttributeValue teav : params) {
                if (teav != null && teav.getValue() != null) {
                    if (!teav.getValue().isEmpty()) {
                        valueParams.add(teav);
//                        QUERY_MAP_FULL.put("filter",teav.getTrackedEntityAttributeId()
// +":LIKE:"+teav.getValue());
                    }
                }
            }
        }
        for (TrackedEntityAttributeValue val : valueParams) {
            if (!QUERY_MAP_FULL.containsKey("filter")) {
                QUERY_MAP_FULL.put("filter",
                        val.getTrackedEntityAttributeId() + ":LIKE:" + val.getValue());
            } else {
                String currentFilter = QUERY_MAP_FULL.get("filter");
                QUERY_MAP_FULL.put("filter",
                        currentFilter + "&filter=" + val.getTrackedEntityAttributeId() + ":LIKE:"
                                + val.getValue());
            }
        }


        //doesnt work with both attribute filter and query
        if (queryString != null && !queryString.isEmpty() && valueParams.isEmpty()) {
            QUERY_MAP_FULL.put("query", "LIKE:"
                    + queryString);//todo: make a map where we can use more than one of each key
        }
        List<TrackedEntityInstance> trackedEntityInstances = unwrapResponse(dhisApi
                .getTrackedEntityInstances(organisationUnitUid,
                        QUERY_MAP_FULL), ApiEndpointContainer.TRACKED_ENTITY_INSTANCES);
        return trackedEntityInstances;
    }

    static List<TrackedEntityInstance> queryTrackedEntityInstancesDataFromAllAccessibleOrgunits(
            DhisApi dhisApi,
            String organisationUnitUid,
            String programUid,
            String queryString,
            boolean detailedSearch,
            TrackedEntityAttributeValue... params) throws APIException {
        if (dhisApi == null) {
            return null;
        }
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        if (programUid != null) {
            QUERY_MAP_FULL.put("program", programUid);
        }
        List<TrackedEntityAttributeValue> valueParams = new LinkedList<>();
        if (params != null) {
            for (TrackedEntityAttributeValue teav : params) {
                if (teav != null && teav.getValue() != null) {
                    if (!teav.getValue().isEmpty()) {
                        valueParams.add(teav);
//                        QUERY_MAP_FULL.put("filter",teav.getTrackedEntityAttributeId()
// +":LIKE:"+teav.getValue());
                    }
                }
            }
        }
        for (TrackedEntityAttributeValue val : valueParams) {
            TrackedEntityAttribute trackedEntityAttribute =
                    MetaDataController.getTrackedEntityAttribute(val.getTrackedEntityAttributeId());
            if (trackedEntityAttribute.getOptionSet() != null) {
                // has option sets. Want to search on exact matching
                if (!QUERY_MAP_FULL.containsKey("filter")) {
                    QUERY_MAP_FULL.put("filter",
                            val.getTrackedEntityAttributeId() + ":EQ:" + val.getValue());
                } else {
                    String currentFilter = QUERY_MAP_FULL.get("filter");
                    QUERY_MAP_FULL.put("filter",
                            currentFilter + "&filter=" + val.getTrackedEntityAttributeId() + ":EQ:"
                                    + val.getValue());
                }
                continue;
            }

            if (!QUERY_MAP_FULL.containsKey("filter")) {
                QUERY_MAP_FULL.put("filter",
                        val.getTrackedEntityAttributeId() + ":LIKE:" + val.getValue());
            } else {
                String currentFilter = QUERY_MAP_FULL.get("filter");
                QUERY_MAP_FULL.put("filter",
                        currentFilter + "&filter=" + val.getTrackedEntityAttributeId() + ":LIKE:"
                                + val.getValue());
            }
        }


        //doesnt work with both attribute filter and query
        if (queryString != null && !queryString.isEmpty() && valueParams.isEmpty()) {
            QUERY_MAP_FULL.put("query", "LIKE:"
                    + queryString);//todo: make a map where we can use more than one of each key
        }
        List<TrackedEntityInstance> trackedEntityInstances = unwrapResponse(dhisApi
                        .getTrackedEntityInstancesFromAllAccessibleOrgUnits(organisationUnitUid,
                                QUERY_MAP_FULL),
                ApiEndpointContainer.TRACKED_ENTITY_INSTANCES);
        return trackedEntityInstances;
    }

    static List<TrackedEntityInstance> getTrackedEntityInstancesDataFromServer(DhisApi dhisApi,
            List<TrackedEntityInstance> trackedEntityInstances, boolean getEnrollments,
            boolean getRecursiveRelations) {
        if (trackedEntityInstances == null) {
            return null;
        }
        if (dhisApi == null) {
            return null;
        }

        DateTime serverDateTime = dhisApi.getSystemInfo()
                .getServerDate();

        List<TrackedEntityInstance> trackedEntityInstancesToReturn = new ArrayList<>();
        for (int teiIndex = 0; teiIndex < trackedEntityInstances.size(); teiIndex++) {

            int userFriendlyIndex = (int) (Math.ceil((teiIndex + 1) / 2.0));

            try {
                trackedEntityInstancesToReturn.add(getTrackedEntityInstanceDataFromServer(dhisApi,
                        trackedEntityInstances.get(teiIndex).getTrackedEntityInstance(),
                        getEnrollments, getRecursiveRelations, serverDateTime));
            } catch (APIException e) { //can't throw this further up because we want to continue
                // loading all the TEIs..
                e.printStackTrace();
                Dhis2Application.getEventBus().post(
                        new OnTeiDownloadedEvent(OnTeiDownloadedEvent.EventType.ERROR,
                                trackedEntityInstances.size(), userFriendlyIndex));
                return new ArrayList<>();
            }

            Dhis2Application.getEventBus().post(
                    new OnTeiDownloadedEvent(OnTeiDownloadedEvent.EventType.UPDATE,
                            trackedEntityInstances.size(), userFriendlyIndex));
        }
        return trackedEntityInstancesToReturn;
    }

    static TrackedEntityInstance getTrackedEntityInstanceDataFromServer(DhisApi dhisApi, String uid,
            boolean getEnrollments, boolean getRecursiveRelations, DateTime serverDateTime)
            throws APIException {
        if (dhisApi == null) {
            return null;
        }
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.TRACKEDENTITYINSTANCE, uid);


        Log.d(CLASS_TAG, "get tei " + uid);
        TrackedEntityInstance trackedEntityInstance = updateTrackedEntityInstance(dhisApi, uid,
                lastUpdated);
        Log.d(CLASS_TAG, "get tei1 " + uid);

        //need to save the TEI first to get a auto-increment id
        DbOperation.save(trackedEntityInstance).getModel().save();

        List<DbOperation> operations = new ArrayList<>();
        if (trackedEntityInstance.getAttributes() != null) {
            for (TrackedEntityAttributeValue value : trackedEntityInstance.getAttributes()) {
                if (value != null) {
                    value.setTrackedEntityInstanceId(
                            trackedEntityInstance.getTrackedEntityInstance());
                    value.setLocalTrackedEntityInstanceId(
                            trackedEntityInstance.getLocalId());
                    operations.add(DbOperation.save(value));
                }
            }
        }
        if (trackedEntityInstance.getRelationships() != null) {
            for (Relationship relationship : trackedEntityInstance.getRelationships()) {
                if (relationship != null) {
                    relationship.async().save();
                    operations.add(DbOperation.save(relationship));
                }
            }
        }
        DbUtils.applyBatch(operations);

        if (getRecursiveRelations) {
            if (trackedEntityInstance.getRelationships() != null) {
                for (Relationship relationship : trackedEntityInstance.getRelationships()) {
                    if (relationship != null) {
                        String targetTrackedEntity = relationship.getTrackedEntityInstanceB();
                        if (trackedEntityInstance.getTrackedEntityInstance().equals(
                                targetTrackedEntity)) {
                            targetTrackedEntity = relationship.getTrackedEntityInstanceA();
                        }
                        if (TrackerController.getTrackedEntityInstanceByUid(targetTrackedEntity)
                                == null) {
                            getTrackedEntityInstanceDataFromServer(dhisApi, targetTrackedEntity,
                                    getEnrollments,
                                    false, serverDateTime);
                        }
                    }
                }
            }
        }
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.TRACKEDENTITYINSTANCE, uid, serverDateTime);
        if (getEnrollments) {
            getEnrollmentsDataFromServer(dhisApi, trackedEntityInstance, serverDateTime);
        }

        return trackedEntityInstance;
    }

    private static TrackedEntityInstance updateTrackedEntityInstance(DhisApi dhisApi, String uid,
            DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        TrackedEntityInstance updatedTrackedEntityInstance = dhisApi.getTrackedEntityInstance(uid,
                QUERY_MAP_FULL);
        return updatedTrackedEntityInstance;
    }

    static List<Enrollment> getEnrollmentsDataFromServer(DhisApi dhisApi,
            TrackedEntityInstance trackedEntityInstance, DateTime serverDateTime)
            throws APIException {
        if (trackedEntityInstance == null) {
            return null;
        }
        if (dhisApi == null) {
            return null;
        }
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.ENROLLMENTS,
                        trackedEntityInstance.getTrackedEntityInstance());
        if (serverDateTime == null) {
            serverDateTime = dhisApi.getSystemInfo().getServerDate();
        }

        List<Enrollment> enrollments = unwrapResponse(dhisApi
                .getEnrollments(trackedEntityInstance.getTrackedEntityInstance(),
                        getBasicQueryMap(lastUpdated)), ApiEndpointContainer.ENROLLMENTS);
        for (Enrollment enrollment : enrollments) {
            enrollment.setLocalTrackedEntityInstanceId(trackedEntityInstance.getLocalId());
        }

        saveResourceDataFromServer(ResourceType.ENROLLMENTS,
                trackedEntityInstance.getTrackedEntityInstance(), dhisApi,
                enrollments, TrackerController.getEnrollments(trackedEntityInstance),
                serverDateTime);
        enrollments = TrackerController.getEnrollments(trackedEntityInstance);
        if (enrollments != null) {
            for (Enrollment enrollment : enrollments) {
                try {
                    getEventsDataFromServer(dhisApi, SyncStrategy.DOWNLOAD_ONLY_NEW, enrollment,
                            serverDateTime);
                } catch (APIException e) {//can't throw this exception up because we want to
                    // continue loading enrollments.. todo: let the user know?
                    e.printStackTrace();
                }
            }
        }
        return enrollments;
    }

    static void getEnrollmentDataFromServer(DhisApi dhisApi, String uid, boolean getEvents,
            DateTime serverDateTime) throws APIException {
        if (dhisApi == null) {
            return;
        }
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.ENROLLMENT, uid);
//        DateTime serverDateTime = dhisApi.getSystemInfo()
//                .getServerDate();

        if (serverDateTime == null) {
            serverDateTime = dhisApi.getSystemInfo().getServerDate();
        }

        Enrollment enrollment = updateEnrollment(dhisApi, uid, lastUpdated);

        DbOperation.save(enrollment).getModel().save();
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.ENROLLMENT, uid, serverDateTime);
        if (getEvents) {
            getEventsDataFromServer(dhisApi, SyncStrategy.DOWNLOAD_ONLY_NEW, enrollment,
                    serverDateTime);
        }
    }

    private static Enrollment updateEnrollment(DhisApi dhisApi, String uid, DateTime lastUpdated)
            throws APIException {
        if (dhisApi == null) {
            return null;
        }
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        Enrollment updatedEnrollment = dhisApi.getEnrollment(uid, QUERY_MAP_FULL);
        return updatedEnrollment;
    }

    static void getEventsDataFromServer(DhisApi dhisApi, SyncStrategy syncStrategy,
            Enrollment enrollment, DateTime serverDateTime) {
        if (enrollment == null) {
            return;
        } else if (dhisApi == null) {
            return;
        }

        if (enrollment.getProgram() == null) {
            Log.d(CLASS_TAG, "Enrollment:" + enrollment.getUid());
            return;
        }

        DateTime lastUpdated = null;

        if (syncStrategy == SyncStrategy.DOWNLOAD_ONLY_NEW) {
            lastUpdated = DateTimeManager.getInstance()
                    .getLastUpdated(ResourceType.EVENTS, enrollment.getEnrollment());
        }

        JsonNode response = dhisApi
                .getEventsForEnrollment(enrollment.getProgram().getUid(), enrollment.getStatus(),
                        enrollment.getTrackedEntityInstance(),
                        getBasicQueryMap(lastUpdated));
        List<Event> events = EventsWrapper.getEvents(response);
        List<Event> invalidEvents = new ArrayList<>();
        for (Event event : events) {
            event.setLocalEnrollmentId(enrollment.getLocalId());
            if(event.getOrganisationUnitId()==null) {
                invalidEvents.add(event);
            }
        }
        events.removeAll(invalidEvents);

        saveResourceDataFromServer(ResourceType.EVENTS, enrollment.getUid(), dhisApi, events,
                TrackerController.getEventsByEnrollment(enrollment.getLocalId()), serverDateTime);
    }

    static void getEventDataFromServer(DhisApi dhisApi, String uid) throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.EVENTS, uid);
        DateTime serverDateTime = dhisApi.getSystemInfo()
                .getServerDate();

        Event event = updateEvent(dhisApi, uid, lastUpdated);

        DbOperation.save(event).getModel().save();
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.EVENT, uid, serverDateTime);
    }

    private static Event updateEvent(DhisApi dhisApi, String uid, DateTime lastUpdated)
            throws APIException {
        if (dhisApi == null) {
            return null;
        }
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        Event updatedEvent = dhisApi.getEvent(uid, QUERY_MAP_FULL);
        return updatedEvent;
    }

    protected static void refreshRelationshipsByTrackedEntityInstance(DhisApi dhisApi,
            String trackedEntityInstanceUid) {
        Log.d(CLASS_TAG, "refreshRelationshipsByTrackedEntityInstance");
        final Map<String, String> map = new HashMap<>();

        try {
            map.put("fields",
                    "relationships[relationship,displayName,trackedEntityInstanceA,"
                            + "trackedEntityInstanceB]");
            List<Relationship> localRelationships = TrackerController.getRelationships(
                    trackedEntityInstanceUid);

            TrackedEntityInstance remoteTrackedEntityInstance;

            remoteTrackedEntityInstance =
                    dhisApi.getTrackedEntityInstance(trackedEntityInstanceUid,
                            map);


            List<Relationship> remoteRelationships = remoteTrackedEntityInstance.getRelationships();

            ResourceController.overwriteRelationsFromServer(remoteRelationships,
                    localRelationships);

            if (remoteRelationships.size() == 0) {
                Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
                return;
            }

            List<String> notSavedTrackedEntityInstanceUIds = getNotSavedTrackedEntityInstancesUIds(
                    trackedEntityInstanceUid, remoteRelationships);

            if (notSavedTrackedEntityInstanceUIds.size() > 0) {
                for (String notSavedTrackedEntityInstanceUid : notSavedTrackedEntityInstanceUIds) {
                    remoteTrackedEntityInstance = queryTrackedEntityInstanceDataFromServer(dhisApi,
                            notSavedTrackedEntityInstanceUid);
                    if (remoteTrackedEntityInstance != null) {
                        DateTime serverDateTime = dhisApi.getSystemInfo()
                                .getServerDate();
                        TrackerDataLoader.getTrackedEntityInstanceDataFromServer(
                                dhisApi, remoteTrackedEntityInstance.getUid(), true, false,
                                serverDateTime);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(CLASS_TAG,"An error occurred refreshing relations");
            e.printStackTrace();
        }
    }

    private static List<String> getNotSavedTrackedEntityInstancesUIds(
            String trackedEntityInstanceUid,
            List<Relationship> remoteRelationships) {
        List<String> notSavedTrackedEntityInstances = new ArrayList<>();
        for (Relationship remoteRelationship : remoteRelationships) {
            String remoteTrackedEntityInstanceA = remoteRelationship.getTrackedEntityInstanceA();
            String remoteTrackedEntityInstanceB = remoteRelationship.getTrackedEntityInstanceB();
            if (!remoteTrackedEntityInstanceA.equals(trackedEntityInstanceUid)) {
                addNotSavedUids(notSavedTrackedEntityInstances, remoteTrackedEntityInstanceA);
            } else {
                addNotSavedUids(notSavedTrackedEntityInstances, remoteTrackedEntityInstanceB);
            }
        }
        return notSavedTrackedEntityInstances;
    }

    private static void addNotSavedUids(List<String> notSavedTrackedEntityInstances,
            String trackedEntityInstance) {
        if (TrackerController.getTrackedEntityInstanceByUid(trackedEntityInstance)
                == null) {
            notSavedTrackedEntityInstances.add(trackedEntityInstance);
        }
    }
}