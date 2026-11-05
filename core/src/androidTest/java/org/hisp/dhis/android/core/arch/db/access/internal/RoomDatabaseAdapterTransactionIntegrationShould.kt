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

package org.hisp.dhis.android.core.arch.db.access.internal

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.KoinStoreRegistry
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTest
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.hisp.dhis.android.persistence.constant.toDB
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseAdapter
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseManager
import org.hisp.dhis.android.persistence.db.access.SqliteDriverFactoryImpl
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

internal abstract class RoomDatabaseAdapterTransactionIntegrationShould : BaseMockIntegrationTest() {
    private val context = InstrumentationRegistry.getInstrumentation().context
    private lateinit var databaseAdapter: DatabaseAdapter

    protected abstract val databaseName: String

    /** Rows left after a nested transaction fails and the outer one catches the error and commits. */
    protected abstract val rowsAfterFailedNestedTransaction: List<String>

    protected abstract fun open(databaseManager: RoomDatabaseManager)

    @Before
    fun setUp() {
        context.deleteDatabase(databaseName)
        databaseAdapter = RoomDatabaseAdapter(KoinStoreRegistry())
        val passwordManager = DatabaseEncryptionPasswordManager.create(InMemorySecureStore())
        open(RoomDatabaseManager(databaseAdapter, context, passwordManager, SqliteDriverFactoryImpl()))
    }

    // Set when a transaction never finished: closing would then wait for it forever.
    private var transactionStuck = false

    @After
    fun tearDown() {
        if (!transactionStuck) {
            databaseAdapter.close()
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun roll_back_the_nested_transaction_that_fails() = runBlocking<Unit> {
        databaseAdapter.withTransaction {
            insertConstant("outer")
            try {
                databaseAdapter.withTransaction {
                    insertConstant("inner")
                    error("nested failure")
                }
            } catch (_: IllegalStateException) {
            }
        }

        assertThat(constantUids()).containsExactlyElementsIn(rowsAfterFailedNestedTransaction)
    }

    @Test
    fun see_uncommitted_rows_in_nested_writes() = runBlocking<Unit> {
        val deleted = databaseAdapter.withTransaction {
            insertConstant("a")
            insertConstant("b")
            databaseAdapter.execSQL("UPDATE Constant SET value = 2 WHERE uid = 'a'")
            databaseAdapter.delete("Constant", "value = ?", arrayOf<Any>(2))
        }

        assertThat(deleted).isEqualTo(1)
        assertThat(constantUids()).containsExactly("b")
    }

    /**
     * Before the fix every nested write scheduled an invalidation refresh. With SQLCipher each one
     * blocked a `Dispatchers.IO` thread until the outer transaction committed, so enough of them
     * starved the dispatcher and the dispatcher hop below never ran.
     *
     * The transaction runs on its own thread and the test waits for it with a timeout: a starved
     * dispatcher cannot run the cancellation that `withTimeout` needs, so a regression would hang
     * the test run instead of failing it.
     */
    @Test
    fun not_starve_the_io_dispatcher_with_many_nested_writes() = runBlocking<Unit> {
        val finished = CountDownLatch(1)
        var failure: Throwable? = null
        thread {
            try {
                runBlocking {
                    databaseAdapter.withTransaction {
                        repeat(NESTED_WRITES) { i ->
                            insertConstant("uid$i")
                            databaseAdapter.delete("Constant", "uid = ?", arrayOf<Any>("uid$i"))
                            databaseAdapter.withTransaction { insertConstant("kept$i") }
                            withContext(Dispatchers.IO) { }
                        }
                    }
                }
            } catch (t: Throwable) {
                failure = t
            } finally {
                finished.countDown()
            }
        }

        transactionStuck = !finished.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        assertWithMessage("transaction finished within ${TIMEOUT_MS / 1000} s")
            .that(transactionStuck).isFalse()
        failure?.let { throw it }
        assertThat(constantUids()).hasSize(NESTED_WRITES)
    }

    private suspend fun insertConstant(uid: String) {
        val constant = Constant.builder().uid(uid).value(1.0).build()
        databaseAdapter.getCurrentDatabase().constantDao().insert(constant.toDB())
    }

    private suspend fun constantUids(): List<String?> {
        return databaseAdapter.rawQuery("SELECT uid FROM Constant ORDER BY uid", null).map { it["uid"] }
    }

    private companion object {
        const val NESTED_WRITES = 200
        const val TIMEOUT_MS = 60_000L
    }
}

@RunWith(D2JunitRunner::class)
internal class RoomDatabaseAdapterTransactionEncryptedIntegrationShould : RoomDatabaseAdapterTransactionIntegrationShould() {
    override val databaseName = "adapter_transaction_encrypted.db"

    // SQLCipher runs Room's nested BEGIN with Android framework semantics: a failed nested
    // transaction rolls back the whole outer one, even when the outer block catches the error.
    override val rowsAfterFailedNestedTransaction = emptyList<String>()

    override fun open(databaseManager: RoomDatabaseManager) {
        databaseManager.createOrOpenEncryptedDatabase(databaseName, "test")
    }
}

@RunWith(D2JunitRunner::class)
internal class RoomDatabaseAdapterTransactionUnencryptedIntegrationShould : RoomDatabaseAdapterTransactionIntegrationShould() {
    override val databaseName = "adapter_transaction_unencrypted.db"

    // Room's own pool implements nested transactions as SAVEPOINTs: only the failed block is undone.
    override val rowsAfterFailedNestedTransaction = listOf("outer")

    override fun open(databaseManager: RoomDatabaseManager) {
        databaseManager.createOrOpenUnencryptedDatabase(databaseName)
    }
}
