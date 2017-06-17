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

package org.hisp.dhis.android.sdk.controllers;

import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.BaseIdentifiableObject;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.DbUtils;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author Simen Skogly Russnes on 24.08.15.
 */
public abstract class ResourceController {

    public static <T extends BaseIdentifiableObject> void saveResourceDataFromServer(ResourceType resourceType, DhisApi dhisApi,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime) {
        saveResourceDataFromServer(resourceType, null, dhisApi, updatedItems, persistedItems, serverDateTime, true);
    }

    public static <T extends BaseIdentifiableObject> void saveResourceDataFromServer(ResourceType resourceType, DhisApi dhisApi,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime, boolean keepOldValues) {
        saveResourceDataFromServer(resourceType, null, dhisApi, updatedItems, persistedItems, serverDateTime, keepOldValues);
    }

    public static <T extends BaseIdentifiableObject> void saveResourceDataFromServer(ResourceType resourceType, String salt, DhisApi dhisApi,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime) {
        saveResourceDataFromServer(resourceType, salt, dhisApi, updatedItems, persistedItems, serverDateTime, true);
    }

    public static <T extends BaseValue> void saveResourceDataFromServer(ResourceType resourceType, String salt,
                                                                        List<T> updatedItems,
                                                                        List<T> persistedItems,
                                                                        DateTime serverDateTime) {
        saveBaseValueDataFromServer(resourceType, salt, updatedItems, persistedItems, serverDateTime, true);
    }

    public static <T extends BaseValue> void saveBaseValueDataFromServer(ResourceType resourceType, String salt,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime, boolean keepOldValues) {
        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createBaseValueOperations(persistedItems, updatedItems, keepOldValues));
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(resourceType, salt, serverDateTime);
    }

    public static <T extends BaseIdentifiableObject> void saveResourceDataFromServer(ResourceType resourceType, String salt, DhisApi dhisApi,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime, boolean keepOldValues) {
        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(persistedItems, updatedItems, keepOldValues));
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(resourceType, salt, serverDateTime);
    }

    /**
     * determines if a meta data item should be loaded. Either because it hasnt been loaded before,
     * or because it needs to be updated based on time.
     * @return
     */
    public static boolean shouldLoad(DateTime serverTime, ResourceType resource, String salt ) {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resource, salt);
        DateTime serverDateTime = serverTime;
//        DateTime serverDateTime = dhisApi.getSystemInfo()
//                .getServerDate();
        if( lastUpdated == null ) {
            return true;
        } else if ( lastUpdated.isBefore( serverDateTime ) ) {
            return true;
        }
        return false;
    }

    /**
     * determines if a meta data item should be loaded. Either because it hasnt been loaded before,
     * or because it needs to be updated based on time.
     * @return
     */
    public static boolean shouldLoad( DateTime serverDateTime, ResourceType resource ) {
        return shouldLoad(serverDateTime, resource, null);
    }

    public static Map<String, String> getBasicQueryMap(DateTime lastUpdated) {
        final Map<String, String> map = new HashMap<>();
        map.put("fields", "[:all]");
        if (lastUpdated != null) {
            map.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }
        return map;
    }

    public static void removeTrackedEntityEnrollments(List<Enrollment> list) {
        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.removeResources(list));
        DbUtils.applyBatch(operations);
    }

    public static void removeTrackedEntityRelationships(List<Relationship> list) {
        Queue<DbOperation> operations = new LinkedList<>();
        for(Relationship relationship: list) {
            operations.add(DbOperation.delete(relationship));
        }
        DbUtils.applyBatch(operations);
    }

    public static void removeTrackedEntityInstances(List<TrackedEntityInstance> list) {
        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.removeResources(list));
        for (TrackedEntityInstance trackedEntityInstance : list) {
            if (trackedEntityInstance.getRelationships() != null) {
                for (Relationship relationship : trackedEntityInstance.getRelationships()) {
                    operations.add(DbOperation.delete(relationship));
                }
            }
        }
        DbUtils.applyBatch(operations);
    }

    public static void removeEvents(List<Event> list) {
        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.removeResources(list));
        for (Event event : list) {
            if (event.getDataValues() != null) {
                for (DataValue dataValue : event.getDataValues()) {
                    operations.add(DbOperation.delete(dataValue));
                }
            }
        }
        DbUtils.applyBatch(operations);
    }

    public static void overwriteRelationsFromServer(List<Relationship> updatedItems,
            List<Relationship> persistedItems) {
        Queue<DbOperation> operations = new LinkedList<>();
        //remove old values
        for(Relationship relationship:persistedItems) {
            operations.add(DbOperation.delete(relationship));
        }
        //save new values
        for(Relationship relationship:updatedItems) {
            operations.add(DbOperation.save(relationship));
        }
        DbUtils.applyBatch(operations);
    }

}
