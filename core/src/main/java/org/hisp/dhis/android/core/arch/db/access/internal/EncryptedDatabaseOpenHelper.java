/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import net.zetetic.database.sqlcipher.SQLiteConnection;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;

import org.hisp.dhis.android.core.common.internal.NativeLibraryLoader;


class EncryptedDatabaseOpenHelper extends SQLiteOpenHelper {

    private final BaseDatabaseOpenHelper baseHelper;

    static final SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
        @Override
        public void preKey(SQLiteConnection connection) {

        }

        @Override
        public void postKey(SQLiteConnection connection) {
            // Should we add a Cancellation signal here?
            connection.executeRaw("PRAGMA cipher_page_size = 16384;", null, null);
            connection.execute("PRAGMA cipher_memory_security = OFF;", null, null);
        }

    };

    EncryptedDatabaseOpenHelper(Context context, String databaseName, String password, int targetVersion) {
        /*
        factory: use default
        minimumSupportedVersion: use default
        errorHandler: use default
        enableWriteAheadLogging: TRUE for enabling Read and Write operations in parallel from more than one thread
        From docs: "It is a good idea to enable write-ahead logging whenever a database will be concurrently
        accessed and modified by multiple threads at the same time."
         */
        super(context, databaseName, password, null, targetVersion, 0, null, hook, true);
        NativeLibraryLoader.INSTANCE.loadSQLCipher();
        this.baseHelper = new BaseDatabaseOpenHelper(context, targetVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        baseHelper.onOpen(new EncryptedDatabaseAdapter(db, getDatabaseName()));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        baseHelper.onCreate(new EncryptedDatabaseAdapter(db, getDatabaseName()));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        baseHelper.onUpgrade(new EncryptedDatabaseAdapter(db, getDatabaseName()), oldVersion, newVersion);
    }

}