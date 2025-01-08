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

package org.hisp.dhis.android.network.visualization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.visualization.DigitGroupSeparator
import org.hisp.dhis.android.core.visualization.DisplayDensity
import org.hisp.dhis.android.core.visualization.HideEmptyItemStrategy
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.LegendStrategy
import org.hisp.dhis.android.core.visualization.LegendStyle
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationLegend
import org.hisp.dhis.android.core.visualization.VisualizationType
import org.hisp.dhis.android.network.common.dto.BaseIdentifiableObjectDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO
import org.hisp.dhis.android.network.common.dto.applyBaseIdentifiableFields

@Serializable
internal data class VisualizationAPI36(
    @SerialName("id") override val uid: String,
    override val code: String? = BaseIdentifiableObjectDTO.CODE,
    override val name: String? = BaseIdentifiableObjectDTO.NAME,
    override val displayName: String? = BaseIdentifiableObjectDTO.DISPLAY_NAME,
    override val created: String? = BaseIdentifiableObjectDTO.CREATED,
    override val lastUpdated: String? = BaseIdentifiableObjectDTO.LAST_UPDATED,
    override val deleted: Boolean? = BaseIdentifiableObjectDTO.DELETED,
    val description: String?,
    val displayDescription: String?,
    val displayFormName: String?,
    val title: String?,
    val displayTitle: String?,
    val subtitle: String?,
    val displaySubtitle: String?,
    val type: String?,
    val hideTitle: Boolean?,
    val hideSubtitle: Boolean?,
    val hideEmptyColumns: Boolean?,
    val hideEmptyRows: Boolean?,
    val hideEmptyRowItems: String?,
    val hideLegend: Boolean?,
    val showHierarchy: Boolean?,
    val rowTotals: Boolean?,
    val rowSubTotals: Boolean?,
    val colTotals: Boolean?,
    val colSubTotals: Boolean?,
    val showDimensionLabels: Boolean?,
    val percentStackedValues: Boolean?,
    val noSpaceBetweenColumns: Boolean?,
    val skipRounding: Boolean?,
    val displayDensity: String?,
    val digitGroupSeparator: String?,
    val legendSet: ObjectWithUidDTO?,
    val legendDisplayStyle: String?,
    val legendDisplayStrategy: String?,
    val aggregationType: String?,
    val columns: List<VisualizationDimensionDTO>,
    val rows: List<VisualizationDimensionDTO>,
    val filters: List<VisualizationDimensionDTO>,
) : BaseIdentifiableObjectDTO {
    fun toDomain(): Visualization =
        Visualization.builder()
            .applyBaseIdentifiableFields(this)
            .description(description)
            .displayDescription(displayDescription)
            .displayFormName(displayFormName)
            .title(title)
            .displayTitle(displayTitle)
            .subtitle(subtitle)
            .displaySubtitle(displaySubtitle)
            .type(type?.let { VisualizationType.valueOf(it) })
            .hideTitle(hideTitle)
            .hideSubtitle(hideSubtitle)
            .hideEmptyColumns(hideEmptyColumns)
            .hideEmptyRows(hideEmptyRows)
            .hideEmptyRowItems(hideEmptyRowItems?.let { HideEmptyItemStrategy.valueOf(it) })
            .hideLegend(hideLegend)
            .showHierarchy(showHierarchy)
            .rowTotals(rowTotals)
            .rowSubTotals(rowSubTotals)
            .colTotals(colTotals)
            .colSubTotals(colSubTotals)
            .showDimensionLabels(showDimensionLabels)
            .percentStackedValues(percentStackedValues)
            .noSpaceBetweenColumns(noSpaceBetweenColumns)
            .skipRounding(skipRounding)
            .displayDensity(displayDensity?.let { DisplayDensity.valueOf(it) })
            .digitGroupSeparator(digitGroupSeparator?.let { DigitGroupSeparator.valueOf(it) })
            .aggregationType(aggregationType?.let { AggregationType.valueOf(it) })
            .legend(
                VisualizationLegend.builder()
                    .set(legendSet?.toDomain())
                    .style(legendDisplayStyle?.let { LegendStyle.valueOf(it) })
                    .strategy(legendDisplayStrategy?.let { LegendStrategy.valueOf(it) })
                    .showKey(false)
                    .build(),
            )
            .columns(columns.map { it.toDomain(uid, LayoutPosition.COLUMN) })
            .rows(rows.map { it.toDomain(uid, LayoutPosition.ROW) })
            .filters(filters.map { it.toDomain(uid, LayoutPosition.FILTER) })
            .build()
}
