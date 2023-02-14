/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.arch.db.access.internal

import android.content.res.AssetManager
import android.util.Log
import androidx.annotation.VisibleForTesting
import java.io.IOException
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter

internal class DatabaseMigrationExecutor(private val databaseAdapter: DatabaseAdapter, assetManager: AssetManager) {
    private val parser = DatabaseMigrationParser(assetManager, databaseAdapter)

    companion object {
        private const val SNAPSHOT_VERSION = BaseDatabaseOpenHelper.VERSION
        private val MIGRATIONS_ACCEPTING_ERRORS = setOf(98)

        @VisibleForTesting
        var USE_SNAPSHOT = true
    }

    fun upgradeFromTo(oldVersion: Int, newVersion: Int) {
        val transaction = databaseAdapter.beginNewTransaction()
        try {
            val initialMigrationVersion = if (USE_SNAPSHOT) performSnapshotIfRequired(oldVersion, newVersion) else 0
            val migrations = parser.parseMigrations(initialMigrationVersion, newVersion)
            migrations.forEach {
                executeSQLMigration(it)
                executeCodeMigration(it)
            }
            transaction.setSuccessful()
        } catch (e: IOException) {
            Log.e("Database Error:", e.message ?: "")
        } finally {
            transaction.end()
        }
    }

    @Throws(IOException::class)
    private fun performSnapshotIfRequired(oldVersion: Int, newVersion: Int): Int {
        return if (oldVersion == 0 && newVersion >= SNAPSHOT_VERSION) {
            executeFileSQL(parser.parseSnapshot(SNAPSHOT_VERSION))
            SNAPSHOT_VERSION
        } else {
            oldVersion
        }
    }

    private fun executeSQLMigration(migration: DatabaseMigration) {
        if (MIGRATIONS_ACCEPTING_ERRORS.contains(migration.version)) {
            try {
                executeFileSQL(migration.sql)
            } catch (_: Throwable) {}
        } else {
            executeFileSQL(migration.sql)
        }
    }

    private fun executeCodeMigration(migration: DatabaseMigration) {
        migration.code?.migrate()
    }

    private fun executeFileSQL(script: List<String>?) {
        script?.forEach { databaseAdapter.execSQL(it) }
    }
}
