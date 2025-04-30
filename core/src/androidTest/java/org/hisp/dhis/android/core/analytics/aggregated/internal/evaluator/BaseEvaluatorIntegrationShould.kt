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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute3
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeAttributeComboLink
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeAttributeOptionLink
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeOption
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeOptionComboAttributeOptionLink
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
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement5
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElementOperand
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.level1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.level2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.option1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.option2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.optionSet
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.organisationUnitGroup
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitParent
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201910
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201911
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201912
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period2019Q4
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period2019SunW25
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202001
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202012
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.relationshipType
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.relationshipTypeFrom
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.relationshipTypeTo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.category.internal.CategoryCategoryComboLinkStoreImpl
import org.hisp.dhis.android.core.category.internal.CategoryCategoryOptionLinkStoreImpl
import org.hisp.dhis.android.core.category.internal.CategoryComboStoreImpl
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboCategoryOptionLinkStoreImpl
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStoreImpl
import org.hisp.dhis.android.core.category.internal.CategoryOptionStoreImpl
import org.hisp.dhis.android.core.category.internal.CategoryStoreImpl
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.constant.internal.ConstantStoreImpl
import org.hisp.dhis.android.core.dataelement.internal.DataElementStoreImpl
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.DataValueStoreImpl
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.expressiondimensionitem.internal.ExpressionDimensionItemStoreImpl
import org.hisp.dhis.android.core.indicator.internal.IndicatorStoreImpl
import org.hisp.dhis.android.core.indicator.internal.IndicatorTypeStoreImpl
import org.hisp.dhis.android.core.option.internal.OptionSetStoreImpl
import org.hisp.dhis.android.core.option.internal.OptionStoreImpl
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStoreImpl
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStoreImpl
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStoreImpl
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.period.internal.PeriodStoreImpl
import org.hisp.dhis.android.core.program.internal.ProgramStageStoreImpl
import org.hisp.dhis.android.core.program.internal.ProgramStoreImpl
import org.hisp.dhis.android.core.relationship.internal.RelationshipConstraintStoreImpl
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeStoreImpl
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.junit.After
import org.junit.Before

