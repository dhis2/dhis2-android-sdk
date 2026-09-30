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

import androidx.sqlite.SQLITE_DATA_INTEGER
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.throwSQLiteException
import net.zetetic.database.DatabaseErrorHandler
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import net.zetetic.database.sqlcipher.driver.SQLCipherConnection
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import net.zetetic.database.sqlcipher.SQLiteStatement as CipherStatement

/**
 * [net.zetetic.database.sqlcipher.driver.SQLCipherDriver] with a faster write path.
 *
 * `SQLCipherStatement` runs every statement, writes included, through `rawQuery` and a
 * `CursorWindow`, and never closes that cursor. This driver opens the same database and hands row
 * statements to it unchanged, but closes their cursor on `close()`. Data-modifying statements, and
 * the `changes()` / `last_insert_rowid()` queries Room runs after each of them, go through reused
 * compiled statements instead.
 */
internal class CompiledWriteSQLCipherDriver(
    private val passphrase: ByteArray,
    private val hook: SQLiteDatabaseHook,
    private val errorHandler: DatabaseErrorHandler,
) : SQLiteDriver {

    override val hasConnectionPool: Boolean = true

    override fun open(fileName: String): SQLiteConnection {
        val database = SQLiteDatabase.openOrCreateDatabase(fileName, passphrase, null, errorHandler, hook)
        return CompiledWriteConnection(database)
    }
}

private class CompiledWriteConnection(private val database: SQLiteDatabase) : SQLiteConnection {
    private val delegate = SQLCipherConnection(database)
    private val cache = CompiledStatementCache(database)

    override fun prepare(sql: String): SQLiteStatement {
        val trimmed = sql.trim()
        return when {
            isScalarQuery(trimmed) ->
                ScalarLongStatement(cache.acquire(trimmed), trimmed.substringAfter(" "))
            isCompilableWrite(trimmed) -> CompiledWriteStatement(cache.acquire(sql))
            else -> CursorClosingStatement(delegate.prepare(sql))
        }
    }

    override fun inTransaction(): Boolean = delegate.inTransaction()

    override fun close() {
        cache.close()
        delegate.close()
    }

    private fun isScalarQuery(trimmed: String): Boolean {
        return SCALAR_QUERIES.any { it.equals(trimmed, ignoreCase = true) }
    }

    private fun isCompilableWrite(trimmed: String): Boolean {
        return trimmed.length > PREFIX_LENGTH &&
            trimmed.substring(0, PREFIX_LENGTH).uppercase() in WRITE_PREFIXES &&
            !trimmed.contains(RETURNING, ignoreCase = true)
    }

    private companion object {
        const val PREFIX_LENGTH = 3
        const val RETURNING = "RETURNING"
        val WRITE_PREFIXES = setOf("INS", "UPD", "DEL", "REP")
        val SCALAR_QUERIES = setOf("SELECT CHANGES()", "SELECT LAST_INSERT_ROWID()")
    }
}

private class CursorClosingStatement(private val delegate: SQLiteStatement) : SQLiteStatement by delegate {
    override fun close() {
        delegate.reset()
        delegate.close()
    }
}

@Suppress("TooManyFunctions")
private class CompiledWriteStatement(private val lease: CompiledStatementCache.Lease) : SQLiteStatement {
    private val statement = lease.statement

    override fun bindBlob(index: Int, value: ByteArray) = statement.bindBlob(index, value)

    override fun bindDouble(index: Int, value: Double) = statement.bindDouble(index, value)

    override fun bindLong(index: Int, value: Long) = statement.bindLong(index, value)

    override fun bindText(index: Int, value: String) = statement.bindString(index, value)

    override fun bindNull(index: Int) = statement.bindNull(index)

    override fun getBlob(index: Int): ByteArray = noRow()

    override fun getDouble(index: Int): Double = noRow()

    override fun getLong(index: Int): Long = noRow()

    override fun getText(index: Int): String = noRow()

    override fun isNull(index: Int): Boolean = noRow()

    override fun getColumnCount(): Int = 0

    override fun getColumnName(index: Int): String = noRow()

