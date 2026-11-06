/*
 *  Copyright (c) 2004-2024, University of Oslo
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
package org.hisp.dhis.android.network.common.fields

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.api.ParametersBuilder
import org.hisp.dhis.android.network.attribute.AttributeDTO
import org.hisp.dhis.android.network.category.CategoryDTO
import org.hisp.dhis.android.network.categorycombo.CategoryComboDTO
import org.hisp.dhis.android.network.categoryoption.CategoryOptionDTO
import org.hisp.dhis.android.network.constant.ConstantDTO
import org.hisp.dhis.android.network.customicon.CustomIconDTO
import org.hisp.dhis.android.network.dataapproval.DataApprovalDTO
import org.hisp.dhis.android.network.dataelement.DataElementDTO
import org.hisp.dhis.android.network.dataset.DataSetDTO
import org.hisp.dhis.android.network.datasetcompleteregistration.DataSetCompleteRegistrationDTO
import org.hisp.dhis.android.network.datavalue.DataValueDTO
import org.hisp.dhis.android.network.eventfilter.EventFilterDTO
import org.hisp.dhis.android.network.expressiondimensionitem.ExpressionDimensionItemDTO
import org.hisp.dhis.android.network.externalmaplayer.ExternalMapLayerDTO
import org.hisp.dhis.android.network.fileresource.FileResourceDTO
import org.hisp.dhis.android.network.indicator.IndicatorDTO
import org.hisp.dhis.android.network.indicatortype.IndicatorTypeDTO
import org.hisp.dhis.android.network.legendset.LegendSetDTO
import org.hisp.dhis.android.network.option.OptionDTO
import org.hisp.dhis.android.network.optiongroup.OptionGroupDTO
import org.hisp.dhis.android.network.optionset.OptionSetDTO
import org.hisp.dhis.android.network.organisationunitlevel.OrganisationUnitLevelDTO
import org.hisp.dhis.android.network.program.ProgramDTO
import org.hisp.dhis.android.network.programindicator.ProgramIndicatorDTO
import org.hisp.dhis.android.network.programrule.ProgramRuleDTO
import org.hisp.dhis.android.network.programstage.ProgramStageDTO
import org.hisp.dhis.android.network.programstageworkinglist.ProgramStageWorkingListDTO
import org.hisp.dhis.android.network.relationshiptype.RelationshipTypeDTO
import org.hisp.dhis.android.network.systeminfo.SystemInfoDTO
import org.hisp.dhis.android.network.systemsettings.AzureMapsApiKeyDTO
import org.hisp.dhis.android.network.systemsettings.BingMapsApiKeyDTO
import org.hisp.dhis.android.network.systemsettings.SystemSettingsDTO
import org.hisp.dhis.android.network.trackedentityattribute.TrackedEntityAttributeDTO
import org.hisp.dhis.android.network.trackedentityinstancefilter.TrackedEntityInstanceFilter37DTO
import org.hisp.dhis.android.network.trackedentityinstancefilter.TrackedEntityInstanceFilterDTO
import org.hisp.dhis.android.network.trackedentitytype.TrackedEntityTypeDTO
import org.hisp.dhis.android.network.trackervisualization.TrackerVisualizationDTO
import org.hisp.dhis.android.network.usersettings.UserSettingsDTO
import org.hisp.dhis.android.network.validationrule.ValidationRuleDTO
import org.hisp.dhis.android.network.visualization.Visualization36DTO
import org.hisp.dhis.android.network.visualization.VisualizationDTO
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Snapshot of the `fields` parameter requested for each DTO. A change here means the SDK requests different data
 * from the server, so it must be intentional.
 */
@RunWith(JUnit4::class)
internal class DTOFieldsShould {

    private inline fun <reified DTO> fieldsOf(): String {
        return ParametersBuilder(mutableListOf()).apply { fields<DTO>() }.parameters.single().second
    }

