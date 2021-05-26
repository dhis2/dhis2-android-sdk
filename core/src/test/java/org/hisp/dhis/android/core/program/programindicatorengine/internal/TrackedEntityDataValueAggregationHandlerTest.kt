package org.hisp.dhis.android.core.program.programindicatorengine.internal

import junit.framework.Assert.assertTrue
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.junit.Before
import org.junit.Test


class TrackedEntityDataValueAggregationHandlerTest {

    private lateinit var aggregationHelper: TrackedEntityDataValueAggregationHandler

    @Before
    fun setUp() {
        aggregationHelper = TrackedEntityDataValueAggregationHandler()
    }

    @Test
    fun shouldCalculateSumValueForNumericInputs() {
        val result = aggregationHelper.getValue(numericCandidates, AggregationType.SUM, ValueType.NUMBER)
        assertTrue(result == "27.8")
    }

    @Test
    fun shouldCalculateAverageValueForNumericInputs() {
        val result = aggregationHelper.getValue(numericCandidates, AggregationType.AVERAGE, ValueType.NUMBER)
        assertTrue(result == "5.56")
    }

    @Test
    fun shouldCalculateStandardDeviationForNumericInputs() {
        val result = aggregationHelper.getValue(numericCandidates, AggregationType.STDDEV, ValueType.NUMBER)
        assertTrue(result == "2.4110165")
    }

    @Test
    fun shouldCalculateVarianceForNumericInputs() {
        val result = aggregationHelper.getValue(numericCandidates, AggregationType.VARIANCE, ValueType.NUMBER)
        assertTrue(result == "5.813")
    }

    @Test
    fun shouldCalculateMinForNumericInputs() {
        val result = aggregationHelper.getValue(numericCandidates, AggregationType.MIN, ValueType.NUMBER)
        assertTrue(result == "3.2")
    }

    @Test
    fun shouldCalculateMaxForNumericInputs() {
        val result = aggregationHelper.getValue(numericCandidates, AggregationType.MAX, ValueType.NUMBER)
        assertTrue(result == "8.9")
    }
    
    @Test
    fun shouldCalculateSumValueForBooleanInputs() {
        val result = aggregationHelper.getValue(booleanCandidates, AggregationType.SUM, ValueType.BOOLEAN)
        assertTrue(result == "3.0")
    }

    @Test
    fun shouldCalculateAverageValueForBooleanInputs() {
        val result = aggregationHelper.getValue(booleanCandidates, AggregationType.AVERAGE, ValueType.BOOLEAN)
        assertTrue(result == "0.6")
    }

    @Test
    fun shouldCalculateStandardDeviationForBooleanInputs() {
        val result = aggregationHelper.getValue(booleanCandidates, AggregationType.STDDEV, ValueType.BOOLEAN)
        assertTrue(result == "0.5477226")
    }

    @Test
    fun shouldCalculateVarianceForBooleanInputs() {
        val result = aggregationHelper.getValue(booleanCandidates, AggregationType.VARIANCE, ValueType.BOOLEAN)
        assertTrue(result == "0.3")
    }

    @Test
    fun shouldCalculateMinForBooleanInputs() {
        val result = aggregationHelper.getValue(booleanCandidates, AggregationType.MIN, ValueType.BOOLEAN)
        assertTrue(result == "0.0")
    }

    @Test
    fun shouldCalculateMaxForBooleanInputs() {
        val result = aggregationHelper.getValue(booleanCandidates, AggregationType.MAX, ValueType.BOOLEAN)
        assertTrue(result == "1.0")
    }

    /*ANY*/

    @Test
    fun shouldCalculateLastForAnyInputs() {
        val numericResult = aggregationHelper.getValue(numericCandidates, AggregationType.LAST, ValueType.NUMBER)
        val booleanResult = aggregationHelper.getValue(booleanCandidates, AggregationType.LAST, ValueType.BOOLEAN)
        val otherResult = aggregationHelper.getValue(otherCandidate, AggregationType.LAST, ValueType.TEXT)
        assertTrue(numericResult == "8.9")
        assertTrue(booleanResult == "yes")
        assertTrue(otherResult == "Marcos")
    }

    @Test
    fun shouldCalculateCountForAnyInputs() {
        val numericResult = aggregationHelper.getValue(numericCandidates, AggregationType.COUNT, ValueType.NUMBER)
        val booleanResult = aggregationHelper.getValue(booleanCandidates, AggregationType.COUNT, ValueType.BOOLEAN)
        val otherResult = aggregationHelper.getValue(otherCandidate, AggregationType.COUNT, ValueType.TEXT)
        assertTrue(numericResult == "5")
        assertTrue(booleanResult == "5")
        assertTrue(otherResult == "4")
    }

    private val numericCandidates = arrayListOf(
        trackedEntityDataValue("5.0"),
        trackedEntityDataValue("3.6"),
        trackedEntityDataValue("7.1"),
        trackedEntityDataValue("3.2"),
        trackedEntityDataValue("8.9")
    )

    private val booleanCandidates = arrayListOf(
        trackedEntityDataValue("yes"),
        trackedEntityDataValue("yes"),
        trackedEntityDataValue("no"),
        trackedEntityDataValue("no"),
        trackedEntityDataValue("yes")
    )

    private val otherCandidate = arrayListOf(
        trackedEntityDataValue("Marta"),
        trackedEntityDataValue("Jose"),
        trackedEntityDataValue("Victor"),
        trackedEntityDataValue("Marcos")
    )

    private fun trackedEntityDataValue(value: String) = TrackedEntityDataValue.builder()
        .dataElement("mockedDataElementUid")
        .value(value)
        .event("mockedEventUid")
        .build()
}