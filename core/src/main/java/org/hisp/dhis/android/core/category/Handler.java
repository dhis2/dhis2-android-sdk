package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

public abstract class Handler<E extends BaseIdentifiableObject> {

    @NonNull
    private final Store<E> store;

    public Handler(@NonNull Store<E> store) {
        this.store = store;
    }

    public void handle(E entity) {

        if (isDeleted(entity)) {
            store.delete(entity);
        } else {

            boolean updated = store.update(entity, entity);

            if (!updated) {
                store.insert(entity);
                afterInsert(entity);
            }
        }
    }

    public abstract void afterInsert(E entity);
}
