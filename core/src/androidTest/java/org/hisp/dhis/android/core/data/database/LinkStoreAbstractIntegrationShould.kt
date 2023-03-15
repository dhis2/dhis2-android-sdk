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
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.common.CoreObject
import org.junit.Before
import org.junit.Test

abstract class LinkStoreAbstractIntegrationShould<M : CoreObject> internal constructor(
    internal var store: LinkStore<M>,
    tableInfo: TableInfo,
    databaseAdapter: DatabaseAdapter
) : ObjectStoreAbstractIntegrationShould<M>(store, tableInfo, databaseAdapter) {

    private val objectWithOtherMasterUid: M
    private val masterUid: String

    protected abstract fun buildObjectWithOtherMasterUid(): M
    protected abstract fun addMasterUid(): String

    @Before
    @Throws(IOException::class)
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun delete_link_for_master_uid() {
        store.insert(`object`)
        store.deleteLinksForMasterUid(masterUid)
        val objectFromDb = store.selectFirst()
        assertThat(objectFromDb).isEqualTo(null)
    }

    @Test
    fun delete_links_for_master_should_delete_only_objects_with_the_master_key() {
        store.insert(`object`)
        store.insert(objectWithOtherMasterUid)
        store.deleteLinksForMasterUid(masterUid)
        val objectFromDb = store.selectFirst()
        assertEqualsIgnoreId(objectFromDb, objectWithOtherMasterUid)
    }

    @Test
    fun select_links_for_master_should_select_only_objects_with_the_master_key() {
        store.insert(`object`)
        store.insert(objectWithOtherMasterUid)
        val links = store.selectLinksForMasterUid(masterUid)
        assertThat(links.size).isEqualTo(1)
        assertEqualsIgnoreId(links.first(), `object`)
    }

    init {
        objectWithOtherMasterUid = buildObjectWithOtherMasterUid()
        masterUid = addMasterUid()
    }
}
