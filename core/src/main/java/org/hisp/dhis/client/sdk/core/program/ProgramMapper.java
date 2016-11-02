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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.database.AbsMapper;
import org.hisp.dhis.client.sdk.core.commons.database.Mapper;
import org.hisp.dhis.client.sdk.core.program.ProgramTable.ProgramColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramType;

import java.io.IOException;
import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class ProgramMapper extends AbsMapper<Program> {
    private final ObjectMapper objectMapper;

    public ProgramMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Uri getContentUri() {
        return ProgramTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(ProgramTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return ProgramTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(Program program) {
        if(!program.isValid()) {
            throw new IllegalArgumentException("Program is not valid");
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProgramColumns.COLUMN_ID, program.id());
        contentValues.put(ProgramColumns.COLUMN_UID, program.uid());
        contentValues.put(ProgramColumns.COLUMN_CODE, program.code());
        contentValues.put(ProgramColumns.COLUMN_CREATED, BaseIdentifiableObject.DATE_FORMAT.format(program.created()));
        contentValues.put(ProgramColumns.COLUMN_LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(program.lastUpdated()));
        contentValues.put(ProgramColumns.COLUMN_NAME, program.name());
        contentValues.put(ProgramColumns.COLUMN_DISPLAY_NAME, program.displayName());
        contentValues.put(ProgramColumns.COLUMN_SHORT_NAME, program.shortName());
        contentValues.put(ProgramColumns.COLUMN_DISPLAY_SHORT_NAME, program.displayShortName());
        contentValues.put(ProgramColumns.COLUMN_DESCRIPTION, program.description());
        contentValues.put(ProgramColumns.COLUMN_DISPLAY_DESCRIPTION, program.displayDescription());
        contentValues.put(ProgramColumns.COLUMN_DISPLAY_FRONT_PAGE_LIST, program.displayFrontPageList() ? 1 : 0);
        contentValues.put(ProgramColumns.COLUMN_PROGRAM_TYPE, program.programType().toString());

        // try to serialize the program into JSON blob
        try {
            contentValues.put(ProgramColumns.COLUMN_BODY, objectMapper.writeValueAsString(program));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        return contentValues;
    }

    @Override
    public Program toModel(Cursor cursor) {
        Program program;
        // trying to deserialize the JSON blob into Program instance
        try {
            program = objectMapper.readValue(getString(cursor, ProgramColumns.COLUMN_BODY), Program.class);
            program.toBuilder().id((getLong(cursor, ProgramColumns.COLUMN_ID)));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return program;
    }
}
