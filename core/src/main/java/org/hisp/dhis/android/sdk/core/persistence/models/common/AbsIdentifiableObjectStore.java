package org.hisp.dhis.android.sdk.core.persistence.models.common;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.IModel;
import org.hisp.dhis.android.sdk.models.common.IdentifiableObject;

public abstract class AbsIdentifiableObjectStore<T extends IdentifiableObject> extends AbsStore<T> implements IIdentifiableObjectStore<T> {

    public <DatabaseEntityType extends Model & IModel> AbsIdentifiableObjectStore(Class<DatabaseEntityType> clazz) {
        super(clazz);
    }

    @Override
    public T queryById(long id) {
        return null;
    }

    @Override
    public T queryByUid(String uid) {
        return null;
    }
}