    private val requestedFields = mapOf(
        "AttributeDTO" to fieldsOf<AttributeDTO>(),
        "AzureMapsApiKeyDTO" to fieldsOf<AzureMapsApiKeyDTO>(),
        "BingMapsApiKeyDTO" to fieldsOf<BingMapsApiKeyDTO>(),
        "CategoryComboDTO" to fieldsOf<CategoryComboDTO>(),
        "CategoryDTO" to fieldsOf<CategoryDTO>(),
        "CategoryOptionDTO" to fieldsOf<CategoryOptionDTO>(),
        "ConstantDTO" to fieldsOf<ConstantDTO>(),
        "CustomIconDTO" to fieldsOf<CustomIconDTO>(),
        "DataApprovalDTO" to fieldsOf<DataApprovalDTO>(),
        "DataElementDTO" to fieldsOf<DataElementDTO>(),
        "DataSetCompleteRegistrationDTO" to fieldsOf<DataSetCompleteRegistrationDTO>(),
        "DataSetDTO" to fieldsOf<DataSetDTO>(),
        "DataValueDTO" to fieldsOf<DataValueDTO>(),
        "EventFilterDTO" to fieldsOf<EventFilterDTO>(),
        "ExpressionDimensionItemDTO" to fieldsOf<ExpressionDimensionItemDTO>(),
        "ExternalMapLayerDTO" to fieldsOf<ExternalMapLayerDTO>(),
        "FileResourceDTO" to fieldsOf<FileResourceDTO>(),
        "IndicatorDTO" to fieldsOf<IndicatorDTO>(),
        "IndicatorTypeDTO" to fieldsOf<IndicatorTypeDTO>(),
        "LegendSetDTO" to fieldsOf<LegendSetDTO>(),
        "OptionDTO" to fieldsOf<OptionDTO>(),
        "OptionGroupDTO" to fieldsOf<OptionGroupDTO>(),
        "OptionSetDTO" to fieldsOf<OptionSetDTO>(),
        "OrganisationUnitLevelDTO" to fieldsOf<OrganisationUnitLevelDTO>(),
        "ProgramDTO" to fieldsOf<ProgramDTO>(),
        "ProgramIndicatorDTO" to fieldsOf<ProgramIndicatorDTO>(),
        "ProgramRuleDTO" to fieldsOf<ProgramRuleDTO>(),
        "ProgramStageDTO" to fieldsOf<ProgramStageDTO>(),
        "ProgramStageWorkingListDTO" to fieldsOf<ProgramStageWorkingListDTO>(),
        "RelationshipTypeDTO" to fieldsOf<RelationshipTypeDTO>(),
        "SystemInfoDTO" to fieldsOf<SystemInfoDTO>(),
        "SystemSettingsDTO" to fieldsOf<SystemSettingsDTO>(),
        "TrackedEntityAttributeDTO" to fieldsOf<TrackedEntityAttributeDTO>(),
        "TrackedEntityInstanceFilter37DTO" to fieldsOf<TrackedEntityInstanceFilter37DTO>(),
        "TrackedEntityInstanceFilterDTO" to fieldsOf<TrackedEntityInstanceFilterDTO>(),
        "TrackedEntityTypeDTO" to fieldsOf<TrackedEntityTypeDTO>(),
        "TrackerVisualizationDTO" to fieldsOf<TrackerVisualizationDTO>(),
        "UserSettingsDTO" to fieldsOf<UserSettingsDTO>(),
        "ValidationRuleDTO" to fieldsOf<ValidationRuleDTO>(),
        "Visualization36DTO" to fieldsOf<Visualization36DTO>(),
        "VisualizationDTO" to fieldsOf<VisualizationDTO>(),
    )

