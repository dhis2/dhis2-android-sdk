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

package org.hisp.dhis.client.sdk.core.organisationunit;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.Mapper;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitTable.OrganisationUnitColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

public class OrganisationUnitMapper implements Mapper<OrganisationUnit> {

    @Override
    public Uri getContentUri() {
        return OrganisationUnitTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(OrganisationUnitTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return OrganisationUnitTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(OrganisationUnit organisationUnit) {
        if (!organisationUnit.isValid()) {
            throw new IllegalArgumentException("Organisation unit is not valid");
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(OrganisationUnitColumns.COLUMN_ID, organisationUnit.id());
        contentValues.put(OrganisationUnitColumns.COLUMN_UID, organisationUnit.uid());
        contentValues.put(OrganisationUnitColumns.COLUMN_CODE, organisationUnit.code());
        contentValues.put(OrganisationUnitColumns.COLUMN_CREATED, organisationUnit.created().toString());
        contentValues.put(OrganisationUnitColumns.COLUMN_LAST_UPDATED, organisationUnit.lastUpdated().toString());
        contentValues.put(OrganisationUnitColumns.COLUMN_NAME, organisationUnit.name());
        contentValues.put(OrganisationUnitColumns.COLUMN_DISPLAY_NAME, organisationUnit.displayName());
        contentValues.put(OrganisationUnitColumns.COLUMN_SHORT_NAME, organisationUnit.shortName());
        contentValues.put(OrganisationUnitColumns.COLUMN_DISPLAY_SHORT_NAME, organisationUnit.displayShortName());
        contentValues.put(OrganisationUnitColumns.COLUMN_DESCRIPTION, organisationUnit.description());
        contentValues.put(OrganisationUnitColumns.COLUMN_DISPLAY_DESCRIPTION, organisationUnit.displayDescription());

        contentValues.put(OrganisationUnitColumns.COLUMN_PARENT,
                organisationUnit.parent() != null ? organisationUnit.parent().uid() : null);
        contentValues.put(OrganisationUnitColumns.COLUMN_OPENING_DATE,
                organisationUnit.openingDate() != null ? organisationUnit.openingDate().toString() : null);
        contentValues.put(OrganisationUnitColumns.COLUMN_CLOSED_DATE,
                organisationUnit.closedDate() != null ? organisationUnit.closedDate().toString() : null);
        contentValues.put(OrganisationUnitColumns.COLUMN_LEVEL, organisationUnit.level());
        contentValues.put(OrganisationUnitColumns.COLUMN_PATH, organisationUnit.path());

        return contentValues;
    }

    @Override
    public OrganisationUnit toModel(Cursor cursor) {
        OrganisationUnit parentOrganisationUnit = OrganisationUnit.builder()
                .uid(getString(cursor, OrganisationUnitColumns.COLUMN_PARENT)).build();

        OrganisationUnit organisationUnit = null;
        try {
            organisationUnit = OrganisationUnit.builder()
                    .id(getLong(cursor, OrganisationUnitColumns.COLUMN_ID))
                    .uid(getString(cursor, OrganisationUnitColumns.COLUMN_UID))
                    .code(getString(cursor, OrganisationUnitColumns.COLUMN_CODE))
                    .name(getString(cursor, OrganisationUnitColumns.COLUMN_NAME))
                    .displayName(getString(cursor, OrganisationUnitColumns.COLUMN_DISPLAY_NAME))
                    .created(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, OrganisationUnitColumns.COLUMN_CREATED)))
                    .lastUpdated(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, OrganisationUnitColumns.COLUMN_LAST_UPDATED)))
                    .shortName(getString(cursor, OrganisationUnitColumns.COLUMN_SHORT_NAME))
                    .displayShortName(getString(cursor, OrganisationUnitColumns.COLUMN_DISPLAY_SHORT_NAME))
                    .description(getString(cursor, OrganisationUnitColumns.COLUMN_DISPLAY_DESCRIPTION))
                    .displayDescription(getString(cursor, OrganisationUnitColumns.COLUMN_DISPLAY_DESCRIPTION))
                    .parent(parentOrganisationUnit)
                    .level(getInt(cursor, OrganisationUnitColumns.COLUMN_LEVEL))
                    .path(getString(cursor, OrganisationUnitColumns.COLUMN_PATH))
                    .openingDate(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, OrganisationUnitColumns.COLUMN_OPENING_DATE)))
                    .closedDate(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, OrganisationUnitColumns.COLUMN_CLOSED_DATE)))
                    .build();

        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return organisationUnit;
    }
}
