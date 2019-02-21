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

import org.hisp.dhis.android.core.arch.db.TableInfo;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public abstract class ObjectStoreAbstractIntegrationShould<M extends Model> {

    final M object;
    final M objectWithId;
    private final ObjectStore<M> store;
    private final TableInfo tableInfo;
    private final DatabaseAdapter databaseAdapter;

    public ObjectStoreAbstractIntegrationShould(ObjectStore<M> store,
                                                TableInfo tableInfo,
                                                DatabaseAdapter databaseAdapter) {
        this.store = store;
        this.object = buildObject();
        this.objectWithId = buildObjectWithId();
        this.tableInfo = tableInfo;
        this.databaseAdapter = databaseAdapter;
    }

    protected abstract M buildObject();
    protected abstract M buildObjectWithId();

    @Before
    public void setUp() throws IOException {
        store.delete();
    }

    @After
    public void tearDown() throws IOException {
        DatabaseAdapterFactory.get(false).database().close();
    }

    @Test
    public void insert_and_select_first_object() {
        store.insert(object);
        M objectFromDb = store.selectFirst();
        assertThat(objectFromDb).isEqualTo(object);
    }

    @Test
    public void insert_as_content_values_and_select_first_object() {
        long rowsInserted = databaseAdapter.database()
                .insert(tableInfo.name(), null, object.toContentValues());
        assertThat(rowsInserted).isEqualTo(1);
        M objectFromDb = store.selectFirst();
        assertThat(objectFromDb).isEqualTo(object);
    }

    @Test
    public void insert_and_select_all_objects() {
        store.insert(object);
        List<M> objectsFromDb = store.selectAll();
        assertThat(objectsFromDb.iterator().next()).isEqualTo(object);
    }

    @Test
    public void delete_inserted_object_by_id() {
        store.insert(objectWithId);
        store.deleteById(objectWithId);
        assertThat(store.selectFirst()).isEqualTo(null);
    }

    @Test
    public void select_inserted_object_where_clause() {
        // TODO Implement test for store.selectWhere() method
    }

    @Test
    public void select_inserted_string_columns_where_clause() {
        // TODO Implement test for store.selectStringColumnsWhereClause() method
    }
}