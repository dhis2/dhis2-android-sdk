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
package org.hisp.dhis.android.core.trackedentity.ownership

import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.QueryMap

internal interface OwnershipService {

    @POST("$OWNERSHIP_URL/override")
    suspend fun breakGlass(
        @QueryMap trackedEntity: Map<String, String>,
        @Query(PROGRAM) program: String,
        @Query(REASON) reason: String,
    ): HttpMessageResponse

    @PUT("$OWNERSHIP_URL/transfer")
    suspend fun transfer(
        @QueryMap trackedEntity: Map<String, String>,
        @Query(PROGRAM) program: String,
        @Query(ORG_UNIT) ou: String,
    ): HttpMessageResponse

    companion object {
        const val OWNERSHIP_URL = "tracker/ownership"

        const val TRACKED_ENTITY_INSTACE = "trackedEntityInstance"
        const val TRACKED_ENTITY = "trackedEntity"
        const val PROGRAM = "program"
        const val REASON = "reason"
        const val ORG_UNIT = "ou"
    }
}
