/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactoryImpl;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.UidsNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.calls.processors.TransactionalNoResourceSyncCallProcessor;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.UidsQuery;

import java.util.Set;

public final class ProgramRuleEndpointCall {

    private ProgramRuleEndpointCall() {
    }

    public static UidsCallFactory<ProgramRule> factory(final APICallExecutor apiCallExecutor) {
        return new UidsCallFactoryImpl<ProgramRule>() {

            // TODO calculate
            private static final int MAX_UID_LIST_SIZE = 64;

            @Override
            protected CallFetcher<ProgramRule> fetcher(GenericCallData data, Set<String> uids) {
                final ProgramRuleService service = data.retrofit().create(ProgramRuleService.class);

                return new UidsNoResourceCallFetcher<ProgramRule>(uids, MAX_UID_LIST_SIZE, apiCallExecutor) {

                    @Override
                    protected retrofit2.Call<Payload<ProgramRule>> getCall(UidsQuery query) {
                        String programUidsFilterStr = "program." + ObjectWithUid.uid.in(query.uids()).generateString();
                        return service.getProgramRules(
                                ProgramRuleFields.allFields,
                                programUidsFilterStr,
                                Boolean.FALSE);
                    }
                };
            }

            @Override
            protected CallProcessor<ProgramRule> processor(GenericCallData data) {
                return new TransactionalNoResourceSyncCallProcessor<>(
                        data.databaseAdapter(),
                        ProgramRuleHandler.create(data.databaseAdapter())
                );
            }
        };
    }
}