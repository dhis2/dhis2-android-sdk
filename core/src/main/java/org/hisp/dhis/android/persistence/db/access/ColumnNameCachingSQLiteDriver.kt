/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.persistence.db.access

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement

/**
 * [SQLiteDriver] whose statements read their result column names only once.
 *
 * Room reads an entity from a raw query by looking up the index of each of its columns on every
 * row, and each lookup walks the result columns through [SQLiteStatement.getColumnName]. With the
 * bundled driver that is a JNI call that allocates a new string, so for a table with N columns
 * every row costs about N²/2 of those calls, more than reading the row itself.
 */
internal class ColumnNameCachingSQLiteDriver(private val delegate: SQLiteDriver) : SQLiteDriver by delegate {

    override fun open(fileName: String): SQLiteConnection {
        return ColumnNameCachingConnection(delegate.open(fileName))
    }
}

private class ColumnNameCachingConnection(private val delegate: SQLiteConnection) : SQLiteConnection by delegate {

    override fun prepare(sql: String): SQLiteStatement {
        return ColumnNameCachingStatement(delegate.prepare(sql))
    }
}

private class ColumnNameCachingStatement(private val delegate: SQLiteStatement) : SQLiteStatement by delegate {
    private var columnNames: List<String>? = null

    override fun getColumnCount(): Int = getColumnNames().size

    override fun getColumnName(index: Int): String = getColumnNames().getOrNull(index) ?: delegate.getColumnName(index)

    override fun getColumnNames(): List<String> {
        return columnNames ?: delegate.getColumnNames().also { columnNames = it }
    }
}
