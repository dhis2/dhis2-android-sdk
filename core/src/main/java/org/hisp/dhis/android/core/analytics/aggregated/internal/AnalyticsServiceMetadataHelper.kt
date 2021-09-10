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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevelTableInfo
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.program.ProgramIndicator

@Suppress("LongParameterList")
internal class AnalyticsServiceMetadataHelper @Inject constructor(
    private val categoryStore: IdentifiableObjectStore<Category>,
    private val categoryOptionStore: IdentifiableObjectStore<CategoryOption>,
    private val dataElementStore: IdentifiableObjectStore<DataElement>,
    private val dataElementOperandStore: IdentifiableObjectStore<DataElementOperand>,
    private val indicatorStore: IdentifiableObjectStore<Indicator>,
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit>,
    private val organisationUnitGroupStore: IdentifiableObjectStore<OrganisationUnitGroup>,
    private val organisationUnitLevelStore: IdentifiableObjectStore<OrganisationUnitLevel>,
    private val programIndicatorStore: IdentifiableObjectStore<ProgramIndicator>,
    private val analyticsOrganisationUnitHelper: AnalyticsOrganisationUnitHelper,
    private val parentPeriodGenerator: ParentPeriodGenerator,
    private val periodHelper: PeriodHelper
) {

    fun getMetadata(evaluationItems: List<AnalyticsServiceEvaluationItem>): Map<String, MetadataItem> {
        val metadata: MutableMap<String, MetadataItem> = mutableMapOf()

        evaluationItems.forEach { evaluationItem ->
            metadata += getMetadata(evaluationItem)
        }

        return metadata
    }

    private fun getMetadata(evaluationItem: AnalyticsServiceEvaluationItem): Map<String, MetadataItem> {
        val metadata: MutableMap<String, MetadataItem> = mutableMapOf()

        (evaluationItem.dimensionItems + evaluationItem.filters)
            .map { it as DimensionItem }
            .forEach { item ->
                if (!metadata.containsKey(item.id)) {
                    val metadataItems = getMetadataItems(item).map { it.id to it }.toMap()
                    metadata += metadataItems
                }
            }

        return metadata
    }

    private fun getMetadataItems(item: DimensionItem): List<MetadataItem> {
        return when (item) {
            is DimensionItem.DataItem -> listOf(
                when (item) {
                    is DimensionItem.DataItem.DataElementItem ->
                        dataElementStore.selectByUid(item.uid)!!
                            .let { dataElement -> MetadataItem.DataElementItem(dataElement) }
                    // TODO Build a meaningful name for DataElementOperand
                    is DimensionItem.DataItem.DataElementOperandItem ->
                        dataElementOperandStore.selectByUid(item.id)!!
                            .let { dataElementOperand -> MetadataItem.DataElementOperandItem(dataElementOperand) }
                    is DimensionItem.DataItem.IndicatorItem ->
                        indicatorStore.selectByUid(item.uid)!!
                            .let { indicator -> MetadataItem.IndicatorItem(indicator) }
                    is DimensionItem.DataItem.ProgramIndicatorItem ->
                        programIndicatorStore.selectByUid(item.uid)!!
                            .let { programIndicator -> MetadataItem.ProgramIndicatorItem(programIndicator) }
                }
            )

            is DimensionItem.PeriodItem -> listOf(
                when (item) {
                    is DimensionItem.PeriodItem.Absolute -> {
                        val period = periodHelper.blockingGetPeriodForPeriodId(item.periodId)
                        MetadataItem.PeriodItem(period)
                    }
                    is DimensionItem.PeriodItem.Relative -> {
                        val periods = parentPeriodGenerator.generateRelativePeriods(item.relative)
                        MetadataItem.RelativePeriodItem(item.relative, periods)
                    }
                }
            )

            is DimensionItem.OrganisationUnitItem -> listOf(
                when (item) {
                    is DimensionItem.OrganisationUnitItem.Absolute ->
                        organisationUnitStore.selectByUid(item.uid)!!
                            .let { organisationUnit -> MetadataItem.OrganisationUnitItem(organisationUnit) }
                    is DimensionItem.OrganisationUnitItem.Relative -> {
                        val orgunitUids = analyticsOrganisationUnitHelper.getRelativeOrganisationUnits(item.relative)
                        MetadataItem.OrganisationUnitRelativeItem(item.relative, orgunitUids)
                    }
                    is DimensionItem.OrganisationUnitItem.Level -> {
                        val levelClauseBuilder = WhereClauseBuilder()
                            .appendKeyNumberValue(OrganisationUnitLevelTableInfo.Columns.LEVEL, item.level)
                            .build()
                        organisationUnitLevelStore.selectOneWhere(levelClauseBuilder)!!
                            .let { level -> MetadataItem.OrganisationUnitLevelItem(level) }
                    }
                    is DimensionItem.OrganisationUnitItem.Group ->
                        organisationUnitGroupStore.selectByUid(item.uid)!!
                            .let { group -> MetadataItem.OrganisationUnitGroupItem(group) }
                }
            )

            is DimensionItem.CategoryItem -> listOf(
                categoryStore.selectByUid(item.uid)!!
                    .let { category -> MetadataItem.CategoryItem(category) },
                categoryOptionStore.selectByUid(item.categoryOption)!!
                    .let { categoryOption -> MetadataItem.CategoryOptionItem(categoryOption) }
            )
        }
    }
}
