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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.DataObject
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.junit.Before
import org.junit.Test
import java.io.IOException

abstract class IdentifiableDataObjectStoreAbstractIntegrationShould<M> internal constructor(
    internal val dataObjectStore: IdentifiableDataObjectStore<M>,
    tableInfo: TableInfo,
    databaseAdapter: DatabaseAdapter,
) : IdentifiableObjectStoreAbstractIntegrationShould<M>(
    dataObjectStore,
    tableInfo,
    databaseAdapter,
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
    fun return_a_deleted_object_if_state_set_as_to_delete() = runTest {
        store.insert(objectWithToDeleteState)
        val obj = store.selectFirst()
        assertThat(obj!!.deleted()).isEqualTo(true)
    }

    @Test
    fun return_a_not_deleted_object_if_state_set_as_synced() = runTest {
        store.insert(objectWithSyncedState)
        val obj = store.selectFirst()
        assertThat(obj!!.deleted()).isEqualTo(false)
    }

    @Test
    fun set_and_get_sync_state() = runTest {
        dataObjectStore.insert(`object`)
        dataObjectStore.setSyncState(`object`.uid(), State.TO_UPDATE)
        val state = dataObjectStore.getSyncState(`object`.uid())
        assertThat(state).isEqualTo(State.TO_UPDATE)
    }

    @Test
    fun set_sync_state_for_multiple_uids() = runTest {
        dataObjectStore.insert(`object`)
        val result = dataObjectStore.setSyncState(listOf(`object`.uid()), State.ERROR)
        assertThat(result).isEqualTo(1)
        val state = dataObjectStore.getSyncState(`object`.uid())
        assertThat(state).isEqualTo(State.ERROR)
    }

    @Test
    fun set_sync_state_if_uploading_updates_when_uploading() = runTest {
        dataObjectStore.insert(`object`)
        dataObjectStore.setSyncState(`object`.uid(), State.UPLOADING)
        val result = dataObjectStore.setSyncStateIfUploading(`object`.uid(), State.SYNCED)
        assertThat(result).isEqualTo(1)
        val state = dataObjectStore.getSyncState(`object`.uid())
        assertThat(state).isEqualTo(State.SYNCED)
    }

    @Test
    fun set_sync_state_if_uploading_does_nothing_when_not_uploading() = runTest {
        dataObjectStore.insert(`object`)
        dataObjectStore.setSyncState(`object`.uid(), State.TO_POST)
        val result = dataObjectStore.setSyncStateIfUploading(`object`.uid(), State.SYNCED)
        assertThat(result).isEqualTo(0)
        val state = dataObjectStore.getSyncState(`object`.uid())
        assertThat(state).isEqualTo(State.TO_POST)
    }

    @Test
    fun exists_returns_true_for_existing_object() = runTest {
        dataObjectStore.insert(`object`)
        val exists = dataObjectStore.exists(`object`.uid())
        assertThat(exists).isTrue()
    }

    @Test
    fun exists_returns_false_for_non_existing_object() = runTest {
        val exists = dataObjectStore.exists("non_existing_uid")
        assertThat(exists).isFalse()
    }

    @Test
    fun get_uploadable_sync_states_including_error_returns_objects() = runTest {
        dataObjectStore.insert(`object`)
        dataObjectStore.setSyncState(`object`.uid(), State.TO_POST)
        val uploadable = dataObjectStore.getUploadableSyncStatesIncludingError()
        assertThat(uploadable).isNotEmpty()
    }

    init {
        objectWithToDeleteState = buildObjectWithToDeleteState()
        objectWithSyncedState = buildObjectWithSyncedState()
    }
}
