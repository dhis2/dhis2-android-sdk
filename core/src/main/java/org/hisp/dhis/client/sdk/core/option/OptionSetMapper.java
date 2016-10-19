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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.database.Mapper;
import org.hisp.dhis.client.sdk.core.option.OptionSetTable.OptionSetColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.option.OptionSet;

import java.io.IOException;
import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class OptionSetMapper implements Mapper<OptionSet> {
    private final ObjectMapper objectMapper;

    OptionSetMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Uri getContentUri() {
        return OptionSetTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(OptionSetTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return OptionSetTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(OptionSet optionSet) {
        OptionSet.validate(optionSet);

        ContentValues contentValues = new ContentValues();
        contentValues.put(OptionSetColumns.COLUMN_ID, optionSet.getId());
        contentValues.put(OptionSetColumns.COLUMN_UID, optionSet.getUid());
        contentValues.put(OptionSetColumns.COLUMN_CODE, optionSet.getCode());
        contentValues.put(OptionSetColumns.COLUMN_CREATED, optionSet.getCreated().toString());
        contentValues.put(OptionSetColumns.COLUMN_LAST_UPDATED, optionSet.getLastUpdated().toString());
        contentValues.put(OptionSetColumns.COLUMN_NAME, optionSet.getName());
        contentValues.put(OptionSetColumns.COLUMN_DISPLAY_NAME, optionSet.getDisplayName());

        // try to serialize the optionSet into JSON blob
        try {
            contentValues.put(OptionSetColumns.COLUMN_BODY, objectMapper.writeValueAsString(optionSet));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        return contentValues;
    }

    @Override
    public OptionSet toModel(Cursor cursor) {
        OptionSet optionSet;
        // trying to deserialize the JSON blob into OptionSet instance
        try {
            optionSet = objectMapper.readValue(getString(cursor, OptionSetColumns.COLUMN_BODY), OptionSet.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        optionSet.setId(getInt(cursor, OptionSetColumns.COLUMN_ID));
        optionSet.setUid(getString(cursor, OptionSetColumns.COLUMN_UID));
        optionSet.setCode(getString(cursor, OptionSetColumns.COLUMN_CODE));
        optionSet.setName(getString(cursor, OptionSetColumns.COLUMN_NAME));
        optionSet.setDisplayName(getString(cursor, OptionSetColumns.COLUMN_DISPLAY_NAME));

        try {
            optionSet.setCreated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, OptionSetColumns.COLUMN_CREATED)));
            optionSet.setLastUpdated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, OptionSetColumns.COLUMN_LAST_UPDATED)));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return optionSet;
    }
}
