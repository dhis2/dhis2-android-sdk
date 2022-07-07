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

package org.hisp.dhis.android.core.user.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.arch.db.access.SqliteCheckerUtility.isDatabaseEmpty;
import static org.hisp.dhis.android.core.arch.db.access.SqliteCheckerUtility.isTableEmpty;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.event.internal.EventCallFactory;
import org.hisp.dhis.android.core.user.AuthenticatedUser;
import org.junit.Before;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class LogoutCallRealIntegrationShould extends BaseRealIntegrationTest {

    private ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        authenticatedUserStore = AuthenticatedUserStore.create(d2.databaseAdapter());
    }

    //@Test
    public void delete_credentials_when_log_out_after_sync_data() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();

        Callable<List<Event>> eventCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0, Collections.emptyList());

        eventCall.call();

        assertThat(isDatabaseEmpty(d2.databaseAdapter())).isFalse();

        d2.userModule().logOut().blockingAwait();

        assertThat(isDatabaseEmpty(d2.databaseAdapter())).isFalse();
        assertThat(isTableEmpty(d2.databaseAdapter(), EventTableInfo.TABLE_INFO.name())).isFalse();

        AuthenticatedUser authenticatedUser = authenticatedUserStore.selectFirst();

        assertThat(authenticatedUser).isNotNull();
        assertThat(getD2DIComponent(d2).credentialsSecureStore().get()).isNull();
    }

    //@Test
    public void recreate_credentials_when_login_again()
            throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();

        assertThat(isDatabaseEmpty(d2.databaseAdapter())).isFalse();

        d2.userModule().logOut().blockingAwait();

        assertThat(isDatabaseEmpty(d2.databaseAdapter())).isFalse();

        AuthenticatedUser authenticatedUser = authenticatedUserStore.selectFirst();

        assertThat(authenticatedUser).isNotNull();
        assertThat(getD2DIComponent(d2).credentialsSecureStore().get()).isNull();

        d2.userModule().logIn(username, password, url).blockingGet();

        authenticatedUser = authenticatedUserStore.selectFirst();

        assertThat(authenticatedUser).isNotNull();
        assertThat(getD2DIComponent(d2).credentialsSecureStore().get()).isNull();
    }

    //@Test
    public void response_successful_on_login_logout_and_login() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();
        d2.userModule().logOut().blockingAwait();
        d2.userModule().logIn(username, password, url).blockingGet();
    }
}