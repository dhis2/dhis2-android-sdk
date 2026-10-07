/*
 *  Copyright (c) 2004-2026, University of Oslo
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

import androidx.room3.useReaderConnection
import androidx.sqlite.SQLITE_DATA_FLOAT
import androidx.sqlite.SQLITE_DATA_NULL
import androidx.sqlite.SQLiteStatement
import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import kotlin.math.abs

private const val MAX_EXACT_INTEGRAL_REAL = 1e15

internal suspend fun AppDatabase.queryScalarAsString(sql: String): String? {
    return useReaderConnection { transactor ->
        transactor.usePrepared(sql) { statement ->
            if (statement.step()) readColumnAsString(statement, 0) else null
        }
    }
}

internal fun readColumnAsString(statement: SQLiteStatement, index: Int): String? {
    return when (statement.getColumnType(index)) {
        SQLITE_DATA_NULL -> null
        SQLITE_DATA_FLOAT -> formatSqlReal(statement.getDouble(index))
        else -> statement.getText(index)
    }
}

internal fun formatSqlReal(value: Double): String {
    return if (value % 1.0 == 0.0 && abs(value) < MAX_EXACT_INTEGRAL_REAL) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}
