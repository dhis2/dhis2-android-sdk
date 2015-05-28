package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Simen Skogly Russnes on 15.04.15.
 */
@JsonIgnoreProperties("modelAdapter")
public class BaseSerializableModel extends BaseModel {
}
