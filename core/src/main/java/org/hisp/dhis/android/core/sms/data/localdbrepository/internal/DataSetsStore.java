/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.sms.data.localdbrepository.internal;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCollectionRepository;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository;
import org.hisp.dhis.android.core.datavalue.DataValueModule;
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

class DataSetsStore {
    private final DataValueModule dataValueModule;
    private final DataValueStore dataValueStore;
    private final DataSetCompleteRegistrationStore dataSetStore;
    private final DataSetCompleteRegistrationCollectionRepository completeRegistrationRepository;

    @Inject
    DataSetsStore(DataValueModule dataValueModule,
                  DataValueStore dataValueStore,
                  DataSetCompleteRegistrationStore dataSetStore,
                  DataSetCompleteRegistrationCollectionRepository completeRegistrationRepository) {
        this.dataValueModule = dataValueModule;
        this.dataValueStore = dataValueStore;
        this.dataSetStore = dataSetStore;
        this.completeRegistrationRepository = completeRegistrationRepository;
    }

    Single<List<DataValue>> getDataValues(String dataSetUid, String orgUnit,
                                          String period, String attributeOptionComboUid) {
        return Single.fromCallable(() -> {
            DataValueCollectionRepository baseDataValuesRepo = dataValueModule.dataValues()
                    .byDataSetUid(dataSetUid)
                    .byOrganisationUnitUid().eq(orgUnit)
                    .byPeriod().eq(period)
                    .byAttributeOptionComboUid().eq(attributeOptionComboUid);

            List<DataValue> dataValues = baseDataValuesRepo
                    .bySyncState().in(Arrays.asList(State.uploadableStatesIncludingError()))
                    .blockingGet();

            // TODO Workaround to prevent empty lists. Not supported in compression library
            if (dataValues.isEmpty()) {
                List<DataValue> allDataValues = baseDataValuesRepo.blockingGet();

                if (!allDataValues.isEmpty()) {
                    dataValues = allDataValues.subList(0, 1);
                }
            }

            return dataValues;
        });
    }

    Completable updateDataSetValuesState(String dataSet,
                                         String orgUnit,
                                         String period,
                                         String attributeOptionComboUid,
                                         State state) {
        return getDataValues(dataSet, orgUnit, period, attributeOptionComboUid)
                .flattenAsObservable(items -> items)
                .flatMapCompletable(item -> Completable.fromAction(() ->
                        dataValueStore.setState(item, state))
                );
    }

    Completable updateDataSetCompleteRegistrationState(String dataSetId,
                                                       String orgUnit,
                                                       String period,
                                                       String attributeOptionComboUid,
                                                       State state) {
        return Completable.fromAction(() -> {
            DataSetCompleteRegistration dataSet = completeRegistrationRepository
                    .byDataSetUid().eq(dataSetId)
                    .byOrganisationUnitUid().eq(orgUnit)
                    .byPeriod().eq(period)
                    .byAttributeOptionComboUid().eq(attributeOptionComboUid)
                    .one().blockingGet();
            if (dataSet != null) {
                dataSetStore.setState(dataSet, state);
            }
        });
    }
}
