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
package org.hisp.dhis.android.realservertests.apischema.tests

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.data.server.RealServerMother
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.fileresource.FileResourceStorageStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundaryType
import org.hisp.dhis.android.core.program.ProgramRuleActionType
import org.hisp.dhis.android.core.program.ProgramRuleVariableSourceType
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.android.core.validation.ValidationRuleImportance
import org.hisp.dhis.android.core.validation.ValidationRuleOperator
import org.hisp.dhis.android.core.visualization.DigitGroupSeparator
import org.hisp.dhis.android.core.visualization.DisplayDensity
import org.hisp.dhis.android.core.visualization.HideEmptyItemStrategy
import org.hisp.dhis.android.core.visualization.LegendStrategy
import org.hisp.dhis.android.core.visualization.VisualizationType
import org.hisp.dhis.android.realservertests.EnumTestHelper.Companion.checkEnum
import org.hisp.dhis.android.realservertests.EnumTestHelper.Companion.entry
import org.hisp.dhis.android.realservertests.apischema.ApiSchema
import org.hisp.dhis.android.realservertests.apischema.ApiSchemaCall
import org.junit.Assert

class ApiSchemaUpdatesCheckerRealIntegrationShould : BaseRealIntegrationTest() {

//    @Test
    fun check_no_enum_have_been_updated_on_server() = runTest {
        d2.userModule().blockingLogIn(username, password, RealServerMother.url2_39)
        val apiSchemas: List<ApiSchema> = ApiSchemaCall(d2.ktor()).download()
        val constantsMap: Map<String, List<String>?> = apiSchemas.flatMap { apiSchema ->
            apiSchema.properties.filter { it.propertyType == "CONSTANT" }
        }.toSet().associate { fullKlassToSimpleKlass(it.klass) to it.constants }

        val errorList = enumsMap.mapNotNull { checkEnum(it, constantsMap[it.key]) }
        if (errorList.isNotEmpty()) {
            Assert.fail(errorList.joinToString(".\n"))
        }
    }

    private fun fullKlassToSimpleKlass(fullKlass: String): String {
        return fullKlass.split(".").last()
    }

    companion object {
        val enumsMap: Map<String, List<String>> = mapOf(
            entry<VisualizationType>("VisualizationType"),
            entry<EnrollmentStatus>("ProgramStatus"),
            entry<FeatureType>("FeatureType"),
            entry<FormType>("FormType"),
            entry<EventStatus>("EventStatus"),
            entry<OrganisationUnitMode>("OrganisationUnitSelectionMode"),
            entry<AccessLevel>("AccessLevel"),
            entry<ProgramRuleActionType>("ProgramRuleActionType"),
            entry<ProgramRuleVariableSourceType>("ProgramRuleVariableSourceType"),
            entry<ProgramType>("ProgramType"),
            entry<MissingValueStrategy>("MissingValueStrategy"),
            entry<ValidationRuleImportance>("Importance"),
            entry<ValidationRuleOperator>("Operator"),
            entry<DimensionalItemType>("DimensionItemType"),
            entry<VisualizationType>("VisualizationType"),
            entry<AggregationType>("AggregationType"),
            entry<AnalyticsType>("AnalyticsType"),
            entry<ValueType>("ValueType"),
            entry<AnalyticsPeriodBoundaryType>("AnalyticsPeriodBoundaryType"),
            entry<DigitGroupSeparator>("DigitGroupSeparator"),
            entry<DisplayDensity>("DisplayDensity"),
            entry<LegendStrategy>("LegendDisplayStrategy"),
            entry<HideEmptyItemStrategy>("HideEmptyItemStrategy"),
            entry<FileResourceStorageStatus>("FileResourceStorageStatus"),
        )
    }
}
