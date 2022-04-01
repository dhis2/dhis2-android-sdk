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
package org.hisp.dhis.android.core.trackedentity.search

import com.google.common.truth.Truth.assertThat
import javax.net.ssl.HttpsURLConnection
import okhttp3.ResponseBody
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class TrackedEntityInstanceQueryErrorCatcherShould {
    private val catcher: TrackedEntityInstanceQueryErrorCatcher = TrackedEntityInstanceQueryErrorCatcher()

    @Test
    fun return_too_many_orgunits() {
        val response =
            Response.error<Any>(HttpsURLConnection.HTTP_REQ_TOO_LONG, ResponseBody.create(null, ""))

        assertThat(catcher.catchError(response, response.errorBody()!!.string()))
            .isEqualTo(D2ErrorCode.TOO_MANY_ORG_UNITS)
    }

    @Test
    fun return_max_tei_reached() {
        val responseError =
            """{
            "httpStatus": "Conflict",
            "httpStatusCode": 409,
            "status": "ERROR",
            "message": "maxteicountreached"
            }"""

        val response = Response.error<Any>(409, ResponseBody.create(null, responseError))
        assertThat(catcher.catchError(response, response.errorBody()!!.string()))
            .isEqualTo(D2ErrorCode.MAX_TEI_COUNT_REACHED)
    }

    @Test
    fun return_orgunit_not_in_search_scope() {
        val responseError =
            """{
            "httpStatus": "Conflict",
            "httpStatusCode": 409,
            "status": "ERROR",
            "message": "Organisation unit is not part of the search scope: O6uvpzGd5pu"
            }"""

        val response = Response.error<Any>(409, ResponseBody.create(null, responseError))
        assertThat(catcher.catchError(response, response.errorBody()!!.string()))
            .isEqualTo(D2ErrorCode.ORGUNIT_NOT_IN_SEARCH_SCOPE)
    }

    @Test
    fun return_min_search_attributes_required() {
        val responseError =
            """{
            "httpStatus": "Conflict",
            "httpStatusCode": 409,
            "status": "ERROR",
            "message": "At least 1 attributes should be mentioned in the search criteria."
            }"""

        val response = Response.error<Any>(409, ResponseBody.create(null, responseError))
        assertThat(catcher.catchError(response, response.errorBody()!!.string()))
            .isEqualTo(D2ErrorCode.MIN_SEARCH_ATTRIBUTES_REQUIRED)
    }
}
