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
package org.hisp.dhis.android.core.arch.db.adapters.custom.internal

import android.content.ContentValues
import android.database.Cursor
import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry

class DbGeometryColumnAdapter : ColumnTypeAdapter<Geometry> {

    override fun fromCursor(cursor: Cursor, columnName: String): Geometry? {
        val geometryTypeColumnIndex = cursor.getColumnIndex(GEOMETRY_TYPE)
        val geometryTypeStr = cursor.getString(geometryTypeColumnIndex)

        val geometryCoordinatesColumnIndex = cursor.getColumnIndex(GEOMETRY_COORDINATES)
        val geometryCoordinates = cursor.getString(geometryCoordinatesColumnIndex)

        return if (geometryTypeStr != null && geometryCoordinates != null) {
            FeatureType.valueOfFeatureType(geometryTypeStr)?.let { geometryType ->
                Geometry.builder().type(geometryType).coordinates(geometryCoordinates).build()
            }
        } else {
            null
        }
    }

    override fun toContentValues(values: ContentValues, columnName: String, value: Geometry?) {
        value?.let {
            values.put(GEOMETRY_TYPE, it.type()?.geometryType)
            values.put(GEOMETRY_COORDINATES, it.coordinates())
        }
    }

    companion object {
        private const val GEOMETRY_TYPE = "geometryType"
        private const val GEOMETRY_COORDINATES = "geometryCoordinates"
    }
}
