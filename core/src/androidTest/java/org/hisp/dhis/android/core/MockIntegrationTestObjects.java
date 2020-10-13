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

package org.hisp.dhis.android.core;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.arch.d2.internal.D2DIComponent;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.mockwebserver.Dhis2MockServer;
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.utils.integration.mock.MockIntegrationTestDatabaseContent;

import java.io.IOException;
import java.util.Date;

public class MockIntegrationTestObjects {
    public final DatabaseAdapter databaseAdapter;

    public Date serverDate = new Date();
    public ResourceHandler resourceHandler;

    public final D2DIComponent d2DIComponent;
    public final D2 d2;
    public final Dhis2MockServer dhis2MockServer;
    public final MockIntegrationTestDatabaseContent content;

    public MockIntegrationTestObjects(MockIntegrationTestDatabaseContent content) throws Exception {
        this.content = content;

        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Stetho.initializeWithDefaults(context);

        dhis2MockServer = new Dhis2MockServer(0);
        CalendarProviderFactory.setFixed();

        d2 = D2Factory.forNewDatabase();

        databaseAdapter = d2.databaseAdapter();
        d2DIComponent = d2.d2DIComponent;

        resourceHandler = ResourceHandler.create(databaseAdapter);
        resourceHandler.setServerDate(serverDate);
    }

    public void tearDown() throws IOException {
        Log.i("MockIntegrationTestObjects", "Objects teardown: " + content);
        dhis2MockServer.shutdown();
    }
}
