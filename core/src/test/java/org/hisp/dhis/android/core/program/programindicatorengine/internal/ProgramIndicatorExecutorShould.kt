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
package org.hisp.dhis.android.core.program.programindicatorengine.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import java.text.ParseException
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit test for in-memory program indicator executor.
 */
@RunWith(MockitoJUnitRunner::class)
class ProgramIndicatorExecutorShould {
    private val programStage1 = "p2adVnEmIei"
    private val programStage2 = "trjLe2gx6lI"
    private val programStage3 = "atjLe6gt6lI"
    private val attributeUid1 = "JiuwgfybPrE"
    private val attributeUid2 = "U4w2S7vUxV7"
    private val dataElementUid1 = "UUqzccMujME"
    private val dataElementUid2 = "JHpWWC1bISq"
    private val constantUid1 = "gzlRs2HEGAf"

    private val dataElementStore: IdentifiableObjectStore<DataElement> = mock()

    private val trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute> = mock()

    private val programStageStore: IdentifiableObjectStore<ProgramStage> = mock()

    private val programIndicator: ProgramIndicator = mock()

    private val programIndicatorContext: ProgramIndicatorContext = mock()

    private val constant: Constant = mock()
    private val constantMap: Map<String, Constant> = mapOf(constantUid1 to constant)

    private val enrollment: Enrollment = mock()

    private val attributeValue1: TrackedEntityAttributeValue = mock()
    private val attributeValue2: TrackedEntityAttributeValue = mock()

    private val attributeValueMap: Map<String, TrackedEntityAttributeValue> = mapOf(
        attributeUid1 to attributeValue1,
        attributeUid2 to attributeValue2
    )

    private val event1: Event = mock()
    private val event2_1: Event = mock()
    private val event2_2: Event = mock()

    private val eventsMap: Map<String, List<Event>> = mapOf(
        programStage1 to listOf(event1),
        programStage2 to listOf(event2_1, event2_2)
    )

    private val dataValue1: TrackedEntityDataValue = mock()
    private val dataValue2_1: TrackedEntityDataValue = mock()
    private val dataValue2_2: TrackedEntityDataValue = mock()

    private val dataElement1: DataElement = mock()
    private val dataElement2: DataElement = mock()

    private val programStage: ProgramStage = mock()

    private val attribute1: TrackedEntityAttribute = mock()
    private val attribute2: TrackedEntityAttribute = mock()

    private val programIndicatorExecutor: ProgramIndicatorExecutor = ProgramIndicatorExecutor(
        constantMap,
        programIndicatorContext,
        dataElementStore,
        trackedEntityAttributeStore,
        programStageStore
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        whenever(attributeValue1.trackedEntityAttribute()) doReturn attributeUid1
        whenever(attributeValue2.trackedEntityAttribute()) doReturn attributeUid2

        // Data values
        whenever(event1.trackedEntityDataValues()) doReturn listOf(dataValue1)
        whenever(event2_1.trackedEntityDataValues()) doReturn listOf(dataValue2_1)
        whenever(event2_2.trackedEntityDataValues()) doReturn listOf(dataValue2_2)
        whenever(dataValue1.dataElement()) doReturn dataElementUid1
        whenever(dataValue2_1.dataElement()) doReturn dataElementUid2
        whenever(dataValue2_2.dataElement()) doReturn dataElementUid2
        whenever(programIndicatorContext.programIndicator) doReturn programIndicator
        whenever(programIndicatorContext.enrollment) doReturn enrollment
        whenever(programIndicatorContext.attributeValues) doReturn attributeValueMap
        whenever(programIndicatorContext.events) doReturn eventsMap
        whenever(dataElementStore.selectByUid(dataElementUid1)) doReturn dataElement1
        whenever(dataElementStore.selectByUid(dataElementUid2)) doReturn dataElement2
        whenever(dataElement1.valueType()) doReturn ValueType.NUMBER
        whenever(dataElement2.valueType()) doReturn ValueType.NUMBER
        whenever(trackedEntityAttributeStore.selectByUid(attributeUid1)) doReturn attribute1
        whenever(trackedEntityAttributeStore.selectByUid(attributeUid2)) doReturn attribute2
        whenever(attribute1.valueType()) doReturn ValueType.NUMBER
        whenever(attribute2.valueType()) doReturn ValueType.NUMBER

        whenever(programIndicator.filter()) doReturn null
    }

    @Test
    fun evaluate_constants() {
        setExpression(cons(constantUid1))
        whenever(constant.value()) doReturn 5.3

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("5.3")
    }

    @Test
    fun evaluate_tracked_entity_attribute_value() {
        setExpression("${att(attributeUid1)} - ${att(attributeUid2)}")
        whenever(attributeValue1.value()) doReturn "7.8"
        whenever(attributeValue2.value()) doReturn "2.5"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("5.3")
    }

