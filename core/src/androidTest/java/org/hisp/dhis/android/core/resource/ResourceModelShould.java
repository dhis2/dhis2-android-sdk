/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.resource;

import android.content.ContentValues;
import android.database.MatrixCursor;
import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.resource.ResourceModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ResourceModelShould {
    private static final Long ID = 2L;
    private static final String RESOURCE_TYPE = "OrganisationUnit";

    // timestamp
    private static final String DATE = "2017-01-18T13:39:00.000";

    @Test
    public void create_model_when_created_from_database_cursor() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.RESOURCE_TYPE, Columns.LAST_SYNCED
        });

        matrixCursor.addRow(new Object[]{
                ID, RESOURCE_TYPE, DATE
        });

        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ResourceModel resource = ResourceModel.create(matrixCursor);
        assertThat(resource.id()).isEqualTo(ID);
        assertThat(resource.resourceType()).isEqualTo(RESOURCE_TYPE);
        assertThat(resource.lastSynced()).isEqualTo(timeStamp);
    }

    @Test
    public void create_content_values_when_created_from_builder() throws Exception {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        ResourceModel resource = ResourceModel.builder()
                .id(ID)
                .resourceType(RESOURCE_TYPE)
                .lastSynced(timeStamp)
                .build();

        ContentValues contentValues = resource.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.RESOURCE_TYPE)).isEqualTo(RESOURCE_TYPE);
        assertThat(contentValues.getAsString(Columns.LAST_SYNCED)).isEqualTo(DATE);
    }
}
