package org.hisp.dhis.android.core.deletedobject;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.user.User;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class DeletedObjectEndPointCall implements Call<Response<Payload<DeletedObject>>> {

    private final DeletedObjectService deletedObjectService;
    private final DatabaseAdapter databaseAdapter;
    private final String deletedObjectKlass;
    private final Date serverDate;
    private final ResourceHandler resourceHandler;
    private final DeletedObjectHandler deletedObjectHandler;

    private boolean isExecuted;

    public DeletedObjectEndPointCall(@NonNull DeletedObjectService deletedObjectService,
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull ResourceStore resourceStore,
            @NonNull DeletedObjectHandler deletedObjectHandler,
            @NonNull Date serverDate,
            @NonNull String deletedObjectKlass) {
        this.deletedObjectService = deletedObjectService;
        this.databaseAdapter = databaseAdapter;
        this.resourceHandler = new ResourceHandler(resourceStore);
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

        ResourceModel.Type type = getResourceModelFromKlass(deletedObjectKlass);

        if (type == null) {
            throw new IllegalArgumentException(deletedObjectKlass + " unsupported klass type");
        }

        String lastSyncedDeletedObjects = resourceHandler.getLastUpdated(type);

        Filter<DeletedObject, String> lastUpdatedFilter = DeletedObject.deletedAt.gt(lastSyncedDeletedObjects
        );
        Response<Payload<DeletedObject>> deletedObjectsByLastUpdated =
                deletedObjectService.getDeletedObjectsDeletedAt(
                getSingleFields(), true, deletedObjectKlass, lastUpdatedFilter).execute();

        if (deletedObjectsByLastUpdated.isSuccessful()
                && deletedObjectsByLastUpdated.body().items() != null) {
            List<DeletedObject> deletedObjects = deletedObjectsByLastUpdated.body().items();

            int size = deletedObjects.size();

            for (int i = 0; i < size; i++) {
                DeletedObject deletedObject = deletedObjects.get(i);
                deletedObjectHandler.handle(deletedObject.uid(), type);
            }

            resourceHandler.handleResource(type, serverDate);
        }
        return deletedObjectsByLastUpdated;
    }

    private ResourceModel.Type getResourceModelFromKlass(String klass) {
        if (klass.equals(User.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_USER;
        } else if (klass.equals(OrganisationUnit.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_ORGANISATION_UNIT;
        } else if (klass.equals(Program.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM;
        } else if (klass.equals(OptionSet.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_OPTION_SET;
        } else if (klass.equals(TrackedEntity.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_TRACKED_ENTITY;
        } else if (klass.equals(Category.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_CATEGORY;
        } else if (klass.equals(CategoryCombo.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_CATEGORY_COMBO;
        }
        return null;
    }

    private Fields<DeletedObject> getSingleFields() {
        return Fields.<DeletedObject>builder().fields(
                DeletedObject.uid
        ).build();
    }

    private Fields<DeletedObject> getAllFields() {
        return Fields.<DeletedObject>builder().fields(
                DeletedObject.uid, DeletedObject.klass,
                DeletedObject.deletedAt, DeletedObject.deletedBy
        ).build();
    }
}
