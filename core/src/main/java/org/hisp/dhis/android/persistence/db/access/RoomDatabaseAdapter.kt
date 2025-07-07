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

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.Transaction
import org.koin.core.annotation.Singleton

/**
 * Room-based implementation of DatabaseAdapter.
 */
@Singleton
class RoomDatabaseAdapter : DatabaseAdapter {
    private var database: RoomDatabase? = null
    private var sqliteDatabase: SupportSQLiteDatabase? = null
    private var databaseName: String = ""

    override val isReady: Boolean
        get() = database != null && sqliteDatabase != null

    override fun activate(database: RoomDatabase) {
        this.database = database
        this.sqliteDatabase = database.openHelper.writableDatabase
        this.databaseName = database.openHelper.databaseName ?: ""
    }

    override fun deactivate() {
        this.database = null
        this.sqliteDatabase = null
        this.databaseName = ""
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

    override fun execSQL(sql: String) {
        checkReady()
        sqliteDatabase!!.execSQL(sql)
    }

    override fun delete(tableName: String, whereClause: String, whereArgs: Array<Any>): Int {
        checkReady()
        return sqliteDatabase!!.delete(tableName, whereClause, whereArgs.map { it.toString() }.toTypedArray())
    }

    override fun delete(tableName: String): Int {
        checkReady()
        return sqliteDatabase!!.delete(tableName, null, null)
    }


    override fun setForeignKeyConstraintsEnabled(enable: Boolean) {
        checkReady()
        sqliteDatabase!!.setForeignKeyConstraintsEnabled(enable)
    }

    override fun enableWriteAheadLogging() {
        checkReady()
        sqliteDatabase!!.enableWriteAheadLogging()
    }

    override fun close() {
        if (database != null) {
            database!!.close()
            database = null
            sqliteDatabase = null
        }
    }

    override fun getDatabaseName(): String {
        return databaseName
    }

    private fun checkReady() {
        if (!isReady) {
            throw IllegalStateException("Database adapter not activated")
        }
    }

    override fun getVersion(): Int {
        return sqliteDatabase?.version ?: 1
    }
}
