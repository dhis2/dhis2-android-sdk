package org.hisp.dhis.android.core.deletedobject;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class DeletedObjectEndPointCall implements Call<Response<Payload<DeletedObject>>> {

    private final DeletedObjectService deletedObjectService;
    private final String deletedObjectKlass;
    private final Date serverDate;
    private final ResourceHandler resourceHandler;
    private final DeletedObjectHandler deletedObjectHandler;

    private boolean isExecuted;

    public DeletedObjectEndPointCall(@NonNull DeletedObjectService deletedObjectService,
            @NonNull ResourceHandler resourceHandler,
            @NonNull DeletedObjectHandler deletedObjectHandler,
            @NonNull Date serverDate,
            @NonNull String deletedObjectKlass) {
        this.deletedObjectService = deletedObjectService;
        this.resourceHandler = resourceHandler;
        this.deletedObjectHandler = deletedObjectHandler;
        this.deletedObjectKlass = deletedObjectKlass;
        this.serverDate = new Date(serverDate.getTime());
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<DeletedObject>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        ResourceModel.Type type = ResourceModel.getResourceModelFromKlass(deletedObjectKlass);

        Response<Payload<DeletedObject>> deletedObjectsByLastUpdated = null;
        String lastSyncedDeletedObjects = resourceHandler.getLastUpdated(type);

        deletedObjectsByLastUpdated =
                deletedObjectService.getDeletedObjectsDeletedAt(
                        getSingleFields(), true, deletedObjectKlass,
                        lastSyncedDeletedObjects).execute();

        if (deletedObjectsByLastUpdated.isSuccessful()
                && deletedObjectsByLastUpdated.body().items() != null) {
            List<DeletedObject> deletedObjects = deletedObjectsByLastUpdated.body().items();

            int size = deletedObjects.size();

            for (int i = 0; i < size; i++) {
                DeletedObject deletedObject = deletedObjects.get(i);
                deletedObjectHandler.handle(deletedObject.uid(), deletedObject.klass());
            }

            resourceHandler.handleResource(type, serverDate);
        }
        return deletedObjectsByLastUpdated;
    }

    private Fields<DeletedObject> getSingleFields() {
        return Fields.<DeletedObject>builder().fields(
                DeletedObject.uid,
                DeletedObject.klass
        ).build();
    }
}
