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

import org.hisp.dhis.client.sdk.core.commons.database.Mapper;
import org.hisp.dhis.client.sdk.core.program.ProgramTable.ProgramColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramType;

import java.io.IOException;
import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class ProgramMapper implements Mapper<Program> {
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
        Program.validate(program);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProgramColumns.COLUMN_ID, program.getId());
        contentValues.put(ProgramColumns.COLUMN_UID, program.getUid());
        contentValues.put(ProgramColumns.COLUMN_CODE, program.getCode());
        contentValues.put(ProgramColumns.COLUMN_CREATED, program.getCreated().toString());
        contentValues.put(ProgramColumns.COLUMN_LAST_UPDATED, program.getLastUpdated().toString());
        contentValues.put(ProgramColumns.COLUMN_NAME, program.getName());
        contentValues.put(ProgramColumns.COLUMN_DISPLAY_NAME, program.getDisplayName());
        contentValues.put(ProgramColumns.COLUMN_SHORT_NAME, program.getShortName());
        contentValues.put(ProgramColumns.COLUMN_DISPLAY_SHORT_NAME, program.getDisplayShortName());
        contentValues.put(ProgramColumns.COLUMN_DESCRIPTION, program.getDescription());
        contentValues.put(ProgramColumns.COLUMN_DISPLAY_DESCRIPTION, program.getDisplayDescription());
        contentValues.put(ProgramColumns.COLUMN_DISPLAY_FRONT_PAGE_LIST, program.isDisplayFrontPageList() ? 1 : 0);
        contentValues.put(ProgramColumns.COLUMN_PROGRAM_TYPE, program.getProgramType().toString());

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
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        program.setId(getInt(cursor, ProgramColumns.COLUMN_ID));
        program.setUid(getString(cursor, ProgramColumns.COLUMN_UID));
        program.setCode(getString(cursor, ProgramColumns.COLUMN_CODE));
        program.setName(getString(cursor, ProgramColumns.COLUMN_NAME));
        program.setDisplayName(getString(cursor, ProgramColumns.COLUMN_DISPLAY_NAME));

        program.setShortName(getString(cursor, ProgramColumns.COLUMN_SHORT_NAME));
        program.setDisplayShortName(getString(cursor, ProgramColumns.COLUMN_DISPLAY_SHORT_NAME));
        program.setDescription(getString(cursor, ProgramColumns.COLUMN_DISPLAY_DESCRIPTION));
        program.setDisplayDescription(getString(cursor, ProgramColumns.COLUMN_DISPLAY_DESCRIPTION));

        program.setDisplayFrontPageList(getInt(cursor, ProgramColumns.COLUMN_DISPLAY_FRONT_PAGE_LIST) == 1);
        program.setProgramType(ProgramType.valueOf(getString(cursor, ProgramColumns.COLUMN_PROGRAM_TYPE)));

        try {
            program.setCreated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, ProgramColumns.COLUMN_CREATED)));
            program.setLastUpdated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, ProgramColumns.COLUMN_LAST_UPDATED)));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return program;
    }
}
