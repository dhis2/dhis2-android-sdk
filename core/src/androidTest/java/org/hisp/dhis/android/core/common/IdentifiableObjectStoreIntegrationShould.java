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

package org.hisp.dhis.android.core.common;

import android.database.Cursor;

import org.hisp.dhis.android.core.BaseIntegrationTestWithDatabase;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetTableInfo;
import org.hisp.dhis.android.core.option.internal.OptionSetStore;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hisp.dhis.android.core.common.StoreMocks.optionSetCursorAssert;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(D2JunitRunner.class)
public class IdentifiableObjectStoreIntegrationShould extends BaseIntegrationTestWithDatabase {

    private IdentifiableObjectStore<OptionSet> store;

    private OptionSet optionSet;
    private OptionSet updatedOptionSet;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.optionSet = StoreMocks.generateOptionSet();
        this.updatedOptionSet = StoreMocks.generateUpdatedOptionSet();
        this.store = OptionSetStore.create(databaseAdapter());
    }

    private Cursor getCursor() {
        return databaseAdapter().query(OptionSetTableInfo.TABLE_INFO.name(), OptionSetTableInfo.TABLE_INFO.columns().all());
    }

    @Test
    public void insert_option_set() {
        store.insert(optionSet);
        Cursor cursor = getCursor();
        optionSetCursorAssert(cursor, optionSet);
    }

    @Test(expected = NullPointerException.class)
    public void throw_exception_for_null_when_inserting() {
        OptionSet optionSet = null;
        store.insert(optionSet);
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_for_second_identical_insertion() {
        store.insert(this.optionSet);
        store.insert(this.optionSet);
    }

    @Test(expected = IllegalStateException.class)
    public void throw_exception_for_option_set_without_uid_inserting() {
        OptionSet withoutUid = OptionSet.builder().code("code").build();
        store.insert(withoutUid);
    }

    @Test
    public void delete_existing_option_set() {
        store.insert(optionSet);
        store.delete(optionSet.uid());
        assertThatCursor(getCursor()).isExhausted();
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_deleting_non_existing_option_set() {
        store.delete("new-id");
    }

    @Test(expected = NullPointerException.class)
    public void throw_exception_deleting_with_null_uid() {
        store.delete(null);
    }

    @Test
    public void do_not_throw_exception_safe_deleting_non_existing_option_set() {
        store.deleteIfExists("new-id");
        assertThatCursor(getCursor()).isExhausted();
    }

    @Test
    public void update_option_set() {
        store.insert(optionSet);
        store.update(updatedOptionSet);
        Cursor cursor = getCursor();
        optionSetCursorAssert(cursor, updatedOptionSet);
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_updating_null() {
        store.update(null);
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_updating_with_null_uid() {
        store.update(StoreMocks.generateOptionSetWithoutUid());
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_updating_non_existing_option_set() {
        store.update(optionSet);
    }

    @Test
    public void insert_when_no_option_set_and_update_or_insert() {
        store.updateOrInsert(optionSet);
        Cursor cursor = getCursor();
        optionSetCursorAssert(cursor, optionSet);
    }

    @Test
    public void update_when_option_set_and_update_or_insert() {
        store.insert(optionSet);
        store.updateOrInsert(updatedOptionSet);
        Cursor cursor = getCursor();
        optionSetCursorAssert(cursor, updatedOptionSet);
    }
}