internal open class BaseEvaluatorIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    // Data stores
    protected val dataValueStore = DataValueStoreImpl(databaseAdapter)
    protected val eventStore = EventStoreImpl(databaseAdapter)
    protected val enrollmentStore = EnrollmentStoreImpl(databaseAdapter)
    protected val trackedEntityStore = TrackedEntityInstanceStoreImpl(databaseAdapter)
    protected val trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl(databaseAdapter)
    protected val trackedEntityAttributeValueStore = TrackedEntityAttributeValueStoreImpl(databaseAdapter)

    // Metadata stores
    protected val categoryStore = CategoryStoreImpl(databaseAdapter)
    protected val categoryOptionStore = CategoryOptionStoreImpl(databaseAdapter)
    protected val categoryCategoryOptionStore = CategoryCategoryOptionLinkStoreImpl(databaseAdapter)
    protected val categoryComboStore = CategoryComboStoreImpl(databaseAdapter)
    protected val categoryOptionComboStore = CategoryOptionComboStoreImpl(databaseAdapter)
    protected val categoryCategoryComboLinkStore = CategoryCategoryComboLinkStoreImpl(databaseAdapter)
    protected val categoryOptionComboCategoryOptionLinkStore = CategoryOptionComboCategoryOptionLinkStoreImpl(
        databaseAdapter,
    )
    protected val optionSetStore = OptionSetStoreImpl(databaseAdapter)
    protected val optionStore = OptionStoreImpl(databaseAdapter)
    protected val dataElementStore = DataElementStoreImpl(databaseAdapter)
    protected val organisationUnitStore = OrganisationUnitStoreImpl(databaseAdapter)
    protected val organisationUnitLevelStore = OrganisationUnitLevelStoreImpl(databaseAdapter)
    protected val organisationUnitGroupStore = OrganisationUnitGroupStoreImpl(databaseAdapter)
    protected val periodStore = PeriodStoreImpl(databaseAdapter)

    protected val trackedEntityTypeStore = TrackedEntityTypeStoreImpl(databaseAdapter)
    protected val trackedEntityAttributeStore = TrackedEntityAttributeStoreImpl(databaseAdapter)
    protected val programStore = ProgramStoreImpl(databaseAdapter)
    protected val programStageStore = ProgramStageStoreImpl(databaseAdapter)

    protected val indicatorTypeStore = IndicatorTypeStoreImpl(databaseAdapter)
    protected val indicatorStore = IndicatorStoreImpl(databaseAdapter)

    protected val relationshipTypeStore = RelationshipTypeStoreImpl(databaseAdapter)
    protected val relationshipConstraintStore = RelationshipConstraintStoreImpl(databaseAdapter)

    protected val constantStore = ConstantStoreImpl(databaseAdapter)

    protected val expressionDimensionItemStore = ExpressionDimensionItemStoreImpl(databaseAdapter)

    protected val expressionService = ExpressionService(
        dataElementStore,
        categoryOptionComboStore,
        organisationUnitGroupStore,
        programStageStore,
    )

    protected val metadata: Map<String, MetadataItem> = mapOf(
        orgunitParent.uid() to MetadataItem.OrganisationUnitItem(orgunitParent),
        orgunitChild1.uid() to MetadataItem.OrganisationUnitItem(orgunitChild1),
        orgunitChild2.uid() to MetadataItem.OrganisationUnitItem(orgunitChild2),
        level1.uid() to MetadataItem.OrganisationUnitLevelItem(level1, listOf(orgunitParent.uid())),
        level2.uid() to MetadataItem.OrganisationUnitLevelItem(
            level2,
            listOf(orgunitChild1.uid(), orgunitChild2.uid()),
        ),
        organisationUnitGroup.uid() to MetadataItem.OrganisationUnitGroupItem(
            organisationUnitGroup,
            listOf(orgunitParent.uid()),
        ),
        dataElement1.uid() to MetadataItem.DataElementItem(dataElement1),
        dataElement2.uid() to MetadataItem.DataElementItem(dataElement2),
        dataElementOperand.uid()!! to MetadataItem.DataElementOperandItem(
            dataElementOperand,
            dataElement1.displayName()!!,
            categoryOptionCombo.displayName(),
        ),
        period2019SunW25.periodId()!! to MetadataItem.PeriodItem(period2019SunW25),
        period201910.periodId()!! to MetadataItem.PeriodItem(period201910),
        period201911.periodId()!! to MetadataItem.PeriodItem(period201911),
        period201912.periodId()!! to MetadataItem.PeriodItem(period201912),
        period202001.periodId()!! to MetadataItem.PeriodItem(period202001),
        period202012.periodId()!! to MetadataItem.PeriodItem(period202012),
        period2019Q4.periodId()!! to MetadataItem.PeriodItem(period2019Q4),
        RelativeOrganisationUnit.USER_ORGUNIT.name to MetadataItem.OrganisationUnitRelativeItem(
            RelativeOrganisationUnit.USER_ORGUNIT,
            listOf(orgunitParent.uid()),
        ),
        RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN.name to MetadataItem.OrganisationUnitRelativeItem(
            RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN,
            listOf(orgunitChild1.uid(), orgunitChild2.uid()),
        ),
        RelativePeriod.THIS_MONTH.name to MetadataItem.RelativePeriodItem(
            RelativePeriod.THIS_MONTH,
            listOf(period201912),
        ),
        RelativePeriod.LAST_MONTH.name to MetadataItem.RelativePeriodItem(
            RelativePeriod.LAST_MONTH,
            listOf(period201911),
        ),
        category.uid() to MetadataItem.CategoryItem(category),
        categoryOption.uid() to MetadataItem.CategoryOptionItem(categoryOption),
        attribute.uid() to MetadataItem.CategoryItem(attribute),
        attributeOption.uid() to MetadataItem.CategoryOptionItem(attributeOption),
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

        categoryStore.insert(attribute)
        categoryOptionStore.insert(attributeOption)
        categoryCategoryOptionStore.insert(attributeAttributeOptionLink)
        categoryComboStore.insert(attributeCombo)
        categoryOptionComboStore.insert(attributeOptionCombo)
        categoryCategoryComboLinkStore.insert(attributeAttributeComboLink)
        categoryOptionComboCategoryOptionLinkStore.insert(attributeOptionComboAttributeOptionLink)

        optionSetStore.insert(optionSet)
        optionStore.insert(option1)
        optionStore.insert(option2)

        dataElementStore.insert(dataElement1)
        dataElementStore.insert(dataElement2)
        dataElementStore.insert(dataElement3)
        dataElementStore.insert(dataElement4)
        dataElementStore.insert(dataElement5)

        periodStore.insert(period2019SunW25)
        periodStore.insert(period201910)
        periodStore.insert(period201911)
        periodStore.insert(period201912)
        periodStore.insert(period202001)
        periodStore.insert(period2019Q4)

        trackedEntityTypeStore.insert(trackedEntityType)
        trackedEntityAttributeStore.insert(attribute1)
        trackedEntityAttributeStore.insert(attribute2)
        trackedEntityAttributeStore.insert(attribute3)

        programStore.insert(program)
        programStageStore.insert(programStage1)
        programStageStore.insert(programStage2)

        relationshipTypeStore.insert(relationshipType)
        relationshipConstraintStore.insert(relationshipTypeFrom)
        relationshipConstraintStore.insert(relationshipTypeTo)

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
        optionSetStore.delete()
        optionStore.delete()
        dataElementStore.delete()
        periodStore.delete()
        dataValueStore.delete()
        trackedEntityTypeStore.delete()
        trackedEntityAttributeStore.delete()
        programStageStore.delete()
        programStore.delete()
        indicatorTypeStore.delete()
        indicatorStore.delete()
        relationshipTypeStore.delete()
        relationshipConstraintStore.delete()
        constantStore.delete()
    }

    protected fun createDataValue(
        value: String,
        dataElementUid: String = dataElement1.uid(),
        orgunitUid: String = orgunitParent.uid(),
        periodId: String = period201912.periodId()!!,
        categoryOptionComboUid: String = categoryOptionCombo.uid(),
        attributeOptionComboUid: String = attributeOptionCombo.uid(),
    ) {
        val dataValue = DataValue.builder()
            .value(value)
            .dataElement(dataElementUid)
            .period(periodId)
            .organisationUnit(orgunitUid)
            .categoryOptionCombo(categoryOptionComboUid)
            .attributeOptionCombo(attributeOptionComboUid)
            .build()

        dataValueStore.insert(dataValue)
    }

    protected fun createEventAndValue(
        value: String,
        dataElementUid: String,
        enrollmentUid: String? = null,
    ) {
        val event = Event.builder()
            .uid(BaseEvaluatorSamples.generator.generate())
            .eventDate(period201912.startDate())
            .enrollment(enrollmentUid)
            .program(program.uid())
            .programStage(programStage1.uid())
            .organisationUnit(orgunitChild1.uid())
            .deleted(false)
            .build()

        eventStore.insert(event)

        val dataValue = TrackedEntityDataValue.builder()
            .event(event.uid())
            .dataElement(dataElementUid)
            .value(value)
            .build()

        trackedEntityDataValueStore.insert(dataValue)
    }

    protected fun createTEIAndAttribute(
        value: String?,
        attributeUid: String,
    ) {
        val tei = TrackedEntityInstance.builder()
            .uid(BaseEvaluatorSamples.generator.generate())
            .trackedEntityType(trackedEntityType.uid())
            .organisationUnit(orgunitChild1.uid())
            .deleted(false)
            .build()

        trackedEntityStore.insert(tei)

        val enrollment = Enrollment.builder()
            .uid(BaseEvaluatorSamples.generator.generate())
            .trackedEntityInstance(tei.uid())
            .organisationUnit(orgunitChild1.uid())
            .program(program.uid())
            .deleted(false)
            .build()

        enrollmentStore.insert(enrollment)

        val attributeValue = TrackedEntityAttributeValue.builder()
            .trackedEntityInstance(tei.uid())
            .trackedEntityAttribute(attributeUid)
            .value(value)
            .build()

        trackedEntityAttributeValueStore.insert(attributeValue)
        createEventAndValue("0", dataElement1.uid(), enrollment.uid())
    }

    protected fun de(dataElementUid: String): String {
        return "#{$dataElementUid}"
    }

    protected fun eventDE(programUid: String, dataElementUid: String): String {
        return "D{$programUid.$dataElementUid}"
    }

    protected fun eventAtt(programUid: String, attributeUid: String): String {
        return "A{$programUid.$attributeUid}"
    }

    protected fun cons(constantUid: String): String {
        return "C{$constantUid}"
    }
}
