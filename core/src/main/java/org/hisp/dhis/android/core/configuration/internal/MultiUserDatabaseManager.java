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

import android.util.Log;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseExport;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class MultiUserDatabaseManager {

    private final DatabaseAdapter databaseAdapter;
    private final ObjectKeyValueStore<DatabasesConfiguration> databaseConfigurationSecureStore;
    private final DatabaseConfigurationHelper configurationHelper;
    private final DatabaseAdapterFactory databaseAdapterFactory;
    private final DatabaseExport databaseExport;

    private static int maxServerUserPairs = 1;

    @Inject
    MultiUserDatabaseManager(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull ObjectKeyValueStore<DatabasesConfiguration> databaseConfigurationSecureStore,
            @NonNull DatabaseConfigurationHelper configurationHelper,
            @NonNull DatabaseAdapterFactory databaseAdapterFactory,
            @NonNull DatabaseExport databaseExport) {
        this.databaseAdapter = databaseAdapter;
        this.databaseConfigurationSecureStore = databaseConfigurationSecureStore;
        this.configurationHelper = configurationHelper;
        this.databaseAdapterFactory = databaseAdapterFactory;
        this.databaseExport = databaseExport;
    }

    public static void setMaxServerUserPairs(int pairs) {
        maxServerUserPairs = pairs;
    }

    public void loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(String serverUrl, String username,
                                                                           boolean encrypt) {
        boolean existing = loadExistingChangingEncryptionIfRequired(serverUrl, username, userConfiguration -> encrypt,
                true);
        if (!existing) {
            createNew(serverUrl, username, encrypt);
        }
    }

    public void loadExistingKeepingEncryptionOtherwiseCreateNew(String serverUrl, String username, boolean encrypt) {
        boolean existing = loadExistingChangingEncryptionIfRequired(serverUrl, username,
                DatabaseUserConfiguration::encrypted, true);
        if (!existing) {
            createNew(serverUrl, username, encrypt);
        }
    }

    public void createNew(String serverUrl, String username, boolean encrypt) {
        DatabasesConfiguration configuration = databaseConfigurationSecureStore.get();
        int pairsCount = configurationHelper.countServerUserPairs(configuration);
        if (pairsCount == maxServerUserPairs) {
            DatabaseUserConfiguration oldestUserConfig = configurationHelper.getOldestServerUser(configuration);
            DatabasesConfiguration updatedConfigurations =
                    configurationHelper.removeServerUserConfiguration(configuration, oldestUserConfig);
            databaseConfigurationSecureStore.set(updatedConfigurations);
            databaseAdapterFactory.deleteDatabase(oldestUserConfig);
        }
        DatabaseUserConfiguration userConfiguration = addNewConfigurationInternal(serverUrl, username, encrypt);
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, userConfiguration);
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
        changeEncryptionIfRequired(serverUrl, existingUserConfiguration, encrypt);

        if (encrypt != existingUserConfiguration.encrypted() || alsoOpenWhenEncryptionDoesntChange) {
            DatabaseUserConfiguration updatedUserConfiguration = addNewConfigurationInternal(serverUrl, username,
                    encrypt);
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, updatedUserConfiguration);
        }

        return true;
    }

    interface EncryptionExtractor {
        boolean extract(DatabaseUserConfiguration userConfiguration);
    }

    private void changeEncryptionIfRequired(String serverUrl, DatabaseUserConfiguration existingUserConfiguration,
                                            boolean encrypt) {
        if (encrypt != existingUserConfiguration.encrypted()) {
            Log.w(MultiUserDatabaseManager.class.getName(),
                    "Encryption value changed for " + existingUserConfiguration.username() +  ": " + encrypt);

            if (encrypt && !existingUserConfiguration.encrypted()) {
                databaseExport.encrypt(serverUrl, existingUserConfiguration);
            } else if (!encrypt && existingUserConfiguration.encrypted()) {
                databaseExport.decrypt(serverUrl, existingUserConfiguration);
            }

            databaseAdapterFactory.deleteDatabase(existingUserConfiguration);
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