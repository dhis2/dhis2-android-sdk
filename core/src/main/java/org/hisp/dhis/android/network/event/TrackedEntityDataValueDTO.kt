/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.event

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.common.dto.ValueDTO

@Serializable
internal data class TrackedEntityDataValueDTO(
    val created: String?,
    val lastUpdated: String?,
    val dataElement: String?,
    val storedBy: String?,
    val value: ValueDTO?,
    val providedElsewhere: Boolean?,
) {
    fun toDomain(event: String): TrackedEntityDataValue {
        return TrackedEntityDataValue.builder()
            .event(event)
            .created(created.toJavaDate())
            .lastUpdated(lastUpdated.toJavaDate())
            .dataElement(dataElement)
            .storedBy(storedBy)
            .value(value?.value)
            .providedElsewhere(providedElsewhere)
            .build()
    }
}

internal fun TrackedEntityDataValue.toDto(): TrackedEntityDataValueDTO {
    return TrackedEntityDataValueDTO(
        created = this.created().dateFormat(),
        lastUpdated = this.lastUpdated().dateFormat(),
        dataElement = this.dataElement(),
        storedBy = this.storedBy(),
        value = ValueDTO(this.value()),
        providedElsewhere = this.providedElsewhere(),
    )
}
