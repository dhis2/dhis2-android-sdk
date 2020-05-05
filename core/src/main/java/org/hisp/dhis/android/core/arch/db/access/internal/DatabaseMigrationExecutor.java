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

import android.content.res.AssetManager;
import android.util.Log;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;

import java.io.IOException;
import java.util.List;

class DatabaseMigrationExecutor {

    private final DatabaseAdapter databaseAdapter;
    private final DatabaseMigrationParser parser;

    private static final int SNAPSHOT_VERSION = 72;

    DatabaseMigrationExecutor(DatabaseAdapter databaseAdapter, AssetManager assetManager) {
        this.databaseAdapter = databaseAdapter;
        this.parser = new DatabaseMigrationParser(assetManager);
    }

    void upgradeFromTo(int oldVersion, int newVersion) {
        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            int initialMigrationVersion = performSnapshotIfRequired(oldVersion, newVersion);
            executeFilesSQL(parser.parseMigrations(initialMigrationVersion, newVersion));
            transaction.setSuccessful();
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        } finally {
            transaction.end();
        }
    }

    private int performSnapshotIfRequired(int oldVersion, int newVersion) throws IOException {
        if (oldVersion == 0 && newVersion >= SNAPSHOT_VERSION) {
            executeFileSQL(parser.parseSnapshot(SNAPSHOT_VERSION));
            return SNAPSHOT_VERSION;
        } else {
            return oldVersion;
        }
    }

    private void executeFilesSQL(List<List<String>> scripts) {
        for (List<String> script : scripts) {
            executeFileSQL(script);
        }
    }

    private void executeFileSQL(List<String> script) {
        if (script != null) {
            for (String line : script) {
                databaseAdapter.execSQL(line);
            }
        }
    }
}