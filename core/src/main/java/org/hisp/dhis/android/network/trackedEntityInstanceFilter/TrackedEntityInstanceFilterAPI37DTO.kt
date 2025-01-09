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

package org.hisp.dhis.android.network.trackedEntityInstanceFilter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.BaseIdentifiableObjectDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.applyBaseIdentifiableFields

@Serializable
internal data class TrackedEntityInstanceFilterAPI37DTO(
    @SerialName("id") override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val program: ObjectWithUidDTO?,
    val description: String?,
    val sortOrder: Int?,
    val enrollmentStatus: EnrollmentStatus?,
    val followup: Boolean?,
    val enrollmentCreatedPeriod: FilterPeriodDTO?,
    val eventFilters: List<TrackedEntityInstanceEventFilterDTO>?,
) : BaseIdentifiableObjectDTO {
    fun toDomain(): TrackedEntityInstanceFilter {
        return TrackedEntityInstanceFilter.builder().apply {
            applyBaseIdentifiableFields(this@TrackedEntityInstanceFilterAPI37DTO)
            program(program?.toDomain())
            description(description)
            sortOrder(sortOrder)
            entityQueryCriteria(
                EntityQueryCriteria.builder()
                    .followUp(followup)
                    .enrollmentStatus(enrollmentStatus)
                    .enrollmentCreatedDate(
                        enrollmentCreatedPeriod?.let {
                            DateFilterPeriod.builder()
                                .startBuffer(it.periodFrom)
                                .endBuffer(it.periodTo)
                                .type(DatePeriodType.RELATIVE)
                                .build()
                        },
                    )
                    .build(),
            )
            eventFilters(eventFilters?.map { it.toDomain() } ?: emptyList())
        }.build()
    }
}

@Serializable
internal class TrackedEntityInstanceFilterAPI37Payload(
    override val pager: PagerDTO?,
    @SerialName("trackedEntityInstanceFilters") override val items: List<TrackedEntityInstanceFilterAPI37DTO> =
        emptyList(),
) : PayloadJson<TrackedEntityInstanceFilterAPI37DTO>(pager, items)
