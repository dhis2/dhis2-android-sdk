/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.arch.modules.MetadataModuleDownloader;
import org.hisp.dhis.android.core.calls.factories.ListCallFactory;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.indicator.IndicatorType;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.period.PeriodHandler;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class DataSetModuleDownloader implements MetadataModuleDownloader<List<DataSet>> {
    private final ListCallFactory<DataSet> dataSetCallFactory;
    private final UidsCallFactory<DataElement> dataElementCallFactory;
    private final UidsCallFactory<Indicator> indicatorCallFactory;
    private final UidsCallFactory<IndicatorType> indicatorTypeCallFactory;
    private final UidsCallFactory<OptionSet> optionSetCallFactory;
    private final PeriodHandler periodHandler;

    @Inject
    DataSetModuleDownloader(ListCallFactory<DataSet> dataSetCallFactory,
                            UidsCallFactory<DataElement> dataElementCallFactory,
                            UidsCallFactory<Indicator> indicatorCallFactory,
                            UidsCallFactory<IndicatorType> indicatorTypeCallFactory,
                            UidsCallFactory<OptionSet> optionSetCallFactory,
                            PeriodHandler periodHandler) {
        this.dataSetCallFactory = dataSetCallFactory;
        this.dataElementCallFactory = dataElementCallFactory;
        this.indicatorCallFactory = indicatorCallFactory;
        this.indicatorTypeCallFactory = indicatorTypeCallFactory;
        this.optionSetCallFactory = optionSetCallFactory;
        this.periodHandler = periodHandler;
    }

    @Override
    public Callable<List<DataSet>> downloadMetadata() {
        return () -> {

            List<DataSet> dataSets = dataSetCallFactory.create().call();

            List<DataElement> dataElements = dataElementCallFactory.create(
                    DataSetParentUidsHelper.getDataElementUids(dataSets)).call();

            List<Indicator> indicators = indicatorCallFactory.create(
                    DataSetParentUidsHelper.getIndicatorUids(dataSets)).call();

            indicatorTypeCallFactory.create(
                    DataSetParentUidsHelper.getIndicatorTypeUids(indicators)).call();

            Set<String> optionSetUids = DataSetParentUidsHelper.getAssignedOptionSetUids(dataElements);
            optionSetCallFactory.create(optionSetUids).call();

            periodHandler.generateAndPersist();

            return dataSets;
        };
    }
}