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

package org.hisp.dhis.android.core.arch.db.access.internal;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper;
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager;
import org.hisp.dhis.android.core.configuration.internal.DatabaseUserConfiguration;

import java.io.File;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.functions.Action;

@SuppressWarnings("PMD.ExcessiveImports")
@Reusable
public class DatabaseExport {

    private final Context context;
    private final DatabaseEncryptionPasswordManager passwordManager;
    private final DatabaseConfigurationHelper configurationHelper;


    @Inject
    DatabaseExport(Context context, DatabaseEncryptionPasswordManager passwordManager, DatabaseConfigurationHelper configurationHelper) {
        this.context = context;
        this.passwordManager = passwordManager;
        this.configurationHelper = configurationHelper;
    }

    public void encrypt(String serverUrl, DatabaseUserConfiguration oldConfiguration) {
        wrapAction(() -> {
            DatabaseUserConfiguration newConfiguration =
                    configurationHelper.changeEncryption(serverUrl, oldConfiguration);

            File oldDatabaseFile = context.getDatabasePath(oldConfiguration.databaseName());
            File newDatabaseFile = context.getDatabasePath(newConfiguration.databaseName());

            String newPassword = passwordManager.getPassword(newConfiguration.databaseName());

            SQLiteDatabase.loadLibs(context);
            SQLiteDatabase oldDatabase = SQLiteDatabase.openOrCreateDatabase(oldDatabaseFile.getAbsolutePath(), "", null);
            oldDatabase.rawExecSQL(String.format(
                    "ATTACH DATABASE '%s' as encrypted KEY '%s';", newDatabaseFile.getAbsolutePath(), newPassword));
            oldDatabase.rawExecSQL("SELECT sqlcipher_export('encrypted');");
            oldDatabase.rawExecSQL("DETACH DATABASE encrypted;");

            oldDatabase.close();
        }, "Encrypt");
    }

    public void decrypt(String serverUrl, DatabaseUserConfiguration oldConfiguration) {
        wrapAction(() -> {
            DatabaseUserConfiguration newConfiguration =
                    configurationHelper.changeEncryption(serverUrl, oldConfiguration);

            File oldDatabaseFile = context.getDatabasePath(oldConfiguration.databaseName());
            File newDatabaseFile = context.getDatabasePath(newConfiguration.databaseName());

            String oldPassword = passwordManager.getPassword(oldConfiguration.databaseName());

            SQLiteDatabase.loadLibs(context);
            SQLiteDatabase oldDatabase = SQLiteDatabase.openOrCreateDatabase(oldDatabaseFile, oldPassword, null, EncryptedDatabaseOpenHelper.hook);
            oldDatabase.rawExecSQL(String.format(
                    "ATTACH DATABASE '%s' as plaintext KEY '';", newDatabaseFile.getAbsolutePath()));
            oldDatabase.rawExecSQL("SELECT sqlcipher_export('plaintext');");
            oldDatabase.rawExecSQL("DETACH DATABASE plaintext;");

            int version = oldDatabase.getVersion();
            SQLiteDatabase newDatabase = SQLiteDatabase.openOrCreateDatabase(newDatabaseFile, null, null);
            newDatabase.setVersion(version);

            newDatabase.close();
            oldDatabase.close();
        }, "Decrypt");
    }

    private void wrapAction(Action action, String tag) {
        long startMillis = System.currentTimeMillis();
        try {
            action.run();
        } catch (Exception e) {
            Log.e("ADAS", "DELETE THISSS!!!");
        }
        long endMillis = System.currentTimeMillis();

        Log.e("DatabaseExport", tag + ": " + (endMillis - startMillis) + "ms");
    }
}