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

package org.hisp.dhis.android.core.configuration.internal;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStore;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.HttpUrl;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationMigration.OLD_DBNAME;

@RunWith(D2JunitRunner.class)
public class DatabaseConfigurationMigrationIntegrationShould {

    private final Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
    private final DatabaseConfigurationTransformer transformer = new DatabaseConfigurationTransformer();
    private final DatabaseNameGenerator nameGenerator = new DatabaseNameGenerator();
    private final DatabaseRenamer renamer = new DatabaseRenamer(context);
    private final DatabaseAdapterFactory databaseAdapterFactory = DatabaseAdapterFactory.create(context,
            new InMemorySecureStore());

    private final String URL_STR = "https://server.org/";
    private final String USERNAME = "usnm";
    private final String newName = nameGenerator.getDatabaseName(URL_STR, USERNAME, false);

    private DatabaseConfigurationMigration migration;

    private final UserCredentials credentials = UserCredentials.builder()
            .id(1L)
            .uid("uid")
            .username(USERNAME)
            .user(ObjectWithUid.create("user"))
            .build();

    private ObjectSecureStore<Configuration> oldConfigurationStore;
    private ObjectSecureStore<DatabasesConfiguration> newConfigurationStore;

    @Before
    public void setUp() throws IOException {
        SecureStore secureStore = new InMemorySecureStore();
        oldConfigurationStore = new ConfigurationSecureStoreImpl(secureStore);
        newConfigurationStore = DatabaseConfigurationSecureStore.get(secureStore);
        migration = new DatabaseConfigurationMigration(context, oldConfigurationStore, newConfigurationStore,
                transformer, nameGenerator, renamer, databaseAdapterFactory);
    }

    @Test
    public void delete_empty_database() {
        DatabaseAdapter databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, OLD_DBNAME, false);
        oldConfigurationStore.set(Configuration.forServerUrl(HttpUrl.parse(URL_STR)));

        assertThat(Arrays.asList(context.databaseList()).contains(OLD_DBNAME)).isTrue();
        migration.apply();
        assertThat(Arrays.asList(context.databaseList()).contains(OLD_DBNAME)).isFalse();
    }

    @Test
    public void rename_database_with_credentials() {
        DatabaseAdapter databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, OLD_DBNAME, false);
        oldConfigurationStore.set(Configuration.forServerUrl(HttpUrl.parse(URL_STR)));
        setCredentials(databaseAdapter);

        assertThat(Arrays.asList(context.databaseList()).contains(OLD_DBNAME)).isTrue();
        migration.apply();
        assertThat(Arrays.asList(context.databaseList()).contains(OLD_DBNAME)).isFalse();
        assertThat(Arrays.asList(context.databaseList()).contains(newName)).isTrue();

        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, newName, false);
        UserCredentialsStore credentialsStore = UserCredentialsStoreImpl.create(databaseAdapter);
        assertThat(credentialsStore.selectFirst()).isEqualTo(credentials);
    }

    @Test
    public void return_null_new_configuration_if_both_configurations_null() {
        assertThat(migration.apply()).isNull();
    }

    @Test
    public void return_existing_new_configuration_if_old_configuration_null() {
        DatabasesConfiguration newConfiguration = new DatabaseConfigurationHelper(nameGenerator)
                .setConfiguration(null, URL_STR, USERNAME, false);
        newConfigurationStore.set(newConfiguration);
        assertThat(migration.apply()).isSameAs(newConfiguration);
    }

    @Test
    public void return_empty_new_configuration_if_existing_empty_database() {
        DatabaseAdapter databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, OLD_DBNAME, false);
        oldConfigurationStore.set(Configuration.forServerUrl(HttpUrl.parse(URL_STR)));
        assertThat(migration.apply()).isNull();
    }

    @Test
    public void delete_old_configuration_after_applying_migration() {
        oldConfigurationStore.set(Configuration.forServerUrl(HttpUrl.parse(URL_STR)));
        assertThat(migration.apply()).isNull();
        assertThat(oldConfigurationStore.get()).isNull();
    }

    public void setCredentials(DatabaseAdapter databaseAdapter) {
        UserCredentialsStore credentialsStore = UserCredentialsStoreImpl.create(databaseAdapter);
        databaseAdapter.setForeignKeyConstraintsEnabled(false);
        credentialsStore.insert(credentials);
    }
}