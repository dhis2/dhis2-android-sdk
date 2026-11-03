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
import org.hisp.dhis.android.network.constant.ConstantDTO
import org.hisp.dhis.android.network.customicon.CustomIconDTO
import org.hisp.dhis.android.network.dataapproval.DataApprovalDTO
import org.hisp.dhis.android.network.datasetcompleteregistration.DataSetCompleteRegistrationDTO
import org.hisp.dhis.android.network.datavalue.DataValueDTO
import org.hisp.dhis.android.network.expressiondimensionitem.ExpressionDimensionItemDTO
import org.hisp.dhis.android.network.fileresource.FileResourceDTO
import org.hisp.dhis.android.network.indicator.IndicatorDTO
import org.hisp.dhis.android.network.indicatortype.IndicatorTypeDTO
import org.hisp.dhis.android.network.legendset.LegendSetDTO
import org.hisp.dhis.android.network.option.OptionDTO
import org.hisp.dhis.android.network.optiongroup.OptionGroupDTO
import org.hisp.dhis.android.network.optionset.OptionSetDTO
import org.hisp.dhis.android.network.organisationunitlevel.OrganisationUnitLevelDTO
import org.hisp.dhis.android.network.programindicator.ProgramIndicatorDTO
import org.hisp.dhis.android.network.systeminfo.SystemInfoDTO
import org.hisp.dhis.android.network.usersettings.UserSettingsDTO
import org.hisp.dhis.android.network.validationrule.ValidationRuleDTO
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
        "CategoryComboDTO" to fieldsOf<CategoryComboDTO>(),
        "CategoryDTO" to fieldsOf<CategoryDTO>(),
        "ConstantDTO" to fieldsOf<ConstantDTO>(),
        "CustomIconDTO" to fieldsOf<CustomIconDTO>(),
        "DataApprovalDTO" to fieldsOf<DataApprovalDTO>(),
        "DataSetCompleteRegistrationDTO" to fieldsOf<DataSetCompleteRegistrationDTO>(),
        "DataValueDTO" to fieldsOf<DataValueDTO>(),
        "ExpressionDimensionItemDTO" to fieldsOf<ExpressionDimensionItemDTO>(),
        "FileResourceDTO" to fieldsOf<FileResourceDTO>(),
        "IndicatorDTO" to fieldsOf<IndicatorDTO>(),
        "IndicatorTypeDTO" to fieldsOf<IndicatorTypeDTO>(),
        "LegendSetDTO" to fieldsOf<LegendSetDTO>(),
        "OptionDTO" to fieldsOf<OptionDTO>(),
        "OptionGroupDTO" to fieldsOf<OptionGroupDTO>(),
        "OptionSetDTO" to fieldsOf<OptionSetDTO>(),
        "OrganisationUnitLevelDTO" to fieldsOf<OrganisationUnitLevelDTO>(),
        "ProgramIndicatorDTO" to fieldsOf<ProgramIndicatorDTO>(),
        "SystemInfoDTO" to fieldsOf<SystemInfoDTO>(),
        "UserSettingsDTO" to fieldsOf<UserSettingsDTO>(),
        "ValidationRuleDTO" to fieldsOf<ValidationRuleDTO>(),
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
        "CategoryComboDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,isDefault,categories[id]," +
            "categoryOptionCombos[id,code,name,displayName,created,lastUpdated,deleted,categoryOptions[id]]",
        "CategoryDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,dataDimensionType,categoryOptions[id]",
        "ConstantDTO" to "id,code,name,displayName,created,lastUpdated,deleted,value",
        "CustomIconDTO" to "key,href,fileResource[id]",
        "DataApprovalDTO" to "wf,ou,pe,aoc,state",
        "DataSetCompleteRegistrationDTO" to
            "period,dataSet,organisationUnit,attributeOptionCombo,date,storedBy,completed",
        "DataValueDTO" to
            "deleted,dataElement,period,orgUnit,categoryOptionCombo,attributeOptionCombo,value,storedBy," +
            "created,lastUpdated,comment,followup",
        "ExpressionDimensionItemDTO" to "id,code,name,displayName,created,lastUpdated,deleted,expression",
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
        "ProgramIndicatorDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,displayInForm,expression,dimensionItem,filter,decimals,aggregationType," +
            "program[id],analyticsType,analyticsPeriodBoundaries[boundaryTarget," +
            "analyticsPeriodBoundaryType,offsetPeriods,offsetPeriodType],legendSets[id],categoryCombo[id]," +
            "attributeCombo[id],categoryMappingIds",
        "SystemInfoDTO" to "serverDate,dateFormat,version,contextPath,systemName,serverTimeZoneId",
        "UserSettingsDTO" to "keyUiLocale,keyDbLocale",
        "ValidationRuleDTO" to
            "id,code,name,displayName,created,lastUpdated,deleted,shortName,displayShortName,description," +
            "displayDescription,instruction,importance,operator,periodType,skipFormValidation," +
            "leftSide[expression,description,missingValueStrategy],rightSide[expression,description," +
            "missingValueStrategy],organisationUnitLevels",
    )

    @Test
    fun request_the_expected_fields_for_each_dto() {
        assertThat(requestedFields).containsExactlyEntriesIn(expectedFields)
    }
}
