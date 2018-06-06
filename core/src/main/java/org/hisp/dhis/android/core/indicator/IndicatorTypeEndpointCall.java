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

package org.hisp.dhis.android.core.indicator;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.EndpointPayloadCall;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.ListPersistor;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.TransactionalResourceListPersistor;
import org.hisp.dhis.android.core.common.UidsCallFactory;
import org.hisp.dhis.android.core.common.UidsQuery;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.List;
import java.util.Set;

public final class IndicatorTypeEndpointCall extends EndpointPayloadCall<IndicatorType, UidsQuery> {
    private final IndicatorTypeService indicatorTypeService;

    private IndicatorTypeEndpointCall(GenericCallData data, IndicatorTypeService indicatorTypeService,
                                      UidsQuery query, ListPersistor<IndicatorType> persistor) {
        super(data, ResourceModel.Type.INDICATOR_TYPE, query, persistor);
        this.indicatorTypeService = indicatorTypeService;
    }

    @Override
    protected retrofit2.Call<Payload<IndicatorType>> getCall(UidsQuery query, String lastUpdated) {
        return indicatorTypeService.getIndicatorTypes(
                IndicatorType.allFields,
                IndicatorType.lastUpdated.gt(lastUpdated),
                IndicatorType.uid.in(query.uids()),
                Boolean.FALSE);
    }

    public static final UidsCallFactory<IndicatorType> FACTORY = new UidsCallFactory<IndicatorType>() {
        @Override
        public Call<List<IndicatorType>> create(GenericCallData data, Set<String> uids) {
            return new IndicatorTypeEndpointCall(data,
                    data.retrofit().create(IndicatorTypeService.class),
                    UidsQuery.create(uids, null),
                    new TransactionalResourceListPersistor<>(
                            data,
                            IndicatorTypeHandler.create(data.databaseAdapter()),
                            ResourceModel.Type.INDICATOR_TYPE,
                            new IndicatorTypeModelBuilder()
                    )
            );
        }
    };
}