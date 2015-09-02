package org.hisp.dhis.android.sdk.models.common.meta;

import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.common.IdentifiableObject;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

/**
 * This class is intended to implement partial
 * functionality of ContentProviderOperation for DbFlow.
 */
public final class DbOperation<T extends IdentifiableObject> {
    private final DbAction mDbAction;
    private final T mModel;
    private final IStore<T> mModelStore;

    private DbOperation(DbAction dbAction, T model, IStore<T> store) {
        mModel = isNull(model, "IdentifiableObject object must nto be null,");
        mDbAction = isNull(dbAction, "BaseModel.DbAction object must not be null");
        mModelStore = isNull(store, "IStore object must not be null");
    }

    public static <T extends IdentifiableObject> DbOperationBuilder<T> with(IStore<T> store) {
        return new DbOperationBuilder<>(store);
    }

    public T getModel() {
        return mModel;
    }

    public DbAction getAction() {
        return mDbAction;
    }

    public IStore<T> getStore() {
        return mModelStore;
    }

    public void execute() {
        switch (mDbAction) {
            case INSERT: {
                mModelStore.insert(mModel);
                break;
            }
            case UPDATE: {
                mModelStore.update(mModel);
                break;
            }
            case SAVE: {
                mModelStore.save(mModel);
                break;
            }
            case DELETE: {
                mModelStore.delete(mModel);
                break;
            }
        }
    }

    public static class DbOperationBuilder<T extends IdentifiableObject> {
        private final IStore<T> mStore;

        DbOperationBuilder(IStore<T> store) {
            mStore = store;
        }

        public DbOperation insert(T model) {
            return new DbOperation<>(DbAction.INSERT, model, mStore);
        }

        public DbOperation update(T model) {
            return new DbOperation<>(DbAction.UPDATE, model, mStore);
        }

        public DbOperation save(T model) {
            return new DbOperation<>(DbAction.SAVE, model, mStore);
        }

        public DbOperation delete(T model) {
            return new DbOperation<>(DbAction.DELETE, model, mStore);
        }
    }
}
