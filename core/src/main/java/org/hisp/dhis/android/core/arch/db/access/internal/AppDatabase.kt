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

package org.hisp.dhis.android.core.arch.db.access.internal

import androidx.room.Database
import androidx.room.RoomDatabase
import org.hisp.dhis.android.persistence.attribute.AttributeDB
import org.hisp.dhis.android.persistence.attribute.AttributeDao
import org.hisp.dhis.android.persistence.attribute.DataElementAttributeValueLinkDB
import org.hisp.dhis.android.persistence.attribute.DataElementAttributeValueLinkDao
import org.hisp.dhis.android.persistence.attribute.ProgramAttributeValueLinkDB
import org.hisp.dhis.android.persistence.attribute.ProgramAttributeValueLinkDao
import org.hisp.dhis.android.persistence.attribute.ProgramStageAttributeValueLinkDB
import org.hisp.dhis.android.persistence.attribute.ProgramStageAttributeValueLinkDao
import org.hisp.dhis.android.persistence.category.CategoryCategoryComboLinkDB
import org.hisp.dhis.android.persistence.category.CategoryCategoryComboLinkDao
import org.hisp.dhis.android.persistence.category.CategoryCategoryOptionLinkDB
import org.hisp.dhis.android.persistence.category.CategoryCategoryOptionLinkDao
import org.hisp.dhis.android.persistence.category.CategoryComboDB
import org.hisp.dhis.android.persistence.category.CategoryComboDao
import org.hisp.dhis.android.persistence.category.CategoryDB
import org.hisp.dhis.android.persistence.category.CategoryDao
import org.hisp.dhis.android.persistence.category.CategoryOptionComboCategoryOptionLinkDB
import org.hisp.dhis.android.persistence.category.CategoryOptionComboCategoryOptionLinkDao
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDB
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDao
import org.hisp.dhis.android.persistence.category.CategoryOptionDB
import org.hisp.dhis.android.persistence.category.CategoryOptionDao
import org.hisp.dhis.android.persistence.category.CategoryOptionOrganisationUnitLinkDB
import org.hisp.dhis.android.persistence.category.CategoryOptionOrganisationUnitLinkDao
import org.hisp.dhis.android.persistence.configuration.ConfigurationDB
import org.hisp.dhis.android.persistence.configuration.ConfigurationDao
import org.hisp.dhis.android.persistence.constant.ConstantDB
import org.hisp.dhis.android.persistence.constant.ConstantDao
import org.hisp.dhis.android.persistence.dataapproval.DataApprovalDB
import org.hisp.dhis.android.persistence.dataapproval.DataApprovalDao
import org.hisp.dhis.android.persistence.dataelement.DataElementDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDao
import org.hisp.dhis.android.persistence.dataelement.DataElementOperandDB
import org.hisp.dhis.android.persistence.dataelement.DataElementOperandDao
import org.hisp.dhis.android.persistence.dataset.DataInputPeriodDB
import org.hisp.dhis.android.persistence.dataset.DataInputPeriodDao
import org.hisp.dhis.android.persistence.dataset.DataSetCompleteRegistrationDB
import org.hisp.dhis.android.persistence.dataset.DataSetCompleteRegistrationDao
import org.hisp.dhis.android.persistence.dataset.DataSetCompulsoryDataElementOperandLinkDao
import org.hisp.dhis.android.persistence.dataset.DataSetCompulsoryDataElementOperandsLinkDB
import org.hisp.dhis.android.persistence.dataset.DataSetDB
import org.hisp.dhis.android.persistence.dataset.DataSetDao
import org.hisp.dhis.android.persistence.dataset.DataSetDataElementLinkDB
import org.hisp.dhis.android.persistence.dataset.DataSetDataElementLinkDao
import org.hisp.dhis.android.persistence.dataset.DataSetOrganisationUnitLinkDB
import org.hisp.dhis.android.persistence.dataset.DataSetOrganisationUnitLinkDao
import org.hisp.dhis.android.persistence.dataset.SectionDB
import org.hisp.dhis.android.persistence.dataset.SectionDao
import org.hisp.dhis.android.persistence.dataset.SectionDataElementLinkDB
import org.hisp.dhis.android.persistence.dataset.SectionDataElementLinkDao
import org.hisp.dhis.android.persistence.dataset.SectionGreyedFieldsLinkDB
import org.hisp.dhis.android.persistence.dataset.SectionGreyedFieldsLinkDao
import org.hisp.dhis.android.persistence.dataset.SectionIndicatorLinkDB
import org.hisp.dhis.android.persistence.dataset.SectionIndicatorLinkDao
import org.hisp.dhis.android.persistence.datastore.DataStoreDB
import org.hisp.dhis.android.persistence.datastore.DataStoreDao
import org.hisp.dhis.android.persistence.datastore.LocalDataStoreDB
import org.hisp.dhis.android.persistence.datastore.LocalDataStoreDao
import org.hisp.dhis.android.persistence.datavalue.DataValueConflictDB
import org.hisp.dhis.android.persistence.datavalue.DataValueConflictDao
import org.hisp.dhis.android.persistence.datavalue.DataValueDB
import org.hisp.dhis.android.persistence.datavalue.DataValueDao
import org.hisp.dhis.android.persistence.domain.AggregatedDataSyncDB
import org.hisp.dhis.android.persistence.domain.AggregatedDataSyncDao
import org.hisp.dhis.android.persistence.enrollment.EnrollmentDB
import org.hisp.dhis.android.persistence.enrollment.EnrollmentDao
import org.hisp.dhis.android.persistence.event.EventDB
import org.hisp.dhis.android.persistence.event.EventDao
import org.hisp.dhis.android.persistence.event.EventDataFilterDB
import org.hisp.dhis.android.persistence.event.EventDataFilterDao
import org.hisp.dhis.android.persistence.event.EventFilterDB
import org.hisp.dhis.android.persistence.event.EventFilterDao
import org.hisp.dhis.android.persistence.event.EventSyncDB
import org.hisp.dhis.android.persistence.event.EventSyncDao
import org.hisp.dhis.android.persistence.expressiondimensionitem.ExpressionDimensionItemDB
import org.hisp.dhis.android.persistence.expressiondimensionitem.ExpressionDimensionItemDao
import org.hisp.dhis.android.persistence.fileresource.FileResourceDB
import org.hisp.dhis.android.persistence.fileresource.FileResourceDao
import org.hisp.dhis.android.persistence.icon.CustomIconDB
import org.hisp.dhis.android.persistence.icon.CustomIconDao
import org.hisp.dhis.android.persistence.imports.TrackerImportConflictDB
import org.hisp.dhis.android.persistence.imports.TrackerImportConflictDao
import org.hisp.dhis.android.persistence.indicator.DataSetIndicatorLinkDB
import org.hisp.dhis.android.persistence.indicator.DataSetIndicatorLinkDao
import org.hisp.dhis.android.persistence.indicator.IndicatorDB
import org.hisp.dhis.android.persistence.indicator.IndicatorDao
import org.hisp.dhis.android.persistence.indicator.IndicatorTypeDB
import org.hisp.dhis.android.persistence.indicator.IndicatorTypeDao
import org.hisp.dhis.android.persistence.legendset.DataElementLegendSetLinkDB
import org.hisp.dhis.android.persistence.legendset.DataElementLegendSetLinkDao
import org.hisp.dhis.android.persistence.legendset.IndicatorLegendSetLinkDB
import org.hisp.dhis.android.persistence.legendset.IndicatorLegendSetLinkDao
import org.hisp.dhis.android.persistence.legendset.LegendDB
import org.hisp.dhis.android.persistence.legendset.LegendDao
import org.hisp.dhis.android.persistence.legendset.LegendSetDB
import org.hisp.dhis.android.persistence.legendset.LegendSetDao
import org.hisp.dhis.android.persistence.legendset.ProgramIndicatorLegendSetLinkDB
import org.hisp.dhis.android.persistence.legendset.ProgramIndicatorLegendSetLinkDao
import org.hisp.dhis.android.persistence.maintenance.D2ErrorDB
import org.hisp.dhis.android.persistence.maintenance.D2ErrorDao
import org.hisp.dhis.android.persistence.maintenance.ForeignKeyViolationDB
import org.hisp.dhis.android.persistence.maintenance.ForeignKeyViolationDao
import org.hisp.dhis.android.persistence.map.MapLayerDB
import org.hisp.dhis.android.persistence.map.MapLayerDao
import org.hisp.dhis.android.persistence.map.MapLayerImageryProviderDB
import org.hisp.dhis.android.persistence.map.MapLayerImageryProviderDao
import org.hisp.dhis.android.persistence.note.NoteDB
import org.hisp.dhis.android.persistence.note.NoteDao
import org.hisp.dhis.android.persistence.option.OptionDB
import org.hisp.dhis.android.persistence.option.OptionDao
import org.hisp.dhis.android.persistence.option.OptionGroupDB
import org.hisp.dhis.android.persistence.option.OptionGroupDao
import org.hisp.dhis.android.persistence.option.OptionGroupOptionLinkDB
import org.hisp.dhis.android.persistence.option.OptionGroupOptionLinkDao
import org.hisp.dhis.android.persistence.option.OptionSetDB
import org.hisp.dhis.android.persistence.option.OptionSetDao
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDao
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitGroupDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitGroupDao
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitLevelDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitLevelDao
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitOrganisationUnitGroupLinkDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitOrganisationUnitGroupLinkDao
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitProgramLinkDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitProgramLinkDao
import org.hisp.dhis.android.persistence.period.PeriodDB
import org.hisp.dhis.android.persistence.period.PeriodDao
import org.hisp.dhis.android.persistence.program.AnalyticsPeriodBoundaryDB
import org.hisp.dhis.android.persistence.program.AnalyticsPeriodBoundaryDao
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramDao
import org.hisp.dhis.android.persistence.program.ProgramIndicatorDB
import org.hisp.dhis.android.persistence.program.ProgramIndicatorDao
import org.hisp.dhis.android.persistence.program.ProgramRuleActionDB
import org.hisp.dhis.android.persistence.program.ProgramRuleActionDao
import org.hisp.dhis.android.persistence.program.ProgramRuleDB
import org.hisp.dhis.android.persistence.program.ProgramRuleDao
import org.hisp.dhis.android.persistence.program.ProgramRuleVariableDB
import org.hisp.dhis.android.persistence.program.ProgramRuleVariableDao
import org.hisp.dhis.android.persistence.program.ProgramSectionAttributeLinkDB
import org.hisp.dhis.android.persistence.program.ProgramSectionAttributeLinkDao
import org.hisp.dhis.android.persistence.program.ProgramSectionDB
import org.hisp.dhis.android.persistence.program.ProgramSectionDao
import org.hisp.dhis.android.persistence.program.ProgramStageDB
import org.hisp.dhis.android.persistence.program.ProgramStageDao
import org.hisp.dhis.android.persistence.program.ProgramStageDataElementDB
import org.hisp.dhis.android.persistence.program.ProgramStageDataElementDao
import org.hisp.dhis.android.persistence.program.ProgramStageSectionDB
import org.hisp.dhis.android.persistence.program.ProgramStageSectionDao
import org.hisp.dhis.android.persistence.program.ProgramStageSectionDataElementLinkDB
import org.hisp.dhis.android.persistence.program.ProgramStageSectionDataElementLinkDao
import org.hisp.dhis.android.persistence.program.ProgramStageSectionProgramIndicatorLinkDB
import org.hisp.dhis.android.persistence.program.ProgramStageSectionProgramIndicatorLinkDao
import org.hisp.dhis.android.persistence.program.ProgramTrackedEntityAttributeDB
import org.hisp.dhis.android.persistence.program.ProgramTrackedEntityAttributeDao
import org.hisp.dhis.android.persistence.programstageworkinglist.ProgramStageWorkingListAttributeValueFilterDB
import org.hisp.dhis.android.persistence.programstageworkinglist.ProgramStageWorkingListAttributeValueFilterDao
import org.hisp.dhis.android.persistence.programstageworkinglist.ProgramStageWorkingListDB
import org.hisp.dhis.android.persistence.programstageworkinglist.ProgramStageWorkingListDao
import org.hisp.dhis.android.persistence.programstageworkinglist.ProgramStageWorkingListEventDataFilterDB
import org.hisp.dhis.android.persistence.programstageworkinglist.ProgramStageWorkingListEventDataFilterDao
import org.hisp.dhis.android.persistence.relationship.RelationshipConstraintDB
import org.hisp.dhis.android.persistence.relationship.RelationshipConstraintDao
import org.hisp.dhis.android.persistence.relationship.RelationshipDB
import org.hisp.dhis.android.persistence.relationship.RelationshipDao
import org.hisp.dhis.android.persistence.relationship.RelationshipItemDB
import org.hisp.dhis.android.persistence.relationship.RelationshipItemDao
import org.hisp.dhis.android.persistence.relationship.RelationshipTypeDB
import org.hisp.dhis.android.persistence.relationship.RelationshipTypeDao
import org.hisp.dhis.android.persistence.resource.ResourceDB
import org.hisp.dhis.android.persistence.resource.ResourceDao
import org.hisp.dhis.android.persistence.settings.AnalyticsDhisVisualizationDB
import org.hisp.dhis.android.persistence.settings.AnalyticsDhisVisualizationDao
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiAttributeDB
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiAttributeDao
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiDataElementDB
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiDataElementDao
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiIndicatorDB
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiIndicatorDao
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiSettingDB
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiSettingDao
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiWHONutritionDataDB
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiWHONutritionDataDao
import org.hisp.dhis.android.persistence.settings.CustomIntentAttributeDB
import org.hisp.dhis.android.persistence.settings.CustomIntentAttributeDao
import org.hisp.dhis.android.persistence.settings.CustomIntentDB
import org.hisp.dhis.android.persistence.settings.CustomIntentDao
import org.hisp.dhis.android.persistence.settings.CustomIntentDataElementDB
import org.hisp.dhis.android.persistence.settings.CustomIntentDataElementDao
import org.hisp.dhis.android.persistence.settings.DataSetConfigurationSettingDB
import org.hisp.dhis.android.persistence.settings.DataSetConfigurationSettingDao
import org.hisp.dhis.android.persistence.settings.DataSetSettingDB
import org.hisp.dhis.android.persistence.settings.DataSetSettingDao
import org.hisp.dhis.android.persistence.settings.FilterSettingDB
import org.hisp.dhis.android.persistence.settings.FilterSettingDao
import org.hisp.dhis.android.persistence.settings.GeneralSettingDB
import org.hisp.dhis.android.persistence.settings.GeneralSettingDao
import org.hisp.dhis.android.persistence.settings.LatestAppVersionDB
import org.hisp.dhis.android.persistence.settings.LatestAppVersionDao
import org.hisp.dhis.android.persistence.settings.ProgramConfigurationSettingDB
import org.hisp.dhis.android.persistence.settings.ProgramConfigurationSettingDao
import org.hisp.dhis.android.persistence.settings.ProgramSettingDB
import org.hisp.dhis.android.persistence.settings.ProgramSettingDao
import org.hisp.dhis.android.persistence.settings.SynchronizationSettingDB
import org.hisp.dhis.android.persistence.settings.SynchronizationSettingDao
import org.hisp.dhis.android.persistence.settings.SystemSettingDB
import org.hisp.dhis.android.persistence.settings.SystemSettingDao
import org.hisp.dhis.android.persistence.settings.UserSettingsDB
import org.hisp.dhis.android.persistence.settings.UserSettingsDao
import org.hisp.dhis.android.persistence.sms.SMSConfigDB
import org.hisp.dhis.android.persistence.sms.SMSConfigDao
import org.hisp.dhis.android.persistence.sms.SMSMetadataIdDB
import org.hisp.dhis.android.persistence.sms.SMSMetadataIdDao
import org.hisp.dhis.android.persistence.sms.SMSOngoingSubmissionDB
import org.hisp.dhis.android.persistence.sms.SMSOngoingSubmissionDao
import org.hisp.dhis.android.persistence.systeminfo.SystemInfoDB
import org.hisp.dhis.android.persistence.systeminfo.SystemInfoDao
import org.hisp.dhis.android.persistence.trackedentity.AttributeValueFilterDB
import org.hisp.dhis.android.persistence.trackedentity.AttributeValueFilterDao
import org.hisp.dhis.android.persistence.trackedentity.EntityQueryCriteriaDB
import org.hisp.dhis.android.persistence.trackedentity.ProgramOwnerDB
import org.hisp.dhis.android.persistence.trackedentity.ProgramOwnerDao
import org.hisp.dhis.android.persistence.trackedentity.ProgramTempOwnerDB
import org.hisp.dhis.android.persistence.trackedentity.ProgramTempOwnerDao
import org.hisp.dhis.android.persistence.trackedentity.ReservedValueSettingDB
import org.hisp.dhis.android.persistence.trackedentity.ReservedValueSettingDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeLegendSetLinkDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeLegendSetLinkDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeReservedValueDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeReservedValueDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceEventFilterDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceEventFilterDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceFilterDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceFilterDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceSyncDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceSyncDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeAttributeDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeAttributeDao
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeDao
import org.hisp.dhis.android.persistence.tracker.TrackerJobObjectDB
import org.hisp.dhis.android.persistence.tracker.TrackerJobObjectDao
import org.hisp.dhis.android.persistence.usecase.StockUseCaseDB
import org.hisp.dhis.android.persistence.usecase.StockUseCaseDao
import org.hisp.dhis.android.persistence.usecase.StockUseCaseTransactionDB
import org.hisp.dhis.android.persistence.usecase.StockUseCaseTransactionLinkDao
import org.hisp.dhis.android.persistence.user.AuthenticatedUserDB
import org.hisp.dhis.android.persistence.user.AuthenticatedUserDao
import org.hisp.dhis.android.persistence.user.AuthorityDB
import org.hisp.dhis.android.persistence.user.AuthorityDao
import org.hisp.dhis.android.persistence.user.UserDB
import org.hisp.dhis.android.persistence.user.UserDao
import org.hisp.dhis.android.persistence.user.UserGroupDB
import org.hisp.dhis.android.persistence.user.UserGroupDao
import org.hisp.dhis.android.persistence.user.UserOrganisationUnitDB
import org.hisp.dhis.android.persistence.user.UserOrganisationUnitDao
import org.hisp.dhis.android.persistence.user.UserRoleDB
import org.hisp.dhis.android.persistence.user.UserRoleDao
import org.hisp.dhis.android.persistence.validation.DataSetValidationRuleLinkDB
import org.hisp.dhis.android.persistence.validation.DataSetValidationRuleLinkDao
import org.hisp.dhis.android.persistence.validation.ValidationRuleDB
import org.hisp.dhis.android.persistence.validation.ValidationRuleDao
import org.hisp.dhis.android.persistence.valuetypedevicerendering.ValueTypeDeviceRenderingDB
import org.hisp.dhis.android.persistence.valuetypedevicerendering.ValueTypeDeviceRenderingDao
import org.hisp.dhis.android.persistence.visualization.TrackerVisualizationDB
import org.hisp.dhis.android.persistence.visualization.TrackerVisualizationDao
import org.hisp.dhis.android.persistence.visualization.TrackerVisualizationDimensionDB
import org.hisp.dhis.android.persistence.visualization.TrackerVisualizationDimensionDao
import org.hisp.dhis.android.persistence.visualization.VisualizationDB
import org.hisp.dhis.android.persistence.visualization.VisualizationDao
import org.hisp.dhis.android.persistence.visualization.VisualizationDimensionItemDB
import org.hisp.dhis.android.persistence.visualization.VisualizationDimensionItemDao

