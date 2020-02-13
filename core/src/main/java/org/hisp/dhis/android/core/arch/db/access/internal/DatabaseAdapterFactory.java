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

import androidx.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatabaseAdapterFactory {

    private static boolean encrypt;
    private static String ENCRYPTION_PASSWORD = "dhis-password";
    private static String databaseName;
    private static Integer version;
    private static Context context;

    private static Map<String, UnencryptedDatabaseOpenHelper> unencryptedOpenHelpers = new HashMap<>();
    private static Map<String, EncryptedDatabaseOpenHelper> encryptedOpenHelpers = new HashMap<>();
    private static List<DatabaseAdapter> adaptersToPreventNotClosedError = new ArrayList<>();

    public static void setExperimentalEncryption(boolean experimentalEncryption) {
        encrypt = experimentalEncryption;
    }

    public static DatabaseAdapter getDatabaseAdapter(Context context, String databaseName) {
        DatabaseAdapterFactory.context = context;
        DatabaseAdapterFactory.databaseName = databaseName;
        DatabaseAdapterFactory.version = null;
        return new ParentDatabaseAdapter();
    }

    @VisibleForTesting
    public static DatabaseAdapter getDatabaseAdapter(Context context, String databaseName, int version) {
        DatabaseAdapterFactory.context = context;
        DatabaseAdapterFactory.databaseName = databaseName;
        DatabaseAdapterFactory.version = version;
        return new ParentDatabaseAdapter();
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    public static void createOrOpenDatabase(DatabaseAdapter adapter) {
        try {
            ParentDatabaseAdapter parentDatabaseAdapter = (ParentDatabaseAdapter) adapter;
            parentDatabaseAdapter.setAdapter(instantiateAdapter());
        } catch (ClassCastException cce) {
            // This ensures tests that mock DatabaseAdapter pass
        }
    }

    private static DatabaseAdapter instantiateAdapter() {
        int actualVersion = version == null ? BaseDatabaseOpenHelper.VERSION : version;
        if (encrypt) {
            EncryptedDatabaseOpenHelper openHelper = instantiateOpenHelper(
                    encryptedOpenHelpers, "-enc.db",
                    dbName -> new EncryptedDatabaseOpenHelper(context, dbName, actualVersion));
            DatabaseAdapter adapter = new EncryptedDatabaseAdapter(openHelper.getWritableDatabase(ENCRYPTION_PASSWORD));
            adaptersToPreventNotClosedError.add(adapter);
            return adapter;
        } else {
            UnencryptedDatabaseOpenHelper openHelper = instantiateOpenHelper(unencryptedOpenHelpers, ".db",
                    dbName -> new UnencryptedDatabaseOpenHelper(context, dbName, actualVersion));
            DatabaseAdapter adapter = new UnencryptedDatabaseAdapter(openHelper.getWritableDatabase());
            adaptersToPreventNotClosedError.add(adapter);
            return adapter;
        }
    }

    interface Function<I, O> {
        O run(I i);
    }

    private static <H> H instantiateOpenHelper(Map<String, H> helpers, String dbPostfix,
                                               Function<String, H> helperCreator) {
        String databaseNameWithExtension = databaseName == null ? null : databaseName + dbPostfix;
        H openHelper;
        if (databaseNameWithExtension == null || !helpers.containsKey(databaseNameWithExtension)) {
            openHelper = helperCreator.run(databaseNameWithExtension);
            if (databaseNameWithExtension != null) {
                helpers.put(databaseNameWithExtension, openHelper);
            }
        } else {
            openHelper = helpers.get(databaseNameWithExtension);
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