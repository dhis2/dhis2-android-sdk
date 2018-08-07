package org.hisp.dhis.android.core.datavalue;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.imports.ImportSummary;

import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Retrofit;

public final class DataValuePostCall extends SyncCall<ImportSummary> {

    private final DataValueService dataValueService;
    private final DataValueStore dataValueStore;

    @Override
    public ImportSummary call() throws Exception {

        setExecuted();

        Collection<DataValue> toPostDataValues = new ArrayList<>();

        appendPostableDataValues(toPostDataValues);
        appendUpdatableDataValues(toPostDataValues);

        if (toPostDataValues.isEmpty()) {
            return null;
        }

        DataValueSet dataValueSet = new DataValueSet(toPostDataValues);

        ImportSummary importSummary = new APICallExecutor().executeObjectCall(
                dataValueService.postDataValues(dataValueSet));

        handleImportSummary(dataValueSet, importSummary);

        return importSummary;
    }

    private void appendPostableDataValues(Collection<DataValue> dataValues) {
        dataValues.addAll(dataValueStore.getDataValuesWithState(State.TO_POST));
    }

    private void appendUpdatableDataValues(Collection<DataValue> dataValues) {
        dataValues.addAll(dataValueStore.getDataValuesWithState(State.TO_UPDATE));
    }

    private void handleImportSummary(DataValueSet dataValueSet, ImportSummary importSummary) {

        DataValueImportHandler dataValueImportHandler =
                new DataValueImportHandler(dataValueStore);

        dataValueImportHandler.handleImportSummary(dataValueSet, importSummary);
    }

    private DataValuePostCall(@NonNull DataValueService dataValueService,
                              @NonNull DataValueStore dataValueSetStore) {

        this.dataValueService = dataValueService;
        this.dataValueStore = dataValueSetStore;
    }

    public static DataValuePostCall create(@NonNull DatabaseAdapter databaseAdapter,
                                     @NonNull Retrofit retrofit) {

        return new DataValuePostCall(retrofit.create(DataValueService.class),
                                     DataValueStore.create(databaseAdapter));
    }
}
