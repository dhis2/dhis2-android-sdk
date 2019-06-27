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

package org.hisp.dhis.android.core.utils.integration.mock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2DIComponent;
import org.hisp.dhis.android.core.d2manager.D2Factory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;

import java.io.IOException;
import java.util.Date;

import androidx.test.InstrumentationRegistry;

public class MockIntegrationTestObjects {
    public final SQLiteDatabase database;
    public final DatabaseAdapter databaseAdapter;

    public Date serverDate = new Date();
    public ResourceHandler resourceHandler;

    public final D2DIComponent d2DIComponent;
    public final D2 d2;
    public final Dhis2MockServer dhis2MockServer;
    public final MockIntegrationTestDatabaseContent content;
    private final String dbName;

    MockIntegrationTestObjects(MockIntegrationTestDatabaseContent content) throws Exception {
        this.content = content;
        dbName = content.toString().toLowerCase() + ".db";

        deleteDatabase();

        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        Stetho.initializeWithDefaults(context);

        dhis2MockServer = new Dhis2MockServer();

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), dbName);

        database = d2.databaseAdapter().database();
        databaseAdapter = d2.databaseAdapter();

        d2DIComponent = D2DIComponent.create(context, d2.retrofit(), databaseAdapter);

        resourceHandler = ResourceHandler.create(databaseAdapter);
        resourceHandler.setServerDate(serverDate);
    }

    private void deleteDatabase() {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        context.deleteDatabase(dbName);
    }

    public void tearDown() throws IOException {
        Log.i("MockIntegrationTestObjects", "Objects teardown: " + content);
        database.close();
        deleteDatabase();
        dhis2MockServer.shutdown();
    }
}
