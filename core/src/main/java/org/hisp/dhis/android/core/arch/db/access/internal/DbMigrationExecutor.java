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
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DbMigrationExecutor {

    private final SQLiteDatabase database;
    private final DbMigrationParser parser;

    private static final int SNAPSHOT_VERSION = 64;

    public DbMigrationExecutor(SQLiteDatabase database, AssetManager assetManager) {
        this.database = database;
        this.parser = new DbMigrationParser(assetManager);
    }

    public void upgradeFromTo(int oldVersion, int newVersion) {
        database.beginTransaction();
        try {
            int initialMigrationVersion = performSnapshotIfRequired(oldVersion, newVersion);
            executeFilesSQL(parser.parseMigrations(initialMigrationVersion, newVersion));
            database.setTransactionSuccessful();
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        } finally {
            database.endTransaction();
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

    private void executeFilesSQL(List<Map<String, List<String>>> scripts) {
        for (Map<String, List<String>> script : scripts) {
            executeFileSQL(script);
        }
    }

    private void executeFileSQL(Map<String, List<String>> scripts) {
        List<String> ups = scripts.get("up");
        if (ups != null) {
            for (String script : ups) {
                database.execSQL(script);
            }
        }
    }
}