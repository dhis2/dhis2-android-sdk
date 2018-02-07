package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.option.OptionHandler;
import org.hisp.dhis.android.core.option.OptionSetHandler;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.option.OptionStore;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;

public class DataElementFactory {
    private final DatabaseAdapter databaseAdapter;
    private final DataElementService dataElementService;
    private final ResourceHandler resourceHandler;
    private final DataElementStore dataElementStore;
    private final DataElementHandler dataElementHandler;
    private final List<DeletableStore> deletableStores;

    public DataElementFactory(Retrofit retrofit, DatabaseAdapter databaseAdapter,
            ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.dataElementService = retrofit.create(DataElementService.class);
        this.resourceHandler = resourceHandler;
        this.dataElementStore = new DataElementStoreImpl(databaseAdapter);
        OptionSetStore optionSetStore = new OptionSetStoreImpl(databaseAdapter);
        OptionStore optionStore = new OptionStoreImpl(databaseAdapter);
        OptionHandler optionHandler = new OptionHandler(optionStore);

        OptionSetHandler optionSetHandler = new OptionSetHandler(optionSetStore, optionHandler);
        this.dataElementHandler = new DataElementHandler(dataElementStore, optionSetHandler);
        deletableStores = new ArrayList<>();
        deletableStores.add(dataElementStore);

    }

    public DataElementEndPointCall newEndPointCall(DataElementQuery dataElementQuery,
            Date serverDate) {
        return new DataElementEndPointCall(dataElementService,
                dataElementQuery,
                dataElementHandler, resourceHandler, databaseAdapter, serverDate);
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStores;
    }

    public DataElementStore getDataElementStore() {
        return dataElementStore;
    }

    public DataElementHandler getDataElementHandler() {
        return dataElementHandler;
    }
}
