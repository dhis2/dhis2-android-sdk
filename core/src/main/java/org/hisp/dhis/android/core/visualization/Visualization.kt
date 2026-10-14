/*
 *  Copyright (c) 2004-2026, University of Oslo
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

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.CoreObject
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class Visualization(
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: Date?,
    override val lastUpdated: Date?,
    override val deleted: Boolean?,
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
    val legend: VisualizationLegend?,
    val displayDensity: DisplayDensity?,
    val digitGroupSeparator: DigitGroupSeparator?,
    val aggregationType: AggregationType?,
    val columns: List<VisualizationDimension>?,
    val rows: List<VisualizationDimension>?,
    val filters: List<VisualizationDimension>?,
) : BaseIdentifiableObject, CoreObject {
    fun description(): String? = description
    fun displayDescription(): String? = displayDescription
    fun displayFormName(): String? = displayFormName
    fun title(): String? = title
    fun displayTitle(): String? = displayTitle
    fun subtitle(): String? = subtitle
    fun displaySubtitle(): String? = displaySubtitle
    fun type(): VisualizationType? = type
    fun hideTitle(): Boolean? = hideTitle
    fun hideSubtitle(): Boolean? = hideSubtitle
    fun hideEmptyColumns(): Boolean? = hideEmptyColumns
    fun hideEmptyRows(): Boolean? = hideEmptyRows
    fun hideEmptyRowItems(): HideEmptyItemStrategy? = hideEmptyRowItems
    fun hideLegend(): Boolean? = hideLegend
    fun showHierarchy(): Boolean? = showHierarchy
    fun rowTotals(): Boolean? = rowTotals
    fun rowSubTotals(): Boolean? = rowSubTotals
    fun colTotals(): Boolean? = colTotals
    fun colSubTotals(): Boolean? = colSubTotals
    fun showDimensionLabels(): Boolean? = showDimensionLabels
    fun percentStackedValues(): Boolean? = percentStackedValues
    fun noSpaceBetweenColumns(): Boolean? = noSpaceBetweenColumns
    fun skipRounding(): Boolean? = skipRounding
    fun legend(): VisualizationLegend? = legend
    fun displayDensity(): DisplayDensity? = displayDensity
    fun digitGroupSeparator(): DigitGroupSeparator? = digitGroupSeparator
    fun aggregationType(): AggregationType? = aggregationType
    fun columns(): List<VisualizationDimension>? = columns
    fun rows(): List<VisualizationDimension>? = rows
    fun filters(): List<VisualizationDimension>? = filters

    fun toBuilder(): Builder = VisualizationBuilder.from(this)

    class Builder : VisualizationBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
