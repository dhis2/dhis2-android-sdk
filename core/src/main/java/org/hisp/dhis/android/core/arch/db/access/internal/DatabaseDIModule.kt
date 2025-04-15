/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.puresqlite.OptionsSqliteDao
import org.hisp.dhis.android.core.arch.db.room.AppDatabase
import org.hisp.dhis.android.core.arch.db.room.OptionSetsDao
import org.hisp.dhis.android.core.arch.db.room.OptionsDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
internal class DatabaseDIModule {

    @Singleton
    fun provideSQLiteOpenHelper(context: Context): SupportSQLiteOpenHelper {
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name("my_sqlite_db")
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
        CREATE TABLE IF NOT EXISTS option_sets (
            uid TEXT PRIMARY KEY,
            code TEXT,
            name TEXT,
            displayName TEXT,
            created TEXT,
            lastUpdated TEXT,
            deleted INTEGER,
            version INTEGER,
            valueType TEXT
        )
    """.trimIndent()
                    )

                    db.execSQL(
                        """
        CREATE TABLE IF NOT EXISTS options (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            uid TEXT,
            code TEXT,
            name TEXT,
            displayName TEXT,
            created TEXT,
            lastUpdated TEXT,
            deleted INTEGER,
            sortOrder INTEGER,
            optionSet TEXT NOT NULL,
            color TEXT,
            icon TEXT,
            FOREIGN KEY(optionSet) REFERENCES option_sets(uid) ON DELETE CASCADE
        )
    """.trimIndent()
                    )
                }

                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int
                ) {
                    // No-op
                }
            })
            .build()

        return FrameworkSQLiteOpenHelperFactory().create(config)
    }

    @Singleton
    fun provideSQLiteDatabase(helper: SupportSQLiteOpenHelper): SupportSQLiteDatabase {
        return helper.writableDatabase
    }

    @Singleton
    fun optionsSqliteDao(db: SupportSQLiteDatabase): OptionsSqliteDao {
        return OptionsSqliteDao(db)
    }

    @Singleton
    fun roomDatabase(app: Context): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "my_room_db"
        ).build()
    }


    @Singleton
    fun optionsDao(appDatabase: AppDatabase): OptionsDao {
        return appDatabase.optionsDao()
    }

    @Singleton
    fun optionSetsDao(appDatabase: AppDatabase): OptionSetsDao {
        return appDatabase.optionSetsDao()
    }

    @Singleton
    fun databaseAdapter(adapterFactory: DatabaseAdapterFactory): DatabaseAdapter {
        return adapterFactory.newParentDatabaseAdapter()
    }

}
