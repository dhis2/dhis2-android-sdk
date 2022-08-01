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

package org.hisp.dhis.android.core.analytics

import javax.inject.Inject
import org.hisp.dhis.android.core.dataelement.DataElementCollectionRepository
import org.hisp.dhis.android.core.indicator.IndicatorCollectionRepository
import org.hisp.dhis.android.core.legendset.LegendCollectionRepository
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeCollectionRepository

@Suppress("TooGenericExceptionCaught")
internal class LegendEvaluator @Inject constructor(
    private val dataElementRepository: DataElementCollectionRepository,
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository,
    private val indicatorRepository: IndicatorCollectionRepository,
    private val legendRepository: LegendCollectionRepository,
    private val trackedEntityAttributeCollectionRepository: TrackedEntityAttributeCollectionRepository,
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

    @Suppress("UnusedPrivateMember", "FunctionOnlyReturningConstant")
    fun getLegendByTrackedEntityAttribute(
        trackedEntityAttributeUid: String,
        value: String?
    ): String? {
        return if (value == null) {
            null
        } else try {
            val trackedEntityAttribute = trackedEntityAttributeCollectionRepository
                .byUid().eq(trackedEntityAttributeUid)
                .withLegendSets()
                .one().blockingGet()

            val legendSet = trackedEntityAttribute.legendSets()!![0]

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
