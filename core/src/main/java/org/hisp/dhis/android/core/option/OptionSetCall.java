package org.hisp.dhis.android.core.option;

import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Filter;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class OptionSetCall implements Call<Response<Payload<OptionSet>>> {
    // retrofit service
    private final OptionSetService optionSetService;

    // databases and stores
    private final SQLiteDatabase database;
    private final OptionSetStore optionSetStore;
    private final OptionStore optionStore;


    private boolean isExecuted;

    public OptionSetCall(OptionSetService optionSetService,
                         SQLiteDatabase database,
                         OptionSetStore optionSetStore,
                         OptionStore optionStore) {
        this.optionSetService = optionSetService;
        this.database = database;
        this.optionSetStore = optionSetStore;
        this.optionStore = optionStore;
    }


    @Override
    public boolean isExecuted() {
        return isExecuted;
    }

    @Override
    public Response<Payload<OptionSet>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalArgumentException("Already executed");
            }

            isExecuted = true;
        }
        Response<Payload<OptionSet>> response = getOptionSets();

        if (response.isSuccessful()) {
            saveOptionSets(response.body().items());
        }
        return response;
    }

    private Response<Payload<OptionSet>> getOptionSets() throws IOException {

        Filter<OptionSet> optionSetFilter = Filter.<OptionSet>builder().fields(OptionSet.uid,
                OptionSet.code, OptionSet.name, OptionSet.displayName,
                OptionSet.created, OptionSet.lastUpdated,
                OptionSet.version, OptionSet.value_type,
                OptionSet.options.with(Option.uid, Option.code, Option.created,
                        Option.name, Option.displayName, Option.created,
                        Option.lastUpdated)).build();


        return optionSetService.optionSets(false, optionSetFilter).execute();
    }

    private void saveOptionSets(List<OptionSet> optionSetList) {
        if (optionSetList != null && !optionSetList.isEmpty()) {
            database.beginTransaction();

            for (OptionSet optionSet : optionSetList) {
                try {

                    // insert optionSet model into option set table
                    optionSetStore.insert(
                            optionSet.uid(), optionSet.code(), optionSet.name(),
                            optionSet.displayName(), optionSet.created(),
                            optionSet.lastUpdated(), optionSet.version(),
                            optionSet.valueType()
                    );

                    List<Option> options = optionSet.options();

                    if (options != null && !options.isEmpty()) {
                        for (Option option : options) {
                            optionStore.insert(option.uid(), option.code(),
                                    option.name(), option.displayName(),
                                    option.created(), option.lastUpdated(),
                                    optionSet.uid());
                        }
                    }

                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
            }
        }
    }
}