@Database(
    entities = [
        AttributeDB::class,
        DataElementAttributeValueLinkDB::class,
        ProgramAttributeValueLinkDB::class,
        ProgramStageAttributeValueLinkDB::class,
        CategoryCategoryComboLinkDB::class,
        CategoryCategoryOptionLinkDB::class,
        CategoryComboDB::class,
        CategoryDB::class,
        CategoryOptionComboCategoryOptionLinkDB::class,
        CategoryOptionComboDB::class,
        CategoryOptionDB::class,
        CategoryOptionOrganisationUnitLinkDB::class,
        ConfigurationDB::class,
        ConstantDB::class,
        DataApprovalDB::class,
        DataElementDB::class,
        DataElementOperandDB::class,
        DataInputPeriodDB::class,
        DataSetCompleteRegistrationDB::class,
        DataSetCompulsoryDataElementOperandsLinkDB::class,
        DataSetDataElementLinkDB::class,
        DataSetDB::class,
        DataSetOrganisationUnitLinkDB::class,
        SectionDataElementLinkDB::class,
        SectionDB::class,
        SectionGreyedFieldsLinkDB::class,
        SectionIndicatorLinkDB::class,
        DataStoreDB::class,
        LocalDataStoreDB::class,
        DataValueConflictDB::class,
        DataValueDB::class,
        AggregatedDataSyncDB::class,
        EnrollmentDB::class,
        EventDataFilterDB::class,
        EventDB::class,
        EventFilterDB::class,
        EventSyncDB::class,
        ExpressionDimensionItemDB::class,
        FileResourceDB::class,
        CustomIconDB::class,
        TrackerImportConflictDB::class,
        DataSetIndicatorLinkDB::class,
        IndicatorDB::class,
        IndicatorTypeDB::class,
        DataElementLegendSetLinkDB::class,
        IndicatorLegendSetLinkDB::class,
        LegendDB::class,
        LegendSetDB::class,
        ProgramIndicatorLegendSetLinkDB::class,
        D2ErrorDB::class,
        ForeignKeyViolationDB::class,
        MapLayerDB::class,
        MapLayerImageryProviderDB::class,
        NoteDB::class,
        OptionDB::class,
        OptionGroupDB::class,
        OptionGroupOptionLinkDB::class,
        OptionSetDB::class,
        OrganisationUnitDB::class,
        OrganisationUnitGroupDB::class,
        OrganisationUnitLevelDB::class,
        OrganisationUnitOrganisationUnitGroupLinkDB::class,
        OrganisationUnitProgramLinkDB::class,
        PeriodDB::class,
        AnalyticsPeriodBoundaryDB::class,
        ProgramDB::class,
        ProgramIndicatorDB::class,
        ProgramRuleActionDB::class,
        ProgramRuleDB::class,
        ProgramRuleVariableDB::class,
        ProgramSectionAttributeLinkDB::class,
        ProgramSectionDB::class,
        ProgramStageDataElementDB::class,
        ProgramStageDB::class,
        ProgramStageSectionDataElementLinkDB::class,
        ProgramStageSectionDB::class,
        ProgramStageSectionProgramIndicatorLinkDB::class,
        ProgramTrackedEntityAttributeDB::class,
        ProgramStageWorkingListAttributeValueFilterDB::class,
        ProgramStageWorkingListDB::class,
        ProgramStageWorkingListEventDataFilterDB::class,
        RelationshipConstraintDB::class,
        RelationshipDB::class,
        RelationshipItemDB::class,
        RelationshipTypeDB::class,
        ResourceDB::class,
        AnalyticsDhisVisualizationDB::class,
        AnalyticsTeiAttributeDB::class,
        AnalyticsTeiDataElementDB::class,
        AnalyticsTeiIndicatorDB::class,
        AnalyticsTeiSettingDB::class,
        AnalyticsTeiWHONutritionDataDB::class,
        CustomIntentAttributeDB::class,
        CustomIntentDataElementDB::class,
        CustomIntentDB::class,
        DataSetConfigurationSettingDB::class,
        DataSetSettingDB::class,
        FilterSettingDB::class,
        GeneralSettingDB::class,
        LatestAppVersionDB::class,
        ProgramConfigurationSettingDB::class,
        ProgramSettingDB::class,
        SynchronizationSettingDB::class,
        SystemSettingDB::class,
        UserSettingsDB::class,
        SMSConfigDB::class,
        SMSMetadataIdDB::class,
        SMSOngoingSubmissionDB::class,
        SystemInfoDB::class,
        AttributeValueFilterDB::class,
        EntityQueryCriteriaDB::class,
        ProgramOwnerDB::class,
        ProgramTempOwnerDB::class,
        ReservedValueSettingDB::class,
        TrackedEntityAttributeDB::class,
        TrackedEntityAttributeLegendSetLinkDB::class,
        TrackedEntityAttributeReservedValueDB::class,
        TrackedEntityAttributeValueDB::class,
        TrackedEntityDataValueDB::class,
        TrackedEntityInstanceDB::class,
        TrackedEntityInstanceEventFilterDB::class,
        TrackedEntityInstanceFilterDB::class,
        TrackedEntityInstanceSyncDB::class,
        TrackedEntityTypeAttributeDB::class,
        TrackedEntityTypeDB::class,
        TrackerJobObjectDB::class,
        StockUseCaseDB::class,
        StockUseCaseTransactionDB::class,
        AuthenticatedUserDB::class,
        AuthorityDB::class,
        UserDB::class,
        UserGroupDB::class,
        UserOrganisationUnitDB::class,
        UserRoleDB::class,
        DataSetValidationRuleLinkDB::class,
        ValidationRuleDB::class,
        ValueTypeDeviceRenderingDB::class,
        TrackerVisualizationDB::class,
        TrackerVisualizationDimensionDB::class,
        VisualizationDB::class,
        VisualizationDimensionItemDB::class,
    ],
    version = BaseDatabaseOpenHelper.VERSION
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun attributeDao(): AttributeDao
    abstract fun dataElementAttributeValueLinkDao(): DataElementAttributeValueLinkDao
    abstract fun programAttributeValueLinkDao(): ProgramAttributeValueLinkDao
    abstract fun programStageAttributeValueLinkDao(): ProgramStageAttributeValueLinkDao
    abstract fun categoryCategoryComboLinkDao(): CategoryCategoryComboLinkDao
    abstract fun categoryCategoryOptionLinkDao(): CategoryCategoryOptionLinkDao
    abstract fun categoryComboDao(): CategoryComboDao
    abstract fun categoryDao(): CategoryDao
    abstract fun categoryOptionComboCategoryOptionLinkDao(): CategoryOptionComboCategoryOptionLinkDao
    abstract fun categoryOptionComboDao(): CategoryOptionComboDao
    abstract fun categoryOptionDao(): CategoryOptionDao
    abstract fun categoryOptionOrganisationUnitLinkDao(): CategoryOptionOrganisationUnitLinkDao
    abstract fun configurationDao(): ConfigurationDao
    abstract fun constantDao(): ConstantDao
    abstract fun dataApprovalDao(): DataApprovalDao
    abstract fun dataElementDao(): DataElementDao
    abstract fun dataElementOperandDao(): DataElementOperandDao
    abstract fun dataInputPeriodDao(): DataInputPeriodDao
    abstract fun dataSetCompleteRegistrationDao(): DataSetCompleteRegistrationDao
    abstract fun dataSetCompulsoryDataElementOperandLinkDao(): DataSetCompulsoryDataElementOperandLinkDao
    abstract fun dataSetDao(): DataSetDao
    abstract fun dataSetDataElementLinkDao(): DataSetDataElementLinkDao
    abstract fun dataSetOrganisationUnitLinkDao(): DataSetOrganisationUnitLinkDao
    abstract fun sectionDao(): SectionDao
    abstract fun sectionDataElementLinkDao(): SectionDataElementLinkDao
    abstract fun sectionGreyedFieldsLinkDao(): SectionGreyedFieldsLinkDao
    abstract fun sectionIndicatorLinkDao(): SectionIndicatorLinkDao
    abstract fun dataStoreDao(): DataStoreDao
    abstract fun localDataStoreDao(): LocalDataStoreDao
    abstract fun dataValueConflictDao(): DataValueConflictDao
    abstract fun dataValueDao(): DataValueDao
    abstract fun aggregatedDataSyncDao(): AggregatedDataSyncDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun eventDao(): EventDao
    abstract fun eventDataFilterDao(): EventDataFilterDao
    abstract fun eventFilterDao(): EventFilterDao
    abstract fun eventSyncDao(): EventSyncDao
    abstract fun expressionDimensionItemDao(): ExpressionDimensionItemDao
    abstract fun fileResourceDao(): FileResourceDao
    abstract fun customIconDao(): CustomIconDao
    abstract fun trackerImportConflictDao(): TrackerImportConflictDao
    abstract fun dataSetIndicatorLinkDao(): DataSetIndicatorLinkDao
    abstract fun indicatorDao(): IndicatorDao
    abstract fun indicatorTypeDao(): IndicatorTypeDao
    abstract fun dataElementLegendSetLinkDao(): DataElementLegendSetLinkDao
    abstract fun indicatorLegendSetLinkDao(): IndicatorLegendSetLinkDao
    abstract fun legendDao(): LegendDao
    abstract fun legendSetDao(): LegendSetDao
    abstract fun programIndicatorLegendSetLinkDao(): ProgramIndicatorLegendSetLinkDao
    abstract fun d2ErrorDao(): D2ErrorDao
    abstract fun foreignKeyViolationDao(): ForeignKeyViolationDao
    abstract fun mapLayerDao(): MapLayerDao
    abstract fun mapLayerImageryProviderDao(): MapLayerImageryProviderDao
    abstract fun noteDao(): NoteDao
    abstract fun optionDao(): OptionDao
    abstract fun optionGroupDao(): OptionGroupDao
    abstract fun optionGroupOptionLinkDao(): OptionGroupOptionLinkDao
    abstract fun optionSetDao(): OptionSetDao
    abstract fun organisationUnitDao(): OrganisationUnitDao
    abstract fun organisationUnitGroupDao(): OrganisationUnitGroupDao
    abstract fun organisationUnitLevelDao(): OrganisationUnitLevelDao
    abstract fun organisationUnitOrganisationUnitGroupLinkDao(): OrganisationUnitOrganisationUnitGroupLinkDao
    abstract fun organisationUnitProgramLinkDao(): OrganisationUnitProgramLinkDao
    abstract fun periodDao(): PeriodDao
    abstract fun analyticsPeriodBoundaryDao(): AnalyticsPeriodBoundaryDao
    abstract fun programDao(): ProgramDao
    abstract fun programIndicatorDao(): ProgramIndicatorDao
    abstract fun programRuleActionDao(): ProgramRuleActionDao
    abstract fun programRuleDao(): ProgramRuleDao
    abstract fun programRuleVariableDao(): ProgramRuleVariableDao
    abstract fun programSectionAttributeLinkDao(): ProgramSectionAttributeLinkDao
    abstract fun programSectionDao(): ProgramSectionDao
    abstract fun programStageDao(): ProgramStageDao
    abstract fun programStageDataElementDao(): ProgramStageDataElementDao
    abstract fun programStageSectionDao(): ProgramStageSectionDao
    abstract fun programStageSectionDataElementLinkDao(): ProgramStageSectionDataElementLinkDao
    abstract fun programStageSectionProgramIndicatorLinkDao(): ProgramStageSectionProgramIndicatorLinkDao
    abstract fun programTrackedEntityAttributeDao(): ProgramTrackedEntityAttributeDao
    abstract fun programStageWorkingListAttributeValueFilterDao(): ProgramStageWorkingListAttributeValueFilterDao
    abstract fun programStageWorkingListDao(): ProgramStageWorkingListDao
    abstract fun programStageWorkingListEventDataFilterDao(): ProgramStageWorkingListEventDataFilterDao
    abstract fun relationshipConstraintDao(): RelationshipConstraintDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun relationshipItemDao(): RelationshipItemDao
    abstract fun relationshipTypeDao(): RelationshipTypeDao
    abstract fun resourceDao(): ResourceDao
    abstract fun analyticsDhisVisualizationDao(): AnalyticsDhisVisualizationDao
    abstract fun analyticsTeiAttributeDao(): AnalyticsTeiAttributeDao
    abstract fun analyticsTeiDataElementDao(): AnalyticsTeiDataElementDao
    abstract fun analyticsTeiIndicatorDao(): AnalyticsTeiIndicatorDao
    abstract fun analyticsTeiSettingDao(): AnalyticsTeiSettingDao
    abstract fun analyticsTeiWHONutritionDataDao(): AnalyticsTeiWHONutritionDataDao
    abstract fun customIntentAttributeDao(): CustomIntentAttributeDao
    abstract fun customIntentDao(): CustomIntentDao
    abstract fun customIntentDataElementDao(): CustomIntentDataElementDao
    abstract fun dataSetConfigurationSettingDao(): DataSetConfigurationSettingDao
    abstract fun dataSetSettingDao(): DataSetSettingDao
    abstract fun filterSettingDao(): FilterSettingDao
    abstract fun generalSettingDao(): GeneralSettingDao
    abstract fun latestAppVersionDao(): LatestAppVersionDao
    abstract fun programConfigurationSettingDao(): ProgramConfigurationSettingDao
    abstract fun programSettingDao(): ProgramSettingDao
    abstract fun synchronizationSettingDao(): SynchronizationSettingDao
    abstract fun systemSettingDao(): SystemSettingDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun SMSConfigDao(): SMSConfigDao
    abstract fun SMSMetadataIdDao(): SMSMetadataIdDao
    abstract fun SMSOngoingSubmissionDao(): SMSOngoingSubmissionDao
    abstract fun systemInfoDao(): SystemInfoDao
    abstract fun attributeValueFilterDao(): AttributeValueFilterDao
    abstract fun programOwnerDao(): ProgramOwnerDao
    abstract fun programTempOwnerDao(): ProgramTempOwnerDao
    abstract fun reservedValueSettingDao(): ReservedValueSettingDao
    abstract fun trackedEntityAttributeDao(): TrackedEntityAttributeDao
    abstract fun trackedEntityAttributeLegendSetLinkDao(): TrackedEntityAttributeLegendSetLinkDao
    abstract fun trackedEntityAttributeReservedValueDao(): TrackedEntityAttributeReservedValueDao
    abstract fun trackedEntityAttributeValueDao(): TrackedEntityAttributeValueDao
    abstract fun trackedEntityDataValueDao(): TrackedEntityDataValueDao
    abstract fun trackedEntityInstanceDao(): TrackedEntityInstanceDao
    abstract fun trackedEntityInstanceEventFilterDao(): TrackedEntityInstanceEventFilterDao
    abstract fun trackedEntityInstanceFilterDao(): TrackedEntityInstanceFilterDao
    abstract fun trackedEntityInstanceSyncDao(): TrackedEntityInstanceSyncDao
    abstract fun trackedEntityTypeAttributeDao(): TrackedEntityTypeAttributeDao
    abstract fun trackedEntityTypeDao(): TrackedEntityTypeDao
    abstract fun trackerJobObjectDao(): TrackerJobObjectDao
    abstract fun stockUseCaseDao(): StockUseCaseDao
    abstract fun stockUseCaseTransactionLinkDao(): StockUseCaseTransactionLinkDao
    abstract fun authenticatedUserDao(): AuthenticatedUserDao
    abstract fun authorityDao(): AuthorityDao
    abstract fun userDao(): UserDao
    abstract fun userGroupDao(): UserGroupDao
    abstract fun userOrganisationUnitDao(): UserOrganisationUnitDao
    abstract fun userRoleDao(): UserRoleDao
    abstract fun dataSetValidationRuleLinkDao(): DataSetValidationRuleLinkDao
    abstract fun validationRuleDao(): ValidationRuleDao
    abstract fun valueTypeDeviceRenderingDao(): ValueTypeDeviceRenderingDao
    abstract fun trackerVisualizationDao(): TrackerVisualizationDao
    abstract fun trackerVisualizationDimensionDao(): TrackerVisualizationDimensionDao
    abstract fun visualizationDao(): VisualizationDao
    abstract fun visualizationDimensionItemDao(): VisualizationDimensionItemDao

    companion object {
        const val DATABASE_NAME = "dhis2-db"
    }
}
