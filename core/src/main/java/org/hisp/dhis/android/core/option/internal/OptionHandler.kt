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
package org.hisp.dhis.android.core.option.internal

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.db.puresqlite.OptionsSqliteDao
import org.hisp.dhis.android.core.arch.db.room.OptionsDao
import org.hisp.dhis.android.core.arch.db.room.OptionsRoom.Companion.toRoom
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.option.Option
import org.koin.core.annotation.Singleton

@Singleton
internal class OptionHandler constructor(
    optionStore: OptionStore,
    private val optionCleaner: OptionSubCollectionCleaner,
    private val optionsDao: OptionsDao,
    private val sqliteDao: OptionsSqliteDao,
) : IdentifiableHandlerImpl<Option>(optionStore) {

    var timeRoom = 0L
    var timedb = 0L
    var timeSql = 0L

    override fun afterCollectionHandled(oCollection: Collection<Option>?) {
        optionCleaner.deleteNotPresent(oCollection)
    }

    override fun handleMany(oCollection: Collection<Option>?) {
        val time = System.currentTimeMillis()
        super.handleMany(oCollection)
        timedb = timedb + System.currentTimeMillis() - time
        val time2 = System.currentTimeMillis()
        runBlocking {
//            oCollection?.let { optionsDao.insert(oCollection.map { it.toRoom() }) }
            oCollection?.forEach { optionsDao.insert(it.toRoom()) }
//            Log.d("ROOM_Options", "count: ${optionsDao.getAllOptions().size}")
        }
        timeRoom = timeRoom + System.currentTimeMillis() - time2

        val time3 = System.currentTimeMillis()
        sqliteDao.db.beginTransaction()
        try {
            oCollection?.forEach { sqliteDao.insertOption(it) }
            sqliteDao.db.setTransactionSuccessful()
        } finally {
            sqliteDao.db.endTransaction()
        }
        timeSql = timeSql + System.currentTimeMillis() - time3



        Log.d("OPTIONS_OLD", "time old: $timedb")
        Log.d("OPTIONS_ROOM", "time new: $timeRoom")
        Log.d("OPTIONS_SQL", "time sql: $timeSql")


    }
}
