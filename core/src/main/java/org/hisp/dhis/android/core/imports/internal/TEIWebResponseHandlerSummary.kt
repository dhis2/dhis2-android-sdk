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
package org.hisp.dhis.android.core.imports.internal

import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

internal data class TEIWebResponseHandlerSummary(
    val teis: TrackerResponseHandlerSummary<TrackedEntityInstance> = TrackerResponseHandlerSummary(),
    val enrollments: TrackerResponseHandlerSummary<Enrollment> = TrackerResponseHandlerSummary(),
    val events: TrackerResponseHandlerSummary<Event> = TrackerResponseHandlerSummary()
) {
    fun add(other: TEIWebResponseHandlerSummary) {
        teis.add(other.teis)
        enrollments.add(other.enrollments)
        events.add(other.events)
    }

    fun update(other: TEIWebResponseHandlerSummary) {
        teis.update(other.teis)
        enrollments.update(other.enrollments)
        events.update(other.events)
    }
}

internal data class TrackerResponseHandlerSummary<E : ObjectWithUidInterface>(
    val success: MutableList<E> = mutableListOf(),
    val error: MutableList<E> = mutableListOf(),
    val ignored: MutableList<E> = mutableListOf()
) {
    fun add(other: TrackerResponseHandlerSummary<E>) {
        success.addAll(other.success)
        error.addAll(other.error)
        ignored.addAll(other.ignored)
    }

    fun update(other: TrackerResponseHandlerSummary<E>) {
        val updatedIds = (other.success + other.ignored + other.error).map { it.uid() }

        val updatedSuccess = success.filter { updatedIds.contains(it.uid()) }
        success.removeAll(updatedSuccess)

        val updatedError = error.filter { updatedIds.contains(it.uid()) }
        error.removeAll(updatedError)

        val updatedIgnored = ignored.filter { updatedIds.contains(it.uid()) }
        ignored.removeAll(updatedIgnored)

        add(other)
    }
}
