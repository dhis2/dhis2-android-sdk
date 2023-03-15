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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.category.*
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.relationship.RelationshipConstraint
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType

object BaseEvaluatorSamples {

    val generator = UidGeneratorImpl()

    val orgunitParent: OrganisationUnit by lazy {
        val uid = generator.generate()
        OrganisationUnit.builder()
            .uid(uid)
            .displayName("Child 1")
            .path("/$uid")
            .level(1)
            .build()
    }

    val orgunitChild1: OrganisationUnit by lazy {
        val uid = generator.generate()
        OrganisationUnit.builder()
            .uid(uid)
            .displayName("Child 1")
            .parent(ObjectWithUid.create(orgunitParent.uid()))
            .path("/${orgunitParent.uid()}/$uid")
            .level(2)
            .build()
    }

    val orgunitChild2: OrganisationUnit by lazy {
        val uid = generator.generate()
        OrganisationUnit.builder()
            .uid(uid)
            .displayName("Child 2")
            .parent(ObjectWithUid.create(orgunitParent.uid()))
            .path("/${orgunitParent.uid()}/$uid")
            .level(2)
            .build()
    }

    val level1: OrganisationUnitLevel = OrganisationUnitLevel.builder()
        .uid(generator.generate())
        .displayName("Level 1")
        .level(1)
        .build()

    val level2: OrganisationUnitLevel = OrganisationUnitLevel.builder()
        .uid(generator.generate())
        .displayName("Level 2")
        .level(2)
        .build()

    val organisationUnitGroup = OrganisationUnitGroup.builder()
        .uid(generator.generate())
        .displayName("Group 1")
        .build()

    val category: Category = Category.builder()
        .uid(generator.generate())
        .displayName("Category 1")
        .dataDimensionType(CategoryDataDimensionType.DISAGGREGATION.name)
        .build()

    val categoryOption: CategoryOption = CategoryOption.builder()
        .uid(generator.generate())
        .displayName("Category Option 1")
        .build()

    val categoryCategoryOptionLink: CategoryCategoryOptionLink = CategoryCategoryOptionLink.builder()
        .category(category.uid())
        .categoryOption(categoryOption.uid())
        .build()

    val categoryCombo: CategoryCombo = CategoryCombo.builder()
        .uid(generator.generate())
        .build()

    val categoryOptionCombo: CategoryOptionCombo = CategoryOptionCombo.builder()
        .uid(generator.generate())
        .displayName("Coc")
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val categoryCategoryComboLink: CategoryCategoryComboLink = CategoryCategoryComboLink.builder()
        .category(category.uid())
        .categoryCombo(categoryCombo.uid())
        .build()

    val categoryOptionComboCategoryOptionLink = CategoryOptionComboCategoryOptionLink.builder()
        .categoryOption(categoryOption.uid())
        .categoryOptionCombo(categoryOptionCombo.uid())
        .build()

    val attribute: Category = Category.builder()
        .uid(generator.generate())
        .displayName("Attribute 1")
        .dataDimensionType(CategoryDataDimensionType.ATTRIBUTE.name)
        .build()

    val attributeOption: CategoryOption = CategoryOption.builder()
        .uid(generator.generate())
        .displayName("Attribute Option 1")
        .build()

    val attributeAttributeOptionLink: CategoryCategoryOptionLink = CategoryCategoryOptionLink.builder()
        .category(attribute.uid())
        .categoryOption(attributeOption.uid())
        .build()

    val attributeCombo: CategoryCombo = CategoryCombo.builder()
        .uid(generator.generate())
        .build()

    val attributeOptionCombo: CategoryOptionCombo = CategoryOptionCombo.builder()
        .uid(generator.generate())
        .displayName("Coc")
        .categoryCombo(ObjectWithUid.fromIdentifiable(attributeCombo))
        .build()

    val attributeAttributeComboLink: CategoryCategoryComboLink = CategoryCategoryComboLink.builder()
        .category(attribute.uid())
        .categoryCombo(attributeCombo.uid())
        .build()

    val attributeOptionComboAttributeOptionLink = CategoryOptionComboCategoryOptionLink.builder()
        .categoryOption(attributeOption.uid())
        .categoryOptionCombo(attributeOptionCombo.uid())
        .build()

    val dataElement1 = DataElement.builder()
        .uid(generator.generate())
        .displayName("Data element 1")
        .valueType(ValueType.NUMBER)
        .aggregationType(AggregationType.SUM.name)
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val dataElement2 = DataElement.builder()
        .uid(generator.generate())
        .displayName("Data element 2")
        .valueType(ValueType.INTEGER)
        .aggregationType(AggregationType.AVERAGE.name)
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val dataElement3 = DataElement.builder()
        .uid(generator.generate())
        .displayName("Data element 3")
        .valueType(ValueType.TEXT)
        .aggregationType(AggregationType.AVERAGE.name)
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val dataElement4 = DataElement.builder()
        .uid(generator.generate())
        .displayName("Data element 4")
        .valueType(ValueType.BOOLEAN)
        .aggregationType(AggregationType.AVERAGE.name)
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val dataElementOperand = DataElementOperand.builder()
        .uid("${dataElement1.uid()}.${categoryOptionCombo.uid()}")
        .dataElement(ObjectWithUid.create(dataElement1.uid()))
        .categoryOptionCombo(ObjectWithUid.create(categoryOptionCombo.uid()))
        .build()

