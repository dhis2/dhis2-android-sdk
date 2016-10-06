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

package org.hisp.dhis.client.sdk.core.program;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramType;

import java.util.List;

public class ProgramStoreImpl extends AbsIdentifiableObjectStore<Program> implements ProgramStore {

    public static final String CREATE_TABLE_PROGRAMS = "CREATE TABLE IF NOT EXISTS " +
            ProgramColumns.TABLE_NAME + " (" +
            ProgramColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramColumns.COLUMN_UID + " TEXT NOT NULL," +
            ProgramColumns.COLUMN_CODE + " TEXT," +
            ProgramColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            ProgramColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            ProgramColumns.COLUMN_NAME + " TEXT," +
            ProgramColumns.COLUMN_DISPLAY_NAME + " TEXT," +
            ProgramColumns.COLUMN_SHORT_NAME + "TEXT," +
            ProgramColumns.COLUMN_DISPLAY_SHORT_NAME + "TEXT," +
            ProgramColumns.COLUMN_DESCRIPTION + "TEXT," +
            ProgramColumns.COLUMN_DISPLAY_DESCRIPTION + "TEXT," +
            ProgramColumns.COLUMN_PROGRAM_TYPE + " TEXT NOT NULL," +
            ProgramColumns.COLUMN_DISPLAY_FRONT_PAGE_LIST + " INTEGER NOT NULL," +
            ProgramColumns.COLUMN_BODY + "TEXT NOT NULL" +
            " UNIQUE " + "(" + ProgramColumns.COLUMN_UID + ")" + " ON CONFLICT REPLACE" + " )";

    private static final String DROP_TABLE_PROGRAMS = "DROP TABLE IF EXISTS " +
            ProgramColumns.TABLE_NAME;

    public ProgramStoreImpl(ContentResolver contentResolver, Mapper<Program> mapper) {
        super(contentResolver, mapper);
    }

    @Override
    public List<Program> query(ProgramType programType) {
        if (programType == null) {
            throw new IllegalArgumentException("uid must not be null");
        }

        final String[] selectionArgs = new String[]{programType.toString()};
        final String selection = ProgramColumns.COLUMN_PROGRAM_TYPE + " = ?";

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModels(cursor);
    }

    @Override
    public List<Program> query(boolean displayFrontPageList) {
        final String selection = ProgramColumns.COLUMN_PROGRAM_TYPE + " = ?";
        final String[] selectionArgs = new String[]{
                String.valueOf(displayFrontPageList ? 1 : 0)
        };

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModels(cursor);
    }
}
