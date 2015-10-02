package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.models.common.base.IModel;

public class BaseModel$Flow extends BaseModel implements IModel {
    public static final String COLUMN_ID = "id";

    @Column(name = COLUMN_ID)
    @PrimaryKey(autoincrement = true)
    long id;

    @Override
    public final long getId() {
        return id;
    }

    @Override
    public final void setId(long id) {
        this.id = id;
    }
}
