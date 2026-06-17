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

package org.hisp.dhis.android.core.trackedentity.search

import org.hisp.dhis.android.annotations.ModelBuilder

@ModelBuilder
data class TrackedEntityInstanceQueryScopeOrderColumn(
    val type: Type,
    val apiName: TrackedEntityInstanceQueryScopeOrderApiName?,
    val value: String?,
) {
    enum class Type {
        CREATED, LAST_UPDATED, ATTRIBUTE, ORGUNIT_NAME,
        ENROLLMENT_DATE, INCIDENT_DATE, ENROLLMENT_STATUS,
        EVENT_DATE, COMPLETION_DATE
    }

    fun type(): Type = type
    fun apiName(): TrackedEntityInstanceQueryScopeOrderApiName? = apiName
    fun value(): String? = value

    fun hasApiName(): Boolean = apiName != null

    fun toBuilder(): Builder = TrackedEntityInstanceQueryScopeOrderColumnBuilder.from(this)

    class Builder : TrackedEntityInstanceQueryScopeOrderColumnBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()

        internal val CREATED = builder()
            .type(Type.CREATED)
            .apiName(TrackedEntityInstanceQueryScopeOrderApiName.Created)
            .build()

        internal val LAST_UPDATED = builder()
            .type(Type.LAST_UPDATED)
            .apiName(TrackedEntityInstanceQueryScopeOrderApiName.LastUpdated)
            .build()

        internal val ORGUNIT_NAME = builder().type(Type.ORGUNIT_NAME).build()

        internal val ENROLLMENT_DATE = builder()
            .type(Type.ENROLLMENT_DATE)
            .apiName(TrackedEntityInstanceQueryScopeOrderApiName.EnrollmentDate)
            .build()

        internal val INCIDENT_DATE = builder().type(Type.INCIDENT_DATE).build()

        internal val COMPLETION_DATE = builder().type(Type.COMPLETION_DATE).build()

        internal val EVENT_DATE = builder().type(Type.EVENT_DATE).build()

        internal val ENROLLMENT_STATUS = builder().type(Type.ENROLLMENT_STATUS).build()

        internal fun attribute(attributeId: String): TrackedEntityInstanceQueryScopeOrderColumn =
            builder()
                .type(Type.ATTRIBUTE)
                .apiName(TrackedEntityInstanceQueryScopeOrderApiName.Attribute(attributeId))
                .value(attributeId)
                .build()
    }
}
