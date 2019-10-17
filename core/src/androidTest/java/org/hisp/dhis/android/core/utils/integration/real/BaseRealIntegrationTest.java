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

package org.hisp.dhis.android.core.utils.integration.real;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.call.internal.GenericCallData;
import org.hisp.dhis.android.core.arch.d2.internal.D2DIComponent;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.DbOpenHelper;
import org.hisp.dhis.android.core.arch.db.access.internal.SqLiteDatabaseAdapter;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.Date;

import androidx.test.InstrumentationRegistry;

import static com.google.common.truth.Truth.assertThat;

public abstract class BaseRealIntegrationTest {
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseAdapter databaseAdapter;

    protected Date serverDate = new Date();
    protected ResourceHandler resourceHandler;

    private String dbName = null;

    protected String username = RealServerMother.username;
    protected String password = RealServerMother.password;
    protected String url = RealServerMother.url;

    @Before
    public void setUp() throws IOException {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getTargetContext().getApplicationContext()
                , dbName);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
        resourceHandler = ResourceHandler.create(databaseAdapter);
        resourceHandler.setServerDate(serverDate);
        Stetho.initializeWithDefaults(InstrumentationRegistry.getTargetContext().getApplicationContext());
    }

    @After
    public void tearDown() throws IOException {
        assertThat(sqLiteDatabase).isNotNull();
        sqLiteDatabase.close();
    }

    protected SQLiteDatabase database() {
        return sqLiteDatabase;
    }

    protected DatabaseAdapter databaseAdapter() {
        return databaseAdapter;
    }

    protected GenericCallData getGenericCallData(D2 d2) {
        return GenericCallData.create(
                databaseAdapter(), d2.retrofit(), resourceHandler, d2.systemInfoModule().versionManager());
    }

    protected Cursor getCursor(String table, String[] columns) {
        return sqLiteDatabase.query(table, columns,
                null, null, null, null, null);
    }

    protected D2DIComponent getD2DIComponent(D2 d2) {
        return D2DIComponent.create(InstrumentationRegistry.getTargetContext().getApplicationContext(), d2.retrofit(),
                databaseAdapter);
    }
}
