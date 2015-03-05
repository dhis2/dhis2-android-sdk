/*
 *  Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis2.android.sdk.controllers.datavalues;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.otto.Subscribe;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadEnrollmentsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadEventsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadTrackedEntityInstancesTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.RegisterEventTask;
import org.hisp.dhis2.android.sdk.controllers.wrappers.TrackedEntityInstancesWrapper;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.DataValueResponseEvent;
import org.hisp.dhis2.android.sdk.events.LoadingEvent;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis2.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis2.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.Program;
import org.hisp.dhis2.android.sdk.persistence.models.ResponseBody;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis2.android.sdk.utils.APIException;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 * Handles management of data values
 */
public class DataValueController {

    private static final String CLASS_TAG = "DataValueController";

    private DataValueLoader dataValueLoader;
    private DataValueSender dataValueSender;


    public DataValueController() {
        Dhis2Application.bus.register(this);
        dataValueLoader = new DataValueLoader();
        dataValueSender = new DataValueSender();
    }

    public Event getEvent(String eventId) {
        Log.e(CLASS_TAG, "getting event for: " + eventId);
        List<Event> result = Select.all(Event.class, Condition.column(Event$Table.ID).is(eventId));
        if( result != null && !result.isEmpty() ) return result.get(0);
        else return null;
    }

    /**
     * Returns a list of failed items from the database, or null if there are none.
     * @return
     */
    public static List<FailedItem> getFailedItems() {
        List<FailedItem> failedItems = Select.all(FailedItem.class);
        if(failedItems == null || failedItems.size() <= 0) return null;
        else return failedItems;
    }

    /**
     * Loads user generated data from the server. Which data is loaded is determined by enabling
     * or disabling flags in DHIS 2.
     */
    public void synchronizeDataValues(Context context) {
        sendLocalData(context);
    }

    /**
     * Loads Tracker Related data including Tracked Entity Instances, Enrollments and Events
     * for the current user's assigned programs and organisation units.
     */
    public void loadTrackerData(Context context) {
        if(dataValueSender.sending || dataValueLoader.loading || Dhis2.getInstance().getMetaDataController().isLoading() ||
                Dhis2.getInstance().getMetaDataController().isSynchronizing()) return; //todo: implement a global checker to see if loading is occurring.
        dataValueLoader.loadTrackerData(context);
    }



    /**
     * Tries to send locally stored data to the server
     */
    public void sendLocalData(Context context) {
        Log.d(CLASS_TAG, "sending local data");
        if( dataValueSender.sending || dataValueLoader.loading || Dhis2.getInstance().getMetaDataController().isLoading() ||
                Dhis2.getInstance().getMetaDataController().isSynchronizing()) return;
        dataValueSender.sendLocalData(context);
    }


    /* called from dataValueLoader */
    static void onFinishLoading(LoadingEvent event) {
        Dhis2Application.bus.post(event); //subscribed to in Dhis2
    }

    public static void onFinishSending() {

    }

    @Subscribe
    public void onResponse(DataValueResponseEvent responseEvent) {
        Log.e(CLASS_TAG, "onResponse");
    }

    public boolean isSending() {
        return dataValueSender.sending;
    }
    public boolean isLoading() { return dataValueLoader.loading; }

}
