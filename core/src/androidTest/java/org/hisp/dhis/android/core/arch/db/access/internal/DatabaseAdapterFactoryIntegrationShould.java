/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.db.access.internal;

import android.content.Context;
import android.database.Cursor;

import androidx.test.platform.app.InstrumentationRegistry;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseAdapterFactoryIntegrationShould {

    private static final String DB_NAME = "database-adapter-factory-integration-should.db";
    private static DatabaseAdapterFactory databaseAdapterFactory;
    
    @BeforeClass
    public static void setUpClass() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        databaseAdapterFactory = DatabaseAdapterFactory.create(context, new InMemorySecureStore());
    }
    
    @AfterClass
    public static void tearDownClass() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        context.deleteDatabase(DB_NAME);
    }

    @Test
    public void get_adapter() {
        databaseAdapterFactory.newParentDatabaseAdapter();
    }

    @Test
    public void get_adapter_create_and_close() {
        DatabaseAdapter databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DB_NAME, false);
        databaseAdapter.close();
    }

    @Test
    public void get_adapter_create_close_and_recreate() {
        DatabaseAdapter databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DB_NAME,  false);
        databaseAdapter.close();

        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DB_NAME,  false);
    }

    @Test
    public void get_adapter_create_and_recreate_without_closing() {
        DatabaseAdapter databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DB_NAME, false);
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DB_NAME, false);
    }

    @Test
    public void get_adapter_create_close_and_recreate_reading_db() {
        DatabaseAdapter databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DB_NAME, false);
        Cursor cursor1 = databaseAdapter.rawQuery("SELECT * FROM User");
        int count1 = cursor1.getCount();
        cursor1.close();

        databaseAdapter.close();

        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DB_NAME, false);
        Cursor cursor2 = databaseAdapter.rawQuery("SELECT * FROM User");
        int count2 = cursor2.getCount();
        cursor2.close();
    }
}