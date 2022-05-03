/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.program.internal

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramIndicator

@Reusable
internal class ProgramIndicatorCall @Inject constructor(
    private val service: ProgramIndicatorService,
    private val handler: Handler<ProgramIndicator>,
    private val apiDownloader: APIDownloader
) : UidsCall<ProgramIndicator> {

    companion object {
        const val MAX_UID_LIST_SIZE = 50
    }

    override fun download(uids: Set<String>): Single<List<ProgramIndicator>> {
        return apiDownloader.downloadPartitioned(
            uids,
            MAX_UID_LIST_SIZE,
            handler
        ) { partitionUids ->
            val displayInFormFilter = ProgramIndicatorFields.displayInForm.eq(true).generateString()
            val programUidsFilter = "program.${ObjectWithUid.uid.`in`(partitionUids).generateString()}"

            val programFilterSingle = service.getProgramIndicator(
                fields = ProgramIndicatorFields.allFields,
                filter = programUidsFilter,
                false
            )
            val displayInFormFilterSingle = service.getProgramIndicator(
                fields = ProgramIndicatorFields.allFields,
                filter = displayInFormFilter,
                false
            )

            Single.merge(programFilterSingle, displayInFormFilterSingle).reduce { t1, t2 ->
                val data = t1.items() + t2.items()
                val updatedPayload = Payload<ProgramIndicator>()
                updatedPayload.apply {
                    items().addAll(data)
                }
            }.toSingle()
        }
    }
}
