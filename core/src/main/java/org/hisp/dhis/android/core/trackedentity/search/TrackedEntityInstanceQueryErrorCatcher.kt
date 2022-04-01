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

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import java.io.IOException
import javax.net.ssl.HttpsURLConnection
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallErrorCatcher
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory.objectMapper
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import retrofit2.Response

internal class TrackedEntityInstanceQueryErrorCatcher : APICallErrorCatcher {
    override fun mustBeStored(): Boolean {
        return false
    }

    @SuppressWarnings("MagicNumber")
    override fun catchError(response: Response<*>, errorBody: String): D2ErrorCode {
        return when {
            response.code() == HttpsURLConnection.HTTP_REQ_TOO_LONG ->
                D2ErrorCode.TOO_MANY_ORG_UNITS

            response.code() == HttpsURLConnection.HTTP_GATEWAY_TIMEOUT || response.code() == 429 ->
                D2ErrorCode.TOO_MANY_REQUESTS

            else ->
                parseErrorMessage(errorBody)
        }
    }

    private fun parseErrorMessage(errorBody: String): D2ErrorCode {
        return try {
            val parsed = objectMapper().readValue(errorBody, HttpMessageResponse::class.java)
            if (parsed.httpStatusCode() == HttpsURLConnection.HTTP_CONFLICT) {
                when {
                    parsed.message() == "maxteicountreached" -> D2ErrorCode.MAX_TEI_COUNT_REACHED
                    OutOfSearchScope.containsMatchIn(parsed.message()) -> D2ErrorCode.ORGUNIT_NOT_IN_SEARCH_SCOPE
                    MinSearchAttributes.containsMatchIn(parsed.message()) -> D2ErrorCode.MIN_SEARCH_ATTRIBUTES_REQUIRED
                    else -> DefaultErrorCode
                }
            } else {
                DefaultErrorCode
            }
        } catch (e: IOException) {
            DefaultErrorCode
        } catch (e: JsonProcessingException) {
            DefaultErrorCode
        } catch (e: JsonMappingException) {
            DefaultErrorCode
        }
    }

    companion object {
        val DefaultErrorCode = D2ErrorCode.API_RESPONSE_PROCESS_ERROR

        val OutOfSearchScope = Regex("Organisation unit is not part of the search scope")
        val MinSearchAttributes = Regex("At least \\d+ attributes should be mentioned in the search criteria")
    }
}
