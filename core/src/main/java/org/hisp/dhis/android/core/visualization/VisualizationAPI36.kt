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

import java.util.*
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativePeriod

internal data class VisualizationAPI36(
    val id: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: Date?,
    val lastUpdated: Date?,
    val deleted: Boolean?,
    val description: String?,
    val displayDescription: String?,
    val displayFormName: String?,
    val title: String?,
    val displayTitle: String?,
    val subtitle: String?,
    val displaySubtitle: String?,
    val type: VisualizationType?,
    val hideTitle: Boolean?,
    val hideSubtitle: Boolean?,
    val hideEmptyColumns: Boolean?,
    val hideEmptyRows: Boolean?,
    val hideEmptyRowItems: HideEmptyItemStrategy?,
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
    val displayDensity: DisplayDensity?,
    val digitGroupSeparator: DigitGroupSeparator?,
    val relativePeriods: Map<RelativePeriod, Boolean>?,
    val categoryDimensions: List<CategoryDimension>?,
    val filterDimensions: List<String>?,
    val rowDimensions: List<String>?,
    val columnDimensions: List<String>?,
    val dataDimensionItems: List<DataDimensionItem>?,
    val organisationUnitLevels: List<Int>?,
    val userOrganisationUnit: Boolean?,
    val userOrganisationUnitChildren: Boolean?,
    val userOrganisationUnitGrandChildren: Boolean?,
    val organisationUnits: List<ObjectWithUid>?,
    val periods: List<ObjectWithUid>?,
    val legendSet: ObjectWithUid?,
    val legendDisplayStyle: LegendStyle?,
    val legendDisplayStrategy: LegendStrategy?,
    val aggregationType: AggregationType?
) {
    fun toVisualization(): Visualization =
        Visualization.builder()
            .uid(id)
            .code(code)
            .name(name)
            .displayName(displayName)
            .created(created)
            .lastUpdated(lastUpdated)
            .deleted(deleted)
            .description(description)
            .displayDescription(displayDescription)
            .displayFormName(displayFormName)
            .title(title)
            .displayTitle(displayTitle)
            .subtitle(subtitle)
            .displaySubtitle(displaySubtitle)
            .type(type)
            .hideTitle(hideTitle)
            .hideSubtitle(hideSubtitle)
            .hideEmptyColumns(hideEmptyColumns)
            .hideEmptyRows(hideEmptyRows)
            .hideEmptyRowItems(hideEmptyRowItems)
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
            .displayDensity(displayDensity)
            .digitGroupSeparator(digitGroupSeparator)
            .relativePeriods(relativePeriods)
            .categoryDimensions(categoryDimensions)
            .filterDimensions(filterDimensions)
            .rowDimensions(rowDimensions)
            .columnDimensions(columnDimensions)
            .dataDimensionItems(dataDimensionItems)
            .organisationUnitLevels(organisationUnitLevels)
            .userOrganisationUnit(userOrganisationUnit)
            .userOrganisationUnitChildren(userOrganisationUnitChildren)
            .userOrganisationUnitGrandChildren(userOrganisationUnitGrandChildren)
            .organisationUnits(organisationUnits)
            .periods(periods)
            .aggregationType(aggregationType)
            .legend(
                VisualizationLegend.builder()
                    .set(legendSet)
                    .style(legendDisplayStyle)
                    .strategy(legendDisplayStrategy)
                    .showKey(false)
                    .build()
            )
            .build()
}
