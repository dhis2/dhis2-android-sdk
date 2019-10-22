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

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2Configuration;
import org.hisp.dhis.android.core.D2Manager;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import androidx.test.InstrumentationRegistry;

import static com.google.common.truth.Truth.assertThat;

public class D2ManagerRealIntegrationShould {

    private static D2Configuration d2Configuration;

    @BeforeClass
    public static void setUpClass() {
        d2Configuration = D2Configuration.builder()
                .appName("app_name")
                .appVersion("1.0.0")
                .networkInterceptors(Lists.newArrayList(new StethoInterceptor()))
                .context(InstrumentationRegistry.getTargetContext().getApplicationContext())
                .build();

        D2Manager.setDatabaseName(null);
    }

    @After
    public void tearDown() {
        if (D2Manager.databaseAdapter != null) {
            D2Manager.databaseAdapter.database().close();
        }
        D2Manager.clear();
    }

    @Test
    public void create_a_d2_instance_which_reads_data_from_db() {
        configureD2();
        persistCredentialsInDb();

        UserCredentials userCredentials = D2Manager.getD2().userModule().userCredentials().blockingGet();
        assertThat(userCredentials.user().uid().equals("user")).isTrue();
    }

    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related the d2 instantiation.
     * It works against the demo server.
     */
    //@Test
    public void create_a_d2_instance_which_downloads_and_persists_data_from_server() throws Exception {
        configureD2();

        D2Manager.getD2().userModule().logIn(RealServerMother.username, RealServerMother.password, RealServerMother.url).blockingGet();

        assertThat(D2Manager.getD2().userModule().authenticatedUser().blockingGet().user() != null).isTrue();
    }

    private void configureD2() {
        D2Manager.instantiateD2(d2Configuration).blockingGet();
    }

    private void persistCredentialsInDb() {
        D2Manager.getD2().databaseAdapter().database().setForeignKeyConstraintsEnabled(Boolean.FALSE);

        UserCredentialsStoreImpl.create(D2Manager.getD2().databaseAdapter()).insert(UserCredentials.builder()
                .user(ObjectWithUid.create("user")).uid("uid").username("username").build());
    }
}