    private val expectedFields = mapOf(
        "AttributeDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,valueType,unique,mandatory,indicatorAttribute,indicatorGroupAttribute," +
            "userGroupAttribute,dataElementAttribute,constantAttribute,categoryOptionAttribute," +
            "optionSetAttribute,sqlViewAttribute,legendSetAttribute,trackedEntityAttributeAttribute," +
            "organisationUnitAttribute,dataSetAttribute,documentAttribute,validationRuleGroupAttribute," +
            "dataElementGroupAttribute,sectionAttribute,trackedEntityTypeAttribute,userAttribute," +
            "categoryOptionGroupAttribute,programStageAttribute,programAttribute,categoryAttribute," +
            "categoryOptionComboAttribute,categoryOptionGroupSetAttribute,validationRuleAttribute," +
            "programIndicatorAttribute,organisationUnitGroupAttribute,dataElementGroupSetAttribute," +
            "organisationUnitGroupSetAttribute,optionAttribute",
        "AzureMapsApiKeyDTO" to "keyAzureMapsApiKey",
        "BingMapsApiKeyDTO" to "keyBingMapsApiKey",
        "CategoryComboDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,isDefault,categories[id]," +
            "categoryOptionCombos[id,code,name,displayName,created,lastUpdated,deleted,categoryOptions[id]]",
        "CategoryDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,dataDimensionType,categoryOptions[id]",
        "CategoryOptionDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,startDate,endDate,access[read,write,data[read,write]]",
        "ConstantDTO" to "id,code,name,displayName,created,lastUpdated,deleted,value",
        "CustomIconDTO" to "key,href,fileResource[id]",
        "DataApprovalDTO" to "wf,ou,pe,aoc,state",
        "DataElementDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,valueType,zeroIsSignificant,aggregationType,formName,domainType,displayFormName," +
            "optionSet[id],categoryCombo[id],legendSets[id],fieldMask,attributeValues[value,attribute[id]]," +
            "style[color,icon]",
        "DataSetCompleteRegistrationDTO" to
            "period,dataSet,organisationUnit,attributeOptionCombo,date,storedBy,completed",
        "DataSetDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,periodType,categoryCombo[id],mobile,version,expiryDays,timelyDays," +
            "notifyCompletingUser,openFuturePeriods,fieldCombinationRequired,validCompleteOnly," +
            "noValueRequiresComment,skipOffline,dataElementDecoration,renderAsTabs,renderHorizontally," +
            "workflow[id],dataSetElements[dataElement[id],categoryCombo[id]],indicators[id],sections[id,code," +
            "name,displayName,created,lastUpdated,deleted,description,sortOrder,showRowTotals,showColumnTotals," +
            "dataElements[id],greyedFields[id,deleted,dataElement[id],categoryOptionCombo[id]],indicators[id]," +
            "disableDataElementAutoGroup,displayOptions],compulsoryDataElementOperands[id,deleted," +
            "dataElement[id],categoryOptionCombo[id]],dataInputPeriods[period[id],openingDate,closingDate]," +
            "displayOptions,access[read,write,data[read,write]],style[color,icon]",
        "DataValueDTO" to
            "deleted,dataElement,period,orgUnit,categoryOptionCombo,attributeOptionCombo,value,storedBy," +
            "created,lastUpdated,comment,followup",
        "EventFilterDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,program,programStage,description," +
            "eventQueryCriteria[followUp,organisationUnit,ouMode,assignedUserMode,order,displayColumnOrder," +
            "eventDate[startBuffer,endBuffer,startDate,endDate,period,type],lastUpdatedDate[startBuffer," +
            "endBuffer,startDate,endDate,period,type],status,dataFilters[le,ge,gt,lt,eq,in,like," +
            "dateFilter[startBuffer,endBuffer,startDate,endDate,period,type],null,dataItem],events," +
            "dueDate[startBuffer,endBuffer,startDate,endDate,period,type],completedDate[startBuffer,endBuffer," +
            "startDate,endDate,period,type]]",
        "ExpressionDimensionItemDTO" to "id,code,name,displayName,created,lastUpdated,deleted,expression",
        "ExternalMapLayerDTO" to
            "id,name,displayName,mapLayerPosition,url,code,mapService,imageFormat,layers,attribution",
        "FileResourceDTO" to "id,name,created,lastUpdated,contentType,contentLength,domain,storageStatus",
        "IndicatorDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,annualized,indicatorType[id],numerator,numeratorDescription,denominator," +
            "denominatorDescription,url,decimals,legendSets[id],style[color,icon]",
        "IndicatorTypeDTO" to "id,code,name,displayName,created,lastUpdated,deleted,number,factor",
        "LegendSetDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,symbolizer,legends[id,code,name," +
            "displayName,created,lastUpdated,deleted,startValue,endValue,color]",
        "OptionDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,sortOrder,optionSet[id],style[color,icon]",
        "OptionGroupDTO" to "id,code,name,displayName,created,lastUpdated,deleted,optionSet[id],options[id]",
        "OptionSetDTO" to "id,code,name,displayName,created,lastUpdated,deleted,version,valueType",
        "OrganisationUnitLevelDTO" to "id,code,name,displayName,created,lastUpdated,deleted,level",
        "ProgramDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,style[color,icon],version,onlyEnrollOnce,enrollmentDateLabel," +
            "displayEnrollmentDateLabel,displayIncidentDate,incidentDateLabel,displayIncidentDateLabel," +
            "registration,selectEnrollmentDatesInFuture,dataEntryMethod,ignoreOverdueEvents," +
            "selectIncidentDatesInFuture,useFirstStageDuringRegistration,displayFrontPageList,programType," +
            "programTrackedEntityAttributes[id,code,name,displayName,created,lastUpdated,deleted,shortName," +
            "displayShortName,description,displayDescription,mandatory,trackedEntityAttribute[id]," +
            "allowFutureDate,displayInList,program[id],sortOrder,searchable,renderType[DESKTOP[type,min,max,step," +
            "decimalPoints],MOBILE[type,min,max,step,decimalPoints]]],relatedProgram[id],trackedEntityType[id]," +
            "categoryCombo[id],access[read,write,data[read,write]],programRuleVariables[id,code,name,displayName," +
            "created,lastUpdated,deleted,useCodeForOptionSet,program[id],programStage[id],dataElement[id]," +
            "trackedEntityAttribute[id],programRuleVariableSourceType],expiryDays,completeEventsExpiryDays," +
            "expiryPeriodType,minAttributesRequiredToSearch,maxTeiCountToReturn,programSections[id,code,name," +
            "displayName,created,lastUpdated,deleted,style[color,icon],description,program[id]," +
            "trackedEntityAttributes[id],programTrackedEntityAttribute[id],sortOrder,formName," +
            "renderType[DESKTOP[type],MOBILE[type]]],featureType,accessLevel,enrollmentLabel," +
            "displayEnrollmentLabel,displayEnrollmentsLabel,followUpLabel,displayFollowUpLabel,orgUnitLabel," +
            "displayOrgUnitLabel,relationshipLabel,displayRelationshipLabel,displayRelationshipsLabel,noteLabel," +
            "displayNoteLabel,trackedEntityAttributeLabel,displayTrackedEntityAttributeLabel,programStageLabel," +
            "displayProgramStageLabel,displayProgramStagesLabel,eventLabel,displayEventLabel,displayEventsLabel," +
            "attributeValues[value,attribute[id]],enrollmentCategoryCombo[id],categoryMappings[id,categoryId," +
            "mappingName,optionMappings[optionId,filter]]",
        "ProgramIndicatorDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,displayInForm,expression,dimensionItem,filter,decimals,aggregationType," +
            "program[id],analyticsType,analyticsPeriodBoundaries[boundaryTarget," +
            "analyticsPeriodBoundaryType,offsetPeriods,offsetPeriodType],legendSets[id],categoryCombo[id]," +
            "attributeCombo[id],categoryMappingIds",
        "ProgramRuleDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,priority,condition,program[id],programStage[id]," +
            "programRuleActions[id,code,name,displayName,created,lastUpdated,deleted,data,content,location," +
            "trackedEntityAttribute[id],programIndicator[id],programStageSection[id],programRuleActionType," +
            "programStage[id],dataElement[id],option[id],optionGroup[id],displayContent,priority,legendSet[id]]",
        "ProgramStageDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,style[color,icon],description," +
            "displayDescription,executionDateLabel,displayExecutionDateLabel,dueDateLabel,displayDueDateLabel," +
            "allowGenerateNextVisit,validCompleteOnly,reportDateToUse,openAfterEnrollment,repeatable,featureType," +
            "formType,displayGenerateEventBox,generatedByEnrollmentDate,autoGenerateEvent,sortOrder,hideDueDate," +
            "blockEntryForm,minDaysFromStart,standardInterval,enableUserAssignment,programStageSections[id,code," +
            "name,displayName,created,lastUpdated,deleted,sortOrder,programIndicators[id],dataElements[id]," +
            "renderType[DESKTOP[type],MOBILE[type]],description,displayDescription],programStageDataElements[id," +
            "code,name,displayName,created,lastUpdated,deleted,displayInReports,compulsory," +
            "allowProvidedElsewhere,sortOrder,allowFutureDate,dataElement[id,code,name,displayName,created," +
            "lastUpdated,deleted,shortName,displayShortName,description,displayDescription,valueType," +
            "zeroIsSignificant,aggregationType,formName,domainType,displayFormName,optionSet[id]," +
            "categoryCombo[id],legendSets[id],fieldMask,attributeValues[value,attribute[id]],style[color,icon]]," +
            "programStage[id],renderType[DESKTOP[type,min,max,step,decimalPoints],MOBILE[type,min,max,step," +
            "decimalPoints]]],periodType,program[id],access[read,write,data[read,write]],remindCompleted," +
            "validationStrategy,programStageLabel,displayProgramStageLabel,eventLabel,displayEventLabel," +
            "displayEventsLabel,attributeValues[value,attribute[id]]",
        "ProgramStageWorkingListDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,description,program[id],programStage[id]," +
            "programStageQueryCriteria[eventStatus,eventCreatedAt[startBuffer,endBuffer,startDate,endDate,period," +
            "type],eventOccurredAt[startBuffer,endBuffer,startDate,endDate,period,type]," +
            "eventScheduledAt[startBuffer,endBuffer,startDate,endDate,period,type],enrollmentStatus," +
            "enrolledAt[startBuffer,endBuffer,startDate,endDate,period,type],enrollmentOccurredAt[startBuffer," +
            "endBuffer,startDate,endDate,period,type],order,displayColumnOrder,orgUnit,ouMode,assignedUserMode," +
            "dataFilters[le,ge,gt,lt,eq,in,like,dateFilter[startBuffer,endBuffer,startDate,endDate,period,type]," +
            "null,dataItem],attributeValueFilters[le,ge,gt,lt,eq,in,like,dateFilter[startBuffer,endBuffer," +
            "startDate,endDate,period,type],null,attribute,ew,sw]]",
        "RelationshipTypeDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,bIsToA,aIsToB,fromToName,toFromName," +
            "fromConstraint[relationshipEntity,trackedEntityType[id],program[id],programStage[id]," +
            "trackerDataView[attributes,dataElements]],toConstraint[relationshipEntity,trackedEntityType[id]," +
            "program[id],programStage[id],trackerDataView[attributes,dataElements]],bidirectional,access[read," +
            "write,data[read,write]]",
        "SystemInfoDTO" to "serverDate,dateFormat,version,contextPath,systemName,serverTimeZoneId",
        "SystemSettingsDTO" to
            "keyFlag,keyStyle,keyCustomColorMobile,keyDefaultBaseMap,analyticsFinancialYearStart," +
            "analyticsWeeklyStart",
        "TrackedEntityAttributeDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,pattern,sortOrderInListNoProgram,optionSet[id],valueType,expression," +
            "aggregationType,programScope,displayInListNoProgram,generated,displayOnVisitSchedule,confidential," +
            "orgunitScope,unique,inherit,fieldMask,style[color,icon],access[read,write,data[read,write]]," +
            "legendSets[id],formName,displayFormName,preferredSearchOperator,blockedSearchOperators," +
            "minCharactersToSearch",
        "TrackedEntityInstanceFilter37DTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,program[id],description,sortOrder," +
            "enrollmentStatus,followup,enrollmentCreatedPeriod[periodFrom,periodTo],eventFilters[programStage," +
            "eventStatus,eventCreatedPeriod[periodFrom,periodTo],assignedUserMode]",
        "TrackedEntityInstanceFilterDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,style[color,icon],program[id],description," +
            "sortOrder,entityQueryCriteria[followUp,organisationUnit,ouMode,assignedUserMode,order," +
            "displayColumnOrder,eventDate[startBuffer,endBuffer,startDate,endDate,period,type]," +
            "lastUpdatedDate[startBuffer,endBuffer,startDate,endDate,period,type],eventStatus,programStage," +
            "trackedEntityInstances,trackedEntityType,enrollmentStatus,enrollmentIncidentDate[startBuffer," +
            "endBuffer,startDate,endDate,period,type],enrollmentCreatedDate[startBuffer,endBuffer,startDate," +
            "endDate,period,type],attributeValueFilters[le,ge,gt,lt,eq,in,like,dateFilter[startBuffer,endBuffer," +
            "startDate,endDate,period,type],null,attribute,ew,sw]],eventFilters[programStage,eventStatus," +
            "eventCreatedPeriod[periodFrom,periodTo],assignedUserMode]",
        "TrackedEntityTypeDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,trackedEntityTypeAttributes[trackedEntityAttribute[id],displayInList,mandatory," +
            "searchable],featureType,access[read,write,data[read,write]],style[color,icon]," +
            "displayTrackedEntityTypesLabel",
        "TrackerVisualizationDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,description,displayDescription,type,outputType," +
            "program[id],programStage[id],trackedEntityType[id],columns[dimension,dimensionType,program[id]," +
            "programStage[id],items[id],filter,repetition[indexes]],filters[dimension,dimensionType,program[id]," +
            "programStage[id],items[id],filter,repetition[indexes]],sorting[dimension,direction]",
        "UserSettingsDTO" to "keyUiLocale,keyDbLocale",
        "ValidationRuleDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,instruction,importance,operator,periodType,skipFormValidation," +
            "leftSide[expression,description,missingValueStrategy],rightSide[expression,description," +
            "missingValueStrategy],organisationUnitLevels",
        "Visualization36DTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,description,displayDescription,displayFormName," +
            "title,displayTitle,subtitle,displaySubtitle,type,hideTitle,hideSubtitle,hideEmptyColumns," +
            "hideEmptyRows,hideEmptyRowItems,hideLegend,showHierarchy,rowTotals,rowSubTotals,colTotals," +
            "colSubTotals,showDimensionLabels,percentStackedValues,noSpaceBetweenColumns,skipRounding," +
            "displayDensity,digitGroupSeparator,legendSet[id],legendDisplayStyle,legendDisplayStrategy," +
            "aggregationType,columns[id,items[dimensionItem,dimensionItemType]],rows[id,items[dimensionItem," +
            "dimensionItemType]],filters[id,items[dimensionItem,dimensionItemType]]",
        "VisualizationDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,description,displayDescription,displayFormName," +
            "title,displayTitle,subtitle,displaySubtitle,type,hideTitle,hideSubtitle,hideEmptyColumns," +
            "hideEmptyRows,hideEmptyRowItems,hideLegend,showHierarchy,rowTotals,rowSubTotals,colTotals," +
            "colSubTotals,showDimensionLabels,percentStackedValues,noSpaceBetweenColumns,skipRounding," +
            "legend[set[id],showKey,strategy,style],displayDensity,digitGroupSeparator,aggregationType," +
            "columns[id,items[dimensionItem,dimensionItemType]],rows[id,items[dimensionItem,dimensionItemType]]," +
            "filters[id,items[dimensionItem,dimensionItemType]]",
    )

    @Test
    fun request_the_expected_fields_for_each_dto() {
        assertThat(requestedFields).containsExactlyEntriesIn(expectedFields)
    }
}
