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

import net.sqlcipher.database.SQLiteDatabase;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.common.Unit;

import java.io.File;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;

@SuppressWarnings("PMD.ExcessiveImports")
@Reusable
class DatabaseExport {

    private final RxAPICallExecutor executor;
    private final Context context;
    private final DatabaseEncryptionPasswordManager passwordManager;
    private final DatabaseConfigurationHelper configurationHelper;


    @Inject
    DatabaseExport(RxAPICallExecutor executor, Context context, DatabaseEncryptionPasswordManager passwordManager, DatabaseConfigurationHelper configurationHelper) {
        this.executor = executor;
        this.context = context;
        this.passwordManager = passwordManager;
        this.configurationHelper = configurationHelper;
    }

    void encrypt(String serverUrl, DatabaseUserConfiguration oldConfiguration) {
        wrapAction(() -> {

            DatabaseUserConfiguration newConfiguration =
                    configurationHelper.changeEncryption(serverUrl, oldConfiguration);

            File oldDatabaseFile = context.getDatabasePath(oldConfiguration.databaseName());
            File newDatabaseFile = context.getDatabasePath(newConfiguration.databaseName());

            String password = passwordManager.getPassword(newConfiguration.databaseName());
            SQLiteDatabase oldDatabase = SQLiteDatabase.openOrCreateDatabase(oldDatabaseFile.getAbsolutePath(), "", null);
            oldDatabase.rawExecSQL(String.format(
                    "ATTACH DATABASE '%s' as encrypted KEY '%s';", newDatabaseFile.getAbsolutePath(), password));
            oldDatabase.rawExecSQL("SELECT sqlcipher_export('encrypted');");
            oldDatabase.rawExecSQL("DETACH DATABASE encrypted;");

            oldDatabase.close();
        }, "Encrypt");
    }

    void decrypt(String serverUrl, DatabaseUserConfiguration oldConfiguration) {
        wrapAction(() -> {
            DatabaseUserConfiguration newConfiguration =
                    configurationHelper.changeEncryption(serverUrl, oldConfiguration);

            File oldDatabaseFile = context.getDatabasePath(oldConfiguration.databaseName());
            File newDatabaseFile = context.getDatabasePath(newConfiguration.databaseName());

            String password = passwordManager.getPassword(oldConfiguration.databaseName());
            SQLiteDatabase oldDatabase = SQLiteDatabase.openOrCreateDatabase(oldDatabaseFile, password, null);
            oldDatabase.rawExecSQL(String.format(
                    "ATTACH DATABASE '%s' as plaintext KEY '';", newDatabaseFile.getAbsolutePath()));
            oldDatabase.rawExecSQL("SELECT sqlcipher_export('plaintext');");
            oldDatabase.rawExecSQL("DETACH DATABASE plaintext;");

            oldDatabase.close();
        }, "Decrypt");
    }

    private void wrapAction(Action action, String tag) {
        executor.wrapObservableTransactionally(Observable.fromCallable(() -> {
            long startMillis = System.currentTimeMillis();
            action.run();
            long endMillis = System.currentTimeMillis();

            Log.e("DatabaseExport", tag + ": " + (endMillis - startMillis) + "ms");
            return new Unit();
        }), true).blockingSubscribe();
    }
}