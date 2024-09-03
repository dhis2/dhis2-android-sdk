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
package org.hisp.dhis.android.core.indicator.internal

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCallFactoryImpl
import org.hisp.dhis.android.core.arch.call.fetchers.internal.CoroutineCallFetcher
import org.hisp.dhis.android.core.arch.call.fetchers.internal.UidsNoResourceCallFetcher
import org.hisp.dhis.android.core.arch.call.internal.GenericCallData
import org.hisp.dhis.android.core.arch.call.processors.internal.CallProcessor
import org.hisp.dhis.android.core.arch.call.processors.internal.TransactionalNoResourceSyncCallProcessor
import org.hisp.dhis.android.core.arch.call.queries.internal.UidsQuery
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.koin.core.annotation.Singleton

@Singleton
internal class IndicatorTypeEndpointCallFactory(
    data: GenericCallData,
    coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val service: IndicatorTypeService,
    private val handler: IndicatorTypeHandler,
) : UidsCallFactoryImpl<IndicatorType>(data, coroutineAPICallExecutor) {
    override suspend fun fetcher(uids: Set<String>): CoroutineCallFetcher<IndicatorType> {
        return object :
            UidsNoResourceCallFetcher<IndicatorType>(uids, MAX_UID_LIST_SIZE, coroutineAPICallExecutor) {

            override suspend fun getCall(query: UidsQuery): Payload<IndicatorType> {
                return service.getIndicatorTypes(
                    IndicatorTypeFields.allFields,
                    null,
                    IndicatorTypeFields.uid.`in`(query.uids()),
                    false,
                )
            }
        }
    }

    override fun processor(): CallProcessor<IndicatorType> {
        return TransactionalNoResourceSyncCallProcessor(
            data.databaseAdapter,
            handler,
        )
    }

    companion object {
        private const val MAX_UID_LIST_SIZE = 140
    }
}
