/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.option;

import android.content.ContentResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.option.OptionSetMapper.OptionSetColumns;
import org.hisp.dhis.client.sdk.models.option.OptionSet;

public class OptionSetStoreImpl extends AbsIdentifiableObjectStore<OptionSet> implements OptionSetStore {
    public static final String CREATE_TABLE_OPTION_SETS = "CREATE TABLE IF NOT EXISTS " +
            OptionSetColumns.TABLE_NAME + " (" +
            OptionSetColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OptionSetColumns.COLUMN_UID + " TEXT NOT NULL," +
            OptionSetColumns.COLUMN_NAME + " TEXT," +
            OptionSetColumns.COLUMN_DISPLAY_NAME + " TEXT," +
            OptionSetColumns.COLUMN_CODE + " TEXT," +
            OptionSetColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            OptionSetColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            OptionSetColumns.COLUMN_VERSION + " INTEGER NOT NULL," +
            OptionSetColumns.COLUMN_BODY + " TEXT NOT NULL" +
            " UNIQUE " + "(" + OptionSetColumns.COLUMN_UID + ")" + " ON CONFLICT REPLACE" + " )";

    public static final String DROP_TABLE_OPTION_SETS = "DROP TABLE IF EXISTS " +
            OptionSetColumns.TABLE_NAME;

    public OptionSetStoreImpl(ContentResolver contentResolver, ObjectMapper objectMapper) {
        super(contentResolver, new OptionSetMapper(objectMapper));
    }
}
