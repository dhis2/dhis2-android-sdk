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
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.common.dto.ValueDTO

@Serializable
internal data class NewTrackedEntityAttributeValueDTO(
    val attribute: String?,
    val value: ValueDTO?,
    val createdAt: String?,
    val updatedAt: String?,
    val trackedEntityInstance: String?,
) {
    fun toDomain(teiUid: String): TrackedEntityAttributeValue {
        return TrackedEntityAttributeValue.builder()
            .trackedEntityAttribute(attribute)
            .value(value?.value)
            .created(createdAt.toJavaDate())
            .lastUpdated(updatedAt.toJavaDate())
            .trackedEntityInstance(teiUid)
            .build()
    }
}

internal fun NewTrackerImporterTrackedEntityAttributeValue.toDto(): NewTrackedEntityAttributeValueDTO {
    return NewTrackedEntityAttributeValueDTO(
        attribute = this.trackedEntityAttribute(),
        value = ValueDTO(value()),
        createdAt = this.createdAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        updatedAt = this.updatedAt()?.let { DateUtils.DATE_FORMAT.format(it) },
        trackedEntityInstance = this.trackedEntityInstance(),
    )
}
