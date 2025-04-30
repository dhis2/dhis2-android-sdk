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

package org.hisp.dhis.android.network.programindicator

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorNetworkHandler
import org.koin.core.annotation.Singleton

@Singleton
internal class ProgramIndicatorNetworkHandlerImpl(
    httpServiceClient: HttpServiceClient,
) : ProgramIndicatorNetworkHandler {
    private val service = ProgramIndicatorService(httpServiceClient)

    override suspend fun getDisplayInFormProgramIndicators(programUids: Set<String>): Payload<ProgramIndicator> {
        val displayInFormFilter = ProgramIndicatorFields.displayInForm.eq(true)
        val programUidsFilter = "program.${ObjectWithUid.uid.`in`(programUids).generateString()}"
        val apiPayload = service.getProgramIndicator(
            fields = ProgramIndicatorFields.allFields,
            displayInForm = displayInFormFilter,
            program = programUidsFilter,
            uids = null,
            false,
        )
        return apiPayload.mapItems(ProgramIndicatorDTO::toDomain)
    }

    override suspend fun getProgramIndicatorsByUid(uids: Set<String>): Payload<ProgramIndicator> {
        val apiPayload = service.getProgramIndicator(
            fields = ProgramIndicatorFields.allFields,
            displayInForm = null,
            program = null,
            uids = ProgramIndicatorFields.uid.`in`(uids),
            false,
        )
        return apiPayload.mapItems(ProgramIndicatorDTO::toDomain)
    }
}
