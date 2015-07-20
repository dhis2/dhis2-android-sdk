package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Simen Skogly Russnes on 15.04.15.
 */
@JsonIgnoreProperties("modelAdapter")
public abstract class BaseSerializableModel extends BaseModel {

    @JsonIgnore
    @Column(name = "fromServer")
    boolean fromServer = true;

    @JsonIgnore
    @Column(name = "localId")
    @PrimaryKey(autoincrement = true)
    long localId = -1;

    public boolean isFromServer() {
        return fromServer;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }
}
