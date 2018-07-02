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
import org.hisp.dhis.android.core.calls.factories.ListCallFactory;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementEndpointCall;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.indicator.IndicatorEndpointCall;
import org.hisp.dhis.android.core.indicator.IndicatorType;
import org.hisp.dhis.android.core.indicator.IndicatorTypeEndpointCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.period.PeriodHandler;
import org.hisp.dhis.android.core.user.User;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class DataSetParentCall extends SyncCall<Void> {
    private final DataSetParentLinkManager linkManager;
    private final GenericCallData data;
    private final ListCallFactory<DataSet> dataSetCallFactory;
    private final UidsCallFactory<DataElement> dataElementCallFactory;
    private final UidsCallFactory<Indicator> indicatorCallFactory;
    private final UidsCallFactory<IndicatorType> indicatorTypeCallFactory;
    private final List<OrganisationUnit> organisationUnits;
    private final PeriodHandler periodHandler;

    private DataSetParentCall(GenericCallData data,
                              DataSetParentLinkManager linkManager,
                              ListCallFactory<DataSet> dataSetCallFactory,
                              UidsCallFactory<DataElement> dataElementCallFactory,
                              UidsCallFactory<Indicator> indicatorCallFactory,
                              UidsCallFactory<IndicatorType> indicatorTypeCallFactory,
                              List<OrganisationUnit> organisationUnits,
                              PeriodHandler periodHandler) {
        this.data = data;
        this.linkManager = linkManager;
        this.dataSetCallFactory = dataSetCallFactory;
        this.dataElementCallFactory = dataElementCallFactory;
        this.indicatorCallFactory = indicatorCallFactory;
        this.indicatorTypeCallFactory = indicatorTypeCallFactory;
        this.organisationUnits = organisationUnits;
        this.periodHandler = periodHandler;
    }

    @Override
    public Void call() throws D2CallException {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor();

        return executor.executeD2CallTransactionally(data.databaseAdapter(), new Callable<Void>() {
            @Override
            public Void call() throws D2CallException {
                List<DataSet> dataSets = executor.executeD2Call(dataSetCallFactory.create(data));
                Set<String> dataSetUids = UidsHelper.getUids(dataSets);

                executor.executeD2Call(dataElementCallFactory.create(data,
                        DataSetParentUidsHelper.getDataElementUids(dataSets)));

                List<Indicator> indicators = executor.executeD2Call(indicatorCallFactory.create(data,
                        DataSetParentUidsHelper.getIndicatorUids(dataSets)));

                executor.executeD2Call(indicatorTypeCallFactory.create(data,
                        DataSetParentUidsHelper.getIndicatorTypeUids(indicators)));

                periodHandler.generateAndPersist();

                linkManager.saveDataSetDataElementAndIndicatorLinks(dataSets);
                linkManager.saveDataSetOrganisationUnitLinks(organisationUnits, dataSetUids);

                return null;
            }
        });


    }

    public interface Factory {
        Call<Void> create(User user, GenericCallData data, List<OrganisationUnit> organisationUnits);
    }

    public static final Factory FACTORY = new Factory() {
        @Override
        public Call<Void> create(User user, GenericCallData data, List<OrganisationUnit> organisationUnits) {
            return new DataSetParentCall(data,
                    DataSetParentLinkManager.create(data.databaseAdapter()),
                    DataSetEndpointCall.FACTORY,
                    DataElementEndpointCall.FACTORY,
                    IndicatorEndpointCall.FACTORY,
                    IndicatorTypeEndpointCall.FACTORY,
                    organisationUnits,
                    PeriodHandler.create(data.databaseAdapter()));
        }
    };
}
