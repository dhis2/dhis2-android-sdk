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

package org.hisp.dhis.android.core.d2manager;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import androidx.test.InstrumentationRegistry;

import static com.google.common.truth.Truth.assertThat;

public class D2ManagerRealIntegrationShould {

    private static D2Configuration d2Configuration;
    private static Context context;

    private D2Manager d2Manager;

    @BeforeClass
    public static void setUpClass() {
        context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        d2Configuration = D2Configuration.builder()
                .databaseName(generateDatabaseName() + ".db")
                .appName("app_name")
                .appVersion("1.0.0")
                .readTimeoutInSeconds(100)
                .connectTimeoutInSeconds(100)
                .writeTimeoutInSeconds(100)
                .networkInterceptors(Lists.newArrayList(new StethoInterceptor()))
                .context(context)
                .build();
    }

    @Before
    public void setUp() {
        d2Manager = new D2Manager(d2Configuration);
    }

    @After
    public void tearDown() {
        if (d2Manager.databaseAdapter != null && d2Manager.databaseAdapter.database() != null) {
            d2Manager.databaseAdapter.database().close();
        }
        context.deleteDatabase(d2Configuration.databaseName());
    }

    @Test
    public void return_false_if_not_configured() {
        assertThat(d2Manager.isD2Configured()).isFalse();
    }

    @Test
    public void return_true_if_configured() {
        configureD2();
        assertThat(d2Manager.isD2Configured()).isTrue();
    }

    @Test
    public void create_a_d2_instance_which_reads_data_from_db() {
        configureD2();
        persistCredentialsInDb();

        UserCredentials userCredentials = d2Manager.getD2().userModule().userCredentials.get();
        assertThat(userCredentials.user().uid().equals("user")).isTrue();
    }

    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related the d2 instantiation.
     * It works against the demo server.
     */
    //@Test
    public void create_a_d2_instance_which_downloads_and_persists_data_from_server() throws Exception {
        configureD2();

        d2Manager.getD2().userModule().logIn("android", "Android123").call();

        assertThat(d2Manager.getD2().userModule().authenticatedUser.get().user() != null).isTrue();
    }

    private void configureD2() {
        d2Manager.configureD2(RealServerMother.url);
    }

    private void persistCredentialsInDb() {
        d2Manager.getD2().databaseAdapter().database().setForeignKeyConstraintsEnabled(Boolean.FALSE);

        UserCredentialsStoreImpl.create(d2Manager.getD2().databaseAdapter()).insert(UserCredentials.builder()
                .user(User.builder().uid("user").build()).uid("uid").username("username").build());
    }

    private static String generateDatabaseName() {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }
}