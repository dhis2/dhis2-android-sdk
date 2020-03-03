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
import org.hisp.dhis.android.core.arch.storage.internal.ObjectSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class MultiUserDatabaseManager {

    private final DatabaseAdapter databaseAdapter;
    private final ObjectSecureStore<DatabasesConfiguration> databaseConfigurationSecureStore;
    private final DatabaseConfigurationHelper configurationHelper;
    private final Context context;
    private final SecureStore secureStore;
    private final DatabaseCopy databaseCopy;

    @Inject
    MultiUserDatabaseManager(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull ObjectSecureStore<DatabasesConfiguration> databaseConfigurationSecureStore,
            @NonNull DatabaseConfigurationHelper configurationHelper,
            @NonNull Context context,
            @NonNull SecureStore secureStore,
            @NonNull DatabaseCopy databaseCopy) {
        this.databaseAdapter = databaseAdapter;
        this.databaseConfigurationSecureStore = databaseConfigurationSecureStore;
        this.configurationHelper = configurationHelper;
        this.context = context;
        this.secureStore = secureStore;
        this.databaseCopy = databaseCopy;
    }

    public static MultiUserDatabaseManager create(DatabaseAdapter databaseAdapter, Context context,
                                                  SecureStore secureStore) {
        DatabaseConfigurationHelper configHelper = new DatabaseConfigurationHelper(new DatabaseNameGenerator());
        return new MultiUserDatabaseManager(databaseAdapter,
                DatabaseConfigurationSecureStore.get(secureStore), configHelper, context, secureStore,
                new DatabaseCopy());
    }

    public void loadIfLogged(Credentials credentials) {
        DatabasesConfiguration databaseConfiguration = DatabaseConfigurationMigration.apply(context, secureStore);

        if (databaseConfiguration != null && credentials != null) {
            ServerURLWrapper.setServerUrl(databaseConfiguration.loggedServerUrl());
            DatabaseUserConfiguration userConfiguration = configurationHelper.getLoggedUserConfiguration(
                    databaseConfiguration, credentials.username());
            createOrOpenDatabase(userConfiguration);
        }
    }

    public void loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(String serverUrl, String username, boolean encrypt) {
        boolean existing = loadExistingChangingEncryptionIfRequired(serverUrl, username, userConfiguration -> encrypt);
        if (!existing) {
            DatabaseUserConfiguration userConfiguration = addNewConfigurationInternal(serverUrl, username, encrypt);
            createOrOpenDatabase(userConfiguration);
        }
    }

    public boolean loadExistingKeepingEncryption(String serverUrl, String username) {
        return loadExistingChangingEncryptionIfRequired(serverUrl, username, DatabaseUserConfiguration::encrypted);
    }

    private boolean loadExistingChangingEncryptionIfRequired(String serverUrl, String username,
                                                             EncryptionExtractor encryptionExtractor) {
        DatabaseUserConfiguration existingUserConfiguration = getUserConfiguration(serverUrl, username);
        if (existingUserConfiguration == null) {
            return false;
        }

        boolean encrypt = encryptionExtractor.extract(existingUserConfiguration);
        DatabaseUserConfiguration updatedUserConfiguration = addNewConfigurationInternal(serverUrl, username, encrypt);
        createOrOpenDatabase(updatedUserConfiguration);

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
            DatabaseAdapter auxOldParentDatabaseAdapter = DatabaseAdapterFactory.newParentDatabaseAdapter();
            DatabaseAdapterFactory.createOrOpenDatabase(auxOldParentDatabaseAdapter, context, existingUserConfiguration);
            databaseCopy.copy(auxOldParentDatabaseAdapter, databaseAdapter);
            context.deleteDatabase(existingUserConfiguration.databaseName());
        }
    }

    private void createOrOpenDatabase(DatabaseUserConfiguration userConfiguration) {
        DatabaseAdapterFactory.createOrOpenDatabase(databaseAdapter, context, userConfiguration);
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