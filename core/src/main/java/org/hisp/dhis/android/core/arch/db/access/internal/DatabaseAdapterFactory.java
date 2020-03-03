/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
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

package org.hisp.dhis.android.core.arch.db.access.internal;

import android.content.Context;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.configuration.internal.DatabaseUserConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatabaseAdapterFactory {

    private static boolean encryptNextNotConfiguredDatabases;
    private static String ENCRYPTION_PASSWORD = "dhis-password";

    private static Map<String, UnencryptedDatabaseOpenHelper> unencryptedOpenHelpers = new HashMap<>();
    private static Map<String, EncryptedDatabaseOpenHelper> encryptedOpenHelpers = new HashMap<>();
    private static List<DatabaseAdapter> adaptersToPreventNotClosedError = new ArrayList<>();

    public static void setExperimentalEncryption(boolean experimentalEncryption) {
        encryptNextNotConfiguredDatabases = experimentalEncryption;
    }

    public static boolean getExperimentalEncryption() {
        return encryptNextNotConfiguredDatabases;
    }

    public static DatabaseAdapter newParentDatabaseAdapter() {
        return new ParentDatabaseAdapter();
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    public static void createOrOpenDatabase(DatabaseAdapter adapter, String databaseName, Context context,
                                            boolean encrypt, Integer version) {
        try {
            ParentDatabaseAdapter parentDatabaseAdapter = (ParentDatabaseAdapter) adapter;
            DatabaseAdapter internalAdapter = instantiateAdapter(databaseName, context, encrypt, version);
            adaptersToPreventNotClosedError.add(internalAdapter);
            parentDatabaseAdapter.setAdapter(internalAdapter);
        } catch (ClassCastException cce) {
            // This ensures tests that mock DatabaseAdapter pass
        }
    }

    public static void createOrOpenDatabase(DatabaseAdapter adapter, String databaseName, Context context,
                                            boolean encrypt) {
        createOrOpenDatabase(adapter, databaseName, context, encrypt, BaseDatabaseOpenHelper.VERSION);
    }

    public static void createOrOpenDatabase(DatabaseAdapter adapter, Context context,
                                            DatabaseUserConfiguration userConfiguration) {
        createOrOpenDatabase(adapter, userConfiguration.databaseName(), context, userConfiguration.encrypted(),
                BaseDatabaseOpenHelper.VERSION);
    }

    private static DatabaseAdapter instantiateAdapter(String databaseName, Context context,
                                                      boolean encrypt, int version) {
        if (encrypt) {
            EncryptedDatabaseOpenHelper openHelper = instantiateOpenHelper(databaseName, encryptedOpenHelpers,
                    v -> new EncryptedDatabaseOpenHelper(context, databaseName, version));
            return new EncryptedDatabaseAdapter(openHelper.getWritableDatabase(ENCRYPTION_PASSWORD));
        } else {
            UnencryptedDatabaseOpenHelper openHelper = instantiateOpenHelper(databaseName, unencryptedOpenHelpers,
                    v -> new UnencryptedDatabaseOpenHelper(context, databaseName, version));
            return new UnencryptedDatabaseAdapter(openHelper.getWritableDatabase());
        }
    }

    private interface Function<I, O> {
        O run(I i);
    }

    private static <H> H instantiateOpenHelper(String databaseName, Map<String, H> helpers,
                                               Function<Void, H> helperCreator) {
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
    public static void removeDatabaseAdapter(DatabaseAdapter adapter) {
        try {
            ParentDatabaseAdapter parentDatabaseAdapter = (ParentDatabaseAdapter) adapter;
            parentDatabaseAdapter.removeAdapter();
        } catch (ClassCastException cce) {
            // This ensures tests that mock DatabaseAdapter pass
        }
    }

    private DatabaseAdapterFactory() {
    }
}