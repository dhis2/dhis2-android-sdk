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

package org.hisp.dhis.android.core.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbOpenHelper extends SQLBriteOpenHelper {

    public static final int VERSION = 43;

    public DbOpenHelper(@NonNull Context context, @Nullable String databaseName) {
        super(context, databaseName, null, VERSION);
    }

    public DbOpenHelper(Context context, String databaseName, int testVersion) {
        super(context, databaseName, null, testVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // enable foreign key support in database only for lollipop and newer versions
            db.setForeignKeyConstraintsEnabled(true);
        }

        db.enableWriteAheadLogging();
    }

    // This fixes the bug in SQLBriteOpenHelper, which doesn't let seeds to be optional
    @Override
    public Map<String, List<String>> parse(int newVersion) throws IOException {
        Map<String, List<String>> versionMigrations = super.parse(newVersion);
        List<String> seeds = versionMigrations.get("seeds");
        if (seeds == null || seeds.size() == 1 && seeds.get(0) == null) {
            versionMigrations.put("seeds", new ArrayList<>());
        }
        return versionMigrations;
    }
}