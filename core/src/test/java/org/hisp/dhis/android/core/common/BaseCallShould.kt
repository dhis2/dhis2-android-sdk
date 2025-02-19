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
package org.hisp.dhis.android.core.common

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.api.internal.D2HttpException
import org.hisp.dhis.android.core.arch.api.internal.D2HttpResponse
import org.hisp.dhis.android.core.arch.call.internal.GenericCallData
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.Transaction
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import java.util.Date
import javax.net.ssl.HttpsURLConnection

@Suppress("UnnecessaryAbstractClass")
abstract class BaseCallShould {

    protected val databaseAdapter: DatabaseAdapter = mock()
    protected val serverDate: Date = mock()
    internal val resourceHandler: ResourceHandler = mock()
    internal val genericCallData: GenericCallData = mock()
    protected val transaction: Transaction = mock()
    protected val d2Error: D2Error = mock()

    protected lateinit var errorResponse: D2HttpException

    @Throws(Exception::class)
    open fun setUp() {
        whenever(genericCallData.databaseAdapter).thenReturn(databaseAdapter)
        whenever(
            genericCallData.resourceHandler,
        ).thenReturn(resourceHandler)

        whenever(
            resourceHandler.getLastUpdated(any()),
        ).thenReturn(null)

        whenever(databaseAdapter.beginNewTransaction()).thenReturn(transaction)

        errorResponse = D2HttpException(
            D2HttpResponse(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                "",
                "{}",
                null,
            ),
        )
    }
}
