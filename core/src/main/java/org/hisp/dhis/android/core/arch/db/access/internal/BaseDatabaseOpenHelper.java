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
import android.content.res.AssetManager;
import android.os.Build;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;

class BaseDatabaseOpenHelper {

    static final int VERSION = 72;

    private final AssetManager assetManager;
    private final int targetVersion;

    BaseDatabaseOpenHelper(Context context, int targetVersion) {
        this.assetManager = context.getAssets();
        this.targetVersion = targetVersion;
    }

    void onOpen(DatabaseAdapter databaseAdapter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // enable foreign key support in database only for lollipop and newer versions
            databaseAdapter.setForeignKeyConstraintsEnabled(true);
        }

        databaseAdapter.enableWriteAheadLogging();
    }

    void onCreate(DatabaseAdapter databaseAdapter) {
        executor(databaseAdapter).upgradeFromTo(0, targetVersion);
    }

    void onUpgrade(DatabaseAdapter databaseAdapter, int oldVersion, int newVersion) {
        executor(databaseAdapter).upgradeFromTo(oldVersion, newVersion);
    }

    private DatabaseMigrationExecutor executor(DatabaseAdapter databaseAdapter) {
        return new DatabaseMigrationExecutor(databaseAdapter, assetManager);
    }
}