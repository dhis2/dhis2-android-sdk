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

package org.hisp.dhis.android.core.arch.db.access.internal;

import android.content.Context;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount;
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class DatabaseAdapterFactory {

    private static Map<String, UnencryptedDatabaseOpenHelper> unencryptedOpenHelpers = new HashMap<>();
    private static Map<String, EncryptedDatabaseOpenHelper> encryptedOpenHelpers = new HashMap<>();
    private static List<DatabaseAdapter> adaptersToPreventNotClosedError = new ArrayList<>();

    private final Context context;
    private final DatabaseEncryptionPasswordManager passwordManager;

    @Inject
    DatabaseAdapterFactory(Context context,
                           DatabaseEncryptionPasswordManager passwordManager) {
        this.context = context;
        this.passwordManager = passwordManager;
    }

    public static DatabaseAdapterFactory create(Context context, SecureStore secureStore) {
        return new DatabaseAdapterFactory(context, DatabaseEncryptionPasswordManager.create(secureStore));
    }

    public DatabaseAdapter newParentDatabaseAdapter() {
        return new ParentDatabaseAdapter();
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    public void createOrOpenDatabase(DatabaseAdapter adapter, String databaseName, boolean encrypt, Integer version) {
        try {
            ParentDatabaseAdapter parentDatabaseAdapter = (ParentDatabaseAdapter) adapter;
            DatabaseAdapter internalAdapter = newInternalAdapter(databaseName, context, encrypt, version);
            adaptersToPreventNotClosedError.add(internalAdapter);
            parentDatabaseAdapter.setAdapter(internalAdapter);
        } catch (ClassCastException cce) {
            // This ensures tests that mock DatabaseAdapter pass
        }
    }

    public void createOrOpenDatabase(DatabaseAdapter adapter, String databaseName, boolean encrypt) {
        createOrOpenDatabase(adapter, databaseName, encrypt, BaseDatabaseOpenHelper.VERSION);
    }

    public void createOrOpenDatabase(DatabaseAdapter adapter, DatabaseAccount userConfiguration) {
        createOrOpenDatabase(adapter, userConfiguration.databaseName(), userConfiguration.encrypted(),
                BaseDatabaseOpenHelper.VERSION);
    }

    public void deleteDatabase(DatabaseAccount userConfiguration) {
        context.deleteDatabase(userConfiguration.databaseName());
        if (userConfiguration.encrypted()) {
            encryptedOpenHelpers.remove(userConfiguration.databaseName());
            passwordManager.deletePassword(userConfiguration.databaseName());
        } else {
            unencryptedOpenHelpers.remove(userConfiguration.databaseName());
        }
    }

    private DatabaseAdapter newInternalAdapter(String databaseName, Context context,
                                                      boolean encrypt, int version) {
        if (encrypt) {
            EncryptedDatabaseOpenHelper openHelper = instantiateOpenHelper(databaseName, encryptedOpenHelpers,
                    v -> new EncryptedDatabaseOpenHelper(context, databaseName, version));
            String password = passwordManager.getPassword(databaseName);
            return new EncryptedDatabaseAdapter(openHelper.getWritableDatabase(password), openHelper.getDatabaseName());
        } else {
            UnencryptedDatabaseOpenHelper openHelper = instantiateOpenHelper(databaseName, unencryptedOpenHelpers,
                    v -> new UnencryptedDatabaseOpenHelper(context, databaseName, version));
            return new UnencryptedDatabaseAdapter(openHelper.getWritableDatabase(), openHelper.getDatabaseName());
        }
    }

    private interface Function<I, O> {
        O run(I i);
    }

    private <H> H instantiateOpenHelper(String databaseName, Map<String, H> helpers, Function<Void, H> helperCreator) {
        H openHelper;
        if (databaseName == null || !helpers.containsKey(databaseName)) {
            openHelper = helperCreator.run(null);
            if (databaseName != null) {
                helpers.put(databaseName, openHelper);
            }
        } else {
            openHelper = helpers.get(databaseName);
        }
        return openHelper;
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    public void removeDatabaseAdapter(DatabaseAdapter adapter) {
        try {
            ParentDatabaseAdapter parentDatabaseAdapter = (ParentDatabaseAdapter) adapter;
            parentDatabaseAdapter.removeAdapter();
        } catch (ClassCastException cce) {
            // This ensures tests that mock DatabaseAdapter pass
        }
    }
}