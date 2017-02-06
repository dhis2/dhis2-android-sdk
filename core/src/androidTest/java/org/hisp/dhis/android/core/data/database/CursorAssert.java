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

package org.hisp.dhis.android.core.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import static com.google.common.truth.Truth.assertThat;

public final class CursorAssert {
    private final Cursor cursor;
    private int row;

    public static CursorAssert assertThatCursor(Cursor cursor) {
        return new CursorAssert(cursor);
    }

    private CursorAssert(Cursor cursor) {
        this.cursor = cursor;

        // set to first row by default
        this.row = 0;
    }

    @NonNull
    public CursorAssert hasRow(@NonNull Object... values) {
        assertThat(cursor.moveToNext()).named("row " + (row + 1) + " exists").isTrue();
        row = row + 1;

        assertThat(cursor.getColumnCount()).named("column count").isEqualTo(values.length);
        for (int index = 0; index < values.length; index++) {
            assertThat(cursor.getString(index))
                    .named("row " + row + " column '" + cursor.getColumnName(index) + "'")
                    .isEqualTo(values[index] == null ? values[index] : String.valueOf(values[index]));
        }

        return this;
    }

    @NonNull
    public CursorAssert hasRow(@NonNull String[] projection, @NonNull ContentValues contentValues) {
        assertThat(projection.length)
                .named("Projection size does not match size of content values")
                .isEqualTo(contentValues.size());

        Object[] values = new Object[projection.length];
        for (int index = 0; index < projection.length; index++) {
            values[index] = contentValues.get(projection[index]);
        }

        hasRow(values);

        return this;
    }

    public void isExhausted() {
        if (cursor.moveToNext()) {
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if (i > 0) {
                    data.append(", ");
                }

                data.append(cursor.getString(i));
            }

            throw new AssertionError("Expected no more rows but was: " + data);
        }

        cursor.close();
    }
}