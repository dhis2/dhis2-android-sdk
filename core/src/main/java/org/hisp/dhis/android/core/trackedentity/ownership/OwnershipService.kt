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

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.koin.core.annotation.Singleton

@Singleton
internal class OwnershipService(private val client: HttpServiceClient) {

    suspend fun breakGlass(
        trackedEntity: Map<String, String>,
        program: String,
        reason: String,
    ): HttpMessageResponse {
        return client.post {
            url("$OWNERSHIP_URL/override")
            parameters {
                attribute(PROGRAM, program)
                attribute(REASON, reason)
            }
            body(trackedEntity)
        }
    }

    suspend fun transfer(
        trackedEntity: Map<String, String>,
        program: String,
        ou: String,
    ): HttpMessageResponse {
        return client.put {
            url("$OWNERSHIP_URL/transfer")
            parameters {
                attribute(PROGRAM, program)
                attribute(ORG_UNIT, ou)
            }
            body(trackedEntity)
        }
    }

    companion object {
        private const val OWNERSHIP_URL = "tracker/ownership"
        private const val PROGRAM = "program"
        private const val REASON = "reason"
        private const val ORG_UNIT = "ou"

        const val TRACKED_ENTITY_INSTACE = "trackedEntityInstance"
        const val TRACKED_ENTITY = "trackedEntity"
    }
}
