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
package org.hisp.dhis.android.core.arch.call.executors.internal

import android.util.Log
import androidx.room.deferredTransaction
import androidx.room.useWriterConnection
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.maintenance.internal.D2ErrorStore
import org.koin.core.annotation.Singleton

@Singleton
internal class D2CallExecutor(
    private val databaseAdapter: DatabaseAdapter,
    private val errorStore: D2ErrorStore,
) : D2CallExecutorInterface {
    private val exceptionBuilder: D2Error.Builder = D2Error
        .builder()
        .errorComponent(D2ErrorComponent.SDK)

    @Throws(D2Error::class)
    override suspend fun <C> executeD2Call(call: suspend () -> C): C {
        try {
            return call()
        } catch (d2E: D2Error) {
            throw d2E
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, e.toString())
            throw exceptionBuilder
                .errorCode(D2ErrorCode.UNEXPECTED)
                .errorDescription("Unexpected error calling $call").build()
        }
    }

    @Throws(D2Error::class)
    override suspend fun <C> executeD2CallTransactionally(call: suspend () -> C): C {
        try {
            return innerExecuteD2CallTransactionally(call)
        } catch (d2E: D2Error) {
            errorStore.insert(d2E)
            throw d2E
        }
    }

    @Throws(D2Error::class)
    @Suppress("TooGenericExceptionCaught")
    private suspend fun <C> innerExecuteD2CallTransactionally(call: suspend () -> C): C {
        return try {
            databaseAdapter.getCurrentDatabase().useWriterConnection { transactor ->
                transactor.deferredTransaction {
                    call()
                }
            }
        } catch (d2E: D2Error) {
            throw d2E
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, e.toString())
            throw exceptionBuilder
                .errorCode(D2ErrorCode.UNEXPECTED)
                .errorDescription("Unexpected error calling $call").build()
        }
    }

    companion object {
        @JvmStatic
        fun create(databaseAdapter: DatabaseAdapter): D2CallExecutor {
            return D2CallExecutor(databaseAdapter, koin.get<D2ErrorStore>())
        }
    }
}
