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
package org.hisp.dhis.android.core.data.database

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.common.CoreObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

abstract class ObjectStoreAbstractIntegrationShould<M : CoreObject> internal constructor(
    private val store: ObjectStore<M>,
    tableInfo: TableInfo,
    databaseAdapter: DatabaseAdapter,
) {
    val `object`: M
    private val tableInfo: TableInfo
    private val databaseAdapter: DatabaseAdapter

    protected abstract fun buildObject(): M

    @Before
    @Throws(IOException::class)
    open fun setUp() {
        runBlocking { store.delete() }
    }

    @After
    open fun tearDown() {
        runBlocking {
            store.delete()
            databaseAdapter.close()
        }
    }

    @Test
    fun insert_and_select_first_object() = runTest {
        store.insert(`object`)
        val objectFromDb = store.selectFirst()
        assertEqualsIgnoreId(objectFromDb)
    }

    @Test
    fun insert_object_and_select_first_object() = runTest {
        store.insert(`object`)
        val objectFromDb = store.selectFirst()
        assertEqualsIgnoreId(objectFromDb)
    }

    @Test
    fun insert_and_select_all_objects() = runTest {
        store.insert(`object`)
        val objectsFromDb = store.selectAll()
        assertEqualsIgnoreId(objectsFromDb.iterator().next())
    }

    fun assertEqualsIgnoreId(localObject: M?) {
        assertEqualsIgnoreId(localObject, `object`)
    }

    fun assertEqualsIgnoreId(m1: M?, m2: M) {
        assertThat(m1).isEqualTo(m2)
    }

    init {
        `object` = buildObject()
        this.tableInfo = tableInfo
        this.databaseAdapter = databaseAdapter
    }
}
