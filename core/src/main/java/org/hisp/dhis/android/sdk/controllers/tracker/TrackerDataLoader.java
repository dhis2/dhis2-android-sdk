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

import android.content.Context;
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
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
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

/**
 * @author Simen Skogly Russnes on 24.08.15.
 */
final class TrackerDataLoader extends ResourceController {

    public static final String CLASS_TAG = TrackerDataLoader.class.getSimpleName();

    private TrackerDataLoader() {}

    /**
     * Loads datavalue items that is scheduled to be loaded but has not yet been.
     */
    static void updateDataValueDataItems(Context context, SyncStrategy syncStrategy,DhisApi dhisApi) throws APIException {
        SystemInfo serverSystemInfo = dhisApi.getSystemInfo();
        DateTime serverDateTime = serverSystemInfo.getServerDate();
        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        Hashtable<String, List<Program>> programsForOrganisationUnits = new Hashtable<>();

        if (LoadingController.isLoadFlagEnabled(context, ResourceType.EVENTS)) {
            for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
                if (organisationUnit.getId() == null || organisationUnit.getId().length() == Utils.randomUUID.length()) {
                    continue;
                }

                List<Program> programsForOrgUnit = new ArrayList<>();
                List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                ProgramType.WITHOUT_REGISTRATION);
                if (programsForOrgUnitSEWoR != null) {
                    programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
                }

                programsForOrganisationUnits.put(organisationUnit.getId(), programsForOrgUnit);
            }