    override fun getColumnType(index: Int): Int = noRow()

    override fun step(): Boolean {
        statement.execute()
        return false
    }

    override fun reset() = Unit

    override fun clearBindings() = statement.clearBindings()

    override fun close() = lease.release()

    private fun noRow(): Nothing = throwSQLiteException(SQLITE_MISUSE, "no row")
}

@Suppress("TooManyFunctions")
private class ScalarLongStatement(
    private val lease: CompiledStatementCache.Lease,
    private val columnName: String,
) : SQLiteStatement {
    private val statement = lease.statement
    private var value: Long? = null

    override fun bindBlob(index: Int, value: ByteArray) = outOfRange()

    override fun bindDouble(index: Int, value: Double) = outOfRange()

    override fun bindLong(index: Int, value: Long) = outOfRange()

    override fun bindText(index: Int, value: String) = outOfRange()

    override fun bindNull(index: Int) = outOfRange()

    override fun getBlob(index: Int): ByteArray = throwSQLiteException(SQLITE_MISUSE, "not a blob")

    override fun getDouble(index: Int): Double = requireValue(index).toDouble()

    override fun getLong(index: Int): Long = requireValue(index)

    override fun getText(index: Int): String = requireValue(index).toString()

    override fun isNull(index: Int): Boolean {
        requireValue(index)
        return false
    }

    override fun getColumnCount(): Int = 1

    override fun getColumnName(index: Int): String {
        checkColumn(index)
        return columnName
    }

    override fun getColumnType(index: Int): Int {
        requireValue(index)
        return SQLITE_DATA_INTEGER
    }

    override fun step(): Boolean {
        if (value != null) {
            return false
        }
        value = statement.simpleQueryForLong()
        return true
    }

    override fun reset() {
        value = null
    }

    override fun clearBindings() = Unit

    override fun close() = lease.release()

    private fun requireValue(index: Int): Long {
        checkColumn(index)
        return value ?: throwSQLiteException(SQLITE_MISUSE, "no row")
    }

    private fun checkColumn(index: Int) {
        if (index != 0) outOfRange()
    }

    private fun outOfRange(): Nothing = throwSQLiteException(SQLITE_RANGE, "column index out of range")
}

private const val SQLITE_MISUSE = 21
private const val SQLITE_RANGE = 25

/**
 * Compiled statements kept for reuse, like Room 2's shared statements: Room 3 prepares a new
 * statement for every DAO call, and with SQLCipher compiling one costs as much as running it.
 * Each thread keeps its own statements, so a statement is never used by two threads at once.
 */
private class CompiledStatementCache(private val database: SQLiteDatabase) {
    private val statements = Collections.newSetFromMap(ConcurrentHashMap<CipherStatement, Boolean>())
    private val perThread = object : ThreadLocal<LinkedHashMap<String, CipherStatement>>() {
        override fun initialValue() = LinkedHashMap<String, CipherStatement>(0, LOAD_FACTOR, true)
    }

    @Volatile
    private var closed = false

    fun acquire(sql: String): Lease {
        val statement = perThread.get().remove(sql)
            ?: database.compileStatement(sql).also { statements.add(it) }
        return Lease(sql, statement)
    }

    fun close() {
        closed = true
        statements.forEach { it.close() }
        statements.clear()
    }

    private fun release(sql: String, statement: CipherStatement) {
        statement.clearBindings()
        val cached = perThread.get()
        if (closed || cached.containsKey(sql)) {
            discard(statement)
            return
        }
        cached[sql] = statement
        if (cached.size > MAX_PER_THREAD) {
            val eldest = cached.entries.iterator()
            discard(eldest.next().value)
            eldest.remove()
        }
    }

    private fun discard(statement: CipherStatement) {
        statements.remove(statement)
        statement.close()
    }

    inner class Lease(private val sql: String, val statement: CipherStatement) {
        private var released = false

        fun release() {
            if (!released) {
                released = true
                release(sql, statement)
            }
        }
    }

    private companion object {
        const val MAX_PER_THREAD = 64
        const val LOAD_FACTOR = 0.75f
    }
}
