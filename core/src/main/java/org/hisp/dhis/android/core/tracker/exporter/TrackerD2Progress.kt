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
package org.hisp.dhis.android.core.tracker.exporter

import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.D2ProgressStatus

data class TrackerD2Progress(
    override val isComplete: Boolean = false,
    override val totalCalls: Int? = null,
    override val doneCalls: List<String?> = emptyList(),
    val programs: Map<String, D2ProgressStatus> = emptyMap(),
) : D2Progress() {

    fun toBuilder(): Builder {
        return Builder(isComplete, totalCalls, doneCalls, programs)
    }

    class Builder(
        override var isComplete: Boolean = false,
        override var totalCalls: Int? = null,
        override var doneCalls: List<String?> = emptyList(),
        var programs: Map<String, D2ProgressStatus> = emptyMap(),
    ) : D2Progress.Builder<Builder>() {

        fun programs(programs: Map<String, D2ProgressStatus>): Builder = apply {
            this.programs = programs
        }

        override fun build(): TrackerD2Progress {
            return TrackerD2Progress(isComplete, totalCalls, doneCalls, programs)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }

        fun empty(totalCalls: Int?): TrackerD2Progress {
            if (totalCalls != null && totalCalls < 0) {
                throw IllegalArgumentException("Negative total calls")
            }
            return builder()
                .isComplete(false)
                .totalCalls(totalCalls)
                .doneCalls(emptyList<String>())
                .programs(emptyMap<String, D2ProgressStatus>())
                .build()
        }
    }
}
