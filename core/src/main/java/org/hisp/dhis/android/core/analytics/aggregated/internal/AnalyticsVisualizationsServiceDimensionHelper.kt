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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.GridDimension
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLink
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit.*
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevelTableInfo
import org.hisp.dhis.android.core.visualization.DataDimensionItemType
import org.hisp.dhis.android.core.visualization.Visualization

internal class AnalyticsVisualizationsServiceDimensionHelper @Inject constructor(
    private val categoryStore: IdentifiableObjectStore<Category>,
    private val categoryOptionLinkStore: LinkStore<CategoryCategoryOptionLink>,
    private val organisationUnitLevelStore: IdentifiableObjectStore<OrganisationUnitLevel>
) {
    private val dataDimension = "dx"
    private val orgUnitDimension = "ou"
    private val periodDimension = "pe"
    private val uidRegex = "^\\w{11}\$".toRegex()
    private val dataElementOperandRegex = "^(\\w{11})\\.(\\w{11})\$".toRegex()

    fun getDimensionItems(visualization: Visualization, dimensions: List<String>?): List<DimensionItem> {
        return dimensions?.map { dimension ->
            when (dimension) {
                dataDimension -> extractDataDimensionItems(visualization)
                orgUnitDimension -> extractOrgunitDimensionItems(visualization)
                periodDimension -> extractPeriodDimensionItems(visualization)
                else -> {
                    if (uidRegex.matches(dimension)) {
                        extractUidDimensionItems(visualization, dimension)
                    } else {
                        emptyList()
                    }
                }
            }
        }?.flatten() ?: emptyList()
    }

    @Suppress("ComplexMethod")
    private fun extractDataDimensionItems(visualization: Visualization): List<DimensionItem> {
        return visualization.dataDimensionItems()?.mapNotNull { item ->
            when (item.dataDimensionItemType()) {
                DataDimensionItemType.INDICATOR ->
                    item.indicator()?.let { DimensionItem.DataItem.IndicatorItem(it.uid()) }
                DataDimensionItemType.DATA_ELEMENT ->
                    item.dataElement()?.let { DimensionItem.DataItem.DataElementItem(it.uid()) }
                DataDimensionItemType.DATA_ELEMENT_OPERAND ->
                    item.dataElementOperand()?.let {
                        val (dataElement, coc) = dataElementOperandRegex.find(it.uid())!!.destructured
                        DimensionItem.DataItem.DataElementOperandItem(dataElement, coc)
                    }
                DataDimensionItemType.PROGRAM_INDICATOR ->
                    item.programIndicator()?.let { DimensionItem.DataItem.ProgramIndicatorItem(it.uid()) }
                DataDimensionItemType.PROGRAM_DATA_ELEMENT ->
                    item.programDataElement()?.let {
                        it.program()?.uid()?.let { program ->
                            it.dataElement()?.uid()?.let { dataElement ->
                                DimensionItem.DataItem.EventDataItem.DataElement(program, dataElement)
                            }
                        }
                    }
                DataDimensionItemType.PROGRAM_ATTRIBUTE ->
                    item.programAttribute()?.let {
                        it.program()?.uid()?.let { program ->
                            it.attribute()?.uid()?.let { attribute ->
                                DimensionItem.DataItem.EventDataItem.Attribute(program, attribute)
                            }
                        }
                    }
                else ->
                    null
            }
        } ?: emptyList()
    }

    private fun extractOrgunitDimensionItems(visualization: Visualization): List<DimensionItem> {
        val absolute = visualization.organisationUnits()?.map {
            DimensionItem.OrganisationUnitItem.Absolute(it.uid())
        } ?: emptyList()

        val levels = visualization.organisationUnitLevels()?.map {
            val level = organisationUnitLevelStore.selectOneWhere(
                WhereClauseBuilder()
                    .appendKeyNumberValue(OrganisationUnitLevelTableInfo.Columns.LEVEL, it)
                    .build()
            ) ?: throw AnalyticsException.InvalidOrganisationUnitLevel(it.toString())
            DimensionItem.OrganisationUnitItem.Level(level.uid())
        } ?: emptyList()

        val relative = listOfNotNull(
            if (visualization.userOrganisationUnit() == true) USER_ORGUNIT else null,
            if (visualization.userOrganisationUnitChildren() == true) USER_ORGUNIT_CHILDREN else null,
            if (visualization.userOrganisationUnitGrandChildren() == true) USER_ORGUNIT_GRANDCHILDREN else null
        ).map { item ->
            DimensionItem.OrganisationUnitItem.Relative(item)
        }

        return absolute + levels + relative
    }

    private fun extractPeriodDimensionItems(visualization: Visualization): List<DimensionItem> {
        val absolute = visualization.periods()?.map {
            DimensionItem.PeriodItem.Absolute(it.uid())
        } ?: emptyList()

        val relative = visualization.relativePeriods()
            ?.filter { it.value == true }
            ?.map {
                DimensionItem.PeriodItem.Relative(it.key)
            } ?: emptyList()

        return absolute + relative
    }

    private fun extractUidDimensionItems(visualization: Visualization, uid: String): List<DimensionItem> {
        return categoryStore.selectByUid(uid)?.let {
            visualization.categoryDimensions()
                ?.find { it.category()?.uid() == uid }
                ?.let { categoryDimension ->
                    val categoryOptions =
                        if (categoryDimension.categoryOptions().isNullOrEmpty()) {
                            categoryOptionLinkStore.selectLinksForMasterUid(uid).mapNotNull { it.categoryOption() }
                        } else {
                            categoryDimension.categoryOptions()!!.map { it.uid() }
                        }

                    categoryOptions.map { DimensionItem.CategoryItem(uid, it) }
                }
        } ?: emptyList()
    }

    fun getGridDimensions(visualization: Visualization): GridDimension {
        val columns = mapDimensions(visualization.columnDimensions())
        val rows = mapDimensions(visualization.rowDimensions())

        return GridDimension(columns, rows)
    }

    private fun mapDimensions(dimensionStrs: List<String>?): List<Dimension> {
        return dimensionStrs?.mapNotNull {
            when (it) {
                dataDimension -> Dimension.Data
                periodDimension -> Dimension.Period
                orgUnitDimension -> Dimension.OrganisationUnit
                else -> {
                    if (uidRegex.matches(it)) {
                        extractUidDimension(it)
                    } else {
                        null
                    }
                }
            }
        } ?: emptyList()
    }

    private fun extractUidDimension(uid: String): Dimension? {
        val category = categoryStore.selectByUid(uid)

        return if (category != null) {
            Dimension.Category(uid)
        } else {
            null
        }
    }
}
