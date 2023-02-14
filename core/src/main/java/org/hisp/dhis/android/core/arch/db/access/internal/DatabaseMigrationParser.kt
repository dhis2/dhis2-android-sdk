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
import java.io.IOException
import java.util.ArrayList
import java.util.Scanner
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.internal.migrations.DatabaseCodeMigrations

internal class DatabaseMigrationParser(
    private val assetManager: AssetManager,
    databaseAdapter: DatabaseAdapter
) {
    private val codeMigrations = DatabaseCodeMigrations(databaseAdapter)

    @Throws(IOException::class)
    fun parseMigrations(oldVersion: Int, newVersion: Int): List<DatabaseMigration> {
        val startVersion = oldVersion + 1
        return (startVersion..newVersion).map {
            parseMigration(it)
        }
    }

    @Throws(IOException::class)
    fun parseSnapshot(version: Int): List<String> {
        return parseFile("snapshots", version)
    }

    @Throws(IOException::class)
    private fun parseMigration(version: Int): DatabaseMigration {
        return DatabaseMigration(version, parseFile("migrations", version), codeMigrations.map[version])
    }

    @Throws(IOException::class)
    private fun parseFile(directory: String, newVersion: Int): List<String> {
        val fileName = "$directory/$newVersion.sql"
        val inputStream = assetManager.open(fileName)
        val sc = Scanner(inputStream, "UTF-8")
        val lines: MutableList<String> = ArrayList()
        while (sc.hasNextLine()) {
            val line = sc.nextLine()
            if (line.length > 1 && !line.contains("#")) {
                lines.add(line)
            }
        }
        sc.close()
        return lines
    }
}
