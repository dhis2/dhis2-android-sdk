package org.hisp.dhis.android.core.program.programindicatorengine.internal

import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

class TrackedEntityDataValueAggregationHandler {
    fun getValue(
        candidates: List<TrackedEntityDataValue>,
        aggregationType: AggregationType?,
        valueType: ValueType
    ): String? {

        return if (aggregationType?.isNumericOrBooleanOnly == true && (valueType.isNumeric || valueType.isBoolean)) {
            val numericCandidates = mapForValueType(candidates, valueType)
            when (aggregationType) {
                AggregationType.SUM -> sum(numericCandidates).toString()
                AggregationType.AVERAGE -> average(numericCandidates).toString()
                AggregationType.STDDEV -> standardDeviation(numericCandidates).toString()
                AggregationType.VARIANCE -> variance(numericCandidates).toString()
                AggregationType.MIN -> min(numericCandidates).toString()
                AggregationType.MAX -> max(numericCandidates).toString()
                else -> candidates.first().value()
            }
        } else {
            when (aggregationType) {
                AggregationType.LAST -> last(candidates)
                AggregationType.COUNT -> count(candidates)
                else -> candidates.first().value()
            }
        }
    }

    private fun mapForValueType(candidates: List<TrackedEntityDataValue>, valueType: ValueType): List<Double> {
        return when {
            valueType.isNumeric -> mapToDouble(candidates)
            else -> mapBooleanToDouble(candidates)
        }
    }

    private fun mapToDouble(candidates: List<TrackedEntityDataValue>): List<Double> {
        return candidates.map { it.value()?.toDouble() ?: 0.0 }
    }

    private fun mapBooleanToDouble(candidates: List<TrackedEntityDataValue>): List<Double> {
        return candidates.filter { it.value() != null }.map {
            when (it.value()!!.toLowerCase(Locale.ROOT)) {
                "0" -> 0.0
                "1" -> 1.0
                "yes" -> 1.0
                "no" -> 0.0
                else -> 0.0
            }
        }
    }

    private fun sum(candidates: List<Double>): Float {
        return candidates.sum().toFloat()
    }

    private fun last(candidates: List<TrackedEntityDataValue>): String? {
        return candidates.last().value()
    }

    private fun average(candidates: List<Double>): Float {
        return sum(candidates) / candidates.size
    }

    private fun count(candidates: List<TrackedEntityDataValue>): String {
        return candidates.count().toString()
    }

    private fun standardDeviation(candidates: List<Double>): Float {
        return sqrt(variance(candidates))
    }

    private fun variance(candidates: List<Double>): Float {
        val avg = average(candidates)
        val standardDeviation = candidates.sumByDouble { value ->
            (value - avg).pow(2.0)
        }.toFloat()
        return standardDeviation / (candidates.size - 1)
    }

    private fun min(candidates: List<Double>): Float {
        return candidates.min()?.toFloat() ?: 0.0f
    }

    private fun max(candidates: List<Double>): Float {
        return candidates.max()?.toFloat() ?: 0.0f
    }
}