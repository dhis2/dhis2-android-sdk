package org.hisp.dhis.android.core.deletedobject;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Where;
import org.hisp.dhis.android.core.data.api.Which;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeletedObjectService {
    String FIELDS = "fields";
    String KLASS = "klass";
    String DELETED_AT = "deletedAt";
    String SKIP_PAGING = "skipPaging";
    String DELETED_OBJECTS = "deletedObjects";

    @GET(DELETED_OBJECTS)
    Call<Payload<DeletedObject>> getDeletedObjectsDeletedAt(
            @Query(FIELDS) @Which Fields<DeletedObject> fields,
            @Query(SKIP_PAGING) Boolean paging, @Query(KLASS) String klass,
            @Query(DELETED_AT) @Where String deletedAt);
}
