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
package org.hisp.dhis.android.realservertests.generatedschema.tests

import okhttp3.OkHttpClient
import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.dataapproval.DataApprovalState
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.fileresource.FileResourceStorageStatus
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType
import org.hisp.dhis.android.core.program.*
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.android.core.validation.ValidationRuleImportance
import org.hisp.dhis.android.core.validation.ValidationRuleOperator
import org.hisp.dhis.android.core.visualization.*
import org.hisp.dhis.android.realservertests.EnumTestHelper
import org.hisp.dhis.android.realservertests.EnumTestHelper.Companion.entry
import org.hisp.dhis.android.realservertests.OutsideRetrofitFactory
import org.hisp.dhis.android.realservertests.generatedschema.GeneratedSchemaCall
import org.junit.Assert

class GeneratedSchemaUpdatesCheckerRealIntegrationShould {
    private val baseUrl = "https://raw.githubusercontent.com/dhis2/dhis2-json-schema-generator/json-schemas/schemas/"
    private val instanceVersion = "v2.38.1"

    // @Test
    fun check_no_enum_have_been_updated_on_generated_schemas() {
        val retrofit = OutsideRetrofitFactory.retrofit(baseUrl, OkHttpClient())
        val generatedSchemaCall = GeneratedSchemaCall(retrofit, instanceVersion)
        val constantsMap: Map<String, List<String>?> = enumsMap.mapValues {
            generatedSchemaCall.download(it.key).blockingGet().enum
        }

        val errorList = enumsMap.mapNotNull { EnumTestHelper.checkEnum(it, constantsMap[it.key]) }
        if (errorList.isNotEmpty()) {
            Assert.fail(errorList.joinToString(".\n"))
        }
    }

    companion object {
        val enumsMap: Map<String, List<String>> = mapOf(
            entry<DatePeriodType>("datePeriodType"),
            entry<AssignedUserMode>("assignedUserSelectionMode"),
            entry<ValueTypeRenderingType>("valueTypeRenderingType"),
            entry<DataApprovalState>("dataApprovalState"),
            entry<ImportStatus>("importStatus"),
            entry<SectionRenderingType>("sectionRenderingType"),
            entry<RelativePeriod>("relativePeriodEnum"),
            entry<LegendStyle>("legendDisplayStyle"),
            entry<VisualizationType>("visualizationType"),
            entry<EnrollmentStatus>("programStatus"),
            entry<FeatureType>("featureType"),
            entry<FormType>("formType"),
            entry<EventStatus>("eventStatus"),
            entry<OrganisationUnitMode>("organisationUnitSelectionMode"),
            entry<AccessLevel>("accessLevel"),
            entry<ProgramRuleActionType>("programRuleActionType"),
            entry<ProgramRuleVariableSourceType>("programRuleVariableSourceType"),
            entry<ProgramType>("programType"),
            entry<MissingValueStrategy>("missingValueStrategy"),
            entry<ValidationRuleImportance>("importance"),
            entry<ValidationRuleOperator>("operator"),
            entry<DimensionalItemType>("dimensionItemType"),
            entry<VisualizationType>("visualizationType"),
            entry<AggregationType>("aggregationType"),
            entry<AnalyticsType>("analyticsType"),
            entry<ValueType>("valueType"),
            entry<AnalyticsPeriodBoundaryType>("analyticsPeriodBoundaryType"),
            entry<DigitGroupSeparator>("digitGroupSeparator"),
            entry<DisplayDensity>("displayDensity"),
            entry<LegendStrategy>("legendDisplayStrategy"),
            entry<HideEmptyItemStrategy>("hideEmptyItemStrategy"),
            entry<FileResourceStorageStatus>("fileResourceStorageStatus")
        )
    }
}
