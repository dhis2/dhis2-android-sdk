/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.configuration.internal;

import android.database.Cursor;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkTableInfo;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLink;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkTableInfo;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboTableInfo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLink;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkTableInfo;
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo;
import org.hisp.dhis.android.core.category.CategoryOptionTableInfo;
import org.hisp.dhis.android.core.category.CategoryTableInfo;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingTableInfo;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.constant.ConstantTableInfo;
import org.hisp.dhis.android.core.dataapproval.DataApproval;
import org.hisp.dhis.android.core.dataapproval.DataApprovalTableInfo;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataelement.DataElementOperandTableInfo;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.dataset.DataInputPeriod;
import org.hisp.dhis.android.core.dataset.DataInputPeriodTableInfo;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLink;
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;
import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.core.dataset.SectionDataElementLink;
import org.hisp.dhis.android.core.dataset.SectionDataElementLinkTableInfo;
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLink;
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLinkTableInfo;
import org.hisp.dhis.android.core.dataset.SectionTableInfo;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.fileresource.FileResourceTableInfo;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.TrackerImportConflictTableInfo;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkTableInfo;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.indicator.IndicatorTableInfo;
import org.hisp.dhis.android.core.indicator.IndicatorType;
import org.hisp.dhis.android.core.indicator.IndicatorTypeTableInfo;
import org.hisp.dhis.android.core.legendset.Legend;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.LegendSetTableInfo;
import org.hisp.dhis.android.core.legendset.LegendTableInfo;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkTableInfo;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorTableInfo;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.NoteTableInfo;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionGroup;
import org.hisp.dhis.android.core.option.OptionGroupOptionLink;
import org.hisp.dhis.android.core.option.OptionGroupOptionLinkTableInfo;
import org.hisp.dhis.android.core.option.OptionGroupTableInfo;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetTableInfo;
import org.hisp.dhis.android.core.option.OptionTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroupTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevelTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLinkTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodTableInfo;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionTableInfo;
import org.hisp.dhis.android.core.program.ProgramRuleTableInfo;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramRuleVariableTableInfo;
import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.core.program.ProgramSectionAttributeLink;
import org.hisp.dhis.android.core.program.ProgramSectionAttributeLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramSectionTableInfo;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementTableInfo;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLink;
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLink;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramStageSectionTableInfo;
import org.hisp.dhis.android.core.program.ProgramStageTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.program.internal.ProgramOrganisationUnitLastUpdated;
import org.hisp.dhis.android.core.program.internal.ProgramOrganisationUnitLastUpdatedTableInfo;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipConstraint;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeTableInfo;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceTableInfo;
import org.hisp.dhis.android.core.settings.DataSetSetting;
import org.hisp.dhis.android.core.settings.DataSetSettingTableInfo;
import org.hisp.dhis.android.core.settings.GeneralSettingTableInfo;
import org.hisp.dhis.android.core.settings.GeneralSettings;
import org.hisp.dhis.android.core.settings.ProgramSetting;
import org.hisp.dhis.android.core.settings.ProgramSettingTableInfo;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.settings.SystemSettingTableInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttributeTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeTableInfo;
import org.hisp.dhis.android.core.user.AuthenticatedUser;
import org.hisp.dhis.android.core.user.AuthenticatedUserTableInfo;
import org.hisp.dhis.android.core.user.Authority;
import org.hisp.dhis.android.core.user.AuthorityTableInfo;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsTableInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.UserRoleTableInfo;
import org.hisp.dhis.android.core.user.UserTableInfo;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

@Reusable
class DatabaseCopy {

    private final RxAPICallExecutor executor;

    private DatabaseAdapter adapterFrom;
    private DatabaseAdapter adapterTo;

    @Inject
    DatabaseCopy(RxAPICallExecutor executor) {
        this.executor = executor;
    }

    void copyDatabase(DatabaseAdapter adapterFrom, DatabaseAdapter adapterTo) {
        executor.wrapObservableTransactionally(Observable.fromCallable((Callable<Unit>) () -> {
            copyDatabaseInternal(adapterFrom, adapterTo);
            return new Unit();
        }), true).blockingSubscribe();
    }

