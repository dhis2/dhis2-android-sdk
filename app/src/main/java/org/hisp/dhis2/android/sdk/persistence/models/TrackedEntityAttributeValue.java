package org.hisp.dhis2.android.sdk.persistence.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@Table
public class TrackedEntityAttributeValue extends BaseModel {

    @Column(columnType = Column.PRIMARY_KEY)
    public String trackedEntityAttributeId;

    @Column(columnType = Column.PRIMARY_KEY)
    public String trackedEntityInstanceId;

    @Column
    public String value;

}
