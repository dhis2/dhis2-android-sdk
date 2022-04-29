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
package org.hisp.dhis.android.core.visualization.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.visualization.*

internal object VisualizationFields {
    internal const val CATEGORY_DIMENSIONS = "categoryDimensions"
    internal const val DATA_DIMENSION_ITEMS = "dataDimensionItems"
    internal const val LEGEND = "legend"

    private const val LEGEND_DISPLAY_STRATEGY = "legendDisplayStrategy"
    private const val LEGEND_DISPLAY_STYLE = "legendDisplayStyle"
    private const val LEGEND_SET = "legendSet"

    private val fh = FieldsHelper<Visualization>()
    val uid = fh.uid()

    val allFields: Fields<Visualization>
        get() =
            commonFields()
                .fields(
                    fh.field<VisualizationLegend>(LEGEND)
                )
                .build()

    val allFieldsAPI36: Fields<Visualization>
        get() =
            commonFields()
                .fields(
                    fh.field<String>(LEGEND_DISPLAY_STRATEGY),
                    fh.field<String>(LEGEND_DISPLAY_STYLE),
                    fh.nestedFieldWithUid(LEGEND_SET)
                ).build()

    private fun commonFields(): Fields.Builder<Visualization> =
        Fields.builder<Visualization>()
            .fields(fh.getIdentifiableFields())
            .fields(
                fh.field<String>(VisualizationTableInfo.Columns.DESCRIPTION),
                fh.field<String>(VisualizationTableInfo.Columns.DISPLAY_DESCRIPTION),
                fh.field<String>(VisualizationTableInfo.Columns.DISPLAY_FORM_NAME),
                fh.field<VisualizationType>(VisualizationTableInfo.Columns.TYPE),
                fh.field<Boolean>(VisualizationTableInfo.Columns.HIDE_TITLE),
                fh.field<Boolean>(VisualizationTableInfo.Columns.HIDE_SUBTITLE),
                fh.field<Boolean>(VisualizationTableInfo.Columns.HIDE_EMPTY_COLUMNS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.HIDE_EMPTY_ROWS),
                fh.field<HideEmptyItemStrategy>(VisualizationTableInfo.Columns.HIDE_EMPTY_ROW_ITEMS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.HIDE_LEGEND),
                fh.field<Boolean>(VisualizationTableInfo.Columns.SHOW_HIERARCHY),
                fh.field<Boolean>(VisualizationTableInfo.Columns.ROW_TOTALS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.ROW_SUB_TOTALS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.COL_TOTALS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.COL_SUB_TOTALS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.SHOW_DIMENSION_LABELS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.PERCENT_STACKED_VALUES),
                fh.field<Boolean>(VisualizationTableInfo.Columns.NO_SPACE_BETWEEN_COLUMNS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.SKIP_ROUNDING),
                fh.field<DisplayDensity>(VisualizationTableInfo.Columns.DISPLAY_DENSITY),
                fh.field<DigitGroupSeparator>(VisualizationTableInfo.Columns.DIGIT_GROUP_SEPARATOR),
                fh.field<String>(VisualizationTableInfo.Columns.RELATIVE_PERIODS),
                fh.nestedField<CategoryDimension>(CATEGORY_DIMENSIONS).with(CategoryDimensionFields.allFields),
                fh.field<String>(VisualizationTableInfo.Columns.FILTER_DIMENSIONS),
                fh.field<String>(VisualizationTableInfo.Columns.ROW_DIMENSIONS),
                fh.field<String>(VisualizationTableInfo.Columns.COLUMN_DIMENSIONS),
                fh.nestedField<DataDimensionItem>(DATA_DIMENSION_ITEMS).with(DataDimensionItemFields.allFields),
                fh.field<List<Int>>(VisualizationTableInfo.Columns.ORGANISATION_UNIT_LEVELS),
                fh.field<Boolean>(VisualizationTableInfo.Columns.USER_ORGANISATION_UNIT),
                fh.field<Boolean>(VisualizationTableInfo.Columns.USER_ORGANISATION_UNIT_CHILDREN),
                fh.field<Boolean>(VisualizationTableInfo.Columns.USER_ORGANISATION_UNIT_GRAND_CHILDREN),
                fh.nestedFieldWithUid(VisualizationTableInfo.Columns.ORGANISATION_UNITS),
                fh.nestedFieldWithUid(VisualizationTableInfo.Columns.PERIODS)
            )
}
