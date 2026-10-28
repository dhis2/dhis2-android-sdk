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
package org.hisp.dhis.android.core.analytics.trackerlinelist.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.trackerlinelist.DataFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.DateFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.EnumFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.OrganisationUnitFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListSortingItem
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.common.SortingDirection
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.TrackerVisualization
import org.hisp.dhis.android.core.visualization.TrackerVisualizationDimension
import org.hisp.dhis.android.core.visualization.TrackerVisualizationDimensionBuilder
import org.hisp.dhis.android.core.visualization.TrackerVisualizationDimensionRepetition
import org.hisp.dhis.android.core.visualization.TrackerVisualizationOutputType
import org.hisp.dhis.android.core.visualization.TrackerVisualizationSorting
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackerVisualizationMapperShould {

    private val organisationUniLevelStore: OrganisationUnitLevelStore = mock()

    private val mapper = TrackerVisualizationMapper(organisationUniLevelStore)

    @Test
    fun should_map_data_filters() {
        val item = trackerVisualizationDimensionBuilder()
            .dimension("uid1")
            .filter("GT:6:ILIKE:ar:NE:4:NE:NV")
            .build()

        val dataFilters = mapper.mapDataFilters(item)

        assertThat(dataFilters).containsExactly(
            DataFilter.GreaterThan("6"),
            DataFilter.Like("ar", ignoreCase = true),
            DataFilter.NotEqualTo("4", ignoreCase = false),
            DataFilter.IsNotNullOrEmpty,
        )
    }

    @Test
    fun should_map_date_filters() {
        val item = trackerVisualizationDimensionBuilder()
            .dimension("uid1")
            .items(
                listOf(
                    ObjectWithUid.create("202403"),
                    ObjectWithUid.create("2024-03-05_2024-04-01"),
                ),
            )
            .build()

        val dateFilters = mapper.mapDateFilters(item)

        assertThat(dateFilters).containsExactly(
            DateFilter.Absolute("202403"),
            DateFilter.Range("2024-03-05", "2024-04-01"),
        )
    }

    @Test
    fun should_map_program_status() {
        val item = trackerVisualizationDimensionBuilder()
            .dimensionType("dataX")
            .dimension("programStatus")
            .items(
                listOf(
                    ObjectWithUid.create(EnrollmentStatus.ACTIVE.name),
                    ObjectWithUid.create(EnrollmentStatus.CANCELLED.name),
                ),
            )
            .build()

        val programStatus = mapper.mapDataX(item)

        assertThat(programStatus).isEqualTo(
            TrackerLineListItem.ProgramStatusItem(
                null,
                listOf(
                    EnumFilter.In(
                        listOf(
                            EnrollmentStatus.ACTIVE,
                            EnrollmentStatus.CANCELLED,
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun should_map_categories() {
        val categoryType = "CATEGORY"
        val categoryId = "categoryId"
        val categoryOption1 = "categoryOption1"
        val categoryOption2 = "categoryOption2"

        val item = trackerVisualizationDimensionBuilder()
            .dimensionType(categoryType)
            .dimension(categoryId)
            .items(
                listOf(
                    ObjectWithUid.create(categoryOption1),
                    ObjectWithUid.create(categoryOption2),
                ),
            )
            .build()
        val category = mapper.mapCategory(item)

        assertThat(category).isEqualTo(
            TrackerLineListItem.Category(
                categoryId,
                listOf(
                    DataFilter.In(
                        listOf(
                            categoryOption1,
                            categoryOption2,
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun should_map_visualization_identifiers_and_output_type() = runTest {
        val visualization = trackerVisualizationBuilder()
            .program(ObjectWithUid.create("programUid"))
            .programStage(ObjectWithUid.create("stageUid"))
            .trackedEntityType(ObjectWithUid.create("tetUid"))
            .outputType(TrackerVisualizationOutputType.ENROLLMENT)
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.trackerVisualization).isEqualTo("visualization_uid")
        assertThat(params.programId).isEqualTo("programUid")
        assertThat(params.programStageId).isEqualTo("stageUid")
        assertThat(params.trackedEntityTypeId).isEqualTo("tetUid")
        assertThat(params.outputType).isEqualTo(TrackerLineListOutputType.ENROLLMENT)
        assertThat(params.columns).isEmpty()
        assertThat(params.filters).isEmpty()
        assertThat(params.sorting).isEmpty()
    }

    @Test
    fun should_map_every_output_type() = runTest {
        val expected = mapOf(
            TrackerVisualizationOutputType.EVENT to TrackerLineListOutputType.EVENT,
            TrackerVisualizationOutputType.ENROLLMENT to TrackerLineListOutputType.ENROLLMENT,
            TrackerVisualizationOutputType.TRACKED_ENTITY_INSTANCE to TrackerLineListOutputType.TRACKED_ENTITY_INSTANCE,
            null to null,
        )

        expected.forEach { (type, outputType) ->
            val params = mapper.toTrackerLineListParams(trackerVisualizationBuilder().outputType(type).build())
            assertThat(params.outputType).isEqualTo(outputType)
        }
    }

    @Test
    fun should_map_columns_and_filters_and_drop_unknown_dimension_types() = runTest {
        val visualization = trackerVisualizationBuilder()
            .columns(
                listOf(
                    dimension("PROGRAM_INDICATOR", "indicatorUid", filter = "GT:5"),
                    dimension("PROGRAM_ATTRIBUTE", "attributeUid", filter = "LIKE:abc"),
                    dimension("UNKNOWN", "unknownUid"),
                    dimension(null, "noTypeUid"),
                ),
            )
            .filters(listOf(dimension("CATEGORY", "categoryUid")))
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.columns).containsExactly(
            TrackerLineListItem.ProgramIndicator("indicatorUid", listOf(DataFilter.GreaterThan("5"))),
            TrackerLineListItem.ProgramAttribute("attributeUid", listOf(DataFilter.Like("abc"))),
        ).inOrder()
        assertThat(params.filters).containsExactly(TrackerLineListItem.Category("categoryUid", emptyList()))
    }

    @Test
    fun should_throw_for_organisation_unit_group_set_dimensions() = runTest {
        val visualization = trackerVisualizationBuilder()
            .filters(listOf(dimension("ORGANISATION_UNIT_GROUP_SET", "groupSetUid")))
            .build()

        val error = runCatching { mapper.toTrackerLineListParams(visualization) }.exceptionOrNull()

        assertThat(error).isInstanceOf(AnalyticsException.InvalidArguments::class.java)
    }

    @Test
    fun should_map_organisation_unit_filters() = runTest {
        whenever(organisationUniLevelStore.selectOneWhere(any())).doReturn(
            OrganisationUnitLevel.builder().uid("level3Uid").level(3).build(),
        )
        val visualization = trackerVisualizationBuilder()
            .columns(
                listOf(
                    dimension(
                        "ORGANISATION_UNIT",
                        "ou",
                        program = "programUid",
                        items = listOf(
                            RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN.name,
                            "LEVEL-3",
                            "OU_GROUP-CXw2yu5fodb",
                            "DiszpKrYNg8",
                            "not a valid uid",
                        ),
                    ),
                ),
            )
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.columns).containsExactly(
            TrackerLineListItem.OrganisationUnitItem(
                programUid = "programUid",
                filters = listOf(
                    OrganisationUnitFilter.Relative(RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN),
                    OrganisationUnitFilter.Level("level3Uid"),
                    OrganisationUnitFilter.Group("CXw2yu5fodb"),
                    OrganisationUnitFilter.Absolute("DiszpKrYNg8"),
                ),
            ),
        )
        verify(organisationUniLevelStore).selectOneWhere(argThat { contains("3") })
    }

    @Test
    fun should_throw_if_organisation_unit_level_does_not_exist() = runTest {
        val visualization = trackerVisualizationBuilder()
            .columns(listOf(dimension("ORGANISATION_UNIT", "ou", items = listOf("LEVEL-9"))))
            .build()

        val error = runCatching { mapper.toTrackerLineListParams(visualization) }.exceptionOrNull()

        assertThat(error).isInstanceOf(AnalyticsException.InvalidOrganisationUnitLevel::class.java)
    }

    @Test
    fun should_map_organisation_unit_without_items() = runTest {
        val visualization = trackerVisualizationBuilder()
            .columns(listOf(dimension("ORGANISATION_UNIT", "ou")))
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.columns).containsExactly(TrackerLineListItem.OrganisationUnitItem(null, emptyList()))
    }

    @Test
    fun should_map_period_dimensions() = runTest {
        val items = listOf(RelativePeriod.LAST_3_DAYS.name)
        val dateFilters = listOf(DateFilter.Relative(RelativePeriod.LAST_3_DAYS))
        val visualization = trackerVisualizationBuilder()
            .columns(
                listOf(
                    dimension("PERIOD", "lastUpdated", items = items),
                    dimension("PERIOD", "incidentDate", program = "programUid", items = items),
                    dimension("PERIOD", "enrollmentDate", program = "programUid", items = items),
                    dimension("PERIOD", "scheduledDate", items = items),
                    dimension("PERIOD", "eventDate"),
                    dimension("PERIOD", "unknownDate", items = items),
                ),
            )
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.columns).containsExactly(
            TrackerLineListItem.LastUpdated(dateFilters),
            TrackerLineListItem.IncidentDate("programUid", dateFilters),
            TrackerLineListItem.EnrollmentDate("programUid", dateFilters),
            TrackerLineListItem.ScheduledDate(dateFilters),
            TrackerLineListItem.EventDate(emptyList()),
        ).inOrder()
    }

    @Test
    fun should_map_program_data_element_with_its_own_program_stage_and_repetition() = runTest {
        val visualization = trackerVisualizationBuilder()
            .programStage(ObjectWithUid.create("visualizationStage"))
            .columns(
                listOf(
                    dimensionBuilder("PROGRAM_DATA_ELEMENT", "dataElementUid")
                        .programStage(ObjectWithUid.create("dimensionStage"))
                        .filter("IN:a;b")
                        .repetition(TrackerVisualizationDimensionRepetition.builder().indexes(listOf(1, -1)).build())
                        .build(),
                ),
            )
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.columns).containsExactly(
            TrackerLineListItem.ProgramDataElement(
                dataElement = "dataElementUid",
                programStage = "dimensionStage",
                filters = listOf(DataFilter.In(listOf("a", "b"))),
                repetitionIndexes = listOf(1, -1),
            ),
        )
    }

    @Test
    fun should_map_program_data_element_using_visualization_program_stage_as_fallback() = runTest {
        val visualization = trackerVisualizationBuilder()
            .programStage(ObjectWithUid.create("visualizationStage"))
            .columns(listOf(dimension("PROGRAM_DATA_ELEMENT", "dataElementUid")))
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.columns).containsExactly(
            TrackerLineListItem.ProgramDataElement("dataElementUid", "visualizationStage", emptyList(), null),
        )
    }

    @Test
    fun should_map_data_x_dimensions() {
        assertThat(mapper.mapDataX(dimension("DATA_X", "createdBy"))).isEqualTo(TrackerLineListItem.CreatedBy)
        assertThat(mapper.mapDataX(dimension("DATA_X", "lastUpdatedBy"))).isEqualTo(TrackerLineListItem.LastUpdatedBy)
        assertThat(mapper.mapDataX(dimension("DATA_X", "unknown"))).isNull()
    }

    @Test
    fun should_map_event_status_ignoring_unknown_values() {
        val item = dimension(
            "DATA_X",
            "eventStatus",
            items = listOf(EventStatus.COMPLETED.name, "UNKNOWN", EventStatus.SCHEDULE.name),
        )

        val eventStatus = mapper.mapDataX(item)

        assertThat(eventStatus).isEqualTo(
            TrackerLineListItem.EventStatusItem(
                listOf(EnumFilter.In(listOf(EventStatus.COMPLETED, EventStatus.SCHEDULE))),
            ),
        )
    }

    @Test
    fun should_map_status_dimensions_without_valid_items_to_empty_filters() {
        val programStatus = mapper.mapDataX(dimension("DATA_X", "programStatus", program = "programUid"))
        val eventStatus = mapper.mapDataX(dimension("DATA_X", "eventStatus", items = listOf("UNKNOWN")))

        assertThat(programStatus).isEqualTo(TrackerLineListItem.ProgramStatusItem("programUid", emptyList()))
        assertThat(eventStatus).isEqualTo(TrackerLineListItem.EventStatusItem(emptyList()))
    }

    @Test
    fun should_map_data_x_dimension_from_visualization_columns() = runTest {
        val visualization = trackerVisualizationBuilder()
            .columns(listOf(dimension("DATA_X", "createdBy"), dimension("DATA_X", "unknown")))
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.columns).containsExactly(TrackerLineListItem.CreatedBy)
    }

    @Test
    fun should_map_every_data_filter_operator() {
        val item = dimension(
            "PROGRAM_ATTRIBUTE",
            "uid1",
            filter = "EQ:NV:EQ:a:!EQ:b:IEQ:c:!IEQ:d:GE:1:LT:2:LE:3:LIKE:e:!LIKE:f:!ILIKE:g:IN:h;i:UNKNOWN:j",
        )

        val dataFilters = mapper.mapDataFilters(item)

        assertThat(dataFilters).containsExactly(
            DataFilter.IsNullOrEmpty,
            DataFilter.EqualTo("a"),
            DataFilter.NotEqualTo("b"),
            DataFilter.EqualTo("c", ignoreCase = true),
            DataFilter.NotEqualTo("d", ignoreCase = true),
            DataFilter.GreaterThanOrEqualTo("1"),
            DataFilter.LowerThan("2"),
            DataFilter.LowerThanOrEqualTo("3"),
            DataFilter.Like("e"),
            DataFilter.NotLike("f"),
            DataFilter.NotLike("g", ignoreCase = true),
            DataFilter.In(listOf("h", "i")),
        ).inOrder()
    }

    @Test
    fun should_return_no_data_filters_when_filter_is_empty() {
        assertThat(mapper.mapDataFilters(dimension("PROGRAM_ATTRIBUTE", "uid1"))).isEmpty()
        assertThat(mapper.mapDataFilters(dimension("PROGRAM_ATTRIBUTE", "uid1", filter = ""))).isEmpty()
    }

    @Test
    fun should_return_no_date_filters_when_there_are_no_items() {
        assertThat(mapper.mapDateFilters(dimension("PERIOD", "eventDate"))).isEmpty()
    }

    @Test
    fun should_map_related_program() {
        assertThat(mapper.mapRelatedProgram(dimension("PERIOD", "incidentDate", program = "programUid")))
            .isEqualTo("programUid")
        assertThat(mapper.mapRelatedProgram(dimension("PERIOD", "incidentDate"))).isNull()
    }

    @Test
    fun should_map_sorting_of_mapped_dimensions() = runTest {
        val visualization = trackerVisualizationBuilder()
            .columns(listOf(dimension("PROGRAM_ATTRIBUTE", "attributeUid")))
            .filters(listOf(dimension("PERIOD", "eventDate")))
            .sorting(
                listOf(
                    TrackerVisualizationSorting.builder().dimension("attributeUid").direction(SortingDirection.ASC)
                        .build(),
                    TrackerVisualizationSorting.builder().dimension("eventDate").direction(SortingDirection.DESC)
                        .build(),
                    TrackerVisualizationSorting.builder().dimension("missingUid").direction(SortingDirection.ASC)
                        .build(),
                ),
            )
            .build()

        val params = mapper.toTrackerLineListParams(visualization)

        assertThat(params.sorting).containsExactly(
            TrackerLineListSortingItem(TrackerLineListItem.ProgramAttribute("attributeUid"), SortingDirection.ASC),
            TrackerLineListSortingItem(TrackerLineListItem.EventDate(), SortingDirection.DESC),
        ).inOrder()
    }

    private fun trackerVisualizationBuilder(): TrackerVisualization.Builder {
        return TrackerVisualization.builder()
            .uid("visualization_uid")
    }

    private fun dimensionBuilder(dimensionType: String?, dimension: String): TrackerVisualizationDimensionBuilder {
        return trackerVisualizationDimensionBuilder()
            .dimensionType(dimensionType)
            .dimension(dimension)
    }

    private fun dimension(
        dimensionType: String?,
        dimension: String,
        program: String? = null,
        items: List<String>? = null,
        filter: String? = null,
    ): TrackerVisualizationDimension {
        return dimensionBuilder(dimensionType, dimension)
            .program(program?.let { ObjectWithUid.create(it) })
            .items(items?.map { ObjectWithUid.create(it) })
            .filter(filter)
            .build()
    }

    private fun trackerVisualizationDimensionBuilder(): TrackerVisualizationDimensionBuilder {
        return TrackerVisualizationDimension.builder()
            .trackerVisualization("tracker_visualization")
            .position(LayoutPosition.COLUMN)
    }
}
