/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import org.hisp.dhis.android.core.program.*

internal class ProgramSectionRenderingColumnAdapter : ColumnTypeAdapter<ProgramSectionRendering> {
    override fun fromCursor(cursor: Cursor, columnName: String): ProgramSectionRendering {
        return ProgramSectionRendering.create(
            getFromCursor(cursor, ProgramStageSectionTableInfo.Columns.DESKTOP_RENDER_TYPE),
            getFromCursor(cursor, ProgramStageSectionTableInfo.Columns.MOBILE_RENDER_TYPE)
        )
    }

    override fun toContentValues(values: ContentValues, columnName: String, value: ProgramSectionRendering) {
        value.desktop()?.type()?.let { values.put(ProgramStageSectionTableInfo.Columns.DESKTOP_RENDER_TYPE, it.name) }
        value.mobile()?.type()?.let { values.put(ProgramStageSectionTableInfo.Columns.MOBILE_RENDER_TYPE, it.name) }
    }

    private fun getFromCursor(cursor: Cursor, column: String): ProgramSectionDeviceRendering? {
        val index = cursor.getColumnIndex(column)
        val renderingType = cursor.getString(index)
        return renderingType?.let {
            ProgramSectionDeviceRendering.create(ProgramSectionRenderingType.valueOf(it))
        }
    }
}