    @Test
    fun evaluate_data_elements_in_stage() {
        setExpression("${de(programStage1, dataElementUid1)} + ${de(programStage2, dataElementUid2)}")
        whenever(dataValue1.value()) doReturn "4.5"
        whenever(dataValue2_2.value()) doReturn "20.6"

        val resultLast = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultLast).isEqualTo("25.1")
    }

    @Test
    fun evaluate_data_elements_with_value_count() {
        setExpression(
            "(${de(programStage1, dataElementUid1)} + ${de(programStage2, dataElementUid2)}) / " +
                `var`("value_count")
        )
        whenever(dataValue1.value()) doReturn "4.5"
        whenever(dataValue2_2.value()) doReturn "1.9"

        val resultNone = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultNone).isEqualTo("3.2")
    }

    @Test
    fun evaluate_data_elements_with_zero_pos_value_count() {
        setExpression(
            "(${de(programStage1, dataElementUid1)} + ${de(programStage2, dataElementUid2)}) / " +
                `var`("zero_pos_value_count")
        )
        whenever(dataValue1.value()) doReturn "7.5"
        whenever(dataValue2_2.value()) doReturn "-1.5"

        val resultNone = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultNone).isEqualTo("6")
    }

    @Test
    fun evaluate_event_count() {
        setExpression("${de(programStage1, dataElementUid1)} / ${`var`("event_count")}")
        whenever(dataValue1.value()) doReturn "10"

        val resultNone = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultNone).isEqualTo("3.33")
    }

    @Test
    @Throws(ParseException::class)
    fun evaluate_enrollment_dates() {
        setExpression("d2:daysBetween(${`var`("enrollment_date")}, ${`var`("incident_date")})")
        whenever(enrollment.enrollmentDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-05-01T00:00:00.000")
        whenever(enrollment.incidentDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-05-05T00:00:00.000")

        val resultNone = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultNone).isEqualTo("4")
    }

    @Test
    fun evaluate_d2_count() {
        setExpression("d2:count(${de(programStage2, dataElementUid2)})")
        whenever(dataValue2_1.value()) doReturn "1.5"
        whenever(dataValue2_2.value()) doReturn "20.5"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("2")
    }

    @Test
    fun evaluate_d2_count_if_condition() {
        setExpression("d2:countIfCondition(${de(programStage2, dataElementUid2)}, '< 10')")
        whenever(dataValue2_1.value()) doReturn "1.5"
        whenever(dataValue2_2.value()) doReturn "20.5"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("1")
    }

    @Test
    fun evaluate_d2_count_if_value_numeric() {
        setExpression("d2:countIfValue(${de(programStage2, dataElementUid2)}, 1.5)")
        whenever(dataValue2_1.value()) doReturn "1.5"
        whenever(dataValue2_2.value()) doReturn "20.5"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("1")
    }

    @Test
    fun evaluate_d2_count_if_value_string() {
        setExpression("d2:countIfValue(${de(programStage2, dataElementUid2)}, 'positive')")
        whenever(dataValue2_1.value()) doReturn "positive"
        whenever(dataValue2_2.value()) doReturn "negative"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("1")
    }

    @Test
    fun evaluate_d2_has_attribute_value() {
        setExpression("d2:hasValue(${att(attributeUid1)})")

        whenever(attributeValue1.value()) doReturn null
        val resultNull = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultNull).isEqualTo("false")

        whenever(attributeValue1.value()) doReturn "3.4"
        val resultNonNull = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultNonNull).isEqualTo("true")
    }

    @Test
    fun evaluate_d2_has_data_value() {
        setExpression("d2:hasValue(${de(programStage1, dataElementUid2)})")
        val resultNull = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultNull).isEqualTo("false")

        setExpression("d2:hasValue(${de(programStage1, dataElementUid1)})")
        whenever(dataValue1.value()) doReturn "value"
        val resultNonNull = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultNonNull).isEqualTo("true")
    }

    @Test
    fun evaluate_d2_condition() {
        setExpression("d2:condition('${de(programStage1, dataElementUid1)} < 10', 150, 50)")

        whenever(dataValue1.value()) doReturn "8"
        val resultTrue = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultTrue).isEqualTo("150")

        whenever(dataValue1.value()) doReturn "15"
        val resultFalse = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(resultFalse).isEqualTo("50")
    }

    @Test
    fun evaluate_boolean_elements() {
        setExpression("${de(programStage1, dataElementUid1)} + ${att(attributeUid1)}")
        whenever(dataValue1.value()) doReturn "true"
        whenever(dataElement1.valueType()) doReturn ValueType.BOOLEAN
        whenever(attributeValue1.value()) doReturn "true"
        whenever(attribute1.valueType()) doReturn ValueType.BOOLEAN

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("2")
    }

    @Test
    fun evaluate_text_data_values() {
        setExpression(de(programStage1, dataElementUid1))
        whenever(dataValue1.value()) doReturn "value"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("value")
    }

    @Test
    fun evaluate_enrollment_status() {
        setExpression(`var`("enrollment_status"))
        whenever(enrollment.status()) doReturn EnrollmentStatus.COMPLETED

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("COMPLETED")
    }

    @Test
    @Throws(ParseException::class)
    fun evaluate_enrollment_date() {
        setExpression(`var`("enrollment_date"))
        whenever(enrollment.enrollmentDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-05T00:00:00.000")

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("2020-01-05")
    }

    @Test
    @Throws(ParseException::class)
    fun evaluate_completed_date() {
        setExpression(`var`("completed_date"))
        whenever(enrollment.completedDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-02T00:00:00.000")

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("2020-01-02")
    }

    @Test
    @Throws(ParseException::class)
    fun evaluate_ps_event_date() {
        setExpression("d2:daysBetween(${`var`("enrollment_date")}, PS_EVENTDATE:$programStage2)")
        whenever(enrollment.enrollmentDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-02T00:00:00.000")
        whenever(event2_2.eventDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-05T00:00:00.000")

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("3")
    }

    @Test
    @Throws(ParseException::class)
    fun evaluate_ps_event_status() {
        setExpression(`var`("event_status"))
        whenever(event1.eventDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
        whenever(event2_1.eventDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-02T00:00:00.000")
        whenever(event2_2.eventDate()) doReturn null

        whenever(event2_1.status()) doReturn EventStatus.ACTIVE

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("ACTIVE")
    }

    @Test
    fun evaluate_values_in_missing_stages() {
        setExpression("${de(programStage1, dataElementUid1)} + ${de(programStage3, dataElementUid1)}")
        whenever(dataValue1.value()) doReturn "5.3"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("5.3")
    }

    @Test
    @Throws(ParseException::class)
    fun evaluate_tei_count() {
        setExpression(`var`("tei_count"))

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("1")
    }

    @Test
    fun evaluate_program_stage_id() {
        setExpression(`var`("program_stage_id"))
        whenever(event1.eventDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
        whenever(event2_1.eventDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-02T00:00:00.000")
        whenever(event2_2.eventDate()) doReturn null

        whenever(event2_1.programStage()) doReturn programStage2

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo(programStage2)
    }

    @Test
    fun evaluate_program_stage_name() {
        setExpression(`var`("program_stage_name"))
        whenever(event1.eventDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
        whenever(event2_1.eventDate()) doReturn DateUtils.DATE_FORMAT.parse("2020-01-02T00:00:00.000")

        whenever(event2_1.programStage()) doReturn programStage2
        whenever(programStageStore.selectByUid(programStage2)) doReturn programStage
        whenever(programStage.name()) doReturn "Program stage name"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("Program stage name")
    }

    @Test
    fun evaluate_empty_filter() {
        setExpression("4")
        setFilter("")

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("4")
    }

    @Test
    fun evaluate_truthy_filter() {
        setExpression("4")
        setFilter("1")

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("4")
    }

    @Test
    fun evaluate_false_filter() {
        setExpression(de(programStage1, dataElementUid1))
        setFilter("${de(programStage1, dataElementUid1)} > 10")
        whenever(dataValue1.value()) doReturn "5.3"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isNull()
    }

    @Test
    fun evaluate_true_filter() {
        setExpression(de(programStage1, dataElementUid1))
        setFilter("${de(programStage1, dataElementUid1)} < 10")
        whenever(dataValue1.value()) doReturn "5.3"

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("5.3")
    }

    @Test
    fun evaluate_value_count_filter() {
        setExpression("${de(programStage1, dataElementUid1)} + ${de(programStage2, dataElementUid2)}")
        whenever(dataValue1.value()) doReturn "4.5"
        whenever(dataValue2_2.value()) doReturn "1.9"

        setFilter("${`var`("value_count")} > 1")
        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isEqualTo("6.4")

        setFilter("${`var`("value_count")} > 2")
        val result2 = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result2).isNull()
    }

    @Test
    fun evaluate_invalid_expressions() {
        setExpression("${de(programStage1, dataElementUid1)} && ")

        val result = programIndicatorExecutor.getProgramIndicatorValue(programIndicator)
        assertThat(result).isNull()
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    private fun setExpression(expression: String) {
        whenever(programIndicator.expression()) doReturn expression
    }

    private fun setFilter(filter: String) {
        whenever(programIndicator.filter()) doReturn filter
    }

    private fun de(programStageUid: String, dataElementUid: String): String {
        return "#{$programStageUid.$dataElementUid}"
    }

    private fun cons(constantUid: String): String {
        return "C{$constantUid}"
    }

    private fun `var`(variable: String): String {
        return "V{$variable}"
    }

    private fun att(attributeUid: String): String {
        return "A{$attributeUid}"
    }
}
