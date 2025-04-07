/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.arch.domain

import org.hisp.dhis.android.core.attribute.Attribute
import org.hisp.dhis.android.core.attribute.ProgramStageAttributeValueLink
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLink
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering
import org.hisp.dhis.android.core.configuration.internal.Configuration
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataset.DataInputPeriod
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLink
import org.hisp.dhis.android.core.dataset.DataSetElement
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink
import org.hisp.dhis.android.core.dataset.Section
import org.hisp.dhis.android.core.dataset.SectionDataElementLink
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLink
import org.hisp.dhis.android.core.datastore.KeyValuePair
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.legendset.DataElementLegendSetLink
import org.hisp.dhis.android.core.legendset.Legend
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.option.OptionGroup
import org.hisp.dhis.android.core.option.OptionGroupOptionLink
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramRule
import org.hisp.dhis.android.core.program.ProgramRuleAction
import org.hisp.dhis.android.core.program.ProgramRuleVariable
import org.hisp.dhis.android.core.program.ProgramSection
import org.hisp.dhis.android.core.program.ProgramSectionAttributeLink
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.program.ProgramStageSection
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLink
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipConstraint
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.resource.internal.Resource
import org.hisp.dhis.android.core.settings.DataSetSetting
import org.hisp.dhis.android.core.settings.GeneralSettings
import org.hisp.dhis.android.core.settings.LatestAppVersion
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.SystemSetting
import org.hisp.dhis.android.core.settings.UserSettings
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter
import org.hisp.dhis.android.core.trackedentity.ReservedValueSetting
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute
import org.hisp.dhis.android.core.usecase.stock.InternalStockUseCase
import org.hisp.dhis.android.core.usecase.stock.InternalStockUseCaseTransaction
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.Authority
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserGroup
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink
import org.hisp.dhis.android.core.user.UserRole
import org.hisp.dhis.android.core.validation.ValidationRule
import org.hisp.dhis.android.core.visualization.CategoryDimension
import org.hisp.dhis.android.core.visualization.Visualization

internal val domainClasses = listOf(
    Attribute::class.java,
    AttributeValueFilter::class.java,
    AuthenticatedUser::class.java,
    Authority::class.java,
    Category::class.java,
    CategoryCategoryComboLink::class.java,
    CategoryCategoryOptionLink::class.java,
    CategoryCombo::class.java,
    CategoryDimension::class.java,
    CategoryOption::class.java,
    CategoryOptionCombo::class.java,
    Configuration::class.java,
    Constant::class.java,
    DataElement::class.java,
    DataElementLegendSetLink::class.java,
    DataElementOperand::class.java,
    DataInputPeriod::class.java,
    DataSet::class.java,
    DataSetCompleteRegistration::class.java,
    DataSetCompulsoryDataElementOperandLink::class.java,
    DataSetElement::class.java,
    DataSetIndicatorLink::class.java,
    DataSetOrganisationUnitLink::class.java,
    DataSetSetting::class.java,
    DataValue::class.java,
    D2Error::class.java,
    Enrollment::class.java,
    Event::class.java,
    EventDataFilter::class.java,
    EventFilter::class.java,
    ForeignKeyViolation::class.java,
    GeneralSettings::class.java,
    Indicator::class.java,
    IndicatorType::class.java,
    InternalStockUseCase::class.java,
    InternalStockUseCaseTransaction::class.java,
    KeyValuePair::class.java,
    LatestAppVersion::class.java,
    Legend::class.java,
    LegendSet::class.java,
    Note::class.java,
    Option::class.java,
    OptionGroup::class.java,
    OptionGroupOptionLink::class.java,
    OptionSet::class.java,
    OrganisationUnit::class.java,
    OrganisationUnitGroup::class.java,
    OrganisationUnitLevel::class.java,
    OrganisationUnitOrganisationUnitGroupLink::class.java,
    OrganisationUnitProgramLink::class.java,
    Period::class.java,
    Program::class.java,
    ProgramIndicator::class.java,
    ProgramIndicatorLegendSetLink::class.java,
    ProgramRule::class.java,
    ProgramRuleAction::class.java,
    ProgramRuleVariable::class.java,
    ProgramSection::class.java,
    ProgramSectionAttributeLink::class.java,
    ProgramStage::class.java,
    ProgramStageAttributeValueLink::class.java,
    ProgramStageDataElement::class.java,
    ProgramStageSection::class.java,
    ProgramStageSectionDataElementLink::class.java,
    ProgramTrackedEntityAttribute::class.java,
    ProgramSetting::class.java,
    Relationship::class.java,
    RelationshipConstraint::class.java,
    RelationshipItem::class.java,
    RelationshipType::class.java,
    Resource::class.java,
    ReservedValueSetting::class.java,
    Section::class.java,
    SectionDataElementLink::class.java,
    SectionGreyedFieldsLink::class.java,
    SystemInfo::class.java,
    SystemSetting::class.java,
    TrackedEntityAttribute::class.java,
    TrackedEntityAttributeReservedValue::class.java,
    TrackedEntityAttributeValue::class.java,
    TrackedEntityDataValue::class.java,
    TrackedEntityInstance::class.java,
    TrackedEntityInstanceEventFilter::class.java,
    TrackedEntityInstanceFilter::class.java,
    TrackedEntityType::class.java,
    TrackedEntityTypeAttribute::class.java,
    TrackerImportConflict::class.java,
    User::class.java,
    UserGroup::class.java,
    UserOrganisationUnitLink::class.java,
    UserRole::class.java,
    UserSettings::class.java,
    ValidationRule::class.java,
    ValueTypeDeviceRendering::class.java,
    Visualization::class.java,
)
