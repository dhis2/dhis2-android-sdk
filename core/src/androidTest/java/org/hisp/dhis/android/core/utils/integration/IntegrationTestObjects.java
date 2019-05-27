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

package org.hisp.dhis.android.core.utils.integration;

import android.database.sqlite.SQLiteDatabase;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.AppContextDIModule;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2DIComponent;
import org.hisp.dhis.android.core.DaggerD2DIComponent;
import org.hisp.dhis.android.core.arch.api.retrofit.APIClientDIModule;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DatabaseDIModule;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;

import java.io.IOException;
import java.util.Date;

import androidx.test.InstrumentationRegistry;

public class IntegrationTestObjects {
    public final SQLiteDatabase database;
    public final DatabaseAdapter databaseAdapter;

    public Date serverDate = new Date();
    public ResourceHandler resourceHandler;

    public final D2DIComponent d2DIComponent;
    public final D2 d2;
    public final Dhis2MockServer dhis2MockServer;

    IntegrationTestObjects() throws Exception {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getTargetContext().getApplicationContext(),
                null);
        database = dbOpenHelper.getWritableDatabase();
        databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
        resourceHandler = new ResourceHandler(ResourceStoreImpl.create(databaseAdapter));
        resourceHandler.setServerDate(serverDate);
        Stetho.initializeWithDefaults(InstrumentationRegistry.getTargetContext().getApplicationContext());

        dhis2MockServer = new Dhis2MockServer();

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter);

        d2DIComponent = DaggerD2DIComponent.builder()
                .databaseDIModule(new DatabaseDIModule(databaseAdapter))
                .apiClientDIModule(new APIClientDIModule(d2.retrofit()))
                .appContextDIModule(new AppContextDIModule(InstrumentationRegistry.getTargetContext().getApplicationContext()))
                .build();
    }

    public void tearDown() throws IOException {
        database.close();
        dhis2MockServer.shutdown();
    }
}
