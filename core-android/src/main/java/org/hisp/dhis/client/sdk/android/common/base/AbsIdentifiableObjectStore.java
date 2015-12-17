package org.hisp.dhis.client.sdk.android.common.base;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.client.sdk.android.flow.BaseIdentifiableObject$Flow;
import org.hisp.dhis.client.sdk.android.flow.BaseModel$Flow;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.common.base.IModel;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;

public abstract class AbsIdentifiableObjectStore<ModelType extends IdentifiableObject,
        DatabaseEntityType extends Model & IModel> extends AbsStore<ModelType, DatabaseEntityType> implements IIdentifiableObjectStore<ModelType> {

    public AbsIdentifiableObjectStore(IMapper<ModelType, DatabaseEntityType> mapper) {
        super(mapper);
    }

    @Override
    public ModelType queryById(long id) {
        DatabaseEntityType databaseEntity = new Select()
                .from(getMapper().getDatabaseEntityTypeClass())
                .where(BaseModel$Flow.COLUMN_ID)
                .querySingle();
        return getMapper().mapToModel(databaseEntity);
    }

    @Override
    public ModelType queryByUid(String uid) {
        DatabaseEntityType databaseEntity = new Select()
                .from(getMapper().getDatabaseEntityTypeClass())
                .where(BaseIdentifiableObject$Flow.COLUMN_UID)
                .querySingle();
        return getMapper().mapToModel(databaseEntity);
    }
}
