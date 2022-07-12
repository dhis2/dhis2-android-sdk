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
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.legendset.Legend
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute

@Suppress("LongParameterList")
internal class AnalyticsServiceMetadataHelper @Inject constructor(
    private val categoryStore: IdentifiableObjectStore<Category>,
    private val categoryOptionStore: IdentifiableObjectStore<CategoryOption>,
    private val categoryOptionComboStore: CategoryOptionComboStore,
    private val dataElementStore: IdentifiableObjectStore<DataElement>,
    private val indicatorStore: IdentifiableObjectStore<Indicator>,
    private val legendStore: IdentifiableObjectStore<Legend>,
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit>,
    private val organisationUnitGroupStore: IdentifiableObjectStore<OrganisationUnitGroup>,
    private val organisationUnitLevelStore: IdentifiableObjectStore<OrganisationUnitLevel>,
    private val programStore: ProgramStoreInterface,
    private val trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>,
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository,
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

    fun includeLegendsToMetadata(
        metadata: Map<String, MetadataItem>,
        legendsUids: List<String>
    ): Map<String, MetadataItem> {
        val finalMetadata = metadata.toMutableMap()
        val legends = legendStore.selectByUids(legendsUids.distinct()).map { MetadataItem.LegendItem(it) }
        val metadataItemsMap = legends.map { it.id to it }.toMap()
        finalMetadata += metadataItemsMap
        return finalMetadata
    }

    private fun getMetadata(evaluationItem: AnalyticsServiceEvaluationItem): Map<String, MetadataItem> {
        val metadata: MutableMap<String, MetadataItem> = mutableMapOf()

        evaluationItem.allDimensionItems
            .forEach { item ->
                if (!metadata.containsKey(item.id)) {
                    val metadataItems = when (item) {
                        is DimensionItem.DataItem -> getDataItems(item)
                        is DimensionItem.PeriodItem -> getPeriodItems(item)
                        is DimensionItem.OrganisationUnitItem -> getOrganisationUnitItems(item)
                        is DimensionItem.CategoryItem -> getCategoryItems(item)
                    }
                    val metadataItemsMap = metadataItems.associateBy { it.id }

                    metadata += metadataItemsMap
                }
            }

        return metadata
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
                        coc.displayName()
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
            }
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
            }
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
            }
        )
    }

    private fun getCategoryItems(item: DimensionItem.CategoryItem): List<MetadataItem> {
        return listOf(
            categoryStore.selectByUid(item.uid)
                ?.let { category -> MetadataItem.CategoryItem(category) }
                ?: throw AnalyticsException.InvalidCategory(item.uid),

            categoryOptionStore.selectByUid(item.categoryOption)
                ?.let { categoryOption -> MetadataItem.CategoryOptionItem(categoryOption) }
                ?: throw AnalyticsException.InvalidCategoryOption(item.categoryOption)
        )
    }
}
