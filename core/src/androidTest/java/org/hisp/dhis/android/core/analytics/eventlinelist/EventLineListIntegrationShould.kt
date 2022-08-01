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
package org.hisp.dhis.android.core.analytics.eventlinelist

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.AnalyticsLegendStrategy
import org.hisp.dhis.android.core.analytics.LegendEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsOrganisationUnitHelper
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.categoryCombo
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.categoryOptionCombo
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.dataElement1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.dataElement2
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.enrollment
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.legendSet1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.legendSet2
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.organisationUnit1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.program1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.program1Stage1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.program1Stage2
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.trackedEntityInstance
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.trackedEntityType
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.userOrganisationUnit
import org.hisp.dhis.android.core.analytics.linelist.EventLineListParams
import org.hisp.dhis.android.core.analytics.linelist.EventLineListService
import org.hisp.dhis.android.core.analytics.linelist.EventLineListServiceImpl
import org.hisp.dhis.android.core.analytics.linelist.LineListItem
import org.hisp.dhis.android.core.category.internal.CategoryComboStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStoreImpl
import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.legendset.DataElementLegendSetLink
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink
import org.hisp.dhis.android.core.legendset.internal.DataElementLegendSetLinkStore
import org.hisp.dhis.android.core.legendset.internal.LegendSetStore
import org.hisp.dhis.android.core.legendset.internal.LegendStore
import org.hisp.dhis.android.core.legendset.internal.ProgramIndicatorLegendSetLinkStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitOrganisationUnitGroupLinkStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorStore
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeStore
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class EventLineListIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    // Stores
    private val trackedEntityTypeStore = TrackedEntityTypeStore.create(databaseAdapter)
    private val categoryComboStore = CategoryComboStore.create(databaseAdapter)
    private val categoryOptionComboStore = CategoryOptionComboStoreImpl.create(databaseAdapter)
    private val programStore = ProgramStore.create(databaseAdapter)
    private val programStageStore = ProgramStageStore.create(databaseAdapter)
    private val dataElementStore = DataElementStore.create(databaseAdapter)
    private val dataElementLegendSetLinkStore = DataElementLegendSetLinkStore.create(databaseAdapter)
    private val organisationUnitStore = OrganisationUnitStore.create(databaseAdapter)
    private val userOrganisationUnitStore = UserOrganisationUnitLinkStoreImpl.create(databaseAdapter)
    private val organisationUnitGroupLinkStore = OrganisationUnitOrganisationUnitGroupLinkStore.create(databaseAdapter)
    private val organisationUnitLevelStore = OrganisationUnitLevelStore.create(databaseAdapter)
    private val trackedEntityInstanceStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter)
    private val eventStore = EventStoreImpl.create(databaseAdapter)
    private val trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter)
    private val programIndicatorStore = ProgramIndicatorStore.create(databaseAdapter)
    private val programIndicatorLegendSetLinkStore = ProgramIndicatorLegendSetLinkStore.create(databaseAdapter)
    private val enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter)
    private val legendSetStore = LegendSetStore.create(databaseAdapter)
    private val legendStore = LegendStore.create(databaseAdapter)
    private val calendarProvider = CalendarProviderFactory.createFixed()
    private val dateFilterPeriodHelper =
        DateFilterPeriodHelper(calendarProvider, ParentPeriodGeneratorImpl.create(calendarProvider))
    private val organisationUnitHelper = AnalyticsOrganisationUnitHelper(
        userOrganisationUnitStore,
        organisationUnitStore,
        organisationUnitLevelStore,
        organisationUnitGroupLinkStore
    )

    private val eventLineListService: EventLineListService = EventLineListServiceImpl(
        eventRepository = d2.eventModule().events(),
        dataValueRepository = d2.trackedEntityModule().trackedEntityDataValues(),
        dataElementRepository = d2.dataElementModule().dataElements(),
        programIndicatorRepository = d2.programModule().programIndicators(),
        organisationUnitRepository = d2.organisationUnitModule().organisationUnits(),
        programStageRepository = d2.programModule().programStages(),
        programIndicatorEngine = d2.programModule().programIndicatorEngine(),
        periodHelper = d2.periodModule().periodHelper(),
        dateFilterPeriodHelper = dateFilterPeriodHelper,
        organisationUnitHelper = organisationUnitHelper,
        legendEvaluator = LegendEvaluator(
            dataElementRepository = d2.dataElementModule().dataElements(),
            programIndicatorRepository = d2.programModule().programIndicators(),
            legendRepository = d2.legendSetModule().legends(),
            indicatorRepository = d2.indicatorModule().indicators(),
            trackedEntityAttributeCollectionRepository = d2.trackedEntityModule().trackedEntityAttributes(),
        )
    )

    @Before
    fun setUp() {
        setUpClass()

        trackedEntityTypeStore.insert(trackedEntityType)

        categoryComboStore.insert(categoryCombo)
        categoryOptionComboStore.insert(categoryOptionCombo)

        programStore.insert(program1)
        programStageStore.insert(program1Stage1)
        programStageStore.insert(program1Stage2)

        legendSetStore.insert(legendSet1)
        legendSetStore.insert(legendSet2)

        val legends1 =
            legendSet1.legends()!!.map { it.toBuilder().legendSet(ObjectWithUid.create(legendSet1.uid())).build() }
        legendStore.insert(legends1)

        val legends2 =
            legendSet2.legends()!!.map { it.toBuilder().legendSet(ObjectWithUid.create(legendSet2.uid())).build() }
        legendStore.insert(legends2)

        dataElementStore.insert(dataElement1)
        createDataElementLegendSetLinks(dataElement1.uid(), dataElement1.legendSets()!!)
        dataElementStore.insert(dataElement2)
        createDataElementLegendSetLinks(dataElement2.uid(), dataElement2.legendSets()!!)

        organisationUnitStore.insert(organisationUnit1)
        userOrganisationUnitStore.insert(userOrganisationUnit)

        createTei()
        createEnrollment()
    }

    @After
    fun tearDown() {
        trackedEntityTypeStore.delete()
        categoryComboStore.delete()
        categoryOptionComboStore.delete()
        programStore.delete()
        programStageStore.delete()
        dataElementStore.delete()
        dataElementLegendSetLinkStore.delete()
        organisationUnitStore.delete()
        userOrganisationUnitStore.delete()
        trackedEntityInstanceStore.delete()
        eventStore.delete()
        trackedEntityDataValueStore.delete()
        programIndicatorStore.delete()
        programIndicatorLegendSetLinkStore.delete()
        enrollmentStore.delete()
        legendSetStore.delete()
        legendStore.delete()
    }

    @Test
    fun should_return_single_data_element_in_repeatable_stage() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")
        val event2 = createEvent(program1Stage2.uid(), "2020-09-02T00:00:00.000")
        val event3 = createEvent(program1Stage2.uid(), "2020-10-03T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "1.0")
        createDataValue(event2.uid(), dataElement1.uid(), "2.0")
        createDataValue(event3.uid(), dataElement1.uid(), "3.0")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            dataElements = listOf(LineListItem(dataElement1.uid()))
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(3)
        result.forEach {
            when (it.uid) {
                event1.uid() -> assertThat(it.period.periodId()).isEqualTo("20200801")
                event2.uid() -> assertThat(it.period.periodId()).isEqualTo("20200902")
                event3.uid() -> assertThat(it.period.periodId()).isEqualTo("20201003")
            }
        }

        assertThat(result.all { it.organisationUnit == organisationUnit1.uid() }).isTrue()
        assertThat(result.all { it.organisationUnitName == organisationUnit1.displayName() }).isTrue()

        assertThat(result.all { it.values.size == 1 }).isTrue()
        assertThat(result.all { it.values[0].uid == dataElement1.uid() }).isTrue()
        assertThat(result.all { it.values[0].displayName == dataElement1.displayName() }).isTrue()

        result.forEach {
            when (it.uid) {
                event1.uid() -> assertThat(it.values[0].value).isEqualTo("1.0")
                event2.uid() -> assertThat(it.values[0].value).isEqualTo("2.0")
                event3.uid() -> assertThat(it.values[0].value).isEqualTo("3.0")
            }
        }
    }

    @Test
    fun should_return_several_data_elements_in_repeatable_stage() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")
        val event2 = createEvent(program1Stage2.uid(), "2020-09-02T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "1.0")
        createDataValue(event1.uid(), dataElement2.uid(), "10.0")
        createDataValue(event2.uid(), dataElement1.uid(), "2.0")
        createDataValue(event2.uid(), dataElement2.uid(), "20.0")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            dataElements = listOf(LineListItem(dataElement1.uid()), LineListItem(dataElement2.uid()))
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(2)
        assertThat(result.all { it.values.size == 2 }).isTrue()
    }

    @Test
    fun should_return_missing_data_elements_in_repeatable_stage() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")
        val event2 = createEvent(program1Stage2.uid(), "2020-09-02T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "1.0")
        createDataValue(event2.uid(), dataElement1.uid(), "2.0")
        createDataValue(event2.uid(), dataElement2.uid(), "20.0")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            dataElements = listOf(LineListItem(dataElement1.uid()), LineListItem(dataElement2.uid()))
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(2)
        assertThat(result.all { it.values.size == 2 }).isTrue()
        result.forEach {
            when (it.uid) {
                event1.uid() -> {
                    assertThat(it.values[0].value).isEqualTo("1.0")
                    assertThat(it.values[1].value).isNull()
                }
                event2.uid() -> {
                    assertThat(it.values[0].value).isEqualTo("2.0")
                    assertThat(it.values[1].value).isEqualTo("20.0")
                }
            }
        }
    }

    @Test
    fun should_return_program_indicators_in_repeatable_stage() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")
        val event2 = createEvent(program1Stage2.uid(), "2020-09-02T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "1.0")
        createDataValue(event1.uid(), dataElement2.uid(), "10.0")
        createDataValue(event2.uid(), dataElement1.uid(), "2.0")
        createDataValue(event2.uid(), dataElement2.uid(), "20.5")

        val programIndicator = createProgramIndicator(
            "#{${program1Stage2.uid()}.${dataElement1.uid()}} + #{${program1Stage2.uid()}.${dataElement2.uid()}}"
        )

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            programIndicators = listOf(LineListItem(programIndicator.uid()))
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(2)
        assertThat(result.all { it.values.size == 1 }).isTrue()
        result.forEach {
            when (it.uid) {
                event1.uid() -> assertThat(it.values[0].value).isEqualTo("11")
                event2.uid() -> assertThat(it.values[0].value).isEqualTo("22.5")
            }
        }
    }

    @Test
    fun should_ignore_deleted_events() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")
        val deletedEvent = createDeletedEvent(program1Stage2.uid(), "2020-09-02T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "1.0")
        createDataValue(deletedEvent.uid(), dataElement1.uid(), "10.0")

        val programIndicator = createProgramIndicator("#{${program1Stage2.uid()}.${dataElement1.uid()}}")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            programIndicators = listOf(LineListItem(programIndicator.uid()))
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(1)
        assertThat(result.first().values.size).isEqualTo(1)
        assertThat(result.first().values.first().value).isEqualTo("1")
    }

    @Test
    fun should_return_program_stage_period_if_defined() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")
        val event2 = createEvent(program1Stage2.uid(), "2020-09-02T00:00:00.000")
        val event3 = createEvent(program1Stage2.uid(), "2020-10-03T00:00:00.000")

        val updatedStage = program1Stage2.toBuilder().periodType(PeriodType.Monthly).build()
        programStageStore.update(updatedStage)

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            dataElements = listOf(LineListItem(dataElement1.uid()))
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(3)
        result.forEach {
            when (it.uid) {
                event1.uid() -> assertThat(it.period.periodId()).isEqualTo("202008")
                event2.uid() -> assertThat(it.period.periodId()).isEqualTo("202009")
                event3.uid() -> assertThat(it.period.periodId()).isEqualTo("202010")
            }
        }
    }

    @Test
    fun should_consider_due_events() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")
        val event2 = createDueEvent(program1Stage2.uid(), "2020-09-02T00:00:00.000")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            dataElements = listOf(LineListItem(dataElement1.uid()))
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(2)
        result.forEach {
            when (it.uid) {
                event1.uid() -> assertThat(it.period.periodId()).isEqualTo("20200801")
                event2.uid() -> assertThat(it.period.periodId()).isEqualTo("20200902")
            }
        }
    }

    @Test
    fun should_evaluate_relative_periods() {
        val event1 = createEvent(program1Stage2.uid(), "2019-12-01T00:00:00.000")
        val event2 = createEvent(program1Stage2.uid(), "2019-06-02T00:00:00.000")
        val event3 = createEvent(program1Stage2.uid(), "2019-05-03T00:00:00.000")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            eventDates = listOf(
                DateFilterPeriod.builder()
                    .period(RelativePeriod.THIS_MONTH)
                    .type(DatePeriodType.RELATIVE)
                    .build()
            )
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(1)
        assertThat(result.first().uid).isEqualTo(event1.uid())
    }

    @Test
    fun should_evaluate_relative_organisationUnits() {
        val event1 = createEvent(program1Stage2.uid(), "2019-12-01T00:00:00.000")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            organisationUnits = listOf(OrganisationUnitFilter(null, RelativeOrganisationUnit.USER_ORGUNIT))
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.size).isEqualTo(1)
        assertThat(result.first().uid).isEqualTo(event1.uid())
    }

    @Test
    fun should_return_program_indicators_without_legend_if_legend_strategy_is_none() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "1.0")
        createDataValue(event1.uid(), dataElement2.uid(), "10.0")

        val programIndicator = createProgramIndicator(
            "#{${program1Stage2.uid()}.${dataElement1.uid()}} + #{${program1Stage2.uid()}.${dataElement2.uid()}}"
        )

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            programIndicators = listOf(LineListItem(programIndicator.uid())),
            analyticsLegendStrategy = AnalyticsLegendStrategy.None
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.all { line -> line.values.all { value -> value.legend == null } }).isTrue()
    }

    @Test
    fun should_return_program_indicators_with_legend_by_PI_if_legend_strategy_is_by_data_item() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "1.0")
        createDataValue(event1.uid(), dataElement2.uid(), "10.0")

        val programIndicator = createProgramIndicator(
            "#{${program1Stage2.uid()}.${dataElement1.uid()}} + #{${program1Stage2.uid()}.${dataElement2.uid()}}"
        )

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            programIndicators = listOf(LineListItem(programIndicator.uid())),
            analyticsLegendStrategy = AnalyticsLegendStrategy.ByDataItem
        )

        val result = eventLineListService.evaluate(eventListParams)

        val values = result[0].values

        assertThat(values.size == 1).isTrue()
        assertThat(values[0].uid == programIndicator.uid()).isTrue()
        assertThat(values[0].legend == legendSet1.legends()?.get(0)?.uid()).isTrue()
    }

    @Test
    fun should_return_program_indicators_legend_by_fixed_if_legend_strategy_is_fixed() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "30.0")
        createDataValue(event1.uid(), dataElement2.uid(), "40.0")

        val programIndicator = createProgramIndicator(
            "#{${program1Stage2.uid()}.${dataElement1.uid()}} + #{${program1Stage2.uid()}.${dataElement2.uid()}}"
        )

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            programIndicators = listOf(LineListItem(programIndicator.uid())),
            analyticsLegendStrategy = AnalyticsLegendStrategy.Fixed(legendSet2.uid())
        )

        val result = eventLineListService.evaluate(eventListParams)

        val values = result[0].values

        assertThat(values.size == 1).isTrue()
        assertThat(values[0].uid == programIndicator.uid()).isTrue()
        assertThat(values[0].legend == legendSet2.legends()?.get(1)?.uid()).isTrue()
    }

    @Test
    fun should_return_data_elements_without_legend_if_legend_strategy_is_none() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "10.0")
        createDataValue(event1.uid(), dataElement2.uid(), "30.0")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            dataElements = listOf(LineListItem(dataElement1.uid()), LineListItem(dataElement2.uid())),
            analyticsLegendStrategy = AnalyticsLegendStrategy.None
        )

        val result = eventLineListService.evaluate(eventListParams)

        assertThat(result.all { line -> line.values.all { value -> value.legend == null } }).isTrue()
    }

    @Test
    fun should_return_data_elements_with_legend_by_DE_if_legend_strategy_is_by_data_item() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "10.0")
        createDataValue(event1.uid(), dataElement2.uid(), "30.0")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            dataElements = listOf(LineListItem(dataElement1.uid()), LineListItem(dataElement2.uid())),
            analyticsLegendStrategy = AnalyticsLegendStrategy.ByDataItem
        )

        val result = eventLineListService.evaluate(eventListParams)

        val values = result[0].values

        assertThat(values.size == 2).isTrue()
        assertThat(values[0].uid == dataElement1.uid()).isTrue()
        assertThat(values[0].value == "10.0").isTrue()
        assertThat(values[0].legend == legendSet1.legends()?.get(0)?.uid()).isTrue()

        assertThat(values[1].uid == dataElement2.uid()).isTrue()
        assertThat(values[1].value == "30.0").isTrue()
        assertThat(values[1].legend == legendSet1.legends()?.get(1)?.uid()).isTrue()
    }

    @Test
    fun should_return_data_elements_with_legend_by_fixed_if_legend_strategy_is_fixed() {
        val event1 = createEvent(program1Stage2.uid(), "2020-08-01T00:00:00.000")

        createDataValue(event1.uid(), dataElement1.uid(), "10.0")
        createDataValue(event1.uid(), dataElement2.uid(), "30.0")

        val eventListParams = EventLineListParams(
            programStage = program1Stage2.uid(),
            trackedEntityInstance = trackedEntityInstance.uid(),
            dataElements = listOf(LineListItem(dataElement1.uid()), LineListItem(dataElement2.uid())),
            analyticsLegendStrategy = AnalyticsLegendStrategy.Fixed(legendSet2.uid())
        )

        val result = eventLineListService.evaluate(eventListParams)

        val values = result[0].values

        assertThat(values.size == 2).isTrue()
        assertThat(values[0].uid == dataElement1.uid()).isTrue()
        assertThat(values[0].value == "10.0").isTrue()
        assertThat(values[0].legend == legendSet2.legends()?.get(0)?.uid()).isTrue()

        assertThat(values[1].uid == dataElement2.uid()).isTrue()
        assertThat(values[1].value == "30.0").isTrue()
        assertThat(values[1].legend == legendSet2.legends()?.get(0)?.uid()).isTrue()
    }

    private fun createTei() {
        trackedEntityInstanceStore.insert(trackedEntityInstance)
    }

    private fun createEnrollment() {
        enrollmentStore.insert(enrollment)
    }

    private fun createEvent(programStageId: String, eventDate: String): Event {
        val event = EventLineListSamples.event(programStageId, BaseIdentifiableObject.parseDate(eventDate))
        eventStore.insert(event)
        return event
    }

    private fun createDueEvent(programStageId: String, dueDate: String): Event {
        val event = EventLineListSamples.dueEvent(programStageId, BaseIdentifiableObject.parseDate(dueDate))
        eventStore.insert(event)
        return event
    }

    private fun createDeletedEvent(programStageId: String, eventDate: String): Event {
        val event = EventLineListSamples
            .event(programStageId, BaseIdentifiableObject.parseDate(eventDate))
            .toBuilder().deleted(true).build()
        eventStore.insert(event)
        return event
    }

    private fun createDataValue(eventId: String, dataElementId: String, value: String) {
        val dataValue = TrackedEntityDataValue.builder().event(eventId).dataElement(dataElementId).value(value).build()
        trackedEntityDataValueStore.insert(dataValue)
    }

    private fun createProgramIndicator(expression: String): ProgramIndicator {
        val programIndicator = EventLineListSamples.programIndicator(expression)
        programIndicatorStore.insert(programIndicator)
        programIndicatorLegendSetLinkStore.insert(
            ProgramIndicatorLegendSetLink.builder().programIndicator(programIndicator.uid()).legendSet(
                legendSet1.uid()
            ).build()
        )
        return programIndicator
    }

    private fun createDataElementLegendSetLinks(dataElement: String, legendSets: List<ObjectWithUid>) {
        legendSets.forEach {
            val dataElementLegendSetLink =
                DataElementLegendSetLink.builder().dataElement(dataElement).legendSet(it.uid()).build()
            dataElementLegendSetLinkStore.insert(dataElementLegendSetLink)
        }
    }
}
