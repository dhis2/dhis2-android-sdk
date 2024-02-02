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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsOrganisationUnitHelper
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionStore
import org.hisp.dhis.android.core.category.internal.CategoryStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.expressiondimensionitem.internal.ExpressionDimensionItemStore
import org.hisp.dhis.android.core.indicator.internal.IndicatorStore
import org.hisp.dhis.android.core.legendset.internal.LegendStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList")
internal class TrackerLineListServiceMetadataHelper(
    private val categoryStore: CategoryStore,
    private val categoryOptionStore: CategoryOptionStore,
    private val categoryOptionComboStore: CategoryOptionComboStore,
    private val dataElementStore: DataElementStore,
    private val indicatorStore: IndicatorStore,
    private val expressionDimensionItemStore: ExpressionDimensionItemStore,
    private val legendStore: LegendStore,
    private val organisationUnitStore: OrganisationUnitStore,
    private val organisationUnitGroupStore: OrganisationUnitGroupStore,
    private val organisationUnitLevelStore: OrganisationUnitLevelStore,
    private val programStore: ProgramStore,
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore,
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository,
    private val analyticsOrganisationUnitHelper: AnalyticsOrganisationUnitHelper,
    private val parentPeriodGenerator: ParentPeriodGenerator,
    private val periodHelper: PeriodHelper,
) {

    fun getMetadata(params: TrackerLineListParams): Map<String, MetadataItem> {
        val metadata: MutableMap<String, MetadataItem> = mutableMapOf()

        (params.columns + params.filters).forEach { item ->
            metadata += getMetadata(item)
        }

        return metadata
    }

    private fun getMetadata(item: TrackerLineListItem): Map<String, MetadataItem> {
        val metadata: MutableMap<String, MetadataItem> = mutableMapOf()

        if (!metadata.containsKey(item.id)) {
            val metadataItems = when (item) {
                is TrackerLineListItem.ProgramAttribute -> getProgramAttributeItems(item)
                else -> emptyList()
            }
            val metadataItemsMap = metadataItems.associateBy { it.id }

            metadata += metadataItemsMap
        }

        return metadata
    }

    private fun getProgramAttributeItems(item: TrackerLineListItem.ProgramAttribute): List<MetadataItem> {
        val attribute = trackedEntityAttributeStore.selectByUid(item.uid)
            ?: throw AnalyticsException.InvalidTrackedEntityAttribute(item.uid)

        return listOf(
            MetadataItem.TrackedEntityAttributeItem(attribute)
        )
    }

    @SuppressWarnings("ThrowsCount", "ComplexMethod")
    private fun getDataItems(item: DimensionItem.DataItem): List<MetadataItem> {
        return listOf(
            when (item) {
                is DimensionItem.DataItem.DataElementItem ->
                    dataElementStore.selectByUid(item.uid)
                        ?.let { dataElement -> MetadataItem.DataElementItem(dataElement) }
                        ?: throw AnalyticsException.InvalidDataElement(item.uid)

                is DimensionItem.DataItem.DataElementOperandItem -> {
                    val dataElement = dataElementStore.selectByUid(item.dataElement)
                    val coc = categoryOptionComboStore.selectByUid(item.categoryOptionCombo)
                    if (dataElement == null || coc == null) {
                        throw AnalyticsException.InvalidDataElementOperand(item.id)
                    }
                    val dataElementOperand = DataElementOperand.builder()
                        .uid("${item.dataElement}.${item.categoryOptionCombo}")
                        .dataElement(ObjectWithUid.create(item.dataElement))
                        .categoryOptionCombo(ObjectWithUid.create(item.categoryOptionCombo))
                        .build()

                    MetadataItem.DataElementOperandItem(
                        dataElementOperand,
                        dataElement.displayName()!!,
                        coc.displayName(),
                    )
                }

                is DimensionItem.DataItem.IndicatorItem ->
                    indicatorStore.selectByUid(item.uid)
                        ?.let { indicator -> MetadataItem.IndicatorItem(indicator) }
                        ?: throw AnalyticsException.InvalidIndicator(item.uid)

                is DimensionItem.DataItem.ProgramIndicatorItem ->
                    programIndicatorRepository.withAnalyticsPeriodBoundaries().uid(item.uid).blockingGet()
                        ?.let { programIndicator -> MetadataItem.ProgramIndicatorItem(programIndicator) }
                        ?: throw AnalyticsException.InvalidProgramIndicator(item.uid)

                is DimensionItem.DataItem.EventDataItem.DataElement -> {
                    val dataElement = dataElementStore.selectByUid(item.dataElement)
                        ?: throw AnalyticsException.InvalidDataElement(item.id)
                    val program = programStore.selectByUid(item.program)
                        ?: throw AnalyticsException.InvalidProgram(item.id)

                    MetadataItem.EventDataElementItem(dataElement, program)
                }

                is DimensionItem.DataItem.EventDataItem.Attribute -> {
                    val attribute = trackedEntityAttributeStore.selectByUid(item.attribute)
                        ?: throw AnalyticsException.InvalidTrackedEntityAttribute(item.id)
                    val program = programStore.selectByUid(item.program)
                        ?: throw AnalyticsException.InvalidProgram(item.id)

                    MetadataItem.EventAttributeItem(attribute, program)
                }

                is DimensionItem.DataItem.ExpressionDimensionItem -> {
                    val expressionItem = expressionDimensionItemStore.selectByUid(item.uid)
                        ?: throw AnalyticsException.InvalidExpressionDimensionItem(item.uid)

                    MetadataItem.ExpressionDimensionItemItem(expressionItem)
                }
            },
        )
    }

    private fun getPeriodItems(item: DimensionItem.PeriodItem): List<MetadataItem> {
        return listOf(
            when (item) {
                is DimensionItem.PeriodItem.Absolute -> {
                    val period = periodHelper.blockingGetPeriodForPeriodId(item.periodId)
                    MetadataItem.PeriodItem(period)
                }

                is DimensionItem.PeriodItem.Relative -> {
                    val periods = parentPeriodGenerator.generateRelativePeriods(item.relative)
                    MetadataItem.RelativePeriodItem(item.relative, periods)
                }
            },
        )
    }

    @SuppressWarnings("ThrowsCount")
    private fun getOrganisationUnitItems(item: DimensionItem.OrganisationUnitItem): List<MetadataItem> {
        return listOf(
            when (item) {
                is DimensionItem.OrganisationUnitItem.Absolute ->
                    organisationUnitStore.selectByUid(item.uid)
                        ?.let { organisationUnit -> MetadataItem.OrganisationUnitItem(organisationUnit) }
                        ?: throw AnalyticsException.InvalidOrganisationUnit(item.uid)

                is DimensionItem.OrganisationUnitItem.Relative -> {
                    val ouUids = analyticsOrganisationUnitHelper.getRelativeOrganisationUnitUids(item.relative)
                    MetadataItem.OrganisationUnitRelativeItem(item.relative, ouUids)
                }

                is DimensionItem.OrganisationUnitItem.Level -> {
                    organisationUnitLevelStore.selectByUid(item.uid)?.let { level ->
                        val ouUids = analyticsOrganisationUnitHelper.getOrganisationUnitUidsByLevel(level.level()!!)
                        MetadataItem.OrganisationUnitLevelItem(level, ouUids)
                    } ?: throw AnalyticsException.InvalidOrganisationUnitLevel(item.uid)
                }

                is DimensionItem.OrganisationUnitItem.Group ->
                    organisationUnitGroupStore.selectByUid(item.uid)?.let { group ->
                        val ouUids = analyticsOrganisationUnitHelper.getOrganisationUnitUidsByGroup(item.uid)
                        MetadataItem.OrganisationUnitGroupItem(group, ouUids)
                    } ?: throw AnalyticsException.InvalidOrganisationUnitGroup(item.uid)
            },
        )
    }

    private fun getCategoryItems(item: DimensionItem.CategoryItem): List<MetadataItem> {
        return listOf(
            categoryStore.selectByUid(item.uid)
                ?.let { category -> MetadataItem.CategoryItem(category) }
                ?: throw AnalyticsException.InvalidCategory(item.uid),

            categoryOptionStore.selectByUid(item.categoryOption)
                ?.let { categoryOption -> MetadataItem.CategoryOptionItem(categoryOption) }
                ?: throw AnalyticsException.InvalidCategoryOption(item.categoryOption),
        )
    }
}
