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
package org.hisp.dhis.android.core.common

import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2Factory
import org.hisp.dhis.android.core.common.schema.Schema
import org.hisp.dhis.android.core.data.server.RealServerMother
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType
import org.hisp.dhis.android.core.program.*
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.android.core.validation.ValidationRuleImportance
import org.hisp.dhis.android.core.validation.ValidationRuleOperator
import org.hisp.dhis.android.core.visualization.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EnumUpdatesCheckerRealIntegrationShould : BaseRealIntegrationTest() {
    private lateinit var d2: D2

    @Before
    override fun setUp() {
        super.setUp()
        d2 = D2Factory.forNewDatabase()
    }

    @Test
    fun check_no_enum_have_been_updated_on_server() {
        d2.userModule().blockingLogIn(username, password, RealServerMother.url2_38)
        val schemas: List<Schema> = getD2DIComponent(d2).schemaCall().download().blockingGet()
        val constantsMap: Map<String, List<String>?> = schemas.flatMap { schema ->
            schema.properties.filter { it.propertyType == "CONSTANT" }
        }.toSet().associate { fullKlassToSimpleKlass(it.klass) to it.constants }

        val errorList = enumsMap.mapNotNull { checkEnum(it, constantsMap[it.key]) }
        if (errorList.isNotEmpty()) {
            Assert.fail(errorList.joinToString())
        }
    }

    private fun checkEnum(sdkEnumEntry: Map.Entry<String, List<String>>, apiConstants: List<String>?): String? {
        if (apiConstants == null) {
            return "Enum ${sdkEnumEntry.key} not found on the server"
        } else if (!sdkEnumEntry.value.containsAll(apiConstants)) {
            val constantsThatDoesNotExistInTheSdk = apiConstants.filter { !sdkEnumEntry.value.contains(it) }
            return "Constants ${constantsThatDoesNotExistInTheSdk.joinToString()} " +
                    "from enum ${sdkEnumEntry.key} does not exist in the SDK"
        }
        return null
    }

    private fun fullKlassToSimpleKlass(fullKlass: String): String {
        return fullKlass.split(".").last()
    }

    companion object {
        val enumsMap: Map<String, List<String>> = mapOf(
            Pair("VisualizationType", VisualizationType.values().map { it.toString() }),
            Pair("ProgramStatus", EnrollmentStatus.values().map { it.toString() }),
            Pair("FeatureType", FeatureType.values().map { it.toString() }),
            Pair("FormType", FormType.values().map { it.toString() }),
            Pair("EventStatus", EventStatus.values().map { it.toString() }),
            Pair("OrganisationUnitSelectionMode", OrganisationUnitMode.values().map { it.toString() }),
            Pair("AccessLevel", AccessLevel.values().map { it.toString() }),
            Pair("ProgramRuleActionType", ProgramRuleActionType.values().map { it.toString() }),
            Pair("ProgramRuleVariableSourceType", ProgramRuleVariableSourceType.values().map { it.toString() }),
            Pair("ProgramType", ProgramType.values().map { it.toString() }),
            Pair("MissingValueStrategy", MissingValueStrategy.values().map { it.toString() }),
            Pair("Importance", ValidationRuleImportance.values().map { it.toString() }),
            Pair("Operator", ValidationRuleOperator.values().map { it.toString() }),
            Pair("DimensionItemType", DimensionalItemType.values().map { it.toString() }),
            Pair("VisualizationType", VisualizationType.values().map { it.toString() }),
            Pair("AggregationType", AggregationType.values().map { it.toString() }),
            Pair("AnalyticsType", AnalyticsType.values().map { it.toString() }),
            Pair("ValueType", ValueType.values().map { it.toString() }),
            Pair("AnalyticsPeriodBoundaryType", AnalyticsPeriodBoundaryType.values().map { it.toString() }),
            Pair("DigitGroupSeparator", DigitGroupSeparator.values().map { it.toString() }),
            Pair("DisplayDensity", DisplayDensity.values().map { it.toString() }),
            Pair("LegendDisplayStrategy", LegendStrategy.values().map { it.toString() }),
            Pair("HideEmptyItemStrategy", HideEmptyItemStrategy.values().map { it.toString() })
        )
    }
}
