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

import androidx.room3.useReaderConnection
import androidx.room3.useWriterConnection
import androidx.room3.withWriteTransaction
import androidx.sqlite.SQLITE_DATA_BLOB
import androidx.sqlite.SQLITE_DATA_FLOAT
import androidx.sqlite.SQLITE_DATA_INTEGER
import androidx.sqlite.SQLITE_DATA_NULL
import androidx.sqlite.SQLiteStatement
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.core.arch.db.stores.StoreRegistry
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.CoreObject
import org.koin.core.annotation.Singleton
import kotlin.reflect.KClass

/**
 * Room-based implementation of DatabaseAdapter.
 *
 * Reads go through [useReaderConnection] and writes through [useWriterConnection]. That split
 * matters: with a pooled driver such as `BundledSQLiteDriver`, Room opens one writer and several
 * reader connections, so routing a SELECT through the writer serialises it behind every pending
 * write for no reason.
 */
@Singleton
@Suppress("TooManyFunctions")
internal class RoomDatabaseAdapter(
    private val storeRegistry: StoreRegistry,
) : DatabaseAdapter {
    private var database: AppDatabase? = null
    private var databaseName: String = ""

    override val isReady: Boolean
        get() = database != null

    override fun activate(database: AppDatabase, databaseName: String) {
        if (this.database != null) {
            deactivate()
        }
        this.database = database
        this.databaseName = databaseName
    }

    override fun close() {
        database?.close()
        database = null
        databaseName = ""
    }

    override fun deactivate() {
        database = null
        databaseName = ""
    }

    override suspend fun <T> withTransaction(block: suspend () -> T): T {
        checkReady()
        return database!!.withWriteTransaction { block() }
    }

    override suspend fun execSQL(sql: String) {
        checkReady()
        // Not wrapped in a transaction: several callers pass PRAGMA statements, which SQLite
        // silently ignores inside one.
        database!!.useWriterConnection { transactor ->
            transactor.usePrepared(sql) { it.step() }
        }
    }

    override suspend fun delete(tableName: String, whereClause: String): Int {
        return delete(tableName, whereClause, null)
    }

    override suspend fun delete(tableName: String, whereClause: String?, whereArgs: Array<Any>?): Int {
        checkReady()
        require(tableName.matches(TABLE_NAME_REGEX)) { "Invalid table name: $tableName" }

        val deleteSql = buildString {
            append("DELETE FROM `")
            append(tableName)
            append("`")
            if (!whereClause.isNullOrBlank()) {
                append(" WHERE ")
                append(whereClause)
            }
        }

        var rowsAffected = 0
        database!!.withWriteTransaction {
            usePrepared(deleteSql) { statement ->
                whereArgs?.forEachIndexed { index, arg -> bindArgument(statement, index + 1, arg) }
                statement.step()
            }
            usePrepared("SELECT changes()") { changesStatement ->
                if (changesStatement.step()) {
                    rowsAffected = changesStatement.getLong(0).toInt()
                }
            }
        }
        return rowsAffected
    }

    override suspend fun delete(tableName: String): Int {
        checkReady()
        return delete(tableName, null, null)
    }

    override suspend fun rawQuery(sqlQuery: String, queryArgs: Array<Any>?): List<Map<String, String?>> {
        return readRows(sqlQuery, queryArgs, ::readColumnAsString)
    }

    override suspend fun rawQueryWithTypedValues(
        sqlQuery: String,
        queryArgs: Array<Any>?,
    ): List<Map<String, Any?>> {
        return readRows(sqlQuery, queryArgs) { statement, index ->
            when (statement.getColumnType(index)) {
                SQLITE_DATA_NULL -> null
                SQLITE_DATA_INTEGER -> statement.getLong(index)
                SQLITE_DATA_FLOAT -> statement.getDouble(index)
                SQLITE_DATA_BLOB -> statement.getBlob(index)
                else -> statement.getText(index)
            }
        }
    }

    /**
     * Runs [sqlQuery] on a reader connection and materialises every row, reading each column with
     * [readColumn].
     */
    private suspend fun <V> readRows(
        sqlQuery: String,
        queryArgs: Array<Any>?,
        readColumn: (SQLiteStatement, Int) -> V,
    ): List<Map<String, V>> {
        checkReady()
        val results = mutableListOf<Map<String, V>>()

        database!!.useReaderConnection { transactor ->
            transactor.usePrepared(sqlQuery) { statement ->
                queryArgs?.forEachIndexed { index, arg -> bindArgument(statement, index + 1, arg) }

                var columnNames: List<String>? = null
                while (statement.step()) {
                    val names = columnNames
                        ?: List(statement.getColumnCount()) { statement.getColumnName(it) }
                            .also { columnNames = it }

                    val row = LinkedHashMap<String, V>(names.size)
                    names.forEachIndexed { index, name -> row[name] = readColumn(statement, index) }
                    results.add(row)
                }
            }
        }
        return results
    }

    private fun bindArgument(statement: SQLiteStatement, index: Int, arg: Any?) {
        when (arg) {
            is String -> statement.bindText(index, arg)
            is Long -> statement.bindLong(index, arg)
            is Double -> statement.bindDouble(index, arg)
            is ByteArray -> statement.bindBlob(index, arg)
            is Int -> statement.bindLong(index, arg.toLong())
            is Boolean -> statement.bindLong(index, if (arg) 1L else 0L)
            is Float -> statement.bindDouble(index, arg.toDouble())
            null -> statement.bindNull(index)
            else -> statement.bindText(index, arg.toString())
        }
    }

    override suspend fun setForeignKeyConstraintsEnabled(enabled: Boolean) {
        checkReady()
        // `PRAGMA foreign_keys` is a no-op inside a transaction, so this must not be wrapped in one.
        val sql = if (enabled) "PRAGMA foreign_keys = ON;" else "PRAGMA foreign_keys = OFF;"
        database!!.useWriterConnection { transactor ->
            transactor.usePrepared(sql) { it.step() }
        }
    }

    override fun getDatabaseName(): String {
        return databaseName
    }

    override fun getCurrentDatabase(): AppDatabase {
        val db = database
        checkNotNull(db) { "No database is currently activated." }
        return db
    }

    /**
     * Upserts a data object or a tracker import conflict into the database.
     * This method is not recomended to be used directly, instead use the repositories in d2
     */
    override suspend fun <O : CoreObject> upsertObject(o: O, kclass: KClass<O>): HandleAction? {
        val store = storeRegistry.getStoreFor(kclass)
        return store?.updateOrInsert(o)
    }

    private fun checkReady() {
        check(isReady) { "Database adapter not activated" }
    }

    override suspend fun getVersion(): Int {
        checkReady()
        var version = 0
        database!!.useReaderConnection { transactor ->
            transactor.usePrepared("PRAGMA user_version;") { statement ->
                if (statement.step()) {
                    version = statement.getLong(0).toInt()
                }
            }
        }
        return version
    }

    override suspend fun checkpointWAL() {
        checkReady()
        database!!.useWriterConnection { transactor ->
            transactor.usePrepared("PRAGMA wal_checkpoint(PASSIVE);") { statement ->
                statement.step()
            }
        }
    }

    private companion object {
        val TABLE_NAME_REGEX = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")
    }
}
