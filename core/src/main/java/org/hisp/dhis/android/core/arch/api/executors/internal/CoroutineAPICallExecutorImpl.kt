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
package org.hisp.dhis.android.core.arch.api.executors.internal

import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.*
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.Transaction
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.internal.D2ErrorStore
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleaner
import org.hisp.dhis.android.core.user.internal.UserAccountDisabledErrorCatcher
import retrofit2.HttpException
import retrofit2.Response

@Reusable
internal class CoroutineAPICallExecutorImpl @Inject constructor(
    private val errorMapper: APIErrorMapper,
    private val userAccountDisabledErrorCatcher: UserAccountDisabledErrorCatcher,
    private val errorStore: D2ErrorStore,
    private val databaseAdapter: DatabaseAdapter,
    private val foreignKeyCleaner: ForeignKeyCleaner
) : CoroutineAPICallExecutor {

    @OptIn(DelicateCoroutinesApi::class)
    private val dbDispatcher = newSingleThreadContext("DB")

    @Suppress("TooGenericExceptionCaught")
    override suspend fun <P> wrap(
        storeError: Boolean,
        acceptedErrorCodes: List<Int>?,
        errorCatcher: APICallErrorCatcher?,
        errorClass: Class<P>?,
        block: suspend () -> P
    ): Result<P, D2Error> {
        return try {
            Result.Success(block.invoke())
        } catch (t: HttpException) {
            val response = t.response()

            if (response != null) {
                val errorBody = errorMapper.getErrorBody(response)
                val errorBuilder = errorBuilder(response)

                if (userAccountDisabledErrorCatcher.isUserAccountLocked(response, errorBody)) {
                    Result.Failure(
                        catchError(userAccountDisabledErrorCatcher, errorBuilder, response, errorBody, storeError)
                    )
                } else if (errorClass != null && acceptedErrorCodes?.contains(response.code()) == true) {
                    Result.Success(
                        ObjectMapperFactory.objectMapper().readValue(errorBody, errorClass)
                    )
                } else if (errorCatcher != null) {
                    Result.Failure(
                        catchError(errorCatcher, errorBuilder, response, errorBody, storeError)
                    )
                } else {
                    Result.Failure(
                        storeAndReturn(
                            errorMapper.responseException(errorBuilder, response, null, errorBody),
                            storeError
                        )
                    )
                }
            } else {
                Result.Failure(
                    storeAndReturn(errorMapper.mapRetrofitException(t, baseErrorBuilder()), storeError)
                )
            }
        } catch (d2Error: D2Error) {
            Result.Failure(d2Error)
        } catch (t: Throwable) {
            Result.Failure(storeAndReturn(errorMapper.mapRetrofitException(t, baseErrorBuilder()), storeError))
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun <P> wrapTransactionally(
        cleanForeignKeyErrors: Boolean,
        block: suspend () -> P
    ): P {
        return withContext(dbDispatcher) {
            val transaction = databaseAdapter.beginNewTransaction()
            try {
                val result = coroutineScope {
                    block.invoke()
                }
                successfulTransaction(transaction, cleanForeignKeyErrors)
                return@withContext result
            } catch (t: Throwable) {
                throw when (t) {
                    is D2Error -> t
                    else -> errorMapper.mapRetrofitException(t, baseErrorBuilder())
                }
            } finally {
                transaction.end()
            }
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

    private fun successfulTransaction(t: Transaction, cleanForeignKeys: Boolean) {
        if (cleanForeignKeys) {
            foreignKeyCleaner.cleanForeignKeyErrors()
        }
        t.setSuccessful()
    }
}
