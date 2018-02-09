package org.hisp.dhis.android.core.deletedobject;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.SoftDeletableStore;
public class DeletedObjectHandler {
    @NonNull
    public final DeletedObjectHandlerFactory deletedObjectHandlerFactory;

    public DeletedObjectHandler(DeletedObjectHandlerFactory deletedObjectHandlerFactory) {
        this.deletedObjectHandlerFactory = deletedObjectHandlerFactory;
    }

    public void handle(String uid, String klass) {
        SoftDeletableStore softDeletableStore = getStore(klass);
        softDeletableStore.delete(uid);
    }

    public SoftDeletableStore getStore(String klass) {
        return deletedObjectHandlerFactory.getByKlass(klass);
    }
}
