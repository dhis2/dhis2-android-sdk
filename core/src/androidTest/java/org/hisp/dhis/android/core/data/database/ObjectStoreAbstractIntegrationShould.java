/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.data.database;

import android.content.ContentValues;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.common.CoreObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public abstract class ObjectStoreAbstractIntegrationShould<M extends CoreObject> {

    final M object;
    private final ObjectStore<M> store;
    protected final TableInfo tableInfo;
    private final DatabaseAdapter databaseAdapter;

    public ObjectStoreAbstractIntegrationShould(ObjectStore<M> store,
                                                TableInfo tableInfo,
                                                DatabaseAdapter databaseAdapter) {
        this.store = store;
        this.object = buildObject();
        this.tableInfo = tableInfo;
        this.databaseAdapter = databaseAdapter;
    }

    protected abstract M buildObject();

    @Before
    public void setUp() throws IOException {
        store.delete();
    }

    @Test
    public void insert_and_select_first_object() {
        store.insert(object);
        M objectFromDb = store.selectFirst();
        assertEqualsIgnoreId(objectFromDb);
    }

    @Test
    public void insert_as_content_values_and_select_first_object() {
        long rowsInserted = databaseAdapter.insert(tableInfo.name(), null, object.toContentValues());
        assertThat(rowsInserted).isEqualTo(1);
        M objectFromDb = store.selectFirst();
        assertEqualsIgnoreId(objectFromDb);
    }

    @Test
    public void insert_and_select_all_objects() {
        store.insert(object);
        List<M> objectsFromDb = store.selectAll();
        assertEqualsIgnoreId(objectsFromDb.iterator().next());
    }

    @Test
    public void delete_inserted_object_by_id() {
        store.insert(object);
        M m = store.selectFirst();
        store.deleteById(m);
        assertThat(store.selectFirst()).isEqualTo(null);
    }


    void assertEqualsIgnoreId(M localObject) {
        assertEqualsIgnoreId(localObject, object);
    }

    void assertEqualsIgnoreId(M m1, M m2) {
        ContentValues cv1 = m1.toContentValues();
        cv1.remove("_id");

        ContentValues cv2 = m2.toContentValues();
        cv2.remove("_id");

        assertThat(cv1).isEqualTo(cv2);
    }
}