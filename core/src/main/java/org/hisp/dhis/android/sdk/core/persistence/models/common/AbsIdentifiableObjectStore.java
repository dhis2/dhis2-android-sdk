package org.hisp.dhis.android.sdk.core.persistence.models.common;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.BaseIdentifiableObject$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.BaseModel$Flow;
import org.hisp.dhis.android.sdk.models.common.base.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.base.IModel;
import org.hisp.dhis.android.sdk.models.common.base.IdentifiableObject;

public abstract class AbsIdentifiableObjectStore<T extends IdentifiableObject> extends AbsStore<T> implements IIdentifiableObjectStore<T> {

    public <DatabaseEntityType extends Model & IModel> AbsIdentifiableObjectStore(Class<DatabaseEntityType> clazz) {
        super(clazz);
    }

    @Override
    public T queryById(long id) {
        Model databaseEntity = new Select()
                .from(getModelClass())
                .where(BaseModel$Flow.COLUMN_ID)
                .querySingle();
        return mapToModel(databaseEntity);
    }

    @Override
    public T queryByUid(String uid) {
        Model databaseEntity = new Select()
                .from(getModelClass())
                .where(BaseIdentifiableObject$Flow.COLUMN_UID)
                .querySingle();
        return mapToModel(databaseEntity);
    }
}
