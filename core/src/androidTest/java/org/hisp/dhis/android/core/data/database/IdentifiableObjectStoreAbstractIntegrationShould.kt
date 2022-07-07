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
package org.hisp.dhis.android.core.data.database

import com.google.common.truth.Truth.assertThat
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.junit.Before
import org.junit.Test

abstract class IdentifiableObjectStoreAbstractIntegrationShould<M> internal constructor(
    internal var store: IdentifiableObjectStore<M>,
    tableInfo: TableInfo,
    databaseAdapter: DatabaseAdapter?
) : ObjectStoreAbstractIntegrationShould<M>(
    store, tableInfo, databaseAdapter!!
) where M : ObjectWithUidInterface, M : CoreObject {
    private val objectToUpdate: M
    protected abstract fun buildObjectToUpdate(): M

    @Before
    @Throws(IOException::class)
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun insert_and_select_by_uid() {
        store.insert(`object`)
        val objectFromDb = store.selectByUid(`object`.uid())
        assertEqualsIgnoreId(objectFromDb)
    }

    @Test
    fun insert_and_select_by_uid_list() {
        store.insert(`object`)
        val listFromDb = store.selectByUids(listOf(`object`.uid()))
        assertThat(listFromDb.size).isEqualTo(1)
        assertEqualsIgnoreId(listFromDb[0])
    }

    @Test
    fun select_inserted_object_uid() {
        store.insert(`object`)
        val objectUidFromDb = store.selectUids().iterator().next()
        assertThat(objectUidFromDb).isEqualTo(`object`.uid())
    }

    @Test
    fun delete_inserted_object_by_uid() {
        store.insert(`object`)
        store.delete(`object`.uid())
        assertThat(store.selectFirst()).isEqualTo(null)
    }

    @Test(expected = RuntimeException::class)
    fun throw_exception_if_try_to_delete_an_object_which_does_not_exists() {
        store.delete(`object`.uid())
    }

    @Test
    fun not_throw_exception_if_try_to_delete_an_object_which_does_not_exists() {
        store.deleteIfExists(`object`.uid())
    }

    @Test
    fun delete_if_exists_inserted_object_by_uid() {
        store.insert(`object`)
        store.deleteIfExists(`object`.uid())
        assertThat(store.selectFirst()).isEqualTo(null)
    }

    @Test
    fun update_inserted_object() {
        store.insert(`object`)
        store.update(objectToUpdate)
        val updatedObjectFromDb = store.selectFirst()
        assertEqualsIgnoreId(updatedObjectFromDb, objectToUpdate)
    }

    @Test
    fun insert_object_if_object_does_not_exists() {
        val handleAction = store.updateOrInsert(objectToUpdate)
        assertThat(handleAction).isEqualTo(HandleAction.Insert)
    }

    @Test
    fun update_inserted_object_if_object_exists() {
        store.insert(`object`)
        val handleAction = store.updateOrInsert(objectToUpdate)
        assertThat(handleAction).isEqualTo(HandleAction.Update)
    }

    @Test
    fun insert_same_object_simultaneously_and_transactionally() {
        val s1 = Single.fromCallable { store.updateOrInsert(`object`) }.subscribeOn(Schedulers.io())
        val s2 = Single.fromCallable { store.updateOrInsert(`object`) }.subscribeOn(Schedulers.io())

        s1.mergeWith(s2).blockingSubscribe()

        val objects = store.selectAll()

        assertThat(objects.size).isEqualTo(1)
    }

    @Test
    fun select_inserted_object_uids_where() {
        // TODO Implement test for store.selectUidsWhere() method
    }

    init {
        objectToUpdate = buildObjectToUpdate()
    }
}
