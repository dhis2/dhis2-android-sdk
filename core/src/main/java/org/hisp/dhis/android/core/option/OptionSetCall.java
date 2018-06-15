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

package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactoryImpl;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.UidsNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.calls.processors.TransactionalResourceCallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.UidsQuery;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Set;

public final class OptionSetCall {

    private OptionSetCall() {}

    private static Fields<OptionSet> getFields() {
        return Fields.<OptionSet>builder().fields(
                OptionSet.uid, OptionSet.code, OptionSet.name,
                OptionSet.displayName, OptionSet.created,
                OptionSet.lastUpdated, OptionSet.version,
                OptionSet.valueType,
                OptionSet.options.with(Option.uid, Option.code, Option.created,
                        Option.name, Option.displayName, Option.created,
                        Option.lastUpdated,
                        Option.optionSet.with(
                                OptionSet.uid
                        ),
                        Option.style.with(ObjectStyle.allFields)
                )
        ).build();
    }

    public static final UidsCallFactory<OptionSet> FACTORY = new UidsCallFactoryImpl<OptionSet>() {

        private final ResourceModel.Type resourceType = ResourceModel.Type.OPTION_SET;
        private static final int MAX_UID_LIST_SIZE = 130;

        @Override
        protected CallFetcher<OptionSet> fetcher(GenericCallData data, Set<String> uids) {
            final OptionSetService service = data.retrofit().create(OptionSetService.class);

            return new UidsNoResourceCallFetcher<OptionSet>(uids, MAX_UID_LIST_SIZE) {

                @Override
                protected retrofit2.Call<Payload<OptionSet>> getCall(UidsQuery query) {
                    return service.optionSets(getFields(), OptionSet.uid.in(query.uids()),
                            null, query.paging());
                }
            };
        }

        @Override
        protected CallProcessor<OptionSet> processor(GenericCallData data) {
            return new TransactionalResourceCallProcessor<>(
                    data,
                    OptionSetHandler.create(data.databaseAdapter()),
                    resourceType,
                    new OptionSetModelBuilder()
            );
        }
    };
}