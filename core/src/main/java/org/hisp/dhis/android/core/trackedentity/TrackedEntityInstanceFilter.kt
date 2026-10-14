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

package org.hisp.dhis.android.core.trackedentity

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.FilterPeriod
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithStyleKt
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class TrackedEntityInstanceFilter(
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: Date?,
    override val lastUpdated: Date?,
    override val deleted: Boolean?,
    override val style: ObjectStyle,
    val program: ObjectWithUid?,
    val description: String?,
    val sortOrder: Int?,
    val entityQueryCriteria: EntityQueryCriteria,
    val eventFilters: List<TrackedEntityInstanceEventFilter>?,
) : BaseIdentifiableObject, CoreObject, ObjectWithStyleKt {

    fun program(): ObjectWithUid? = program
    fun description(): String? = description
    fun sortOrder(): Int? = sortOrder
    fun entityQueryCriteria(): EntityQueryCriteria = entityQueryCriteria
    fun eventFilters(): List<TrackedEntityInstanceEventFilter>? = eventFilters

    @Deprecated("Use entityQueryCriteria().enrollmentStatus() instead")
    fun enrollmentStatus(): EnrollmentStatus? = entityQueryCriteria().enrollmentStatus()

    @Deprecated("Use entityQueryCriteria().followUp() instead")
    fun followUp(): Boolean? = entityQueryCriteria().followUp()

    @Deprecated("Use entityQueryCriteria().enrollmentCreatedDate() instead")
    fun enrollmentCreatedPeriod(): FilterPeriod? {
        val dateFilterPeriod = entityQueryCriteria().enrollmentCreatedDate()
        return dateFilterPeriod?.let {
            FilterPeriod.builder()
                .periodFrom(it.startBuffer())
                .periodTo(it.endBuffer())
                .build()
        }
    }

    fun toBuilder(): Builder = TrackedEntityInstanceFilterBuilder.from(this)

    class Builder : TrackedEntityInstanceFilterBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
            .style(ObjectStyle.builder().build())
            .entityQueryCriteria(EntityQueryCriteria.builder().build())
    }
}