            for (final OrganisationUnit organisationUnit : assignedOrganisationUnits) {
                if (organisationUnit.getId() == null || organisationUnit.getId().length() == Utils.randomUUID.length())
                    continue;

                for (final Program program : programsForOrganisationUnits.get(organisationUnit.getId())) {
                    if (program.getUid() == null || program.getUid().length() == Utils.randomUUID.length())
                        continue;

                    if (shouldLoad(serverDateTime, ResourceType.EVENTS, organisationUnit.getId() + program.getUid())) {
                        UiUtils.postProgressMessage(context.getString(R.string.loading_events) + ": "
                                + organisationUnit.getLabel() + ": " + program.getName(), LoadingMessageEvent.EventType.DATA);
                        try {
                        getEventsDataFromServer(dhisApi, syncStrategy, organisationUnit.getId(), program.getUid(), serverDateTime);
                        } catch (APIException e) {
                        e.printStackTrace();
                        //todo: could probably do something prettier here. This catch is done to prevent
                        // stopping loading of the following program/orgUnit as throwing and exception would exit the loop..
                        }
                    }
                }
            }
        }
        UiUtils.postProgressMessage("", LoadingMessageEvent.EventType.FINISH);
    }

    /**
     * Loads datavalue items that is scheduled to be loaded but has not yet been.
     */
    static void deleteRemotelyDeletedEvents(Context context, DhisApi dhisApi) throws APIException {
        Hashtable<String, List<Program>> myProgramsByOrganisationUnit = new Hashtable<>();

        if (LoadingController.isLoadFlagEnabled(context, ResourceType.EVENTS)) {
            myProgramsByOrganisationUnit = MetaDataController.getAssignedProgramsByOrganisationUnit();

            for (String organisationUnitUid : myProgramsByOrganisationUnit.keySet()) {
                for (Program program : myProgramsByOrganisationUnit.get(organisationUnitUid)) {
                    if (program.getUid() == null
                            || program.getUid().length() == Utils.randomUUID.length())
                        continue;

                    UiUtils.postProgressMessage(context.getString(R.string.sync_deleted_events) + ": "
                            + organisationUnitUid + ": " + program.getName(), LoadingMessageEvent.EventType.REMOVE_EVENTS);

                    try {
                        deleteRemotelyDeletedEvents(dhisApi, organisationUnitUid, program.getUid());
                    } catch (APIException e) {
                        e.printStackTrace();
                        //todo: could probably do something prettier here. This catch is done to
                        // prevent
                        // stopping loading of the following program/orgUnit as throwing and exception would exit the loop..
                    }
                }
            }
        }
        UiUtils.postProgressMessage("",LoadingMessageEvent.EventType.FINISH);
    }


    static void getEventsDataFromServer(DhisApi dhisApi, SyncStrategy syncStrategy,String organisationUnitUid, String programUid, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getEventsDataFromServer");

        DateTime lastUpdated = null;

        if (syncStrategy == SyncStrategy.DOWNLOAD_ONLY_NEW)
            lastUpdated = DateTimeManager.getInstance()
                    .getLastUpdated(ResourceType.EVENTS,organisationUnitUid+programUid);

        final Map<String, String> map = new HashMap<>();
        map.put("fields", "[:all]");
        if (lastUpdated != null) {
            map.put("lastUpdated", lastUpdated.toString());
        }
        JsonNode response = dhisApi.getEvents(programUid, organisationUnitUid, 50,
                map);
        List<Event> events = EventsWrapper.getEvents(response);
        saveResourceDataFromServer(ResourceType.EVENTS,organisationUnitUid+programUid, dhisApi, events, null, serverDateTime);
    }

    static void deleteRemotelyDeletedEvents(DhisApi dhisApi, String organisationUnitUid, String programUid) throws APIException {
        Log.d(CLASS_TAG, "getEventsDataFromServer");
        final Map<String, String> map = new HashMap<>();
        map.put("fields", "[event]");
        map.put("skipPaging", "true");

        List<Event> localEvents = TrackerController.getServerEvents(organisationUnitUid,programUid);
        List<Event> eventsToBeRemoved = new ArrayList<>();
        if(localEvents.size()==0) {
            return;
        }

        JsonNode response = dhisApi.getEventUids(programUid, organisationUnitUid,
                map);

        List<Event> remoteEvents = EventsWrapper.getEvents(response);
        for (Event localEvent:localEvents){
            boolean isRemoved = true;
            for (Event remoteEvent:remoteEvents){
                if(remoteEvent.getEvent().equals(localEvent.getEvent())){
                    isRemoved=false;
                    break;
                }
            }
            if(isRemoved){
                eventsToBeRemoved.add(localEvent);
            }
        }

        removeResource(eventsToBeRemoved);
    }

    public static void removeResource(List<Event> list) {
        ResourceController.removeResource(list);
    }

    static List<TrackedEntityInstance> queryTrackedEntityInstancesDataFromServer(DhisApi dhisApi,
                                                                                        String organisationUnitUid,
                                                                                        String programUid,
                                                                                        String queryString,
                                                                                        TrackedEntityAttributeValue... params) throws APIException {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        if(programUid != null) {
            QUERY_MAP_FULL.put("program", programUid);
        }
        List<TrackedEntityAttributeValue> valueParams = new LinkedList<>();
        if( params != null ) {
            for(TrackedEntityAttributeValue teav: params ) {
                if( teav != null && teav.getValue() != null ) {
                    if( !teav.getValue().isEmpty() ) {
                        valueParams.add( teav );
                        QUERY_MAP_FULL.put("filter",teav.getTrackedEntityAttributeId()+":LIKE:"+teav.getValue());
                    }
                }
            }
        }

        //doesnt work with both attribute filter and query
        if(queryString!=null && !queryString.isEmpty() && valueParams.isEmpty() ) {
            QUERY_MAP_FULL.put("query","LIKE:"+queryString);//todo: make a map where we can use more than one of each key
        }
        List<TrackedEntityInstance> trackedEntityInstances = unwrapResponse(dhisApi
                .getTrackedEntityInstances(organisationUnitUid,
                        QUERY_MAP_FULL), ApiEndpointContainer.TRACKED_ENTITY_INSTANCES);
        return trackedEntityInstances;
    }

    static void getTrackedEntityInstancesDataFromServer(DhisApi dhisApi, List<TrackedEntityInstance> trackedEntityInstances, boolean getEnrollments) {
        if(trackedEntityInstances == null) {
            return;
        }
        for(TrackedEntityInstance trackedEntityInstance: trackedEntityInstances) {
            try {
                getTrackedEntityInstanceDataFromServer(dhisApi, trackedEntityInstance.getTrackedEntityInstance(), getEnrollments);
            } catch (APIException e) { //can't throw this further up because we want to continue loading all the TEIs..
                e.printStackTrace();
            }
        }
    }

    static void getTrackedEntityInstanceDataFromServer(DhisApi dhisApi, String uid, boolean getEnrollments) throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.TRACKEDENTITYINSTANCE, uid);
        DateTime serverDateTime = dhisApi.getSystemInfo()
                .getServerDate();

        Log.d(CLASS_TAG, "get tei " + uid);
        TrackedEntityInstance trackedEntityInstance = updateTrackedEntityInstance(dhisApi, uid, lastUpdated);
        Log.d(CLASS_TAG, "get tei1 " + uid);

        //need to save the TEI first to get a auto-increment id
        DbOperation.save(trackedEntityInstance).getModel().save();

        List<DbOperation> operations = new ArrayList<>();
        if (trackedEntityInstance.getAttributes() != null) {
            for (TrackedEntityAttributeValue value : trackedEntityInstance.getAttributes()) {
                if (value != null) {
                    value.setTrackedEntityInstanceId(trackedEntityInstance.getTrackedEntityInstance());
                    value.setLocalTrackedEntityInstanceId(trackedEntityInstance.getLocalId());
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
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.TRACKEDENTITYINSTANCE, uid, serverDateTime);
        if(getEnrollments) {
            getEnrollmentsDataFromServer(dhisApi, trackedEntityInstance);
        }
    }

    private static TrackedEntityInstance updateTrackedEntityInstance(DhisApi dhisApi, String uid, DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        TrackedEntityInstance updatedTrackedEntityInstance = dhisApi.getTrackedEntityInstance(uid, QUERY_MAP_FULL);
        return updatedTrackedEntityInstance;
    }

    static void getEnrollmentsDataFromServer(DhisApi dhisApi, TrackedEntityInstance trackedEntityInstance) throws APIException {
        if(trackedEntityInstance == null) {
            return;
        }
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.ENROLLMENTS, trackedEntityInstance.getTrackedEntityInstance());
        DateTime serverDateTime = dhisApi.getSystemInfo()
                .getServerDate();
        List<Enrollment> enrollments = unwrapResponse(dhisApi
                .getEnrollments(trackedEntityInstance.getTrackedEntityInstance(),
                        getBasicQueryMap(lastUpdated)), ApiEndpointContainer.ENROLLMENTS);
        for(Enrollment enrollment: enrollments) {
            enrollment.setLocalTrackedEntityInstanceId(trackedEntityInstance.getLocalId());
        }

        saveResourceDataFromServer(ResourceType.ENROLLMENTS,
                trackedEntityInstance.getTrackedEntityInstance(), dhisApi,
                enrollments, TrackerController.getEnrollments(trackedEntityInstance), serverDateTime);
        enrollments = TrackerController.getEnrollments(trackedEntityInstance);
        if(enrollments != null) {
            for(Enrollment enrollment: enrollments) {
                try {
                    getEventsDataFromServer(dhisApi, enrollment);
                } catch (APIException e) {//can't throw this exception up because we want to continue loading enrollments.. todo: let the user know?
                    e.printStackTrace();
                }
            }
        }
    }

    static void getEnrollmentDataFromServer(DhisApi dhisApi, String uid, boolean getEvents) throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.ENROLLMENT, uid);
        DateTime serverDateTime = dhisApi.getSystemInfo()
                .getServerDate();

        Enrollment enrollment = updateEnrollment(dhisApi, uid, lastUpdated);

        DbOperation.save(enrollment).getModel().save();
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.ENROLLMENT, uid, serverDateTime);
        if(getEvents) {
            getEventsDataFromServer(dhisApi, enrollment);
        }
    }

    private static Enrollment updateEnrollment(DhisApi dhisApi, String uid, DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        Enrollment updatedEnrollment = dhisApi.getEnrollment(uid, QUERY_MAP_FULL);
        return updatedEnrollment;
    }

    static void getEventsDataFromServer(DhisApi dhisApi, Enrollment enrollment) {
        if(enrollment == null) {
            return;
        }

        if(enrollment.getProgram() == null) {
            Log.d(CLASS_TAG, "Enrollment:" + enrollment.getUid());
            return;
        }

        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.EVENTS, enrollment.getEnrollment());
        DateTime serverDateTime = dhisApi.getSystemInfo()
                .getServerDate();
        JsonNode response = dhisApi
                .getEventsForEnrollment(enrollment.getProgram().getUid(), enrollment.getStatus(),
                        enrollment.getTrackedEntityInstance(),
                        getBasicQueryMap(lastUpdated));
        List<Event> events = EventsWrapper.getEvents(response);
        for(Event event: events) {
            event.setLocalEnrollmentId(enrollment.getLocalId());
        }

        saveResourceDataFromServer(ResourceType.EVENTS, enrollment.getUid(), dhisApi, events, TrackerController.getEventsByEnrollment(enrollment.getLocalId()), serverDateTime);
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

    private static Event updateEvent(DhisApi dhisApi, String uid, DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        Event updatedEvent = dhisApi.getEvent(uid, QUERY_MAP_FULL);
        return updatedEvent;
    }
}