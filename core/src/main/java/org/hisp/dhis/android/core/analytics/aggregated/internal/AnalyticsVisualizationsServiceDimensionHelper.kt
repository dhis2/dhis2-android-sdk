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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.GridDimension
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.composedUidOperandRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.orgunitLevelRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.tripleComposedUidOperandRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.uidRegex
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.category.internal.CategoryCategoryOptionLinkStore
import org.hisp.dhis.android.core.category.internal.CategoryStore
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevelTableInfo
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.visualization.DataDimensionItemType
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationDimension
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItem
import org.koin.core.annotation.Singleton

@Singleton
internal class AnalyticsVisualizationsServiceDimensionHelper(
    private val categoryStore: CategoryStore,
    private val categoryOptionLinkStore: CategoryCategoryOptionLinkStore,
    private val organisationUnitLevelStore: OrganisationUnitLevelStore,
) {
    private val dataDimension = "dx"
    private val orgUnitDimension = "ou"
    private val periodDimension = "pe"

    fun getDimensionItems(dimensions: List<VisualizationDimension>?): List<DimensionItem> {
        return dimensions?.map { dimension ->
            when (dimension.id()) {
                dataDimension -> extractDataDimensionItems(dimension.items())
                orgUnitDimension -> extractOrgunitDimensionItems(dimension.items())
                periodDimension -> extractPeriodDimensionItems(dimension.items())
                else -> {
                    dimension.id()?.let {
                        if (uidRegex.matches(it)) {
                            extractUidDimensionItems(dimension.items(), it)
                        } else {
                            emptyList()
                        }
                    } ?: emptyList()
                }
            }
        }?.flatten() ?: emptyList()
    }

    @Suppress("ComplexMethod")
    private fun extractDataDimensionItems(items: List<VisualizationDimensionItem>?): List<DimensionItem> {
        return items?.mapNotNull { item ->
            val dataType = DataDimensionItemType.values().find { it.name == item.dimensionItemType() }

            when (dataType) {
                DataDimensionItemType.INDICATOR ->
                    item.dimensionItem()?.let { DimensionItem.DataItem.IndicatorItem(it) }

                DataDimensionItemType.DATA_ELEMENT ->
                    item.dimensionItem()?.let { DimensionItem.DataItem.DataElementItem(it) }

                DataDimensionItemType.DATA_ELEMENT_OPERAND ->
                    item.dimensionItem()?.let {
                        val (dataElement, coc) = composedUidOperandRegex.find(it)!!.destructured
                        DimensionItem.DataItem.DataElementOperandItem(dataElement, coc)
                    }

                DataDimensionItemType.PROGRAM_INDICATOR ->
                    item.dimensionItem()?.let { DimensionItem.DataItem.ProgramIndicatorItem(it) }

                DataDimensionItemType.PROGRAM_DATA_ELEMENT ->
                    item.dimensionItem()?.let {
                        val (program, dataElement) = composedUidOperandRegex.find(it)!!.destructured
                        DimensionItem.DataItem.EventDataItem.DataElement(program, dataElement)
                    }

                DataDimensionItemType.PROGRAM_ATTRIBUTE ->
                    item.dimensionItem()?.let {
                        val (program, attribute) = composedUidOperandRegex.find(it)!!.destructured
                        DimensionItem.DataItem.EventDataItem.Attribute(program, attribute)
                    }

                DataDimensionItemType.EXPRESSION_DIMENSION_ITEM ->
                    item.dimensionItem()?.let { DimensionItem.DataItem.ExpressionDimensionItem(it) }

                DataDimensionItemType.PROGRAM_ATTRIBUTE_OPTION ->
                    item.dimensionItem()?.let {
                        val (program, attribute, option) = tripleComposedUidOperandRegex.find(it)!!.destructured
                        DimensionItem.DataItem.EventDataItem.AttributeOption(program, attribute, option)
                    }

                DataDimensionItemType.PROGRAM_DATA_ELEMENT_OPTION ->
                    item.dimensionItem()?.let {
                        val (program, dataElement, option) = tripleComposedUidOperandRegex.find(it)!!.destructured
                        DimensionItem.DataItem.EventDataItem.DataElementOption(program, dataElement, option)
                    }

                else ->
                    null
            }
        } ?: emptyList()
    }

    private fun extractOrgunitDimensionItems(items: List<VisualizationDimensionItem>?): List<DimensionItem> {
        return items?.mapNotNull { it.dimensionItem() }?.map { item ->
            val relativeOrgUnit = RelativeOrganisationUnit.values().find { it.name == item }

            when {
                relativeOrgUnit != null -> {
                    DimensionItem.OrganisationUnitItem.Relative(relativeOrgUnit)
                }

                orgunitLevelRegex.matches(item) -> {
                    val (levelNumber) = orgunitLevelRegex.find(item)!!.destructured
                    val level = organisationUnitLevelStore.selectOneWhere(
                        WhereClauseBuilder()
                            .appendKeyNumberValue(OrganisationUnitLevelTableInfo.Columns.LEVEL, levelNumber.toInt())
                            .build(),
                    ) ?: throw AnalyticsException.InvalidOrganisationUnitLevel(levelNumber)
                    DimensionItem.OrganisationUnitItem.Level(level.uid())
                }

                else -> {
                    DimensionItem.OrganisationUnitItem.Absolute(item)
                }
            }
        } ?: emptyList()
    }

    private fun extractPeriodDimensionItems(items: List<VisualizationDimensionItem>?): List<DimensionItem> {
        return items?.mapNotNull { it.dimensionItem() }?.map { item ->
            val relativePeriod = RelativePeriod.values().find { it.name == item }

            if (relativePeriod != null) {
                DimensionItem.PeriodItem.Relative(relativePeriod)
            } else {
                DimensionItem.PeriodItem.Absolute(item)
            }
        } ?: emptyList()
    }

    private fun extractUidDimensionItems(items: List<VisualizationDimensionItem>?, uid: String): List<DimensionItem> {
        return categoryStore.selectByUid(uid)?.let { category ->
            val categoryOptions =
                if (items.isNullOrEmpty()) {
                    categoryOptionLinkStore.selectLinksForMasterUid(category.uid()).mapNotNull { it.categoryOption() }
                } else {
                    items.mapNotNull { it.dimensionItem() }
                }

            categoryOptions.map { DimensionItem.CategoryItem(category.uid(), it) }
        } ?: emptyList()
    }

    fun getGridDimensions(visualization: Visualization): GridDimension {
        val columns = mapDimensions(visualization.columns())
        val rows = mapDimensions(visualization.rows())

        return GridDimension(columns, rows)
    }

    private fun mapDimensions(dimensionStrs: List<VisualizationDimension>?): List<Dimension> {
        return dimensionStrs?.mapNotNull { dimension ->
            when (dimension.id()) {
                dataDimension -> Dimension.Data
                periodDimension -> Dimension.Period
                orgUnitDimension -> Dimension.OrganisationUnit
                else -> {
                    if (uidRegex.matches(dimension.id()!!)) {
                        extractUidDimension(dimension)
                    } else {
                        null
                    }
                }
            }
        } ?: emptyList()
    }

    private fun extractUidDimension(dimension: VisualizationDimension): Dimension? {
        return if (dimension.items()?.all { it.dimensionItemType() == "CATEGORY_OPTION" } == true) {
            Dimension.Category(dimension.id()!!)
        } else {
            null
        }
    }
}
