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

package org.hisp.dhis.android.core.visualization

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns

object VisualizationTableInfo {

    @JvmField
    val TABLE_INFO: TableInfo = object : TableInfo() {
        override fun name(): String {
            return "Visualization"
        }

        override fun columns(): CoreColumns {
            return Columns()
        }
    }

    class Columns : IdentifiableColumns() {
        override fun all(): Array<String> {
            return CollectionsHelper.appendInNewArray(
                super.all(),
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                DISPLAY_FORM_NAME,
                TITLE,
                DISPLAY_TITLE,
                SUBTITLE,
                DISPLAY_SUBTITLE,
                TYPE,
                HIDE_TITLE,
                HIDE_SUBTITLE,
                HIDE_EMPTY_COLUMNS,
                HIDE_EMPTY_ROWS,
                HIDE_EMPTY_ROW_ITEMS,
                HIDE_LEGEND,
                SHOW_HIERARCHY,
                ROW_TOTALS,
                ROW_SUB_TOTALS,
                COL_TOTALS,
                COL_SUB_TOTALS,
                SHOW_DIMENSION_LABELS,
                PERCENT_STACKED_VALUES,
                NO_SPACE_BETWEEN_COLUMNS,
                SKIP_ROUNDING,
                DISPLAY_DENSITY,
                DIGIT_GROUP_SEPARATOR,
                RELATIVE_PERIODS,
                FILTER_DIMENSIONS,
                ROW_DIMENSIONS,
                COLUMN_DIMENSIONS,
                ORGANISATION_UNIT_LEVELS,
                USER_ORGANISATION_UNIT,
                USER_ORGANISATION_UNIT_CHILDREN,
                USER_ORGANISATION_UNIT_GRAND_CHILDREN,
                ORGANISATION_UNITS,
                PERIODS,
                LEGEND_SHOW_KEY,
                LEGEND_STYLE,
                LEGEND_SET_ID,
                LEGEND_STRATEGY,
                AGGREGATION_TYPE
            )
        }

        companion object {
            const val DESCRIPTION = "description"
            const val DISPLAY_DESCRIPTION = "displayDescription"
            const val DISPLAY_FORM_NAME = "displayFormName"
            const val TITLE = "title"
            const val DISPLAY_TITLE = "displayTitle"
            const val SUBTITLE = "subtitle"
            const val DISPLAY_SUBTITLE = "displaySubtitle"
            const val TYPE = "type"
            const val HIDE_TITLE = "hideTitle"
            const val HIDE_SUBTITLE = "hideSubtitle"
            const val HIDE_EMPTY_COLUMNS = "hideEmptyColumns"
            const val HIDE_EMPTY_ROWS = "hideEmptyRows"
            const val HIDE_EMPTY_ROW_ITEMS = "hideEmptyRowItems"
            const val HIDE_LEGEND = "hideLegend"
            const val SHOW_HIERARCHY = "showHierarchy"
            const val ROW_TOTALS = "rowTotals"
            const val ROW_SUB_TOTALS = "rowSubTotals"
            const val COL_TOTALS = "colTotals"
            const val COL_SUB_TOTALS = "colSubTotals"
            const val SHOW_DIMENSION_LABELS = "showDimensionLabels"
            const val PERCENT_STACKED_VALUES = "percentStackedValues"
            const val NO_SPACE_BETWEEN_COLUMNS = "noSpaceBetweenColumns"
            const val SKIP_ROUNDING = "skipRounding"
            const val DISPLAY_DENSITY = "displayDensity"
            const val DIGIT_GROUP_SEPARATOR = "digitGroupSeparator"
            const val RELATIVE_PERIODS = "relativePeriods"
            const val FILTER_DIMENSIONS = "filterDimensions"
            const val ROW_DIMENSIONS = "rowDimensions"
            const val COLUMN_DIMENSIONS = "columnDimensions"
            const val ORGANISATION_UNIT_LEVELS = "organisationUnitLevels"
            const val USER_ORGANISATION_UNIT = "userOrganisationUnit"
            const val USER_ORGANISATION_UNIT_CHILDREN = "userOrganisationUnitChildren"
            const val USER_ORGANISATION_UNIT_GRAND_CHILDREN = "userOrganisationUnitGrandChildren"
            const val ORGANISATION_UNITS = "organisationUnits"
            const val PERIODS = "periods"
            const val LEGEND_SHOW_KEY = "legendShowKey"
            const val LEGEND_STYLE = "legendStyle"
            const val LEGEND_SET_ID = "legendSetId"
            const val LEGEND_STRATEGY = "legendStrategy"
            const val AGGREGATION_TYPE = "aggregationType"
        }
    }
}
