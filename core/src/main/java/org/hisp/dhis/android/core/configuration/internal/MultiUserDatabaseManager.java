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
import android.util.Log;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class MultiUserDatabaseManager {

    private final DatabaseAdapter databaseAdapter;
    private final ObjectKeyValueStore<DatabasesConfiguration> databaseConfigurationSecureStore;
    private final DatabaseConfigurationHelper configurationHelper;
    private final Context context;
    private final DatabaseCopy databaseCopy;
    private final DatabaseConfigurationMigration migration;
    private final DatabaseAdapterFactory databaseAdapterFactory;
    private final DatabaseEncryptionPasswordManager passwordManager;

    private final int MAX_SERVER_USER_PAIRS = 1;

    @Inject
    MultiUserDatabaseManager(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull ObjectKeyValueStore<DatabasesConfiguration> databaseConfigurationSecureStore,
            @NonNull DatabaseConfigurationHelper configurationHelper,
            @NonNull Context context,
            @NonNull DatabaseCopy databaseCopy,
            @NonNull DatabaseConfigurationMigration migration,
            @NonNull DatabaseAdapterFactory databaseAdapterFactory,
            @NonNull DatabaseEncryptionPasswordManager passwordManager) {
        this.databaseAdapter = databaseAdapter;
        this.databaseConfigurationSecureStore = databaseConfigurationSecureStore;
        this.configurationHelper = configurationHelper;
        this.context = context;
        this.databaseCopy = databaseCopy;
        this.migration = migration;
        this.databaseAdapterFactory = databaseAdapterFactory;
        this.passwordManager = passwordManager;
    }

    public static MultiUserDatabaseManager create(DatabaseAdapter databaseAdapter, Context context,
                                                  SecureStore secureStore,
                                                  InsecureStore insecureStore,
                                                  DatabaseAdapterFactory databaseAdapterFactory) {
        return new MultiUserDatabaseManager(databaseAdapter,
                DatabaseConfigurationInsecureStore.get(insecureStore),
                DatabaseConfigurationHelper.create(), context,
                new DatabaseCopy(), DatabaseConfigurationMigration.create(context, secureStore,
                insecureStore, databaseAdapterFactory), databaseAdapterFactory,
                DatabaseEncryptionPasswordManager.create(secureStore));
    }

    public void loadIfLogged(Credentials credentials) {
        DatabasesConfiguration databaseConfiguration = migration.apply();

        if (databaseConfiguration != null && credentials != null) {
            ServerURLWrapper.setServerUrl(databaseConfiguration.loggedServerUrl());
            DatabaseUserConfiguration userConfiguration = configurationHelper.getLoggedUserConfiguration(
                    databaseConfiguration, credentials.username());
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, userConfiguration);
        }
    }

    public void loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(String serverUrl, String username,
                                                                           boolean encrypt) {
        boolean existing = loadExistingChangingEncryptionIfRequired(serverUrl, username, userConfiguration -> encrypt,
                true);
        if (!existing) {
            DatabasesConfiguration configuration = databaseConfigurationSecureStore.get();
            int pairsCount = configurationHelper.countServerUserPairs(configuration);
            if (pairsCount == MAX_SERVER_USER_PAIRS) {
                DatabaseUserConfiguration userConfiguration = configurationHelper.getOldestServerUser(configuration);
                DatabasesConfiguration updatedConfigurations =
                        configurationHelper.removeServerUserConfiguration(configuration, userConfiguration);
                databaseConfigurationSecureStore.set(updatedConfigurations);
                passwordManager.deletePassword(userConfiguration.databaseName());
                context.deleteDatabase(userConfiguration.databaseName());
            }
            DatabaseUserConfiguration userConfiguration = addNewConfigurationInternal(serverUrl, username, encrypt);
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, userConfiguration);
        }
    }

    public void changeEncryptionIfRequired(Credentials credentials, boolean encrypt) {
        DatabasesConfiguration databasesConfiguration = databaseConfigurationSecureStore.get();
        loadExistingChangingEncryptionIfRequired(databasesConfiguration.loggedServerUrl(),
                credentials.username(), userConfiguration -> encrypt, false);
    }

    public boolean loadExistingKeepingEncryption(String serverUrl, String username) {
        return loadExistingChangingEncryptionIfRequired(serverUrl, username, DatabaseUserConfiguration::encrypted,
                true);
    }

    private boolean loadExistingChangingEncryptionIfRequired(String serverUrl, String username,
                                                             EncryptionExtractor encryptionExtractor,
                                                             boolean alsoOpenWhenEncryptionDoesntChange) {
        DatabaseUserConfiguration existingUserConfiguration = getUserConfiguration(serverUrl, username);
        if (existingUserConfiguration == null) {
            return false;
        }

        boolean encrypt = encryptionExtractor.extract(existingUserConfiguration);
        if (encrypt != existingUserConfiguration.encrypted() || alsoOpenWhenEncryptionDoesntChange) {
            DatabaseUserConfiguration updatedUserConfiguration = addNewConfigurationInternal(serverUrl, username,
                    encrypt);
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, updatedUserConfiguration);
        }

        changeEncryptionIfRequired(existingUserConfiguration, encrypt);
        return true;
    }

    interface EncryptionExtractor {
        boolean extract(DatabaseUserConfiguration userConfiguration);
    }

    private void changeEncryptionIfRequired(DatabaseUserConfiguration existingUserConfiguration, boolean encrypt) {
        if (encrypt != existingUserConfiguration.encrypted()) {
            Log.w(MultiUserDatabaseManager.class.getName(),
                    "Encryption value changed for " + existingUserConfiguration.username() +  ": " + encrypt);
            DatabaseAdapter auxOldParentDatabaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
            databaseAdapterFactory.createOrOpenDatabase(auxOldParentDatabaseAdapter, existingUserConfiguration);
            databaseCopy.copy(auxOldParentDatabaseAdapter, databaseAdapter);
            context.deleteDatabase(existingUserConfiguration.databaseName());
        }
    }

    private DatabaseUserConfiguration getUserConfiguration(String serverUrl, String username) {
        DatabasesConfiguration configuration = databaseConfigurationSecureStore.get();
        return configurationHelper.getUserConfiguration(configuration, serverUrl, username);
    }

    private DatabaseUserConfiguration addNewConfigurationInternal(String serverUrl, String username, boolean encrypt) {
        DatabasesConfiguration updatedConfiguration = configurationHelper.setConfiguration(
                databaseConfigurationSecureStore.get(), serverUrl, username, encrypt);
        databaseConfigurationSecureStore.set(updatedConfiguration);
        return configurationHelper.getLoggedUserConfiguration(updatedConfiguration, username);
    }
}