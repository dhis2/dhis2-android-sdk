/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.visualization.internal

import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationDimension
import org.hisp.dhis.android.core.visualization.VisualizationTableInfo.Columns

internal object VisualizationFields : BaseFields<Visualization>() {
    private const val LEGEND = "legend"
    private const val COLUMNS = "columns"
    private const val ROWS = "rows"
    private const val FILTERS = "filters"
    private const val LEGEND_DISPLAY_STRATEGY = "legendDisplayStrategy"
    private const val LEGEND_DISPLAY_STYLE = "legendDisplayStyle"
    private const val LEGEND_SET = "legendSet"
    internal const val ITEMS = "items"

    val uid = fh.uid()

    val allFields = Fields.from(
        commonFields(),
        fh.field(LEGEND),
    )

    val allFieldsAPI36 = Fields.from(
        commonFields(),
        fh.field(LEGEND_DISPLAY_STRATEGY),
        fh.field(LEGEND_DISPLAY_STYLE),
        fh.nestedFieldWithUid(LEGEND_SET),
    )

    private fun commonFields() = fh.getIdentifiableFields() + listOf(
        fh.field(Columns.DESCRIPTION),
        fh.field(Columns.DISPLAY_DESCRIPTION),
        fh.field(Columns.DISPLAY_FORM_NAME),
        fh.field(Columns.TYPE),
        fh.field(Columns.HIDE_TITLE),
        fh.field(Columns.HIDE_SUBTITLE),
        fh.field(Columns.HIDE_EMPTY_COLUMNS),
        fh.field(Columns.HIDE_EMPTY_ROWS),
        fh.field(Columns.HIDE_EMPTY_ROW_ITEMS),
        fh.field(Columns.HIDE_LEGEND),
        fh.field(Columns.SHOW_HIERARCHY),
        fh.field(Columns.ROW_TOTALS),
        fh.field(Columns.ROW_SUB_TOTALS),
        fh.field(Columns.COL_TOTALS),
        fh.field(Columns.COL_SUB_TOTALS),
        fh.field(Columns.SHOW_DIMENSION_LABELS),
        fh.field(Columns.PERCENT_STACKED_VALUES),
        fh.field(Columns.NO_SPACE_BETWEEN_COLUMNS),
        fh.field(Columns.SKIP_ROUNDING),
        fh.field(Columns.DISPLAY_DENSITY),
        fh.field(Columns.DIGIT_GROUP_SEPARATOR),
        fh.nestedField<VisualizationDimension>(COLUMNS).with(VisualizationDimensionFields.allFields),
        fh.nestedField<VisualizationDimension>(ROWS).with(VisualizationDimensionFields.allFields),
        fh.nestedField<VisualizationDimension>(FILTERS).with(VisualizationDimensionFields.allFields),
    )
}
