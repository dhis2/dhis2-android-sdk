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
package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.TransactionalCall;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementEndpointCall;
import org.hisp.dhis.android.core.datavalue.DataValueEndpointCall;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.indicator.IndicatorEndpointCall;
import org.hisp.dhis.android.core.indicator.IndicatorTypeEndpointCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;

import java.util.List;
import java.util.Set;

import retrofit2.Response;

import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getAssignedDataSetUids;
import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getDataElementUids;
import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getIndicatorTypeUids;
import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getIndicatorUids;
import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getOrganisationUnitUids;
import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getPeriodsIds;

public class DataSetParentCall extends TransactionalCall {
    private final User user;
    private final DataSetParentLinkManager linkManager;
    private final DataSetEndpointCall.Factory dataSetCallFactory;
    private final DataElementEndpointCall.Factory dataElementCallFactory;
    private final IndicatorEndpointCall.Factory indicatorCallFactory;
    private final IndicatorTypeEndpointCall.Factory indicatorTypeCallFactory;
    private final DataValueEndpointCall.Factory dataValueCallFactory;
    private final List<OrganisationUnit> organisationUnits;

    private DataSetParentCall(User user, GenericCallData data, DataSetParentLinkManager linkManager,
                              DataSetEndpointCall.Factory dataSetCallFactory,
                              DataElementEndpointCall.Factory dataElementCallFactory,
                              IndicatorEndpointCall.Factory indicatorCallFactory,
                              IndicatorTypeEndpointCall.Factory indicatorTypeCallFactory,
                              DataValueEndpointCall.Factory dataValueCallFactory,
                              List<OrganisationUnit> organisationUnits) {
        super(data);
        this.user = user;
        this.linkManager = linkManager;
        this.dataSetCallFactory = dataSetCallFactory;
        this.dataElementCallFactory = dataElementCallFactory;
        this.indicatorCallFactory = indicatorCallFactory;
        this.indicatorTypeCallFactory = indicatorTypeCallFactory;
        this.dataValueCallFactory = dataValueCallFactory;
        this.organisationUnits = organisationUnits;
    }

    @Override
    public Response callBody() throws Exception {
        Set<String> dataSetUids = getAssignedDataSetUids(user);
        DataSetEndpointCall dataSetEndpointCall = dataSetCallFactory.create(data, dataSetUids);
        Response<Payload<DataSet>> dataSetResponse = dataSetEndpointCall.call();

        List<DataSet> dataSets = dataSetResponse.body().items();
        DataElementEndpointCall dataElementEndpointCall =
                dataElementCallFactory.create(data, getDataElementUids(dataSets));
        Response<Payload<DataElement>> dataElementResponse = dataElementEndpointCall.call();

        IndicatorEndpointCall indicatorEndpointCall
                = indicatorCallFactory.create(data, getIndicatorUids(dataSets));
        Response<Payload<Indicator>> indicatorResponse = indicatorEndpointCall.call();

        List<Indicator> indicators = indicatorResponse.body().items();
        IndicatorTypeEndpointCall indicatorTypeEndpointCall
                = indicatorTypeCallFactory.create(data, getIndicatorTypeUids(indicators));
        indicatorTypeEndpointCall.call();

        DataValueEndpointCall dataValueEndpointCall = dataValueCallFactory.create(data, dataSetUids,
                getPeriodsIds(), getOrganisationUnitUids(user));
        dataValueEndpointCall.call();

        linkManager.saveDataSetDataElementAndIndicatorLinks(dataSets);
        linkManager.saveDataSetOrganisationUnitLinks(organisationUnits);

        return dataElementResponse;
    }

    public interface Factory {
        Call<Response> create(User user, GenericCallData data, List<OrganisationUnit> organisationUnits);
    }

    public static final Factory FACTORY = new Factory() {
        @Override
        public Call<Response> create(User user, GenericCallData data, List<OrganisationUnit> organisationUnits) {
            return new DataSetParentCall(user, data,
                    DataSetParentLinkManager.create(data.databaseAdapter()),
                    DataSetEndpointCall.FACTORY,
                    DataElementEndpointCall.FACTORY,
                    IndicatorEndpointCall.FACTORY,
                    IndicatorTypeEndpointCall.FACTORY,
                    DataValueEndpointCall.FACTORY,
                    organisationUnits);
        }
    };
}
