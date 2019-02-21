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
import org.hisp.dhis.android.core.common.DataModel;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public abstract class IdentifiableDataObjectStoreAbstractIntegrationShould<M extends ObjectWithUidInterface & Model
        & DataModel & ObjectWithDeleteInterface> extends IdentifiableObjectStoreAbstractIntegrationShould<M> {

    private M objectWithToDeleteState;
    private M objectWithSyncedState;

    public IdentifiableDataObjectStoreAbstractIntegrationShould(IdentifiableObjectStore<M> store,
                                                                TableInfo tableInfo,
                                                                DatabaseAdapter databaseAdapter) {
        super(store, tableInfo, databaseAdapter);
        this.objectWithToDeleteState = buildObjectWithToDeleteState();
        this.objectWithSyncedState = buildObjectWithSyncedState();
    }

    protected abstract M buildObjectWithToDeleteState();

    protected abstract M buildObjectWithSyncedState();

    @Before
    public void setUp() throws IOException {
        super.setUp();
    }

    @Test
    public void return_a_deleted_object_if_state_set_as_to_delete() {
        store.insert(objectWithToDeleteState);
        M object = store.selectFirst();
        assertThat(object.deleted()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void return_a_not_deleted_object_if_state_set_as_synced() {
        store.insert(objectWithSyncedState);
        M object = store.selectFirst();
        assertThat(object.deleted()).isEqualTo(Boolean.FALSE);
    }
}