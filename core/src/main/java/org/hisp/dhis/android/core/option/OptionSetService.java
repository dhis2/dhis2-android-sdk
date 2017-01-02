package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OptionSetService {

    @GET("optionSets")
    Call<Payload<OptionSet>> optionSets(@Query("paging") boolean paging,
                                        @Query("fields") @Fields Filter<OptionSet> filter);
}
