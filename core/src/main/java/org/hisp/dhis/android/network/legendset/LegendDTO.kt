/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.network.legendset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.legendset.Legend
import org.hisp.dhis.android.network.common.dto.BaseIdentifiableObjectDTO
import org.hisp.dhis.android.network.common.dto.applyBaseIdentifiableFields

@Serializable
internal data class LegendDTO(
    @SerialName("id") override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val startValue: Double?,
    val endValue: Double?,
    val color: String?,
) : BaseIdentifiableObjectDTO {
    fun toDomain(legendSetUid: String): Legend {
        return Legend.builder()
            .applyBaseIdentifiableFields(this)
            .legendSet(ObjectWithUid.create(legendSetUid))
            .startValue(startValue)
            .endValue(endValue)
            .color(color)
            .build()
    }
}