    val attribute1 = TrackedEntityAttribute.builder()
        .uid(generator.generate())
        .displayName("Attribute 1")
        .valueType(ValueType.INTEGER)
        .build()

    val period201911: Period = Period.builder()
        .periodId("201911")
        .periodType(PeriodType.Monthly)
        .startDate(DateUtils.DATE_FORMAT.parse("2019-11-01T00:00:00.000"))
        .endDate(DateUtils.DATE_FORMAT.parse("2019-11-30T23:59:59.999"))
        .build()

    val period201912: Period = Period.builder()
        .periodId("201912")
        .periodType(PeriodType.Monthly)
        .startDate(DateUtils.DATE_FORMAT.parse("2019-12-01T00:00:00.000"))
        .endDate(DateUtils.DATE_FORMAT.parse("2019-12-31T23:59:59.999"))
        .build()

    val period202001: Period = Period.builder()
        .periodId("202001")
        .periodType(PeriodType.Monthly)
        .startDate(DateUtils.DATE_FORMAT.parse("2020-01-01T00:00:00.000"))
        .endDate(DateUtils.DATE_FORMAT.parse("2020-01-31T23:59:59.999"))
        .build()

    val period202012: Period = Period.builder()
        .periodId("202012")
        .periodType(PeriodType.Monthly)
        .startDate(DateUtils.DATE_FORMAT.parse("2020-12-01T00:00:00.000"))
        .endDate(DateUtils.DATE_FORMAT.parse("2020-12-31T23:59:59.999"))
        .build()

    val period2019Q4: Period = Period.builder()
        .periodId("2019Q4")
        .periodType(PeriodType.Quarterly)
        .startDate(DateUtils.DATE_FORMAT.parse("2019-10-01T00:00:00.000"))
        .endDate(DateUtils.DATE_FORMAT.parse("2019-12-31T23:59:59.999"))
        .build()

    val trackedEntityType: TrackedEntityType = TrackedEntityType.builder()
        .uid(generator.generate())
        .build()

    val program: Program = Program.builder()
        .uid(generator.generate())
        .trackedEntityType(trackedEntityType)
        .categoryCombo(ObjectWithUid.create(categoryCombo.uid()))
        .build()

    val programStage1: ProgramStage = ProgramStage.builder()
        .uid(generator.generate())
        .name("Program stage 1")
        .program(ObjectWithUid.create(program.uid()))
        .formType(FormType.DEFAULT)
        .build()

    val programStage2: ProgramStage = ProgramStage.builder()
        .uid(generator.generate())
        .program(ObjectWithUid.create(program.uid()))
        .formType(FormType.DEFAULT)
        .build()

    val trackedEntity1: TrackedEntityInstance = TrackedEntityInstance.builder()
        .uid(generator.generate())
        .trackedEntityType(trackedEntityType.uid())
        .build()

    val trackedEntity2: TrackedEntityInstance = TrackedEntityInstance.builder()
        .uid(generator.generate())
        .trackedEntityType(trackedEntityType.uid())
        .build()

    val day20191101 = DateUtils.DATE_FORMAT.parse("2019-11-01T00:00:00.000")
    val day20191102 = DateUtils.DATE_FORMAT.parse("2019-11-02T00:00:00.000")
    val day20191110 = DateUtils.DATE_FORMAT.parse("2019-11-10T00:00:00.000")
    val day20191201 = DateUtils.DATE_FORMAT.parse("2019-12-01T00:00:00.000")
    val day20200101 = DateUtils.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
    val day20201202 = DateUtils.DATE_FORMAT.parse("2020-12-02T00:00:00.000")

    val constant1 = Constant.builder()
        .uid(generator.generate())
        .displayName("Five")
        .value(5.0)
        .build()

    val relationshipType = RelationshipType.builder()
        .uid(generator.generate())
        .name("Relationship type")
        .bidirectional(false)
        .build()

    val relationshipTypeFrom = RelationshipConstraint.builder()
        .relationshipType(ObjectWithUid.create(relationshipType.uid()))
        .constraintType(RelationshipConstraintType.FROM)
        .trackedEntityType(ObjectWithUid.create(trackedEntityType.uid()))
        .build()

    val relationshipTypeTo = relationshipTypeFrom.toBuilder()
        .constraintType(RelationshipConstraintType.TO)
        .build()
}
