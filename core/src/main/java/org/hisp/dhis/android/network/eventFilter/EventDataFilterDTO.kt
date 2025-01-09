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

package org.hisp.dhis.android.network.eventFilter

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.network.common.dto.DateFilterPeriodDTO
import org.hisp.dhis.android.network.common.dto.FilterOperatorsDTO
import org.hisp.dhis.android.network.common.dto.applyFilterOperatorsFields

@Serializable
internal data class EventDataFilterDTO(
    override val le: String?,
    override val ge: String?,
    override val gt: String?,
    override val lt: String?,
    override val eq: String?,
    override val `in`: Set<String>?,
    override val like: String?,
    override val dateFilter: DateFilterPeriodDTO?,
    val eventFilter: String?,
    val dataItem: String?,
) : FilterOperatorsDTO {
    fun toDomain(): EventDataFilter {
        return EventDataFilter.builder()
            .applyFilterOperatorsFields(this)
            .eventFilter(eventFilter)
            .dataItem(dataItem)
            .build()
    }
}
