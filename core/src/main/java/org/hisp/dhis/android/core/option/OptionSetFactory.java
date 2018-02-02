package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;
import java.util.Set;

import retrofit2.Retrofit;

public class OptionSetFactory {
    private final DatabaseAdapter databaseAdapter;
    private final OptionSetService optionSetService;
    private final ResourceHandler resourceHandler;
    private final OptionSetHandler optionSetHandler;
    private final OptionHandler optionHandler;
    private final OptionStore optionStore;
    private final OptionSetStore optionSetStore;

    public OptionSetFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.optionSetService = retrofit.create(OptionSetService.class);
        this.resourceHandler = resourceHandler;
        this.optionStore = new OptionStoreImpl(databaseAdapter);
        this.optionHandler = new OptionHandler(optionStore);
        this.optionSetStore = new OptionSetStoreImpl(databaseAdapter);
        this.optionSetHandler =
                new OptionSetHandler(new OptionSetStoreImpl(databaseAdapter), optionHandler);
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

    public OptionSetStore getOptionSetStore() {
        return optionSetStore;
    }
}
