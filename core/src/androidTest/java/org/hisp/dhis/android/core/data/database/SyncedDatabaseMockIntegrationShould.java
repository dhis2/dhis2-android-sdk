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

package org.hisp.dhis.android.core.data.database;

import android.database.sqlite.SQLiteDatabase;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.AppContextDIModule;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2DIComponent;
import org.hisp.dhis.android.core.DaggerD2DIComponent;
import org.hisp.dhis.android.core.arch.api.retrofit.APIClientDIModule;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.ObjectStore;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.imports.TrackerImportConflictSamples;
import org.hisp.dhis.android.core.data.maintenance.D2ErrorSamples;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.TrackerImportConflictStore;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.maintenance.D2ErrorStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public abstract class SyncedDatabaseMockIntegrationShould {

    private static SQLiteDatabase sqLiteDatabase;
    private static Dhis2MockServer dhis2MockServer;

    protected static DatabaseAdapter databaseAdapter;
    protected static D2 d2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        if (d2 == null) {
            DbOpenHelper dbOpenHelper = new DbOpenHelper(
                    InstrumentationRegistry.getTargetContext().getApplicationContext(), "synced.db");
            sqLiteDatabase = dbOpenHelper.getWritableDatabase();
            databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
            dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
            d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter);
            Stetho.initializeWithDefaults(InstrumentationRegistry.getTargetContext().getApplicationContext());

            d2.wipeModule().wipeEverything();

            login();
            downloadMetadata();
            downloadTrackedEntityInstances();
            downloadEvents();
            downloadAggregatedData();
            storeSomeD2Errors();
            storeSomeConflicts();
        }
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        dhis2MockServer.shutdown();
        sqLiteDatabase.close();
    }

    private static void login() throws Exception {
        dhis2MockServer.enqueueLoginResponses();
        d2.userModule().logIn("android", "Android123").call();
    }

    private static void downloadMetadata() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }

    private static void downloadTrackedEntityInstances() throws Exception {
        dhis2MockServer.enqueueTrackedEntityInstanceResponses();
        d2.trackedEntityModule().downloadTrackedEntityInstances(2, false).call();
    }

    private static void downloadEvents() throws Exception {
        dhis2MockServer.enqueueEventResponses();
        d2.eventModule().downloadSingleEvents(2, false).call();
    }

    private static void downloadAggregatedData() throws Exception {
        dhis2MockServer.enqueueAggregatedDataResponses();
        d2.aggregatedModule().data().download().call();
    }

    private static void storeSomeD2Errors() {
        ObjectStore<D2Error> d2ErrorStore = D2ErrorStore.create(databaseAdapter);
        d2ErrorStore.insert(D2ErrorSamples.get());
        d2ErrorStore.insert(D2Error.builder()
                .resourceType("DataElement")
                .uid("uid")
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.DIFFERENT_SERVER_OFFLINE)
                .url("http://dhis2.org/api/programs/uid")
                .errorDescription("Different server offline")
                .httpErrorCode(402)
                .build()
        );
    }

    private static void storeSomeConflicts() {
        ObjectStore<TrackerImportConflict> trackerImportConflictStore =
                TrackerImportConflictStore.create(databaseAdapter);
        trackerImportConflictStore.insert(TrackerImportConflictSamples.get().toBuilder()
                .trackedEntityInstance(null)
                .enrollment(null)
                .event(null)
                .build());

        trackerImportConflictStore.insert(TrackerImportConflictSamples.get().toBuilder()
                .conflict("conflict_2")
                .value("value_2")
                .trackedEntityInstance("nWrB0TfWlvh")
                .enrollment("enroll2")
                .event("event2")
                .tableReference("table_reference_2")
                .errorCode("error_code_2")
                .status(ImportStatus.ERROR)
                .build()
        );
    }

    protected D2DIComponent getD2DIComponent() {
        return DaggerD2DIComponent.builder()
                .databaseDIModule(new DatabaseDIModule(databaseAdapter))
                .apiClientDIModule(new APIClientDIModule(d2.retrofit()))
                .appContextDIModule(new AppContextDIModule(InstrumentationRegistry.getTargetContext().getApplicationContext()))
                .build();
    }
}