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

package org.hisp.dhis.android.network.tracker

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate

@Serializable
internal data class NewTrackedEntityDataValueDTO(
    val event: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val dataElement: String?,
    val createdBy: UserInfoDTO?,
    val value: String?,
    val providedElsewhere: Boolean?,
) {
    fun toDomain(): TrackedEntityDataValue {
        return TrackedEntityDataValue.builder()
            .event(event)
            .created(createdAt.toJavaDate())
            .lastUpdated(updatedAt.toJavaDate())
            .dataElement(dataElement)
            .storedBy(createdBy?.username)
            .value(value)
            .providedElsewhere(providedElsewhere)
            .build()
    }
}

internal fun NewTrackerImporterTrackedEntityDataValue.toDto(): NewTrackedEntityDataValueDTO {
    return NewTrackedEntityDataValueDTO(
        event = this.event(),
        createdAt = this.createdAt()?.let { it.dateFormat() },
        updatedAt = this.updatedAt()?.let { it.dateFormat() },
        dataElement = this.dataElement(),
        createdBy = this.createdBy()?.let { it.toDto() },
        value = this.value(),
        providedElsewhere = this.providedElsewhere(),
    )
}
