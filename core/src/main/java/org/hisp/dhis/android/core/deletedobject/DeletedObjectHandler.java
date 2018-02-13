package org.hisp.dhis.android.core.deletedobject;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.IdentifiableStore;
public class DeletedObjectHandler {
    @NonNull
    public final IdentifiableStoreFactory IdentifiableStoreFactory;

    public DeletedObjectHandler(IdentifiableStoreFactory identifiableStoreFactory) {
        this.IdentifiableStoreFactory = identifiableStoreFactory;
    }

    public void handle(String uid, String klass) {
        IdentifiableStore identifiableStore = getStore(klass);
        identifiableStore.delete(uid);
    }

    public IdentifiableStore getStore(String klass) {
        return IdentifiableStoreFactory.getByKlass(klass);
    }
}
