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
package org.hisp.dhis.android.core.domain.aggregated.data

import android.util.Log
import org.hisp.dhis.android.core.BaseRealIntegrationTest

class AggregatedDataCallRealIntegrationShould : BaseRealIntegrationTest() {

    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to
     * the
     * metadataSyncCall. It works against the demo server.
     */

    /* How to extract database from tests:
    edit: AbsStoreTestCase.java (adding database name.)
    DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getTargetContext()
    .getApplicationContext(), "test.db");
    make a debugger break point where desired (after sync complete)

    Then while on the breakpoint :
    Android/platform-tools/adb pull /data/user/0/org.hisp.dhis.android.test/databases/test.db
    test.db

    in datagrip:
    pragma foreign_keys = on;
    pragma foreign_key_check;*/
    // This test is uncommented because technically it is flaky.
    // It depends on a live server to operate and the login is hardcoded here.
    // Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.
    // @Test
    fun response_successful_on_sync_data_once() {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()
        d2.aggregatedModule().data().blockingDownload()
    }

    // @Test
    fun response_successful_on_sync_data_value_two_times() {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()

        val start = System.currentTimeMillis()
        d2.aggregatedModule().data().blockingDownload()
        val end = System.currentTimeMillis()
        Log.e("AGGSYN", "AGGSYN first: " + (end - start))

        // d2.metadataModule().blockingDownload();
        d2.aggregatedModule().data().blockingDownload()
        val end2 = System.currentTimeMillis()
        Log.e("AGGSYN", "AGGSYN second: " + (end2 - end))
    }
}
