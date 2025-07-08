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

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseIntegrationTestWithDatabase
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.hisp.dhis.android.persistence.option.OptionSetStoreImpl
import org.hisp.dhis.android.persistence.option.OptionSetTableInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(D2JunitRunner::class)
class ObjectStoreIntegrationShould : BaseIntegrationTestWithDatabase() {
    private lateinit var store: IdentifiableObjectStore<OptionSet>

    private lateinit var optionSet: OptionSet

    @Before
    @Throws(IOException::class)
    override fun setUp() {
        super.setUp()
        this.optionSet = StoreMocks.generateOptionSet()
        this.store = OptionSetStoreImpl(databaseAdapter())
    }

    @Test
    fun insert_option_set() = runTest {
        store.insert(optionSet)
        val cursor = databaseAdapter().query(
            OptionSetTableInfo.TABLE_INFO.name(),
            *OptionSetTableInfo.TABLE_INFO.columns().all(),
        )
        StoreMocks.optionSetCursorAssert(cursor, optionSet)
    }

    @Test(expected = RuntimeException::class)
    fun throw_exception_for_second_identical_insertion() = runTest {
        store.insert(optionSet)
        store.insert(optionSet)
    }
}
