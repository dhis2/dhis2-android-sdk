package org.hisp.dhis.android.core.deletedobject;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableObjectStore;
public class DeletedObjectHandler {


    @NonNull
    public final DeletedObjectHandlerFactory deletedObjectHandlerFactory;

    public DeletedObjectHandler(DeletedObjectHandlerFactory deletedObjectHandlerFactory) {
        this.deletedObjectHandlerFactory = deletedObjectHandlerFactory;
    }

    public void handle(String uid, String klass) {
        DeletableObjectStore deletableObjectStore = getStore(klass);
        deletableObjectStore.delete(uid);
    }

    public DeletableObjectStore getStore(String klass) {
        return deletedObjectHandlerFactory.getByKlass(klass);
    }
}
