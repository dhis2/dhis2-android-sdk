/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.mockwebserver

import android.util.Log
import io.ktor.http.HttpStatusCode
import okhttp3.internal.UTC
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hisp.dhis.android.core.arch.file.IFileReader
import org.hisp.dhis.android.core.arch.file.ResourcesFileReader
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("TooManyFunctions")
class Dhis2MockServer(private val fileReader: IFileReader, port: Int) {
    private val server = MockWebServer()
    private val dhis2Dispatcher = Dhis2Dispatcher(fileReader, ResponseController())

    init {
        start(port)
    }

    constructor(port: Int) : this(ResourcesFileReader(), port) {
        dhis2Dispatcher.configInternalResponseController()
    }

    private fun start(port: Int) {
        try {
            server.start(port)
        } catch (e: IOException) {
            Log.e(MOCKWEBSERVER, "Could not start server")
        }
    }

    fun shutdown() {
        try {
            server.shutdown()
        } catch (e: IOException) {
            Log.e(MOCKWEBSERVER, "Could not shutdown server")
        }
    }

    @JvmOverloads
    fun enqueueMockResponse(code: Int = HttpStatusCode.OK.value) {
        enqueueMockResponseText(code, "{}")
    }

    fun enqueueMockResponseWithEmptyBody(code: Int) {
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(code)
        server.enqueue(mockResponse)
    }

    fun enqueueMockResponseText(code: Int, response: String?) {
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(code)
        mockResponse.setBody(response!!)
        mockResponse.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
        server.enqueue(mockResponse)
    }

    fun enqueueMockResponse(code: Int, fileName: String) {
        val response = createMockResponse(fileName, code)
        server.enqueue(response)
    }

    fun enqueueMockResponse(fileName: String) {
        val response = createMockResponse(fileName)
        response.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
        server.enqueue(response)
    }

    fun setDhis2Dispatcher() {
        server.dispatcher = dhis2Dispatcher
    }

