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
import java.io.IOException
import kotlin.Throws
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.DataObject
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.junit.Before
import org.junit.Test

abstract class IdentifiableDataObjectStoreAbstractIntegrationShould<M> internal constructor(
    store: IdentifiableObjectStore<M>,
    tableInfo: TableInfo,
    databaseAdapter: DatabaseAdapter
) : IdentifiableObjectStoreAbstractIntegrationShould<M>(
    store, tableInfo, databaseAdapter
) where M : ObjectWithUidInterface, M : CoreObject, M : DataObject, M : ObjectWithDeleteInterface {

    private val objectWithToDeleteState: M
    private val objectWithSyncedState: M

    protected abstract fun buildObjectWithToDeleteState(): M
    protected abstract fun buildObjectWithSyncedState(): M

    @Before
    @Throws(IOException::class)
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun return_a_deleted_object_if_state_set_as_to_delete() {
        store.insert(objectWithToDeleteState)
        val obj = store.selectFirst()
        assertThat(obj!!.deleted()).isEqualTo(true)
    }

    @Test
    fun return_a_not_deleted_object_if_state_set_as_synced() {
        store.insert(objectWithSyncedState)
        val obj = store.selectFirst()
        assertThat(obj!!.deleted()).isEqualTo(false)
    }

    init {
        objectWithToDeleteState = buildObjectWithToDeleteState()
        objectWithSyncedState = buildObjectWithSyncedState()
    }
}
