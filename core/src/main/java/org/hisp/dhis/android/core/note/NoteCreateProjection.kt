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

package org.hisp.dhis.android.core.note

import org.hisp.dhis.android.annotations.ModelBuilder

@ModelBuilder
data class NoteCreateProjection(
    val noteType: Note.NoteType?,
    val event: String?,
    val enrollment: String?,
    val value: String?,
) {
    fun noteType(): Note.NoteType? = noteType
    fun event(): String? = event
    fun enrollment(): String? = enrollment
    fun value(): String? = value

    fun toBuilder(): Builder = NoteCreateProjectionBuilder.from(this)

    class Builder : NoteCreateProjectionBuilder() {
        override fun build(): NoteCreateProjection {
            when {
                noteType == null ->
                    throw IllegalArgumentException("Note type is null")

                noteType == Note.NoteType.ENROLLMENT_NOTE && enrollment == null ->
                    throw IllegalArgumentException("Enrollment note type need an enrollment uid")

                noteType == Note.NoteType.EVENT_NOTE && event == null ->
                    throw IllegalArgumentException("Event note type need an event uid")
            }
            return super.build()
        }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()

        @Deprecated("replaced by create(Note.NoteType, String, String)")
        @JvmStatic
        fun create(enrollment: String, value: String): NoteCreateProjection {
            return NoteCreateProjection(
                noteType = Note.NoteType.ENROLLMENT_NOTE,
                event = null,
                enrollment = enrollment,
                value = value,
            )
        }

        @JvmStatic
        fun create(noteType: Note.NoteType, ownerUid: String, value: String): NoteCreateProjection {
            return NoteCreateProjection(
                noteType = noteType,
                event = if (noteType == Note.NoteType.EVENT_NOTE) ownerUid else null,
                enrollment = if (noteType == Note.NoteType.ENROLLMENT_NOTE) ownerUid else null,
                value = value,
            )
        }
    }
}
