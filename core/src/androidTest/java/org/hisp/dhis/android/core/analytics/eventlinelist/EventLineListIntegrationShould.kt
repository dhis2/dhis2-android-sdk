/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.categoryCombo
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.categoryOptionCombo
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.dataElement1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.dataElement2
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.enrollment
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.organisationUnit1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.program1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.program1Stage1
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.program1Stage2
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.trackedEntityInstance
import org.hisp.dhis.android.core.analytics.eventlinelist.EventLineListSamples.trackedEntityType
import org.hisp.dhis.android.core.analytics.linelist.EventLineListParams
import org.hisp.dhis.android.core.analytics.linelist.EventLineListService
import org.hisp.dhis.android.core.analytics.linelist.EventLineListServiceImpl
import org.hisp.dhis.android.core.analytics.linelist.LineListItem
import org.hisp.dhis.android.core.category.internal.CategoryComboStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStoreImpl
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorStore
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class EventLineListIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    private val eventLineListService: EventLineListService = EventLineListServiceImpl(
        eventRepository = d2.eventModule().events(),
        dataValueRepository = d2.trackedEntityModule().trackedEntityDataValues(),
        dataElementRepository = d2.dataElementModule().dataElements(),
        programIndicatorRepository = d2.programModule().programIndicators(),
        organisationUnitRepository = d2.organisationUnitModule().organisationUnits(),
        programStageRepository = d2.programModule().programStages(),
        programIndicatorEngine = d2.programModule().programIndicatorEngine(),
        periodHelper = d2.periodModule().periodHelper()
    )

    // Stores
    private val trackedEntityTypeStore = TrackedEntityTypeStore.create(databaseAdapter)
    private val categoryComboStore = CategoryComboStore.create(databaseAdapter)
    private val categoryOptionComboStore = CategoryOptionComboStoreImpl.create(databaseAdapter)
    private val programStore = ProgramStore.create(databaseAdapter)
    private val programStageStore = ProgramStageStore.create(databaseAdapter)
    private val dataElementStore = DataElementStore.create(databaseAdapter)
    private val organisationUnitStore = OrganisationUnitStore.create(databaseAdapter)
    private val trackedEntityInstanceStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter)
    private val eventStore = EventStoreImpl.create(databaseAdapter)
    private val trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter)
    private val programIndicatorStore = ProgramIndicatorStore.create(databaseAdapter)
    private val enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter)

    @Before
    fun setUp() {
        setUpClass()

        trackedEntityTypeStore.insert(trackedEntityType)

        categoryComboStore.insert(categoryCombo)
        categoryOptionComboStore.insert(categoryOptionCombo)

        programStore.insert(program1)
        programStageStore.insert(program1Stage1)
        programStageStore.insert(program1Stage2)

        dataElementStore.insert(dataElement1)
        dataElementStore.insert(dataElement2)

        organisationUnitStore.insert(organisationUnit1)

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
        organisationUnitStore.delete()
        trackedEntityInstanceStore.delete()
        eventStore.delete()
        trackedEntityDataValueStore.delete()
        programIndicatorStore.delete()
        enrollmentStore.delete()
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
        return programIndicator
    }
}
