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

import org.hisp.dhis.android.core.arch.api.internal.D2HttpException
import org.hisp.dhis.android.core.arch.api.internal.D2HttpResponse
import org.hisp.dhis.android.core.arch.api.testutils.KtorFactory.fromServerUrl
import org.hisp.dhis.android.core.arch.call.internal.GenericCallData
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.Transaction
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.resource.internal.Resource
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.Date
import javax.net.ssl.HttpsURLConnection

abstract class BaseCallShould {
    @Mock
    protected lateinit var databaseAdapter: DatabaseAdapter

    @Mock
    protected lateinit var serverDate: Date

    @Mock
    internal lateinit var resourceHandler: ResourceHandler

    @Mock
    protected lateinit var genericCallData: GenericCallData

    @Mock
    protected lateinit var transaction: Transaction

    @Mock
    protected lateinit var d2Error: D2Error

    protected lateinit var errorResponse: D2HttpException

    @Throws(Exception::class)
    open fun setUp() {
        MockitoAnnotations.initMocks(this)

        var httpClient = fromServerUrl("https://fake.dhis.org")

        Mockito.`when`(genericCallData.databaseAdapter()).thenReturn(databaseAdapter)
        Mockito.`when`(genericCallData.httpClient()).thenReturn(httpClient)
        Mockito.`when`(
            genericCallData.resourceHandler(),
        ).thenReturn(resourceHandler)

        Mockito.`when`(
            resourceHandler.getLastUpdated(
                ArgumentMatchers.any(
                    Resource.Type::class.java,
                ),
            ),
        ).thenReturn(null)

        Mockito.`when`(databaseAdapter.beginNewTransaction()).thenReturn(transaction)

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
