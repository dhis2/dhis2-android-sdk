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
package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall;
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCallFactory;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.modules.internal.MetadataModuleByUidDownloader;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.indicator.IndicatorType;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.period.internal.PeriodHandler;
import org.hisp.dhis.android.core.validation.ValidationRule;
import org.hisp.dhis.android.core.validation.internal.ValidationRuleUidsCall;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public class DataSetModuleDownloader implements MetadataModuleByUidDownloader<List<DataSet>> {
    private final UidsCallFactory<DataSet> dataSetCallFactory;
    private final UidsCallFactory<DataElement> dataElementCallFactory;
    private final UidsCallFactory<Indicator> indicatorCallFactory;
    private final UidsCallFactory<IndicatorType> indicatorTypeCallFactory;
    private final UidsCall<OptionSet> optionSetCall;
    private final UidsCall<Option> optionCall;
    private final UidsCall<ValidationRule> validationRuleCall;
    private final ValidationRuleUidsCall validationRuleUidsCall;
    private final PeriodHandler periodHandler;

    @Inject
    DataSetModuleDownloader(UidsCallFactory<DataSet> dataSetCallFactory,
                            UidsCallFactory<DataElement> dataElementCallFactory,
                            UidsCallFactory<Indicator> indicatorCallFactory,
                            UidsCallFactory<IndicatorType> indicatorTypeCallFactory,
                            UidsCall<OptionSet> optionSetCall,
                            UidsCall<Option> optionCall,
                            UidsCall<ValidationRule> validationRuleCall,
                            ValidationRuleUidsCall validationRuleUidsCall,
                            PeriodHandler periodHandler) {
        this.dataSetCallFactory = dataSetCallFactory;
        this.dataElementCallFactory = dataElementCallFactory;
        this.indicatorCallFactory = indicatorCallFactory;
        this.indicatorTypeCallFactory = indicatorTypeCallFactory;
        this.optionSetCall = optionSetCall;
        this.optionCall = optionCall;
        this.validationRuleCall = validationRuleCall;
        this.validationRuleUidsCall = validationRuleUidsCall;
        this.periodHandler = periodHandler;
    }

    @Override
    public Single<List<DataSet>> downloadMetadata(Set<String> orgUnitDataSetUids) {
        return Single.fromCallable(() -> {

            List<DataSet> dataSets = dataSetCallFactory.create(orgUnitDataSetUids).call();

            List<DataElement> dataElements = dataElementCallFactory.create(
                    DataSetParentUidsHelper.getDataElementUids(dataSets)).call();

            List<Indicator> indicators = indicatorCallFactory.create(
                    DataSetParentUidsHelper.getIndicatorUids(dataSets)).call();

            indicatorTypeCallFactory.create(
                    DataSetParentUidsHelper.getIndicatorTypeUids(indicators)).call();

            Set<String> optionSetUids = DataSetParentUidsHelper.getAssignedOptionSetUids(dataElements);
            optionSetCall.download(optionSetUids).blockingGet();

            optionCall.download(optionSetUids).blockingGet();

            List<ObjectWithUid> validationRuleUids =
                    validationRuleUidsCall.download(UidsHelper.getUids(dataSets)).blockingGet();

            validationRuleCall.download(UidsHelper.getUids(validationRuleUids)).blockingGet();

            periodHandler.generateAndPersist();

            return dataSets;
        });
    }
}