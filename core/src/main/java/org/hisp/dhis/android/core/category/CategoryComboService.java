package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Which;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CategoryComboService {

    @GET("categoryCombos")
    Call<Payload<CategoryCombo>> getCategoryCombos(
            @Query("fields") @Which Fields<CategoryCombo> fields,
            @Query("paging") Boolean paging, @Query("page") int page,
            @Query("pageSize") int pageSize);
}
