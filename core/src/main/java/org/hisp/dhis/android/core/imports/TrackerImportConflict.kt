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

package org.hisp.dhis.android.core.imports

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.CoreObject
import java.util.Date

@Suppress("TooManyFunctions")
@ModelBuilder
data class TrackerImportConflict(
    val conflict: String?,
    val value: String?,
    val trackedEntityInstance: String?,
    val enrollment: String?,
    val event: String?,
    val trackedEntityAttribute: String?,
    val dataElement: String?,
    val tableReference: String?,
    val errorCode: String?,
    val displayDescription: String?,
    val status: ImportStatus?,
    val created: Date?,
) : CoreObject {

    fun conflict(): String? = conflict
    fun value(): String? = value
    fun trackedEntityInstance(): String? = trackedEntityInstance
    fun enrollment(): String? = enrollment
    fun event(): String? = event
    fun trackedEntityAttribute(): String? = trackedEntityAttribute
    fun dataElement(): String? = dataElement
    fun tableReference(): String? = tableReference
    fun errorCode(): String? = errorCode
    fun displayDescription(): String? = displayDescription
    fun status(): ImportStatus? = status
    fun created(): Date? = created

    fun toBuilder(): Builder = TrackerImportConflictBuilder.from(this)

    class Builder : TrackerImportConflictBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
