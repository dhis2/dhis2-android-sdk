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
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public abstract class ObjectWithoutUidStoreAbstractIntegrationShould<M extends Model>
        extends ObjectStoreAbstractIntegrationShould<M> {

    private M objectToUpdate;
    protected ObjectWithoutUidStore<M> store;

    public ObjectWithoutUidStoreAbstractIntegrationShould(ObjectWithoutUidStore<M> store,
                                                          TableInfo tableInfo,
                                                          DatabaseAdapter databaseAdapter) {
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
    public void insert_and_update_where() {
        store.insert(object);
        store.updateWhere(objectToUpdate);
        M objectFromDb = store.selectFirst();
        assertThat(objectFromDb).isEqualTo(objectToUpdate);
    }

    @Test
    public void update_when_call_update_or_insert_where_and_there_is_a_previous_object() {
        store.insert(object);
        HandleAction handleAction = store.updateOrInsertWhere(objectToUpdate);
        assertThat(handleAction).isEqualTo(HandleAction.Update);
        M objectFromDb = store.selectFirst();
        assertThat(objectFromDb).isEqualTo(objectToUpdate);
    }

    @Test
    public void insert_when_call_update_or_insert_where_and_there_is_no_previous_object() {
        HandleAction handleAction = store.updateOrInsertWhere(objectToUpdate);
        assertThat(handleAction).isEqualTo(HandleAction.Insert);
        M objectFromDb = store.selectFirst();
        assertThat(objectFromDb).isEqualTo(objectToUpdate);
    }
}