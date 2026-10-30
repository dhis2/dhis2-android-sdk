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
import org.hisp.dhis.android.network.constant.ConstantDTO
import org.hisp.dhis.android.network.customicon.CustomIconDTO
import org.hisp.dhis.android.network.dataapproval.DataApprovalDTO
import org.hisp.dhis.android.network.datasetcompleteregistration.DataSetCompleteRegistrationDTO
import org.hisp.dhis.android.network.datavalue.DataValueDTO
import org.hisp.dhis.android.network.option.OptionDTO
import org.hisp.dhis.android.network.optiongroup.OptionGroupDTO
import org.hisp.dhis.android.network.organisationunitlevel.OrganisationUnitLevelDTO
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

    @Test
    fun request_the_expected_fields_for_each_dto() {
        val fields = mapOf(
            "ConstantDTO" to fieldsOf<ConstantDTO>(),
            "CustomIconDTO" to fieldsOf<CustomIconDTO>(),
            "DataApprovalDTO" to fieldsOf<DataApprovalDTO>(),
            "DataSetCompleteRegistrationDTO" to fieldsOf<DataSetCompleteRegistrationDTO>(),
            "DataValueDTO" to fieldsOf<DataValueDTO>(),
            "OptionDTO" to fieldsOf<OptionDTO>(),
            "OptionGroupDTO" to fieldsOf<OptionGroupDTO>(),
            "OrganisationUnitLevelDTO" to fieldsOf<OrganisationUnitLevelDTO>(),
            "SystemInfoDTO" to fieldsOf<SystemInfoDTO>(),
            "UserSettingsDTO" to fieldsOf<UserSettingsDTO>(),
            "ValidationRuleDTO" to fieldsOf<ValidationRuleDTO>(),
        )

        assertThat(fields).containsExactlyEntriesIn(
            mapOf(
                "ConstantDTO" to "id,code,name,displayName,created,lastUpdated,deleted,value",
                "CustomIconDTO" to "key,href,fileResource[id]",
                "DataApprovalDTO" to "wf,ou,pe,aoc,state",
                "DataSetCompleteRegistrationDTO" to
                    "period,dataSet,organisationUnit,attributeOptionCombo,date,storedBy,completed",
                "DataValueDTO" to "deleted,dataElement,period,orgUnit,categoryOptionCombo,attributeOptionCombo," +
                    "value,storedBy,created,lastUpdated,comment,followup",
                "OptionDTO" to
                    "id,code,name,displayName,created,lastUpdated,deleted,sortOrder,optionSet[id],style[color,icon]",
                "OptionGroupDTO" to "id,code,name,displayName,created,lastUpdated,deleted,optionSet[id],options[id]",
                "OrganisationUnitLevelDTO" to "id,code,name,displayName,created,lastUpdated,deleted,level",
                "SystemInfoDTO" to "serverDate,dateFormat,version,contextPath,systemName,serverTimeZoneId",
                "UserSettingsDTO" to "keyUiLocale,keyDbLocale",
                "ValidationRuleDTO" to "id,code,name,displayName,created,lastUpdated,deleted,shortName," +
                    "displayShortName,description,displayDescription,instruction,importance,operator,periodType," +
                    "skipFormValidation,leftSide[expression,description,missingValueStrategy]," +
                    "rightSide[expression,description,missingValueStrategy],organisationUnitLevels",
            ),
        )
    }
}
