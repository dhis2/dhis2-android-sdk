package org.hisp.dhis.android.core.relationship;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.Where;
import org.hisp.dhis.android.core.data.api.Which;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RelationshipTypeService {
    String FILTER = "filter";
    String FIELDS = "fields";

    @GET("relationshipTypes")
    Call<Payload<RelationshipType>> getRelationshipTypes(
            @NonNull @Query(FIELDS) @Which Fields<RelationshipType> fields,
            @NonNull @Query(FILTER) @Where Filter<RelationshipType, String> idFilter,
            @NonNull @Query(FILTER) @Where Filter<RelationshipType, String> lastUpdate);
}
