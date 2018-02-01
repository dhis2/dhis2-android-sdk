/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.common;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hisp.dhis.android.core.common.StoreMocks.optionSetCursorAssert;

@RunWith(AndroidJUnit4.class)
public class ObjectStoreIntegrationShould extends AbsStoreTestCase {

    private ObjectStore<OptionSetModel> store;

    private OptionSetModel model;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.model = StoreMocks.generateOptionSetModel();
        this.store = StoreFactory.objectStore(databaseAdapter(),
                OptionSetModel.TABLE, OptionSetModel.Columns.all());
    }

    @Test
    public void insert_model() {
        store.insert(model);
        Cursor cursor = getCursor(OptionSetModel.TABLE, OptionSetModel.Columns.all());
        optionSetCursorAssert(cursor, model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_exception_for_null_when_inserting() {
        store.insert(null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_exception_for_second_identical_insertion() {
        store.insert(this.model);
        store.insert(this.model);
    }
}
