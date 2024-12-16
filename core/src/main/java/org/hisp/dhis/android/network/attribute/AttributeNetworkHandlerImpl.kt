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
package org.hisp.dhis.android.network.attribute

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Filter
import org.hisp.dhis.android.core.attribute.Attribute
import org.hisp.dhis.android.core.attribute.internal.AttributeNetworkHandler
import org.hisp.dhis.android.network.common.HttpServiceClientKotlinx
import org.hisp.dhis.android.network.common.PayloadJson
import org.koin.core.annotation.Singleton

@Singleton
internal class AttributeNetworkHandlerImpl(
    httpClient: HttpServiceClientKotlinx,
) : AttributeNetworkHandler {
    private val service: AttributeService = AttributeService(httpClient)

    override suspend fun getAttributes(
        fields: Fields<Attribute>,
        uids: Filter<Attribute>,
        paging: Boolean,
    ): PayloadJson<Attribute> {
        val apiPayload = service.attributes(fields, uids, paging)
        return apiPayload.mapItems(::attributeDtoToDomainMapper)
    }
}
