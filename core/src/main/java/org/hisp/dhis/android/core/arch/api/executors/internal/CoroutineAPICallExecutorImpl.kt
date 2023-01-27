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
package org.hisp.dhis.android.core.arch.api.executors.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.internal.UserAccountDisabledErrorCatcher
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

@Reusable
internal class CoroutineAPICallExecutorImpl @Inject constructor(
    private val errorMapper: APIErrorMapper,
    private val userAccountDisabledErrorCatcher: UserAccountDisabledErrorCatcher,
    private val errorStore: ObjectStore<D2Error>
) : CoroutineAPICallExecutor {
    override suspend fun <P> wrap(
        storeError: Boolean,
        acceptedErrorCodes: List<Int>?,
        errorCatcher: APICallErrorCatcher?,
        errorClass: Class<P>?,
        block: suspend () -> P
    ): Result<P> {
        return try {
            Result.success(block.invoke())
        } catch (t: HttpException) {
            val response = t.response()

            if (response != null) {
                val errorBody = errorMapper.getErrorBody(response)
                val errorBuilder = errorBuilder(response)

                if (userAccountDisabledErrorCatcher.isUserAccountLocked(response, errorBody)) {
                    Result.failure(
                        catchError(userAccountDisabledErrorCatcher, errorBuilder, response, errorBody, storeError)
                    )
                } else if (errorClass != null && acceptedErrorCodes?.contains(response.code()) == true) {
                    Result.success(
                        ObjectMapperFactory.objectMapper().readValue(errorBody, errorClass)
                    )
                } else if (errorCatcher != null) {
                    Result.failure(
                        catchError(errorCatcher, errorBuilder, response, errorBody, storeError)
                    )
                } else {
                    Result.failure(
                        storeAndReturn(
                            errorMapper.responseException(errorBuilder, response, null, errorBody), storeError
                        )
                    )
                }
            } else {
                Result.failure(
                    storeAndReturn(errorMapper.mapRetrofitException(t, baseErrorBuilder()), storeError)
                )
            }
        } catch (d2Error: D2Error) {
            Result.failure(d2Error)
        } catch (t: Throwable) {
            Result.failure(storeAndReturn(errorMapper.mapRetrofitException(t, baseErrorBuilder()), storeError))
        }
    }

    private fun <P> catchError(
        errorCatcher: APICallErrorCatcher,
        errorBuilder: D2Error.Builder,
        response: Response<P>,
        errorBody: String,
        storeError: Boolean
    ): D2Error {
        return errorCatcher.catchError(response, errorBody)?.let { errorCode ->
            val d2Error = errorMapper.responseException(errorBuilder, response, errorCode, errorBody)

            if (errorCatcher.mustBeStored() == true) {
                storeAndReturn(d2Error, storeError = true)
            } else {
                d2Error
            }
        }
            ?: storeAndReturn(errorMapper.responseException(errorBuilder, response, null, errorBody), storeError)
    }

    private fun storeAndReturn(d2Error: D2Error, storeError: Boolean): D2Error {
        if (errorStore.isReady && storeError) {
            errorStore.insert(d2Error)
        }
        return d2Error
    }

    private fun <P> errorBuilder(response: Response<P>): D2Error.Builder {
        return errorMapper.getBaseErrorBuilder(response)
    }

    private fun baseErrorBuilder(): D2Error.Builder {
        return errorMapper.getBaseErrorBuilder()
    }
}
