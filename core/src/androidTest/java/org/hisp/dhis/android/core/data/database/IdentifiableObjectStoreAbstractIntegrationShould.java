/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.data.database;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public abstract class IdentifiableObjectStoreAbstractIntegrationShould<M extends ObjectWithUidInterface & CoreObject>
        extends ObjectStoreAbstractIntegrationShould<M> {

    private M objectToUpdate;
    IdentifiableObjectStore<M> store;

    public IdentifiableObjectStoreAbstractIntegrationShould(IdentifiableObjectStore<M> store, TableInfo tableInfo, DatabaseAdapter databaseAdapter) {
        super(store, tableInfo, databaseAdapter);
        this.store = store;
        this.objectToUpdate = buildObjectToUpdate();
    }

    protected abstract M buildObjectToUpdate();

    @Before
    public void setUp() throws IOException {
        super.setUp();
    }

    @Test
    public void insert_and_select_by_uid() {
        store.insert(object);
        M objectFromDb = store.selectByUid(object.uid());
        assertEqualsIgnoreId(objectFromDb);
    }

    @Test
    public void insert_and_select_by_uid_list() {
        store.insert(object);
        List<M> listFromDb = store.selectByUids(Collections.singletonList(object.uid()));
        assertThat(listFromDb.size()).isEqualTo(1);
        assertEqualsIgnoreId(listFromDb.get(0));
    }

    @Test
    public void select_inserted_object_uid() {
        store.insert(object);
        String objectUidFromDb = store.selectUids().iterator().next();
        assertThat(objectUidFromDb).isEqualTo(object.uid());
    }

    @Test
    public void delete_inserted_object_by_uid() {
        store.insert(object);
        store.delete(object.uid());
        assertThat(store.selectFirst()).isEqualTo(null);
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_if_try_to_delete_an_object_which_does_not_exists() {
        store.delete(object.uid());
    }

    @Test
    public void not_throw_exception_if_try_to_delete_an_object_which_does_not_exists() {
        store.deleteIfExists(object.uid());
    }

    @Test
    public void delete_if_exists_inserted_object_by_uid() {
        store.insert(object);
        store.deleteIfExists(object.uid());
        assertThat(store.selectFirst()).isEqualTo(null);
    }

    @Test
    public void update_inserted_object() {
        store.insert(object);
        store.update(objectToUpdate);
        M updatedObjectFromDb = store.selectFirst();
        assertEqualsIgnoreId(updatedObjectFromDb, objectToUpdate);
    }

    @Test
    public void insert_object_if_object_does_not_exists() {
        HandleAction handleAction = store.updateOrInsert(objectToUpdate);
        assertThat(handleAction).isEqualTo(HandleAction.Insert);
    }

    @Test
    public void update_inserted_object_if_object_exists() {
        store.insert(object);
        HandleAction handleAction = store.updateOrInsert(objectToUpdate);
        assertThat(handleAction).isEqualTo(HandleAction.Update);
    }

    @Test
    public void select_inserted_object_uids_where() {
        // TODO Implement test for store.selectUidsWhere() method
    }
}