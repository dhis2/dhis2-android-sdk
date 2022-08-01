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

package org.hisp.dhis.android.core.arch.db.adapters.custom.internal;

import android.content.ContentValues;
import android.database.Cursor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"PMD.PreserveStackTrace"})
public class StringArrayColumnAdapter implements ColumnTypeAdapter<List<String>> {

    @Override
    public List<String> fromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        String sourceValue = cursor.getString(columnIndex);

        if (sourceValue == null || sourceValue.equals("")) {
            return Collections.emptyList();
        } else if (sourceValue.charAt(0) == '/') {
            return Arrays.asList(sourceValue.substring(1).split("/"));
        } else {
            ObjectMapper objectMapper  = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            try {
                return objectMapper.readValue(sourceValue, typeFactory.constructCollectionType(List.class,
                        String.class));
            } catch (IOException e) {
                throw new RuntimeException("Couldn't deserialize string array");
            }
        }
    }

    @Override
    public void toContentValues(ContentValues values, String columnName, List<String> value) {
        values.put(columnName, serialize(value));
    }

    public static String serialize(List<String> value) {
        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't serialize string array");
        }
    }
}