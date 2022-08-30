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
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.CoreObject
import org.junit.Before
import org.junit.Test

abstract class ObjectWithoutUidStoreAbstractIntegrationShould<M : CoreObject> internal constructor(
    internal val store: ObjectWithoutUidStore<M>,
    tableInfo: TableInfo,
    databaseAdapter: DatabaseAdapter
) : ObjectStoreAbstractIntegrationShould<M>(store, tableInfo, databaseAdapter) {

    private val objectToUpdate: M
    protected abstract fun buildObjectToUpdate(): M

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun insert_and_update_where() {
        store.insert(`object`)
        store.updateWhere(objectToUpdate)
        val objectFromDb = store.selectFirst()
        assertEqualsIgnoreId(objectFromDb, objectToUpdate)
    }

    @Test
    fun insert_and_delete_where() {
        store.insert(`object`)
        assertThat(store.count()).isEqualTo(1)
        store.deleteWhere(`object`)
        assertThat(store.count()).isEqualTo(0)
    }

    @Test
    fun update_when_call_update_or_insert_where_and_there_is_a_previous_object() {
        store.insert(`object`)
        val handleAction = store.updateOrInsertWhere(objectToUpdate)
        assertThat(handleAction).isEqualTo(HandleAction.Update)
        val objectFromDb = store.selectFirst()
        assertEqualsIgnoreId(objectFromDb, objectToUpdate)
    }

    @Test
    fun insert_when_call_update_or_insert_where_and_there_is_no_previous_object() {
        val handleAction = store.updateOrInsertWhere(objectToUpdate)
        assertThat(handleAction).isEqualTo(HandleAction.Insert)
        val objectFromDb = store.selectFirst()
        assertEqualsIgnoreId(objectFromDb, objectToUpdate)
    }

    @Test
    fun insert_same_object_simultaneously_and_transactionally() {
        val s1 = Single.fromCallable { store.updateOrInsertWhere(`object`) }.subscribeOn(Schedulers.io())
        val s2 = Single.fromCallable { store.updateOrInsertWhere(`object`) }.subscribeOn(Schedulers.io())

        s1.mergeWith(s2).blockingSubscribe()

        val objects = store.selectAll()

        assertThat(objects.size).isEqualTo(1)
    }

    init {
        objectToUpdate = buildObjectToUpdate()
    }
}
