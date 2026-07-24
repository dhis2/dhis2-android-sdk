/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.visualization

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUid

@ModelBuilder
@Suppress("TooManyFunctions")
data class TrackerVisualizationDimension(
    val trackerVisualization: String,
    val position: LayoutPosition,
    val dimension: String,
    val dimensionType: String?,
    val program: ObjectWithUid?,
    val programStage: ObjectWithUid?,
    val items: List<ObjectWithUid>?,
    val filter: String?,
    val repetition: TrackerVisualizationDimensionRepetition?,
    val sortOrder: Int?,
) : CoreObject {
    fun trackerVisualization(): String = trackerVisualization
    fun position(): LayoutPosition = position
    fun dimension(): String = dimension
    fun dimensionType(): String? = dimensionType
    fun program(): ObjectWithUid? = program
    fun programStage(): ObjectWithUid? = programStage
    fun items(): List<ObjectWithUid>? = items
    fun filter(): String? = filter
    fun repetition(): TrackerVisualizationDimensionRepetition? = repetition
    fun sortOrder(): Int? = sortOrder

    fun toBuilder(): Builder = TrackerVisualizationDimensionBuilder.from(this)

    class Builder : TrackerVisualizationDimensionBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
