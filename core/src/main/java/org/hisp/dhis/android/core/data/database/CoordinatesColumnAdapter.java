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

import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.event.EventTableInfo;

public class CoordinatesColumnAdapter implements ColumnTypeAdapter<Coordinates> {

    @Override
    public Coordinates fromCursor(Cursor cursor, String columnName) {
        int latitudeColumnIndex = cursor.getColumnIndex(EventTableInfo.Columns.LATITUDE);
        String latitude = (latitudeColumnIndex == -1 || cursor.isNull(latitudeColumnIndex)) ?
                null : cursor.getString(latitudeColumnIndex);
        int longitudeColumnIndex = cursor.getColumnIndex(EventTableInfo.Columns.LONGITUDE);
        String longitude = (longitudeColumnIndex == -1 || cursor.isNull(longitudeColumnIndex)) ?
                null : cursor.getString(longitudeColumnIndex);

        return Coordinates.create(
                Double.parseDouble(latitude),
                Double.parseDouble(longitude)
        );
    }

    @Override
    public void toContentValues(ContentValues values, String columnName, Coordinates value) {
        values.put(EventTableInfo.Columns.LATITUDE, value.latitude());
        values.put(EventTableInfo.Columns.LONGITUDE, value.longitude());
    }
}