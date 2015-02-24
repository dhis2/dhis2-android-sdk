package org.hisp.dhis2.android.sdk.persistence.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
@Table
public class OrganisationUnitProgramRelationship extends BaseModel {

    @Column(columnType = Column.PRIMARY_KEY)
    public String organisationUnitId;

    @Column(columnType = Column.PRIMARY_KEY)
    public String programId;

}
