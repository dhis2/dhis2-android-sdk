/*
 * Copyright (c) 2017, University of Oslo
 *
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

package org.hisp.dhis.android.core.data.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbOpenHelper extends SQLiteOpenHelper {

    public static final int VERSION = 20;
    private final AssetManager assetManager;
    private Integer testVersion = null;

    public DbOpenHelper(@NonNull Context context, @Nullable String databaseName) {
        super(context, databaseName, null, VERSION);
        SQLiteDatabase.loadLibs(context);
        this.assetManager = context.getAssets();
    }

    public DbOpenHelper(Context context, String databaseName, int testVersion) {
        super(context, databaseName, null, testVersion);
        SQLiteDatabase.loadLibs(context);
        this.testVersion = testVersion;
        this.assetManager = context.getAssets();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // enable foreign key support in database
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.rawExecSQL("PRAGMA journal_mode = WAL;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            int version = db.getVersion();
            if (testVersion == null) {
                version = version > VERSION ? version : VERSION;
            } else {
                version = version > testVersion ? version : testVersion;
            }
            List<Map<String, List<String>>> parsed = parseList(0, version);
            upList(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            List<Map<String, List<String>>> parsed = parseList(oldVersion, newVersion);
            upList(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }

    private synchronized List<Map<String, List<String>>> parseList(int oldVersion, int newVersion) throws IOException {

        List<Map<String, List<String>>> scripts = new ArrayList<Map<String, List<String>>>();

        int startVersion = oldVersion + 1;
        for (int i = startVersion; i <= newVersion; i++) {
            Map<String, List<String>> script = this.parse(i);
            scripts.add(script);
        }

        return scripts;
    }

    public synchronized Map<String, List<String>> parse(int newVersion) throws IOException {

        InputStream inputStream;
        String migrationDir = "migrations";
        String migrationPath = migrationDir + "/" + newVersion + ".yaml";

        inputStream = assetManager.open(migrationPath);

        Yaml yaml = new Yaml();
        Map<String, List<String>> parsed = (Map) yaml.load(inputStream);

        return parsed;
    }

    private synchronized void upList(SQLiteDatabase database, List<Map<String, List<String>>> scripts) {
        database.beginTransaction();
        try {
            for (Map<String, List<String>> script : scripts) {
                up(database, script);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private synchronized void up(SQLiteDatabase database, Map<String, List<String>> scripts) {
        database.beginTransaction();
        try {
            List<String> ups = scripts.get("up");
            if (ups != null) {
                for (String script : ups) {
                    database.execSQL(script);
                }
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
}