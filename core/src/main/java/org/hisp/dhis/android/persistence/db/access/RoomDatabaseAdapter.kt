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

import androidx.room.Transactor
import androidx.room.execSQL
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteStatement
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.Transaction
import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.koin.core.annotation.Singleton

/**
 * Room-based implementation of DatabaseAdapter.
 */
@Singleton
@Suppress("TooManyFunctions")
internal class RoomDatabaseAdapter : DatabaseAdapter {
    private var database: AppDatabase? = null
    private var databaseName: String = ""

    override val isReady: Boolean
        get() = database != null

    override fun activate(database: AppDatabase, databaseName: String) {
        this.database = database
        this.databaseName = databaseName
    }

    override fun close() {
        if (database != null) {
            database!!.close()
            database = null
            database = null
        }
    }

    override fun deactivate() {
        database?.close()
        database = null
        databaseName = ""
    }

    override fun beginNewTransaction(): Transaction {
        checkReady()
        database!!.beginTransaction()
        return RoomTransaction(database!!)
    }

    override fun setTransactionSuccessful() {
        checkReady()
        database!!.setTransactionSuccessful()
    }

    override fun runInTransaction(block: Runnable) {
        checkReady()
        database!!.runInTransaction(block)
    }

    override fun endTransaction() {
        checkReady()
        database!!.endTransaction()
    }

    override suspend fun execSQL(sql: String) { // Renamed from override for clarity if it's a new method
        checkReady()

        database!!.useWriterConnection { transactor ->
            transactor.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                this.execSQL(sql)
            }
        }
    }

    override suspend fun delete(tableName: String, whereClause: String): Int {
        return delete(tableName, whereClause, null)
    }

    @Suppress("ComplexMethod")
    override suspend fun delete(tableName: String, whereClause: String?, whereArgs: Array<Any>?): Int {
        checkReady()
        if (!tableName.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
            throw IllegalArgumentException("Invalid table name: $tableName")
        }

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

        database!!.useWriterConnection { transactor: Transactor ->
            transactor.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                this.usePrepared(deleteSql) { statement: SQLiteStatement ->
                    if (whereArgs != null) {
                        whereArgs.forEachIndexed { index, arg ->
                            val argIndex = index + 1
                            when (arg) {
                                is String -> statement.bindText(argIndex, arg)
                                is Long -> statement.bindLong(argIndex, arg)
                                is Double -> statement.bindDouble(argIndex, arg)
                                is ByteArray -> statement.bindBlob(argIndex, arg)
                                is Int -> statement.bindLong(argIndex, arg.toLong())
                                is Boolean -> statement.bindLong(argIndex, if (arg) 1L else 0L)
                                is Float -> statement.bindDouble(argIndex, arg.toDouble())
                                null -> statement.bindNull(argIndex)
                                else -> statement.bindText(argIndex, arg.toString())
                            }
                        }
                    }
                    statement.step()
                }

                this.usePrepared("SELECT changes()") { changesStatement: SQLiteStatement ->
                    if (changesStatement.step()) {
                        rowsAffected = changesStatement.getLong(0).toInt()
                    }
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
        checkReady()
        val results = mutableListOf<Map<String, String?>>()
        database!!.useWriterConnection { transactor ->
            // For SELECT queries, a transaction might not be strictly needed,
            // but your existing `useWriterConnection` wraps in `withTransaction`.
            // If you have a `useReaderConnection` or a way to execute without an explicit
            // transaction for reads, that might be slightly more optimal.
            // However, this will work.
            transactor.withTransaction(
                Transactor.SQLiteTransactionType.IMMEDIATE,
            ) { // Or IMMEDIATE, DEFERRED is often better for reads
                this.usePrepared(sqlQuery) { statement: SQLiteStatement ->
                    // Bind arguments if any
                    queryArgs?.forEachIndexed { index, arg ->
                        val argIndex = index + 1 // SQLiteStatement binding is 1-based
                        bindArgument(statement, argIndex, arg) // Use a helper for binding
                    }

                    var columnNamesCache: List<String>? = null

                    while (statement.step()) {
                        if (columnNamesCache == null) {
                            columnNamesCache = List(statement.getColumnCount()) { i ->
                                statement.getColumnName(i)
                            }
                        }

                        val rowMap = mutableMapOf<String, String?>()
                        columnNamesCache.forEachIndexed { idx, name ->
                            // For SQLiteStatement from androidx.sqlite.driver:
                            // getText(columnIndex) will return null if the column's value is SQL NULL.
                            // No explicit isNull() check is needed before calling getText() for this purpose.
                            rowMap[name] = statement.getText(idx)
                        }
                        results.add(rowMap)
                    }
                }
            }
        }
        return results
    }

    override suspend fun rawQueryWithTypedValues(
        sqlQuery: String,
        queryArgs: Array<Any>?,
    ): List<Map<String, Any?>> {
        checkReady()
        val results = mutableListOf<Map<String, Any?>>()

        database!!.useWriterConnection { transactor -> // Or useReaderConnection if appropriate for PRAGMAs
            transactor.withTransaction(Transactor.SQLiteTransactionType.DEFERRED) {
                this.usePrepared(sqlQuery) { statement: SQLiteStatement ->
                    queryArgs?.forEachIndexed { index, arg ->
                        val argIndex = index + 1
                        bindArgument(statement, argIndex, arg)
                    }

                    var columnNamesCache: List<String>? = null

                    while (statement.step()) {
                        if (columnNamesCache == null) {
                            columnNamesCache = List(statement.getColumnCount()) { i ->
                                statement.getColumnName(i)
                            }
                        }

                        val rowMap = mutableMapOf<String, Any?>()
                        columnNamesCache.forEachIndexed { idx, name ->
                            val value = statement.getText(idx) // Read as text, which can be null
                            rowMap[name] = value
                        }
                        results.add(rowMap)
                    }
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
            else -> statement.bindText(index, arg.toString()) // Default to String
        }
    }

    override suspend fun setForeignKeyConstraintsEnabled(enabled: Boolean) {
        checkReady()
        val sql = if (enabled) "PRAGMA foreign_keys = ON;" else "PRAGMA foreign_keys = OFF;"

        // This PRAGMA affects the current connection session.
        // For tests, where connections might be short-lived or reused within a test method,
        // this is usually effective.
        database!!.useWriterConnection { transactor: Transactor ->
            // PRAGMAs that don't return results can often be run outside an explicit
            // transactor.transaction {} block if they are simple configuration settings
            // for the connection. However, running within a transaction is also safe.
            transactor.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                this.execSQL(sql)
            }
        }
    }

    override fun getDatabaseName(): String {
        return databaseName
    }

    override fun getCurrentDatabase(): AppDatabase {
        val db = database
        if (db == null) {
            throw IllegalStateException("No database is currently activated.")
        }
//        if (!db.isOpen) {
//            throw IllegalStateException("The activated database is closed.")
//        }
        return db
    }

    private fun checkReady() {
        if (!isReady) {
            throw IllegalStateException("Database adapter not activated")
        }
    }

    override fun getVersion(): Int {
        checkReady()
        return database?.openHelper?.readableDatabase?.version!!
    }
}
