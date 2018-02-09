package org.hisp.dhis.android.core.deletedobject;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;

import retrofit2.Retrofit;

public class DeletedObjectFactory {
    DeletedObjectHandler deletedObjectHandler;
    DeletedObjectService deletedObjectService;
    ResourceHandler resourceHandler;

    public DeletedObjectFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.deletedObjectService = retrofit.create(DeletedObjectService.class);
        DeletedObjectHandlerFactory deletedObjectHandlerFactory = new DeletedObjectHandlerFactory(databaseAdapter);
        deletedObjectHandler = new DeletedObjectHandler(deletedObjectHandlerFactory);
        this.resourceHandler = resourceHandler;
    }

    public DeletedObjectEndPointCall newEndPointCall(String deletedObjectKlass, Date serverDate)
            throws Exception {
        return new DeletedObjectEndPointCall(deletedObjectService, resourceHandler,
                deletedObjectHandler, serverDate, deletedObjectKlass);
    }
}
