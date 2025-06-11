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
package org.hisp.dhis.android.core.common

import android.database.Cursor
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseIntegrationTestWithDatabase
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.data.database.CursorAssert
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.option.OptionSetTableInfo
import org.hisp.dhis.android.core.option.internal.OptionSetStoreImpl
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(D2JunitRunner::class)
class IdentifiableObjectStoreIntegrationShould : BaseIntegrationTestWithDatabase() {
    private lateinit var store: IdentifiableObjectStore<OptionSet>
    private lateinit var optionSet: OptionSet
    private lateinit var updatedOptionSet: OptionSet

    @Before
    @Throws(IOException::class)
    override fun setUp() {
        super.setUp()
        this.optionSet = StoreMocks.generateOptionSet()
        this.updatedOptionSet = StoreMocks.generateUpdatedOptionSet()
        this.store = OptionSetStoreImpl(databaseAdapter())
    }

    private val cursor: Cursor
        get() = databaseAdapter().query(
            OptionSetTableInfo.TABLE_INFO.name(),
            *OptionSetTableInfo.TABLE_INFO.columns().all(),
        )

    @Test
    fun insert_option_set() = runTest {
        store.insert(optionSet)
        val cursor = cursor
        StoreMocks.optionSetCursorAssert(cursor, optionSet)
    }

    @Test(expected = RuntimeException::class)
    fun throw_exception_for_second_identical_insertion() = runTest {
        store.insert(optionSet)
        store.insert(optionSet)
    }

    @Test(expected = IllegalStateException::class)
    fun throw_exception_for_option_set_without_uid_inserting() = runTest {
        val withoutUid = OptionSet.builder().code("code").build()
        store.insert(withoutUid)
    }

    @Test
    fun delete_existing_option_set() = runTest {
        store.insert(optionSet)
        store.delete(optionSet.uid())
        CursorAssert.assertThatCursor(cursor).isExhausted()
    }

    @Test(expected = RuntimeException::class)
    fun throw_exception_deleting_non_existing_option_set() = runTest {
        store.delete("new-id")
    }

    @Test
    fun do_not_throw_exception_safe_deleting_non_existing_option_set() = runTest {
        store.deleteIfExists("new-id")
        CursorAssert.assertThatCursor(cursor).isExhausted()
    }

    @Test
    fun update_option_set() = runTest {
        store.insert(optionSet)
        store.update(updatedOptionSet)
        val cursor = cursor
        StoreMocks.optionSetCursorAssert(cursor, updatedOptionSet)
    }

    @Test(expected = RuntimeException::class)
    fun throw_exception_updating_with_null_uid() = runTest {
        store.update(StoreMocks.generateOptionSetWithoutUid())
    }

    @Test(expected = RuntimeException::class)
    fun throw_exception_updating_non_existing_option_set() = runTest {
        store.update(optionSet)
    }

    @Test
    fun insert_when_no_option_set_and_update_or_insert() = runTest {
        store.updateOrInsert(optionSet)
        val cursor = cursor
        StoreMocks.optionSetCursorAssert(cursor, optionSet)
    }

    @Test
    fun update_when_option_set_and_update_or_insert() = runTest {
        store.insert(optionSet)
        store.updateOrInsert(updatedOptionSet)
        val cursor = cursor
        StoreMocks.optionSetCursorAssert(cursor, updatedOptionSet)
    }
}