    @Suppress("LongMethod")
    fun setRequestDispatcher() {
        val dispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse()
                    .setResponseCode(HttpStatusCode.NotFound.value)
                    .setBody("Path not found")
                return when {
                    path == "/api/me/authorization" ->
                        createMockResponse(AUTHORITIES_JSON)

                    path.startsWith("/api/me?") ->
                        createMockResponse(USER_JSON)

                    path.startsWith("/api/system/info?") ->
                        createMockResponse(SYSTEM_INFO_JSON)

                    path.startsWith("/api/systemSettings?") ->
                        createMockResponse(SYSTEM_SETTINGS_JSON)

                    path.startsWith("/api/dataStore/USE_CASES/stockUseCases") ->
                        createMockResponse(STOCK_USE_CASES_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/info") ->
                        createMockResponse(ANDROID_SETTINGS_INFO_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTING_APP/general_settings") ->
                        createMockResponse(GENERAL_SETTINGS_V1_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTING_APP/dataSet_settings") ->
                        createMockResponse(DATASET_SETTINGS_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTING_APP/program_settings") ->
                        createMockResponse(PROGRAM_SETTINGS_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/generalSettings") ->
                        createMockResponse(GENERAL_SETTINGS_V2_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/synchronization") ->
                        createMockResponse(SYNCHRONIZATION_SETTTINGS_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/appearance") ->
                        createMockResponse(APPEARANCE_SETTINGS_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/analytics") ->
                        createMockResponse(ANALYTICS_SETTINGS_JSON)

                    path.startsWith("/api/userSettings?") ->
                        createMockResponse(USER_SETTINGS_JSON)

                    path.startsWith("/api/dataStore/APK_DISTRIBUTION/versions") ->
                        createMockResponse(VERSIONS_JSON)

                    path.startsWith("/api/dataStore/APK_DISTRIBUTION/latestVersion") ->
                        createMockResponse(LATEST_APP_VERSION_JSON)

                    path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/customIntents") ->
                        createMockResponse(CUSTOM_INTENTS)

                    path.startsWith("/api/programs?") ->
                        createMockResponse(PROGRAMS_JSON)

                    path.startsWith("/api/programIndicators?") ->
                        createMockResponse(PROGRAMS_INDICATORS_JSON)

                    path.startsWith("/api/programStages?") ->
                        createMockResponse(PROGRAM_STAGES_JSON)

                    path.startsWith("/api/trackedEntityTypes?") ->
                        createMockResponse(TRACKED_ENTITY_TYPES_JSON)

                    path.startsWith("/api/trackedEntityAttributes?") ->
                        createMockResponse(TRACKED_ENTITY_ATTRIBUTES_JSON)

                    path.startsWith("/api/programRules?") ->
                        createMockResponse(PROGRAM_RULES_JSON)

                    path.startsWith("/api/trackedEntityInstanceFilters?") ->
                        createMockResponse(TRACKED_ENTITY_INSTANCE_FILTERS_JSON)

                    path.startsWith("/api/eventFilters?") ->
                        createMockResponse(EVENT_FILTERS_JSON)

                    path.startsWith("/api/programStageWorkingLists?") ->
                        createMockResponse(PROGRAM_STAGE_WORKING_LISTS)

                    path.startsWith("/api/relationshipTypes?") ->
                        createMockResponse(RELATIONSHIP_TYPES_JSON)

                    path.startsWith("/api/optionSets?") ->
                        createMockResponse(OPTION_SETS_JSON)

                    path.startsWith("/api/options?") ->
                        createMockResponse(OPTIONS_JSON)

                    path.startsWith("/api/optionGroups?") ->
                        createMockResponse(OPTION_GROUPS_JSON)

                    path.startsWith("/api/validationRules?dataSet") ->
                        createMockResponse(VALIDATION_RULE_UIDS_JSON)

                    path.startsWith("/api/validationRules?") ->
                        createMockResponse(VALIDATION_RULES_JSON)

                    path.startsWith("/api/dataSets?") ->
                        createMockResponse(DATA_SETS_JSON)

                    path.startsWith("/api/dataElements?") ->
                        createMockResponse(DATA_ELEMENTS_JSON)

                    path.startsWith("/api/attributes?") ->
                        createMockResponse(ATTRIBUTES_JSON)

                    path.startsWith("/api/indicators?") ->
                        createMockResponse(INDICATORS_JSON)

                    path.startsWith("/api/indicatorTypes?") ->
                        createMockResponse(INDICATOR_TYPES_JSON)

                    path.startsWith("/api/categoryCombos?") ->
                        createMockResponse(CATEGORY_COMBOS_JSON)

                    path.startsWith("/api/categories?") ->
                        createMockResponse(CATEGORIES_JSON)

                    path.startsWith("/api/categoryOptions?") ->
                        createMockResponse(CATEGORY_OPTIONS_JSON)

                    path.startsWith("/api/categoryOptions/orgUnits?") ->
                        createMockResponse(CATEGORY_OPTION_ORGUNITS_JSON)

                    path.startsWith("/api/visualizations/PYBH8ZaAQnC?") ->
                        createMockResponse(VISUALIZATIONS_1_JSON)

                    path.startsWith("/api/visualizations/FAFa11yFeFe?") ->
                        createMockResponse(VISUALIZATIONS_2_JSON)

                    path.startsWith("/api/eventVisualizations/s85urBIkN0z?") ->
                        createMockResponse(TRACKER_VISUALIZATIONS_1_JSON)

                    path.startsWith("/api/organisationUnits?") ->
                        createMockResponse(ORGANISATION_UNITS_JSON)

                    path.startsWith("/api/organisationUnitLevels?") ->
                        createMockResponse(ORGANISATION_UNIT_LEVELS_JSON)

                    path.startsWith("/api/constants?") ->
                        createMockResponse(CONSTANTS_JSON)

                    path.startsWith("/api/trackedEntityInstances?") ->
                        createMockResponse(TRACKED_ENTITY_INSTANCES_JSON)

                    path.startsWith("/api/tracker/trackedEntities?") ->
                        createMockResponse(NEW_TRACKED_ENTITY_INSTANCES_JSON)

                    path.startsWith("/api/events?") ->
                        createMockResponse(EVENTS_JSON)

                    path.startsWith("/api/tracker/events?") ->
                        createMockResponse(NEW_EVENTS_JSON)

                    path.startsWith("/api/dataValueSets?") ->
                        createMockResponse(DATA_VALUES_JSON)

                    path.startsWith("/api/completeDataSetRegistrations?") ->
                        createMockResponse(DATA_SET_COMPLETE_REGISTRATIONS_JSON)

                    path.startsWith("/api/dataApprovals/multiple?") ->
                        createMockResponse(DATA_APPROVALS_MULTIPLE_JSON)

                    path.startsWith("/api/legendSets?") ->
                        createMockResponse(LEGEND_SETS_JSON)

                    path.startsWith("/api/expressionDimensionItems?") ->
                        createMockResponse(EXPRESSION_DIMENSION_ITEMS)

                    path.startsWith("/api/icons?") ->
                        createMockResponse(CUSTOM_ICONS_JSON)

                    path.startsWith("/api/trackedEntityAttributes/aejWyOfXge6/generateAndReserve") ->
                        createMockResponse(RESERVE_VALUES_JSON)

                    path.startsWith("/api/metadata") ->
                        createMockResponse(SMS_METADATA)

                    path.startsWith("/api/fileResources?") ->
                        createMockResponse(FILE_RESOURCES)

                    path.startsWith("/api/fileResources/befryEfXge5") ->
                        createMockResponse(FILE_RESOURCE)

                    path.startsWith("/api/trackedEntityInstances/nWrB0TfWlvh/aejWyOfXge6/image") ->
                        createMockResponse(TRACKED_ENTITY_IMAGE)

                    path == "/api/dataStore" ->
                        createMockResponse(DATA_STORE_NAMESPACES)

                    path.startsWith("/api/dataStore/capture") ->
                        createMockResponse(DATA_STORE_NAMESPACE_CAPTURE)

                    path.startsWith("/api/dataStore/scorecard") ->
                        createMockResponse(DATA_STORE_NAMESPACE_SCORECARD)

                    else -> {
                        MockResponse()
                            .setResponseCode(HttpStatusCode.NotFound.value)
                            .setBody("Path not present in Dhis2MockServer dispatcher: $path")
                    }
                }
            }
        }
        server.dispatcher = dispatcher
    }

    fun enqueueLoginResponses() {
        enqueueMockResponse(USER_JSON)
        enqueueMockResponse(ANDROID_SETTINGS_INFO_JSON)
        enqueueMockResponse(GENERAL_SETTINGS_V2_JSON)
        enqueueMockResponse(SYSTEM_INFO_JSON)
    }

    fun enqueueSystemInfoResponse() {
        enqueueMockResponse(SYSTEM_INFO_JSON)
    }

    fun enqueueMetadataResponses() {
        enqueueMockResponse(ANDROID_SETTINGS_INFO_JSON)
        enqueueMockResponse(GENERAL_SETTINGS_V2_JSON)
        enqueueMockResponse(SYSTEM_INFO_JSON)
        enqueueMockResponse(GENERAL_SETTINGS_V2_JSON)
        enqueueMockResponse(SYNCHRONIZATION_SETTTINGS_JSON)
        enqueueMockResponse(APPEARANCE_SETTINGS_JSON)
        enqueueMockResponse(ANALYTICS_SETTINGS_JSON)
        enqueueMockResponse(CUSTOM_INTENTS)
        enqueueMockResponse(USER_SETTINGS_JSON)
        enqueueMockResponse(SYSTEM_SETTINGS_JSON)
        enqueueMockResponse(VERSIONS_JSON)
        enqueueMockResponse(STOCK_USE_CASES_JSON)
        enqueueMockResponse(CONSTANTS_JSON)
        enqueueMockResponse(USER_JSON)
        enqueueMockResponse(AUTHORITIES_JSON)
        enqueueMockResponse(ORGANISATION_UNIT_LEVELS_JSON)
        enqueueMockResponse(ORGANISATION_UNITS_JSON)
        enqueueMockResponse(PROGRAMS_JSON)
        enqueueMockResponse(PROGRAM_STAGES_JSON)
        enqueueMockResponse(TRACKED_ENTITY_TYPES_JSON)
        enqueueMockResponse(TRACKED_ENTITY_ATTRIBUTES_JSON)
        enqueueMockResponse(PROGRAM_RULES_JSON)
        enqueueMockResponse(RELATIONSHIP_TYPES_JSON)
        enqueueMockResponse(OPTION_SETS_JSON)
        enqueueMockResponse(OPTIONS_JSON)
        enqueueMockResponse(OPTION_GROUPS_JSON)
        enqueueMockResponse(TRACKED_ENTITY_INSTANCE_FILTERS_JSON)
        enqueueMockResponse(EVENT_FILTERS_JSON)
        enqueueMockResponse(PROGRAM_STAGE_WORKING_LISTS)
        enqueueMockResponse(DATA_SETS_JSON)
        enqueueMockResponse(DATA_ELEMENTS_JSON)
        enqueueMockResponse(VALIDATION_RULE_UIDS_JSON)
        enqueueMockResponse(VALIDATION_RULE_UIDS_JSON)
        enqueueMockResponse(VALIDATION_RULE_UIDS_JSON)
        enqueueMockResponse(VALIDATION_RULES_JSON)
        enqueueMockResponse(CATEGORY_COMBOS_JSON)
        enqueueMockResponse(CATEGORIES_JSON)
        enqueueMockResponse(CATEGORY_OPTIONS_JSON)
        enqueueMockResponse(CATEGORY_OPTION_ORGUNITS_JSON)
        enqueueMockResponse(VISUALIZATIONS_1_JSON)
        enqueueMockResponse(VISUALIZATIONS_2_JSON)
        enqueueMockResponse(HttpStatusCode.NotFound.value)
        enqueueMockResponse(TRACKER_VISUALIZATIONS_1_JSON)
        enqueueMockResponse(PROGRAMS_INDICATORS_JSON)
        enqueueMockResponse(PROGRAMS_INDICATORS_JSON)
        enqueueMockResponse(INDICATORS_JSON)
        enqueueMockResponse(INDICATOR_TYPES_JSON)
        enqueueMockResponse(LEGEND_SETS_JSON)
        enqueueMockResponse(ATTRIBUTES_JSON)
        enqueueMockResponse(EXPRESSION_DIMENSION_ITEMS)
        enqueueMockResponse(CUSTOM_ICONS_JSON)
    }

    private fun createMockResponse(fileName: String): MockResponse {
        return createMockResponse(fileName, HttpStatusCode.OK.value)
    }

    private fun createMockResponse(fileName: String, code: Int): MockResponse {
        try {
            val body = fileReader.getStringFromFile(fileName)
            val response = MockResponse()
            response.setResponseCode(code)
            response.setBody(body!!)
            response.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
            return response
        } catch (e: IOException) {
            return MockResponse().setResponseCode(HttpStatusCode.InternalServerError.value)
                .setBody("Error reading JSON file for MockServer")
        }
    }

    fun enqueueMockResponse(fileName: String, dateHeader: Date) {
        val response = createMockResponse(fileName)

        val rfc1123: DateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)
        rfc1123.isLenient = false
        rfc1123.timeZone = UTC
        val dateHeaderValue = rfc1123.format(dateHeader)

        response.setHeader("Date", dateHeaderValue)

        server.enqueue(response)
    }

    val baseEndpoint: String
        get() = server.url("/").toString()

    @Throws(InterruptedException::class)
    fun takeRequest(): RecordedRequest {
        return server.takeRequest()
    }

    fun addResponse(
        method: String,
        path: String,
        responseName: String,
        responseCode: Int,
        contentType: String = CONTENT_TYPE_JSON,
    ) {
        dhis2Dispatcher.addResponse(method, path, responseName, responseCode, contentType)
    }

    companion object {
        private const val AUTHORITIES_JSON = "authority/authorities.json"
        private const val SYSTEM_INFO_JSON = "systeminfo/system_info.json"
        private const val SYSTEM_SETTINGS_JSON = "settings/system_settings.json"
        private const val STOCK_USE_CASES_JSON = "usecase.stock/stock_use_cases.json"
        private const val ANDROID_SETTINGS_INFO_JSON = "settings/app_info.json"
        private const val GENERAL_SETTINGS_V1_JSON = "settings/general_settings_v1.json"
        private const val GENERAL_SETTINGS_V2_JSON = "settings/general_settings_v2.json"
        private const val DATASET_SETTINGS_JSON = "settings/dataset_settings.json"
        private const val PROGRAM_SETTINGS_JSON = "settings/program_settings.json"
        private const val SYNCHRONIZATION_SETTTINGS_JSON = "settings/synchronization_settings.json"
        private const val APPEARANCE_SETTINGS_JSON = "settings/appearance_settings_v2.json"
        private const val ANALYTICS_SETTINGS_JSON = "settings/analytics_settings_v3.json"
        private const val USER_SETTINGS_JSON = "settings/user_settings.json"
        private const val VERSIONS_JSON = "settings/versions.json"
        private const val LATEST_APP_VERSION_JSON = "settings/latest_app_version.json"
        private const val CUSTOM_INTENTS = "settings/custom_intents.json"
        private const val PROGRAMS_JSON = "program/programs.json"
        private const val PROGRAMS_INDICATORS_JSON = "program/program_indicators.json"
        private const val PROGRAM_STAGES_JSON = "program/program_stages.json"
        private const val PROGRAM_RULES_JSON = "program/program_rules.json"
        private const val TRACKED_ENTITY_INSTANCE_FILTERS_JSON =
            "trackedentity/tracked_entity_instance_filters.json"
        private const val EVENT_FILTERS_JSON = "event/event_filters.json"

        private const val PROGRAM_STAGE_WORKING_LISTS =
            "programstageworkinglist/program_stage_working_lists.json"
        private const val TRACKED_ENTITY_TYPES_JSON = "trackedentity/tracked_entity_types.json"
        private const val TRACKED_ENTITY_ATTRIBUTES_JSON = "trackedentity/tracked_entity_attributes.json"
        private const val RELATIONSHIP_TYPES_JSON = "relationship/relationship_types.json"
        private const val OPTION_SETS_JSON = "option/option_sets.json"
        private const val OPTIONS_JSON = "option/options.json"
        private const val OPTION_GROUPS_JSON = "option/option_groups.json"
        private const val VALIDATION_RULE_UIDS_JSON = "validation/validation_rule_uids.json"
        private const val VALIDATION_RULES_JSON = "validation/validation_rules.json"
        private const val DATA_SETS_JSON = "dataset/data_sets.json"
        private const val DATA_ELEMENTS_JSON = "dataelement/data_elements.json"
        private const val ATTRIBUTES_JSON = "attribute/attributes.json"
        private const val INDICATORS_JSON = "indicators/indicators.json"
        private const val INDICATOR_TYPES_JSON = "indicators/indicator_types.json"
        private const val CATEGORY_COMBOS_JSON = "category/category_combos.json"
        private const val CATEGORIES_JSON = "category/categories.json"
        private const val CATEGORY_OPTIONS_JSON = "category/category_options.json"
        private const val CATEGORY_OPTION_ORGUNITS_JSON = "category/category_option_orgunits.json"
        private const val VISUALIZATIONS_1_JSON = "visualization/visualizations_1.json"
        private const val VISUALIZATIONS_2_JSON = "visualization/visualizations_2.json"
        private const val TRACKER_VISUALIZATIONS_1_JSON = "visualization/tracker_visualizations_1.json"
        private const val ORGANISATION_UNIT_LEVELS_JSON = "organisationunit/organisation_unit_levels.json"
        private const val CONSTANTS_JSON = "constant/constants.json"
        private const val USER_JSON = "user/user38.json"
        private const val EVENTS_JSON = "event/events.json"
        private const val NEW_EVENTS_JSON = "event/new_tracker_importer_events.json"
        private const val LEGEND_SETS_JSON = "legendset/legend_sets.json"
        private const val EXPRESSION_DIMENSION_ITEMS =
            "expressiondimensionitem/expression_dimension_items.json"
        private const val CUSTOM_ICONS_JSON = "icon/custom_icons.json"
        private const val TRACKED_ENTITY_INSTANCES_JSON = "trackedentity/tracked_entity_instances.json"
        private const val NEW_TRACKED_ENTITY_INSTANCES_JSON =
            "trackedentity/new_tracker_importer_tracked_entities.json"
        private const val DATA_VALUES_JSON = "datavalue/data_values.json"
        private const val TRACKED_ENTITY_IMAGE = "trackedentity/tracked_entity_attribute_value_image.png"
        private const val FILE_RESOURCES =
            "trackedentity/tracked_entity_attribute_value_image_resources.json"
        private const val FILE_RESOURCE =
            "trackedentity/tracked_entity_attribute_value_image_resource.json"
        private const val DATA_STORE_NAMESPACES = "datastore/namespaces.json"
        private const val DATA_STORE_NAMESPACE_CAPTURE = "datastore/namespace_capture.json"
        private const val DATA_STORE_NAMESPACE_SCORECARD = "datastore/namespace_scorecard.json"
        private const val DATA_SET_COMPLETE_REGISTRATIONS_JSON =
            "dataset/data_set_complete_registrations.json"
        private const val DATA_APPROVALS_MULTIPLE_JSON = "dataapproval/data_approvals_multiple.json"
        private const val ORGANISATION_UNITS_JSON = "organisationunit/organisation_units.json"
        private const val RESERVE_VALUES_JSON =
            "trackedentity/tracked_entity_attribute_reserved_values.json"
        private const val SMS_METADATA = "sms/metadata_ids.json"
        private const val MOCKWEBSERVER = "Dhis2MockWebServer"
        private const val CONTENT_TYPE = "Content-Type"
        private const val CONTENT_TYPE_JSON = "application/json"
    }
}
