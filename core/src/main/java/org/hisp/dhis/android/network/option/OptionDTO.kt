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

package org.hisp.dhis.android.network.option

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.network.common.BaseIdentifiableObjectInterface
import org.hisp.dhis.android.network.common.ObjectWithStyle
import org.hisp.dhis.android.network.common.ObjectWithUidInterface
import org.hisp.dhis.android.network.common.Pager
import org.hisp.dhis.android.network.common.Payload

@Serializable
internal class OptionPayload(
    override val pager: Pager? = null,
    @SerialName("options") override val items: List<OptionDTO> = emptyList(),
) : Payload<OptionDTO>(pager, items)

@Serializable
internal data class OptionDTO(
    @SerialName("id") override val uid: String,
    override val code: String? = null,
    override val name: String? = null,
    override val displayName: String? = null,
    override val created: String = "",
    override val lastUpdated: String = "",
    override val deleted: Boolean? = null,
    val sortOrder: Int? = null,
    val optionSet: ObjectWithUidInterface? = null,
    val style: ObjectWithStyle? = null,
) : BaseIdentifiableObjectInterface
