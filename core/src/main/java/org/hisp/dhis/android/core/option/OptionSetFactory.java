package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import retrofit2.Retrofit;

public class OptionSetFactory {
    private final DatabaseAdapter databaseAdapter;
    private final OptionSetService optionSetService;
    private final ResourceHandler resourceHandler;
    private final OptionSetHandler optionSetHandler;
    private final OptionHandler optionHandler;
    private final OptionStore optionStore;

    private final List<DeletableStore> deletableStores;

    public OptionSetFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.optionSetService = retrofit.create(OptionSetService.class);
        this.resourceHandler = resourceHandler;
        this.optionStore = new OptionStoreImpl(databaseAdapter);

        OptionSetStore optionSetStore = new OptionSetStoreImpl(databaseAdapter);

        this.optionHandler = new OptionHandler(optionStore);
        this.optionSetHandler =
                new OptionSetHandler(optionSetStore, optionHandler);

        this.deletableStores = new ArrayList<>();
        this.deletableStores.add(optionSetStore);
        this.deletableStores.add(optionStore);
    }

    public OptionSetCall newEndPointCall(Set<String> optionSetUids, Date serverDate) {
        return new OptionSetCall(optionSetService, optionSetHandler, databaseAdapter,
                resourceHandler, optionSetUids, serverDate);
    }

    public OptionSetHandler getOptionSetHandler() {
        return optionSetHandler;
    }

    public OptionHandler getOptionHandler() {
        return optionHandler;
    }

    public OptionStore getOptionStore() {
        return optionStore;
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStores;
    }
}
