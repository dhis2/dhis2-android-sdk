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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.category
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryCategoryComboLink
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryCategoryOptionLink
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOption
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOptionComboCategoryOptionLink
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.constant1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement3
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement4
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElementOperand
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.level1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.level2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.organisationUnitGroup
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitParent
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.periodDec
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.periodNov
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.periodQ4
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.category.internal.*
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.constant.internal.ConstantStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.indicator.internal.IndicatorStore
import org.hisp.dhis.android.core.indicator.internal.IndicatorTypeStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.period.internal.PeriodStoreImpl
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.junit.After
import org.junit.Before

internal open class BaseEvaluatorIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    // Stores
    protected val dataValueStore = DataValueStore.create(databaseAdapter)
    protected val categoryStore = CategoryStore.create(databaseAdapter)
    protected val categoryOptionStore = CategoryOptionStore.create(databaseAdapter)
    protected val categoryCategoryOptionStore = CategoryCategoryOptionLinkStore.create(databaseAdapter)
    protected val categoryComboStore = CategoryComboStore.create(databaseAdapter)
    protected val categoryOptionComboStore = CategoryOptionComboStoreImpl.create(databaseAdapter)
    protected val categoryCategoryComboLinkStore = CategoryCategoryComboLinkStore.create(databaseAdapter)
    protected val categoryOptionComboCategoryOptionLinkStore = CategoryOptionComboCategoryOptionLinkStore.create(
        databaseAdapter
    )
    protected val dataElementStore = DataElementStore.create(databaseAdapter)
    protected val organisationUnitStore = OrganisationUnitStore.create(databaseAdapter)
    protected val organisationUnitLevelStore = OrganisationUnitLevelStore.create(databaseAdapter)
    protected val organisationUnitGroupStore = OrganisationUnitGroupStore.create(databaseAdapter)
    protected val periodStore = PeriodStoreImpl.create(databaseAdapter)

    protected val trackedEntityTypeStore = TrackedEntityTypeStore.create(databaseAdapter)
    protected val trackedEntityAttributeStore = TrackedEntityAttributeStore.create(databaseAdapter)
    protected val programStore = ProgramStore.create(databaseAdapter)
    protected val programStageStore = ProgramStageStore.create(databaseAdapter)

    protected val indicatorTypeStore = IndicatorTypeStore.create(databaseAdapter)
    protected val indicatorStore = IndicatorStore.create(databaseAdapter)

    protected val constantStore = ConstantStore.create(databaseAdapter)

    protected val metadata: Map<String, MetadataItem> = mapOf(
        orgunitParent.uid() to MetadataItem.OrganisationUnitItem(orgunitParent),
        orgunitChild1.uid() to MetadataItem.OrganisationUnitItem(orgunitChild1),
        orgunitChild2.uid() to MetadataItem.OrganisationUnitItem(orgunitChild2),
        level1.uid() to MetadataItem.OrganisationUnitLevelItem(level1, listOf(orgunitParent.uid())),
        level2.uid() to MetadataItem.OrganisationUnitLevelItem(
            level2,
            listOf(orgunitChild1.uid(), orgunitChild2.uid())
        ),
        organisationUnitGroup.uid() to MetadataItem.OrganisationUnitGroupItem(
            organisationUnitGroup,
            listOf(orgunitParent.uid())
        ),
        dataElement1.uid() to MetadataItem.DataElementItem(dataElement1),
        dataElement2.uid() to MetadataItem.DataElementItem(dataElement2),
        dataElementOperand.uid()!! to MetadataItem.DataElementOperandItem(
            dataElementOperand,
            dataElement1.displayName()!!,
            categoryOptionCombo.displayName()
        ),
        periodNov.periodId()!! to MetadataItem.PeriodItem(periodNov),
        periodDec.periodId()!! to MetadataItem.PeriodItem(periodDec),
        periodQ4.periodId()!! to MetadataItem.PeriodItem(periodQ4),
        RelativeOrganisationUnit.USER_ORGUNIT.name to MetadataItem.OrganisationUnitRelativeItem(
            RelativeOrganisationUnit.USER_ORGUNIT,
            listOf(orgunitParent.uid())
        ),
        RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN.name to MetadataItem.OrganisationUnitRelativeItem(
            RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN,
            listOf(orgunitChild1.uid(), orgunitChild2.uid())
        ),
        RelativePeriod.THIS_MONTH.name to MetadataItem.RelativePeriodItem(
            RelativePeriod.THIS_MONTH,
            listOf(periodDec)
        ),
        RelativePeriod.LAST_MONTH.name to MetadataItem.RelativePeriodItem(
            RelativePeriod.LAST_MONTH,
            listOf(periodNov)
        )
    )

    @Before
    fun setUpBase() {
        organisationUnitLevelStore.insert(level1)
        organisationUnitLevelStore.insert(level2)

        organisationUnitStore.insert(orgunitParent)
        organisationUnitStore.insert(orgunitChild1)
        organisationUnitStore.insert(orgunitChild2)

        organisationUnitGroupStore.insert(organisationUnitGroup)

        categoryStore.insert(category)
        categoryOptionStore.insert(categoryOption)
        categoryCategoryOptionStore.insert(categoryCategoryOptionLink)
        categoryComboStore.insert(categoryCombo)
        categoryOptionComboStore.insert(categoryOptionCombo)
        categoryCategoryComboLinkStore.insert(categoryCategoryComboLink)
        categoryOptionComboCategoryOptionLinkStore.insert(categoryOptionComboCategoryOptionLink)

        dataElementStore.insert(dataElement1)
        dataElementStore.insert(dataElement2)
        dataElementStore.insert(dataElement3)
        dataElementStore.insert(dataElement4)

        periodStore.insert(periodNov)
        periodStore.insert(periodDec)
        periodStore.insert(periodQ4)

        trackedEntityTypeStore.insert(trackedEntityType)
        trackedEntityAttributeStore.insert(attribute1)
        programStore.insert(program)
        programStageStore.insert(programStage1)
        programStageStore.insert(programStage2)

        constantStore.insert(constant1)
    }

    @After
    fun tearDown() {
        organisationUnitLevelStore.delete()
        organisationUnitStore.delete()
        organisationUnitGroupStore.delete()
        categoryStore.delete()
        categoryOptionStore.delete()
        categoryCategoryOptionStore.delete()
        categoryComboStore.delete()
        categoryOptionComboStore.delete()
        categoryCategoryComboLinkStore.delete()
        categoryOptionComboCategoryOptionLinkStore.delete()
        dataElementStore.delete()
        periodStore.delete()
        dataValueStore.delete()
        trackedEntityTypeStore.delete()
        trackedEntityAttributeStore.delete()
        programStageStore.delete()
        programStore.delete()
        indicatorTypeStore.delete()
        indicatorStore.delete()
        constantStore.delete()
    }
}
