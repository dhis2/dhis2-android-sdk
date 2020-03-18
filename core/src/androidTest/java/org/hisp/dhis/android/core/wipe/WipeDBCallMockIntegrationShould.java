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

package org.hisp.dhis.android.core.wipe;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Factory;
import org.hisp.dhis.android.core.D2Manager;
import org.hisp.dhis.android.core.data.database.DatabaseAssert;
import org.junit.AfterClass;
import org.junit.Test;

public class WipeDBCallMockIntegrationShould {

    private static Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
    private static String dbName = "wipe-db.db";

    @AfterClass
    public static void tearDownClass() {
        context.deleteDatabase(dbName);
    }

    @Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        new DBTestLoader(context).copyDatabaseFromAssetsIfNeeded(dbName);
        D2Manager.setTestingDatabase(dbName, "android");
        D2 d2 = D2Manager.blockingInstantiateD2(D2Factory.d2Configuration(context));

        DatabaseAssert.assertThatDatabase(d2.databaseAdapter()).isNotEmpty();

        d2.wipeModule().wipeEverything();

        DatabaseAssert.assertThatDatabase(d2.databaseAdapter()).isEmpty();
    }
}
