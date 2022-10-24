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
package org.hisp.dhis.android.core.tracker.exporter

import org.hisp.dhis.android.core.arch.call.D2ProgressStatus
import org.hisp.dhis.android.core.arch.call.D2ProgressSyncStatus
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager

internal class TrackerD2ProgressManager(totalCalls: Int?) : D2ProgressManager(totalCalls) {

    private var progress: TrackerD2Progress

    override fun getProgress(): TrackerD2Progress {
        return progress
    }

    override fun <T> increaseProgress(resourceClass: Class<T>, isComplete: Boolean): TrackerD2Progress {
        return progress.toBuilder()
            .doneCalls(progress.doneCalls() + resourceClass.simpleName)
            .isComplete(isComplete)
            .build()
            .also { progress = it }
    }

    fun setTotalCalls(totalCalls: Int): TrackerD2Progress {
        return progress.toBuilder()
            .totalCalls(totalCalls)
            .build()
            .also { progress = it }
    }

    fun setPrograms(programs: Collection<String>): TrackerD2Progress {
        return progress.toBuilder()
            .programs(programs.associateWith { D2ProgressStatus() })
            .build()
            .also { progress = it }
    }

    fun updateProgramSyncStatus(program: String, status: D2ProgressSyncStatus): TrackerD2Progress {
        val newProgramStatus = (progress.programs()[program] ?: D2ProgressStatus()).addSyncStatus(status)
        return progress.toBuilder()
            .programs(progress.programs() + (program to newProgramStatus))
            .build()
            .also { progress = it }
    }

    fun completeProgram(program: String): TrackerD2Progress {
        val newProgramStatus = (progress.programs()[program] ?: D2ProgressStatus()).copy(isComplete = true)
        return progress.toBuilder()
            .programs(progress.programs() + (program to newProgramStatus))
            .build()
            .also { progress = it }
    }

    fun completePrograms(): TrackerD2Progress {
        return progress.toBuilder()
            .programs(
                progress.programs().mapValues {
                    it.value.copy(isComplete = true).addSyncStatus(D2ProgressSyncStatus.SUCCESS)
                }
            )
            .build()
            .also { progress = it }
    }

    fun complete(): TrackerD2Progress {
        return progress.toBuilder()
            .isComplete(true)
            .build()
            .also { progress = it }
    }

    init {
        this.progress = TrackerD2Progress.empty(totalCalls)
    }
}
