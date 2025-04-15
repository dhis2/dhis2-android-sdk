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

package org.hisp.dhis.android.core.arch.db.puresqlite

import androidx.sqlite.db.SupportSQLiteDatabase
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.util.dateFormat
import org.koin.core.annotation.Singleton

@Singleton
internal class OptionsSqliteDao(val db: SupportSQLiteDatabase) {

    fun insertOptionSet(optionSet: OptionSet) {
        db.execSQL(
            """
            INSERT OR REPLACE INTO option_sets 
            (uid, code, name, displayName, created, lastUpdated, deleted, version, valueType) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf(
                optionSet.uid(),
                optionSet.code(),
                optionSet.name(),
                optionSet.displayName(),
                optionSet.created().dateFormat(),
                optionSet.lastUpdated().dateFormat(),
                optionSet.deleted()?.let { if (it) 1 else 0 },
                optionSet.version(),
                optionSet.valueType()?.name
            )
        )
    }

    fun insertOption(option: Option) {
        db.execSQL(
            """
            INSERT OR REPLACE INTO options 
            (uid, code, name, displayName, created, lastUpdated, deleted, sortOrder, optionSet, color, icon) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf(
                option.uid(),
                option.code(),
                option.name(),
                option.displayName(),
                option.created().dateFormat(),
                option.lastUpdated().dateFormat(),
                option.deleted()?.let { if (it) 1 else 0 },
                option.sortOrder(),
                option.optionSet(),
            )
        )
    }

    fun countOptionSets(): Int {
        db.query("SELECT COUNT(*) FROM option_sets").use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    fun countOptions(): Int {
        db.query("SELECT COUNT(*) FROM options").use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }
}

