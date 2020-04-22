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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStore;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl;

import java.util.Arrays;

class DatabaseConfigurationMigration {

    static final String OLD_DBNAME = "dhis.db";

    private final Context context;
    private final ObjectKeyValueStore<DatabasesConfiguration> newConfigurationStore;
    private final DatabaseConfigurationTransformer transformer;
    private final DatabaseNameGenerator nameGenerator;
    private final DatabaseRenamer renamer;
    private final DatabaseAdapterFactory databaseAdapterFactory;

    DatabaseConfigurationMigration(Context context,
                                   ObjectKeyValueStore<DatabasesConfiguration> newConfigurationStore,
                                   DatabaseConfigurationTransformer transformer,
                                   DatabaseNameGenerator nameGenerator,
                                   DatabaseRenamer renamer,
                                   DatabaseAdapterFactory databaseAdapterFactory) {
        this.context = context;
        this.newConfigurationStore = newConfigurationStore;
        this.transformer = transformer;
        this.nameGenerator = nameGenerator;
        this.renamer = renamer;
        this.databaseAdapterFactory = databaseAdapterFactory;
    }

    DatabasesConfiguration apply() {
        boolean oldDatabaseExist = Arrays.asList(context.databaseList()).contains(OLD_DBNAME);
        if (oldDatabaseExist) {
            DatabaseAdapter databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, OLD_DBNAME, false);

            String username = getUsername(databaseAdapter);
            String serverUrl = getServerUrl(databaseAdapter);
            databaseAdapter.close();

            if (username == null || serverUrl == null) {
                context.deleteDatabase(OLD_DBNAME);
                return null;
            } else {
                String databaseName = nameGenerator.getDatabaseName(serverUrl, username, false);
                renamer.renameDatabase(OLD_DBNAME, databaseName);
                DatabasesConfiguration newConfiguration = transformer.transform(serverUrl, databaseName, username);
                newConfigurationStore.set(newConfiguration);
                return newConfiguration;
            }
        } else {
            return newConfigurationStore.get();
        }
    }

    private String getUsername(DatabaseAdapter databaseAdapter) {
        UserCredentialsStore store = UserCredentialsStoreImpl.create(databaseAdapter);
        UserCredentials credentials = store.selectFirst();
        return credentials == null ? null : credentials.username();
    }

    private String getServerUrl(DatabaseAdapter databaseAdapter) {
        ObjectStore<Configuration> store = ConfigurationStore.create(databaseAdapter);
        Configuration configuration = store.selectFirst();
        return configuration == null ? null : configuration.serverUrl();
    }

    static DatabaseConfigurationMigration create(Context context,
                                                 InsecureStore insecureStore,
                                                 DatabaseAdapterFactory databaseAdapterFactory) {
        return new DatabaseConfigurationMigration(
                context,
                DatabaseConfigurationInsecureStore.get(insecureStore),
                new DatabaseConfigurationTransformer(),
                new DatabaseNameGenerator(),
                new DatabaseRenamer(context),
                databaseAdapterFactory);
    }
}