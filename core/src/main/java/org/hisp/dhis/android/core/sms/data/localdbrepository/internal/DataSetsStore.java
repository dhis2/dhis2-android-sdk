package org.hisp.dhis.android.core.sms.data.localdbrepository.internal;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCollectionRepository;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore;
import org.hisp.dhis.android.core.datavalue.DataValue;
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
    private final DataSetCompleteRegistrationCollectionRepository dataSetRepository;

    @Inject
    DataSetsStore(DataValueModule dataValueModule,
                  DataValueStore dataValueStore,
                  DataSetCompleteRegistrationStore dataSetStore,
                  DataSetCompleteRegistrationCollectionRepository dataSetRepository) {
        this.dataValueModule = dataValueModule;
        this.dataValueStore = dataValueStore;
        this.dataSetStore = dataSetStore;
        this.dataSetRepository = dataSetRepository;
    }

    Single<List<DataValue>> getDataValues(String orgUnit, String period, String attributeOptionComboUid) {
        return Single.fromCallable(() -> dataValueModule.dataValues()
                .byOrganisationUnitUid().eq(orgUnit)
                .byPeriod().eq(period)
                .byAttributeOptionComboUid().eq(attributeOptionComboUid)
                .byState().in(Arrays.asList(State.uploadableStates()))
                .blockingGet());
    }

    Completable updateDataSetValuesState(String orgUnit,
                                         String period,
                                         String attributeOptionComboUid,
                                         State state) {
        return getDataValues(orgUnit, period, attributeOptionComboUid)
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
            DataSetCompleteRegistration dataSet = dataSetRepository
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
