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

package org.hisp.dhis.android.core.enrollment

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.DataObjectKt
import org.hisp.dhis.android.core.common.DeletableDataObjectKt
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.ObjectWithUidInterfaceKt
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.relationship.Relationship
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class Enrollment(
    override val uid: String,
    override val syncState: State?,
    override val deleted: Boolean?,
    val created: Date?,
    val lastUpdated: Date?,
    val createdAtClient: Date?,
    val lastUpdatedAtClient: Date?,
    val organisationUnit: String?,
    val program: String?,
    val enrollmentDate: Date?,
    val incidentDate: Date?,
    val completedDate: Date?,
    val followUp: Boolean?,
    val status: EnrollmentStatus?,
    val trackedEntityInstance: String?,
    val attributeOptionCombo: String,
    val geometry: Geometry?,
    internal val events: List<Event>?,
    val notes: List<Note>?,
    internal val relationships: List<Relationship>?,
    val aggregatedSyncState: State?,
) : ObjectWithUidInterfaceKt, DataObjectKt, DeletableDataObjectKt {

    fun created(): Date? = created
    fun lastUpdated(): Date? = lastUpdated
    fun createdAtClient(): Date? = createdAtClient
    fun lastUpdatedAtClient(): Date? = lastUpdatedAtClient
    fun organisationUnit(): String? = organisationUnit
    fun program(): String? = program
    fun enrollmentDate(): Date? = enrollmentDate
    fun incidentDate(): Date? = incidentDate
    fun completedDate(): Date? = completedDate
    fun followUp(): Boolean? = followUp
    fun status(): EnrollmentStatus? = status
    fun trackedEntityInstance(): String? = trackedEntityInstance
    fun attributeOptionCombo(): String = attributeOptionCombo
    fun geometry(): Geometry? = geometry
    internal fun events(): List<Event>? = events
    fun notes(): List<Note>? = notes
    internal fun relationships(): List<Relationship>? = relationships
    fun aggregatedSyncState(): State? = aggregatedSyncState

    @Deprecated("Use aggregatedSyncState() instead")
    override fun state(): State? = aggregatedSyncState()

    fun toBuilder(): Builder = EnrollmentBuilder.from(this)

    class Builder : EnrollmentBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
