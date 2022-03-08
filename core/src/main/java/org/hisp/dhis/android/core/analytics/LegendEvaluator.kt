package org.hisp.dhis.android.core.analytics

import javax.inject.Inject
import org.hisp.dhis.android.core.dataelement.DataElementCollectionRepository
import org.hisp.dhis.android.core.indicator.IndicatorCollectionRepository
import org.hisp.dhis.android.core.legendset.LegendCollectionRepository
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository

@Suppress("TooGenericExceptionCaught")
internal class LegendEvaluator @Inject constructor(
    private val dataElementRepository: DataElementCollectionRepository,
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository,
    private val indicatorRepository: IndicatorCollectionRepository,
    private val legendRepository: LegendCollectionRepository
) {
    fun getLegendByProgramIndicator(
        programIndicatorUid: String,
        value: String?
    ): String? {
        return if (value == null) {
            null
        } else try {
            val programIndicator = programIndicatorRepository
                .byUid().eq(programIndicatorUid)
                .withLegendSets()
                .one().blockingGet()

            val legendSet = programIndicator.legendSets()!![0]

            return getLegendByLegendSet(legendSet.uid(), value)
        } catch (e: Exception) {
            null
        }
    }

    fun getLegendByDataElement(
        dataElementUid: String,
        value: String?
    ): String? {
        return if (value == null) {
            null
        } else try {
            val dataElement = dataElementRepository
                .byUid().eq(dataElementUid)
                .withLegendSets()
                .one().blockingGet()

            val legendSet = dataElement.legendSets()!![0]

            return getLegendByLegendSet(legendSet.uid(), value)
        } catch (e: Exception) {
            null
        }
    }

    fun getLegendByIndicator(
        indicatorUid: String,
        value: String?
    ): String? {
        return if (value == null) {
            null
        } else try {
            val indicator = indicatorRepository
                .byUid().eq(indicatorUid)
                .withLegendSets()
                .one().blockingGet()

            val legendSet = indicator.legendSets()!![0]

            return getLegendByLegendSet(legendSet.uid(), value)
        } catch (e: Exception) {
            null
        }
    }

    fun getLegendByLegendSet(
        legendSetUid: String,
        value: String?
    ): String? {

        return if (value == null || value.toDouble().isNaN()) {
            null
        } else try {
            return legendRepository
                .byStartValue().smallerThan(value.toDouble())
                .byEndValue().biggerOrEqualTo(value.toDouble())
                .byLegendSet().eq(legendSetUid)
                .one()
                .blockingGet().uid()
        } catch (e: Exception) {
            null
        }
    }
}
