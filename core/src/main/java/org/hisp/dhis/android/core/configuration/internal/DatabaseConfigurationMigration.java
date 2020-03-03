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
import org.hisp.dhis.android.core.arch.storage.internal.ObjectSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStore;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl;

final class DatabaseConfigurationMigration {

    static final String OLD_DBNAME = "dhis.db";

    private final Context context;
    private final ObjectSecureStore<Configuration> oldConfigurationStore;
    private final ObjectSecureStore<DatabasesConfiguration> newConfigurationStore;
    private final DatabaseConfigurationTransformer transformer;
    private final DatabaseNameGenerator nameGenerator;
    private final DatabaseRenamer renamer;

    DatabaseConfigurationMigration(Context context,
                                   ObjectSecureStore<Configuration> oldConfigurationStore,
                                   ObjectSecureStore<DatabasesConfiguration> newConfigurationStore,
                                   DatabaseConfigurationTransformer transformer,
                                   DatabaseNameGenerator nameGenerator,
                                   DatabaseRenamer renamer) {
        this.context = context;
        this.oldConfigurationStore = oldConfigurationStore;
        this.newConfigurationStore = newConfigurationStore;
        this.transformer = transformer;
        this.nameGenerator = nameGenerator;
        this.renamer = renamer;
    }

    DatabasesConfiguration apply() {
        Configuration oldConfiguration = oldConfigurationStore.get();
        if (oldConfiguration == null) {
            return newConfigurationStore.get();
        } else {
            oldConfigurationStore.remove();
            DatabaseAdapter databaseAdapter = DatabaseAdapterFactory.newParentDatabaseAdapter();
            DatabaseAdapterFactory.createOrOpenDatabase(databaseAdapter, OLD_DBNAME, context, false);
            UserCredentialsStore userCredentialsStore = UserCredentialsStoreImpl.create(databaseAdapter);
            UserCredentials credentials = userCredentialsStore.selectFirst();
            String username = credentials == null ? null : credentials.username();
            databaseAdapter.close();

            String databaseName = nameGenerator.getDatabaseName(oldConfiguration.serverUrl().toString(),
                    username, false);
            if (username == null) {
                context.deleteDatabase(OLD_DBNAME);
                return null;
            } else {
                renamer.renameDatabase(OLD_DBNAME, databaseName);
                DatabasesConfiguration newConfiguration = transformer.transform(oldConfiguration, databaseName,
                        username);
                newConfigurationStore.set(newConfiguration);
                return newConfiguration;
            }
        }
    }

    static DatabaseConfigurationMigration create(Context context, SecureStore secureStore) {
        return new DatabaseConfigurationMigration(
                context,
                new ConfigurationSecureStoreImpl(secureStore),
                DatabaseConfigurationSecureStore.get(secureStore),
                new DatabaseConfigurationTransformer(),
                new DatabaseNameGenerator(),
                new DatabaseRenamer(context)
        );
    }
}