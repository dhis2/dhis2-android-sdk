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

    public OptionSetFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.optionSetService = retrofit.create(OptionSetService.class);
        this.resourceHandler = resourceHandler;
        OptionHandler optionHandler = new OptionHandler(new OptionStoreImpl(databaseAdapter));
        this.optionSetHandler =
                new OptionSetHandler(new OptionSetStoreImpl(databaseAdapter), optionHandler);
    }

    public OptionSetCall newEndPointCall(Set<String> optionSetUids, Date serverDate) {
        return new OptionSetCall(optionSetService, optionSetHandler, databaseAdapter,
                resourceHandler, optionSetUids, serverDate);
    }

    public OptionSetHandler getHandler() {
        return optionSetHandler;
    }
}
