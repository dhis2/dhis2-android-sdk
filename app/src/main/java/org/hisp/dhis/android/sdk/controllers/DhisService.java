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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.job.Job;
import org.hisp.dhis.android.sdk.job.JobExecutor;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.UserAccount;
import org.hisp.dhis.android.sdk.network.Credentials;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

import java.util.Map;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
public final class DhisService extends Service {
    public static final int LOG_IN = 1;
    public static final int CONFIRM_USER = 2;
    public static final int LOG_OUT = 3;
    public static final int SYNC_DASHBOARDS = 5;
    public static final int SYNC_DASHBOARD_CONTENT = 6;
    public static final int SYNC_INTERPRETATIONS = 7;

    private final IBinder mBinder = new ServiceBinder();
    //private DhisController mDhisController;

    @Override
    public void onCreate() {
        super.onCreate();
        //mDhisController = DhisController.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServiceBinder extends Binder {
        public DhisService getService() {
            return DhisService.this;
        }
    }

    public static void logInUser(final HttpUrl serverUrl, final Credentials credentials) {
        JobExecutor.enqueueJob(new NetworkJob<UserAccount>(LOG_IN,
                ResourceType.USERS) {

            @Override
            public UserAccount execute() throws APIException {
                return DhisController.logInUser(serverUrl, credentials);
            }
        });
    }

    public static void logOutUser(final Context context) {
        JobExecutor.enqueueJob(new Job<UiEvent>(LOG_OUT) {
            @Override
            public UiEvent inBackground() {
                DhisController.logOutUser(context);
                return new UiEvent(UiEvent.UiEventType.USER_LOG_OUT);
            }

            @Override
            public void onFinish(UiEvent result) {
                Dhis2Application.getEventBus().post(result);
            }
        });
    }

    public static void confirmUser(final Credentials credentials) {
        JobExecutor.enqueueJob(new NetworkJob<UserAccount>(CONFIRM_USER,
                ResourceType.USERS) {

            @Override
            public UserAccount execute() throws APIException {
                return DhisController.confirmUser(credentials);
            }
        });
    }

    public static void syncDashboardContents() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_DASHBOARD_CONTENT,
                ResourceType.DASHBOARDS_CONTENT) {

            @Override
            public Object execute() throws APIException {
                //Dhis2.syncDashboardContent();
                return new Object();
            }
        });
    }

    public static void syncDashboardsAndContent() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_DASHBOARDS,
                ResourceType.DASHBOARDS) {

            @Override
            public Object execute() throws APIException {
                //Dhis2.syncDashboardContent();
                //Dhis2.syncDashboards();
                return new Object();
            }
        });
    }

    public static void syncDashboards() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_DASHBOARDS,
                ResourceType.DASHBOARDS) {

            @Override
            public Object execute() throws APIException {
                //Dhis2.syncDashboards();
                return new Object();
            }
        });
    }

    public static void synchronize(final Context context) {
        JobExecutor.enqueueJob(new NetworkJob<Object>(0,
                null) {
            @Override
            public Object execute() throws APIException {
                DhisController.synchronize(context);
                return new Object();
            }
        });
    }

    public static Job loadData(final Context context) {
        Job job=JobExecutor.enqueueJob(new NetworkJob<Object>(0,null) {
            @Override
            public Object execute() throws APIException {
                DhisController.loadData(context);
                return new Object();
            }
        });
        return job;
    }

    public static Job loadMetaData(final Context context) {
        Job job=JobExecutor.enqueueJob(new NetworkJob<Object>(0,null) {
            @Override
            public Object execute() throws APIException {
                DhisController.loadMetaData(context);
                return new Object();
            }
        });
        return job;
    }

    public static Job loadDataValues(final Context context) {
        Job job=JobExecutor.enqueueJob(new NetworkJob<Object>(0,null) {
            @Override
            public Object execute() throws APIException {
                DhisController.loadDataValues(context);
                return new Object();
            }
        });
        return job;
    }

    public static void sendData() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(0,
                null) {
            @Override
            public Object execute() throws APIException {
                DhisController.sendData();
                return new Object();
            }
        });
    }

    public static void sendEventChanges() {
        JobExecutor.enqueueJob(new NetworkJob<Map<Long,ImportSummary>>(0,
                null) {
            @Override
            public Map<Long,ImportSummary> execute() throws APIException {
                Map<Long,ImportSummary> importSummaryMap=DhisController.sendEventChanges();
                return importSummaryMap;
            }
        });
    }

    public static void loadInitialData(final Context context) {
        JobExecutor.enqueueJob(new NetworkJob<Object>(0,
                null) {
            @Override
            public Object execute() throws APIException {
                LoadingController.loadInitialData(context, DhisController.getInstance().getDhisApi());
                return new Object();
            }
        });
    }

    public static void syncInterpretations() {
        JobExecutor.enqueueJob(new NetworkJob<Object>(SYNC_INTERPRETATIONS,
                ResourceType.INTERPRETATIONS) {
            @Override
            public Object execute() throws APIException {
                //Dhis2.syncInterpretations();
                return new Object();
            }
        });
    }

    public boolean isJobRunning(int jobId) {
        return JobExecutor.isJobRunning(jobId);
    }
}
