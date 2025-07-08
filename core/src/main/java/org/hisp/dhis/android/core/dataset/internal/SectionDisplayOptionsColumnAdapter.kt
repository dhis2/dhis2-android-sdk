/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.dataset.internal

import android.content.ContentValues
import android.database.Cursor
import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter
import org.hisp.dhis.android.core.dataset.SectionDisplayOptions
import org.hisp.dhis.android.core.dataset.SectionPivotMode
import org.hisp.dhis.android.persistence.dataset.SectionTableInfo.Columns.AFTER_SECTION_TEXT
import org.hisp.dhis.android.persistence.dataset.SectionTableInfo.Columns.BEFORE_SECTION_TEXT
import org.hisp.dhis.android.persistence.dataset.SectionTableInfo.Columns.PIVOTED_CATEGORY
import org.hisp.dhis.android.persistence.dataset.SectionTableInfo.Columns.PIVOT_MODE

internal class SectionDisplayOptionsColumnAdapter : ColumnTypeAdapter<SectionDisplayOptions> {
    override fun fromCursor(cursor: Cursor, columnName: String?): SectionDisplayOptions {
        val beforeSectionTextIndx = cursor.getColumnIndex(BEFORE_SECTION_TEXT)
        val afterSectionTextIndx = cursor.getColumnIndex(AFTER_SECTION_TEXT)
        val pivotedCategoryIndx = cursor.getColumnIndex(PIVOTED_CATEGORY)

        val pivotModeIndx = cursor.getColumnIndex(PIVOT_MODE)
        val pivotMode = cursor.getString(pivotModeIndx)

        var sectionPivotMode: SectionPivotMode? = null
        if (pivotMode != null) {
            try {
                sectionPivotMode = SectionPivotMode.valueOf(pivotMode)
            } catch (exception: IllegalArgumentException) {
                throw IllegalArgumentException("Unknown SectionPivotMode type", exception)
            }
        }

        return SectionDisplayOptions.builder()
            .beforeSectionText(cursor.getString(beforeSectionTextIndx))
            .afterSectionText(cursor.getString(afterSectionTextIndx))
            .pivotMode(sectionPivotMode)
            .pivotedCategory(cursor.getString(pivotedCategoryIndx))
            .build()
    }

    override fun toContentValues(values: ContentValues?, columnName: String?, value: SectionDisplayOptions?) {
        value?.beforeSectionText()?.let { values?.put(BEFORE_SECTION_TEXT, it) }
        value?.afterSectionText()?.let { values?.put(AFTER_SECTION_TEXT, it) }
        value?.pivotMode()?.let { values?.put(PIVOT_MODE, it.name) }
        value?.pivotedCategory()?.let { values?.put(PIVOTED_CATEGORY, it) }
    }
}
