/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.data.server.RealServerMother;

public class MultiUserRealIntegrationShould extends BaseRealIntegrationTest {

    //@Test
    public void connect_to_server_with_2_different_users() {
        d2.userModule().blockingLogIn(username, password, url, null);
        d2.metadataModule().blockingDownload();
        int programsA0 = d2.programModule().programs().blockingCount();
        d2.userModule().blockingLogOut();


        d2.userModule().blockingLogIn("admin", "district", url, null);
        d2.metadataModule().blockingDownload();
        int programsA1 = d2.programModule().programs().blockingCount();
        d2.userModule().blockingLogOut();

        d2.userModule().blockingLogIn(username, password, url, null);
        int programsA2 = d2.programModule().programs().blockingCount();
    }

    //@Test
    public void connect_to_2_different_servers() {
        d2.userModule().blockingLogIn(username, password, RealServerMother.android_current, null);
        d2.metadataModule().blockingDownload();
        int programsA0 = d2.programModule().programs().blockingCount();
        d2.userModule().blockingLogOut();


        d2.userModule().blockingLogIn(username, password, RealServerMother.url2_29, null);
        d2.metadataModule().blockingDownload();
        int programsA1 = d2.programModule().programs().blockingCount();
        d2.userModule().blockingLogOut();

        d2.userModule().blockingLogIn(username, password, RealServerMother.android_current, null);
        int programsA2 = d2.programModule().programs().blockingCount();
    }
}