    private void copyDatabaseInternal(DatabaseAdapter adapterFrom, DatabaseAdapter adapterTo) {
        this.adapterFrom = adapterFrom;
        this.adapterTo = adapterTo;
        copyTable(UserTableInfo.TABLE_INFO, User::create);
        copyTable(UserCredentialsTableInfo.TABLE_INFO, UserCredentials::create);
        copyTable(OrganisationUnitTableInfo.TABLE_INFO, OrganisationUnit::create);
        copyTable(OptionSetTableInfo.TABLE_INFO, OptionSet::create);
        copyTable(OptionTableInfo.TABLE_INFO, Option::create);
        copyTable(TrackedEntityTypeTableInfo.TABLE_INFO, TrackedEntityType::create);
        copyTable(ProgramStageSectionTableInfo.TABLE_INFO, ProgramStageSection::create);
        copyTable(ProgramRuleVariableTableInfo.TABLE_INFO, ProgramRuleVariable::create);
        copyTable(ProgramTrackedEntityAttributeTableInfo.TABLE_INFO, ProgramTrackedEntityAttribute::create);
        copyTable(ConstantTableInfo.TABLE_INFO, Constant::create);
        copyTable(SystemInfoTableInfo.TABLE_INFO, SystemInfo::create);
        copyTable(ProgramRuleTableInfo.TABLE_INFO, ProgramRule::create);
        copyTable(ProgramIndicatorTableInfo.TABLE_INFO, ProgramIndicator::create);
        copyTable(ResourceTableInfo.TABLE_INFO, Resource::create);
        copyTable(OrganisationUnitProgramLinkTableInfo.TABLE_INFO, OrganisationUnitProgramLink::create);
        copyTable(UserRoleTableInfo.TABLE_INFO, UserRole::create);
        copyTable(ProgramStageSectionProgramIndicatorLinkTableInfo.TABLE_INFO,
                ProgramStageSectionProgramIndicatorLink::create);
        copyTable(CategoryTableInfo.TABLE_INFO, Category::create);
        copyTable(CategoryOptionTableInfo.TABLE_INFO, CategoryOption::create);
        copyTable(CategoryCategoryOptionLinkTableInfo.TABLE_INFO, CategoryCategoryOptionLink::create);
        copyTable(CategoryComboTableInfo.TABLE_INFO, CategoryCombo::create);
        copyTable(CategoryCategoryComboLinkTableInfo.TABLE_INFO, CategoryCategoryComboLink::create);
        copyTable(CategoryOptionComboTableInfo.TABLE_INFO, CategoryOptionCombo::create);
        copyTable(DataSetTableInfo.TABLE_INFO, DataSet::create);
        copyTable(DataSetDataElementLinkTableInfo.TABLE_INFO, DataSetElement::create);
        copyTable(IndicatorTableInfo.TABLE_INFO, Indicator::create);
        copyTable(DataSetIndicatorLinkTableInfo.TABLE_INFO, DataSetIndicatorLink::create);
        copyTable(PeriodTableInfo.TABLE_INFO, Period::create);
        copyTable(ValueTypeDeviceRenderingTableInfo.TABLE_INFO, ValueTypeDeviceRendering::create);
        copyTable(NoteTableInfo.TABLE_INFO, Note::create);
        copyTable(LegendTableInfo.TABLE_INFO, Legend::create);
        copyTable(LegendSetTableInfo.TABLE_INFO, LegendSet::create);
        copyTable(ProgramIndicatorLegendSetLinkTableInfo.TABLE_INFO, ProgramIndicatorLegendSetLink::create);
        copyTable(SystemSettingTableInfo.TABLE_INFO, SystemSetting::create);
        copyTable(ProgramSectionAttributeLinkTableInfo.TABLE_INFO, ProgramSectionAttributeLink::create);
        copyTable(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO,
                TrackedEntityAttributeReservedValue::create);
        copyTable(CategoryOptionComboCategoryOptionLinkTableInfo.TABLE_INFO,
                CategoryOptionComboCategoryOptionLink::create);
        copyTable(SectionTableInfo.TABLE_INFO, Section::create);
        copyTable(SectionDataElementLinkTableInfo.TABLE_INFO, SectionDataElementLink::create);
        copyTable(DataSetCompulsoryDataElementOperandLinkTableInfo.TABLE_INFO,
                DataSetCompulsoryDataElementOperandLink::create);
        copyTable(DataInputPeriodTableInfo.TABLE_INFO, DataInputPeriod::create);
        copyTable(RelationshipConstraintTableInfo.TABLE_INFO, RelationshipConstraint::create);
        copyTable(RelationshipItemTableInfo.TABLE_INFO, RelationshipItem::create);
        copyTable(OrganisationUnitGroupTableInfo.TABLE_INFO, OrganisationUnitGroup::create);
        copyTable(OrganisationUnitOrganisationUnitGroupLinkTableInfo.TABLE_INFO,
                OrganisationUnitOrganisationUnitGroupLink::create);
        copyTable(ProgramStageDataElementTableInfo.TABLE_INFO, ProgramStageDataElement::create);
        copyTable(ProgramStageSectionDataElementLinkTableInfo.TABLE_INFO, ProgramStageSectionDataElementLink::create);
        copyTable(DataElementOperandTableInfo.TABLE_INFO, DataElementOperand::create);
        copyTable(IndicatorTypeTableInfo.TABLE_INFO, IndicatorType::create);
        copyTable(ForeignKeyViolationTableInfo.TABLE_INFO, ForeignKeyViolation::create);
        copyTable(D2ErrorTableInfo.TABLE_INFO, D2Error::create);
        copyTable(AuthorityTableInfo.TABLE_INFO, Authority::create);
        copyTable(TrackedEntityTypeAttributeTableInfo.TABLE_INFO, TrackedEntityTypeAttribute::create);
        copyTable(RelationshipTableInfo.TABLE_INFO, Relationship::create);
        copyTable(DataElementTableInfo.TABLE_INFO, DataElement::create);
        copyTable(OptionGroupTableInfo.TABLE_INFO, OptionGroup::create);
        copyTable(OptionGroupOptionLinkTableInfo.TABLE_INFO, OptionGroupOptionLink::create);
        copyTable(ProgramRuleActionTableInfo.TABLE_INFO, ProgramRuleAction::create);
        copyTable(OrganisationUnitLevelTableInfo.TABLE_INFO, OrganisationUnitLevel::create);
        copyTable(ProgramSectionTableInfo.TABLE_INFO, ProgramSection::create);
        copyTable(DataApprovalTableInfo.TABLE_INFO, DataApproval::create);
        copyTable(TrackedEntityAttributeTableInfo.TABLE_INFO, TrackedEntityAttribute::create);
        copyTable(TrackerImportConflictTableInfo.TABLE_INFO, TrackerImportConflict::create);
        copyTable(DataSetOrganisationUnitLinkTableInfo.TABLE_INFO, DataSetOrganisationUnitLink::create);
        copyTable(UserOrganisationUnitLinkTableInfo.TABLE_INFO, UserOrganisationUnitLink::create);
        copyTable(ProgramOrganisationUnitLastUpdatedTableInfo.TABLE_INFO, ProgramOrganisationUnitLastUpdated::create);
        copyTable(RelationshipTypeTableInfo.TABLE_INFO, RelationshipType::create);
        copyTable(ProgramStageTableInfo.TABLE_INFO, ProgramStage::create);
        copyTable(ProgramTableInfo.TABLE_INFO, Program::create);
        copyTable(TrackedEntityInstanceTableInfo.TABLE_INFO, TrackedEntityInstance::create);
        copyTable(EnrollmentTableInfo.TABLE_INFO, Enrollment::create);
        copyTable(EventTableInfo.TABLE_INFO, Event::create);
        copyTable(DataValueTableInfo.TABLE_INFO, DataValue::create);
        copyTable(TrackedEntityDataValueTableInfo.TABLE_INFO, TrackedEntityDataValue::create);
        copyTable(TrackedEntityAttributeValueTableInfo.TABLE_INFO, TrackedEntityAttributeValue::create);
        copyTable(FileResourceTableInfo.TABLE_INFO, FileResource::create);
        copyTable(DataSetCompleteRegistrationTableInfo.TABLE_INFO, DataSetCompleteRegistration::create);
        copyTable(SectionGreyedFieldsLinkTableInfo.TABLE_INFO, SectionGreyedFieldsLink::create);
        copyTable(AuthenticatedUserTableInfo.TABLE_INFO, AuthenticatedUser::create);
        copyTable(GeneralSettingTableInfo.TABLE_INFO, GeneralSettings::create);
        copyTable(DataSetSettingTableInfo.TABLE_INFO, DataSetSetting::create);
        copyTable(ProgramSettingTableInfo.TABLE_INFO, ProgramSetting::create);
    }

    private <O extends CoreObject> void copyTable(TableInfo tableInfo, ObjectFactory<O> objectFactory) {
        try (Cursor cursor = adapterFrom.rawQuery("SELECT * FROM " + tableInfo.name())) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    adapterTo.insert(tableInfo.name(), null,
                            objectFactory.fromCursor(cursor).toContentValues());
                }
                while (cursor.moveToNext());
            }
        }
    }
}