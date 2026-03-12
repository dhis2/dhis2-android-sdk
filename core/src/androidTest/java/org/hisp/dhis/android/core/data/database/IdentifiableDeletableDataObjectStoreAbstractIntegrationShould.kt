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
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStore
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.DeletableDataObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.junit.Before
import org.junit.Test
import java.io.IOException

abstract class IdentifiableDeletableDataObjectStoreAbstractIntegrationShould<M> internal constructor(
    private val deletableStore: IdentifiableDeletableDataObjectStore<M>,
    tableInfo: TableInfo,
    databaseAdapter: DatabaseAdapter,
) : IdentifiableDataObjectStoreAbstractIntegrationShould<M>(
    deletableStore,
    tableInfo,
    databaseAdapter,
) where M : ObjectWithUidInterface, M : CoreObject, M : DeletableDataObject {

    protected abstract fun buildObjectWithUploadingState(): M

    @Before
    @Throws(IOException::class)
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun set_deleted_marks_object_as_deleted() = runTest {
        deletableStore.insert(`object`)
        deletableStore.setDeleted(`object`.uid())
        val obj = deletableStore.selectByUid(`object`.uid())
        assertThat(obj!!.deleted()).isTrue()
    }

    @Test
    fun set_sync_state_or_delete_updates_state_when_not_synced() = runTest {
        val uploadingObj = buildObjectWithUploadingState()
        deletableStore.insert(uploadingObj)
        val action = deletableStore.setSyncStateOrDelete(uploadingObj.uid(), State.ERROR)
        assertThat(action).isEqualTo(HandleAction.Update)
    }

    @Test
    fun set_sync_state_or_delete_deletes_when_synced_and_deleted() = runTest {
        val uploadingObj = buildObjectWithUploadingState()
        deletableStore.insert(uploadingObj)
        deletableStore.setDeleted(uploadingObj.uid())
        val action = deletableStore.setSyncStateOrDelete(uploadingObj.uid(), State.SYNCED)
        assertThat(action).isEqualTo(HandleAction.Delete)
        val obj = deletableStore.selectByUid(uploadingObj.uid())
        assertThat(obj).isNull()
    }

    @Test
    fun select_sync_state_where_returns_states() = runTest {
        deletableStore.insert(`object`)
        val states = deletableStore.selectSyncStateWhere("uid = '${`object`.uid()}'")
        assertThat(states).isNotEmpty()
    }
}
