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

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.stores.StoreRegistry
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseAdapter
import org.hisp.dhis.android.persistence.db.access.RoomTransaction
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class RoomDatabaseAdapterShould {

    @Mock
    private lateinit var database: AppDatabase
    private lateinit var roomDatabaseAdapter: RoomDatabaseAdapter
    private val storeRegistry: StoreRegistry = mock()
    private val objectStore: ObjectStore<DataValue> = mock()

    @Before
    fun setUp() {
        roomDatabaseAdapter = RoomDatabaseAdapter(storeRegistry)
    }

    @Test
    fun `isReady should return false when database is null`() {
        Assert.assertFalse(roomDatabaseAdapter.isReady)
    }

    @Test
    fun `isReady should return true when database is not null`() {
        roomDatabaseAdapter.activate(database, "testDb")
        Assert.assertTrue(roomDatabaseAdapter.isReady)
    }

    @Test
    fun `activate should set database and databaseName`() {
        val dbName = "testDatabase"
        roomDatabaseAdapter.activate(database, dbName)
        Assert.assertTrue(roomDatabaseAdapter.isReady)
        Assert.assertEquals(dbName, roomDatabaseAdapter.getDatabaseName())
        Assert.assertEquals(database, roomDatabaseAdapter.getCurrentDatabase())
    }

    @Test
    fun `close should close database and set it to null`() {
        roomDatabaseAdapter.activate(database, "testDb")
        Assert.assertTrue(roomDatabaseAdapter.isReady)

        doNothing().`when`(database).close()

        roomDatabaseAdapter.close()

        verify(database, times(1)).close()
        Assert.assertFalse(roomDatabaseAdapter.isReady)
    }

    @Test
    fun `deactivate should close database and reset fields`() {
        roomDatabaseAdapter.activate(database, "testDb")
        Assert.assertTrue(roomDatabaseAdapter.isReady)

        doNothing().`when`(database).close()

        roomDatabaseAdapter.deactivate()

        verify(database, times(1)).close()
        Assert.assertFalse(roomDatabaseAdapter.isReady)
        Assert.assertEquals("", roomDatabaseAdapter.getDatabaseName())
    }

    @Test
    fun `beginNewTransaction should begin transaction on database and return RoomTransaction`() {
        roomDatabaseAdapter.activate(database, "testDb")
        doNothing().`when`(database).beginTransaction()

        val transaction = roomDatabaseAdapter.beginNewTransaction()

        verify(database, times(1)).beginTransaction()
        Assert.assertNotNull(transaction)
        Assert.assertTrue(transaction is RoomTransaction)
    }

    @Test(expected = IllegalStateException::class)
    fun `beginNewTransaction should throw IllegalStateException if not ready`() {
        roomDatabaseAdapter.beginNewTransaction()
    }

    @Test
    fun `setTransactionSuccessful should call setTransactionSuccessful on database`() {
        roomDatabaseAdapter.activate(database, "testDb")
        doNothing().`when`(database).setTransactionSuccessful()

        roomDatabaseAdapter.setTransactionSuccessful()

        verify(database, times(1)).setTransactionSuccessful()
    }

    @Test(expected = IllegalStateException::class)
    fun `setTransactionSuccessful should throw IllegalStateException if not ready`() {
        roomDatabaseAdapter.setTransactionSuccessful()
    }

    @Test
    fun `runInTransaction should call runInTransaction on database`() {
        roomDatabaseAdapter.activate(database, "testDb")
        val runnable = mock<Runnable>()
        doNothing().`when`(database).runInTransaction(runnable)

        roomDatabaseAdapter.runInTransaction(runnable)

        verify(database, times(1)).runInTransaction(runnable)
    }

    @Test(expected = IllegalStateException::class)
    fun `runInTransaction should throw IllegalStateException if not ready`() {
        val runnable = mock<Runnable>()
        roomDatabaseAdapter.runInTransaction(runnable)
    }

    @Test
    fun `endTransaction should call endTransaction on database`() {
        roomDatabaseAdapter.activate(database, "testDb")
        doNothing().`when`(database).endTransaction()

        roomDatabaseAdapter.endTransaction()

        verify(database, times(1)).endTransaction()
    }

    @Test(expected = IllegalStateException::class)
    fun `endTransaction should throw IllegalStateException if not ready`() {
        roomDatabaseAdapter.endTransaction()
    }

    @Test
    fun `getDatabaseName should return correct name`() {
        val dbName = "myTestDb"
        roomDatabaseAdapter.activate(database, dbName)
        Assert.assertEquals(dbName, roomDatabaseAdapter.getDatabaseName())
    }

    @Test
    fun `getCurrentDatabase should return current database`() {
        roomDatabaseAdapter.activate(database, "testDb")
        Assert.assertEquals(database, roomDatabaseAdapter.getCurrentDatabase())
    }

    @Test(expected = IllegalStateException::class)
    fun `getCurrentDatabase should throw IllegalStateException if not ready`() {
        roomDatabaseAdapter.getCurrentDatabase()
    }

    @Test(expected = IllegalStateException::class)
    fun `getVersion should throw IllegalStateException if not ready`() {
        roomDatabaseAdapter.getVersion()
    }

    @Test
    fun `upsertObject calls updateOrInsert on correct store`() = runTest {
        val dataValue = DataValue.builder().build()
        val expectedAction = HandleAction.Update

        // Stub
        whenever(storeRegistry.getStoreFor(DataValue::class)).thenReturn(objectStore)
        whenever(objectStore.updateOrInsert(dataValue)).thenReturn(expectedAction)

        // Act
        val result = roomDatabaseAdapter.upsertObject(dataValue, DataValue::class)

        // Assert
        verify(objectStore, times(1)).updateOrInsert(dataValue)
        Assert.assertEquals(expectedAction, result)
    }
}
