package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.runtime.DBTransactionInfo;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.BaseTransaction;
import com.raizlabs.android.dbflow.sql.Queriable;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis2.android.sdk.utils.Utils;

import java.util.List;
import java.util.UUID;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table
public class TrackedEntityInstance extends BaseSerializableModel {

    public TrackedEntityInstance() {

    }

    public TrackedEntityInstance (Program program, String organisationUnit) {
        fromServer = false;
        trackedEntityInstance = Dhis2.QUEUED + UUID.randomUUID().toString();
        trackedEntity = program.getTrackedEntity().getId();
        created = Utils.getCurrentTime();
        lastUpdated = Utils.getCurrentTime();
        orgUnit = organisationUnit;
    }

    @JsonIgnore
    @Column
    public boolean fromServer = true;

    @JsonIgnore
    @Column(columnType = Column.PRIMARY_KEY_AUTO_INCREMENT)
    public long localId;

    @JsonIgnore
    @Column(unique = true)
    public String trackedEntityInstance;

    @JsonProperty("trackedEntityInstance")
    public void setTrackedEntityInstance(String trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    /**
     * Should only be used by Jackson so that event is included only if its non-local generated
     * Use Event.event instead to access it.
     */
    @JsonProperty("trackedEntityInstance")
    public String getTrackedEntityInstance() {
        if(Utils.isLocal(trackedEntityInstance))
            return null;
        else return trackedEntityInstance;
    }

    @JsonProperty("trackedEntity")
    @Column
    public String trackedEntity;

    //@JsonProperty("created")
    @JsonIgnore
    @Column
    public String created;

    //@JsonProperty("lastUpdated")
    @JsonIgnore
    @Column
    public String lastUpdated;

    @JsonProperty("orgUnit")
    @Column
    public String orgUnit;

    @JsonProperty("attributes")
    public List<TrackedEntityAttributeValue> getAttributes() {
        return DataValueController.getTrackedEntityAttributeValues(localId);
    }

    @Override
    public void save(boolean async) {
        /* check if there is an existing tei with the same UID to avoid duplicates */
        TrackedEntityInstance existingTei = DataValueController.
                getTrackedEntityInstance(trackedEntityInstance);
        boolean exists = false;
        if(existingTei != null) {
            localId = existingTei.localId;
            exists = true;
        }
        if(getTrackedEntityInstance() == null && DataValueController.getTrackedEntityInstance(localId) != null) {
            //means that the tei is local and has previosuly been saved
            //then we don't want to update the tei reference in fear of overwriting
            //an updated reference from server while the item has been loaded in memory
            //unfortunately a bit of hard coding I suppose but it's important to verify data integrity
            updateManually(async);
        } else {
            if(!exists) super.save(false); //ensuring a localId is created to give foreign key to attributes
            else super.save(async);
        }
    }

    /**
     * Updates manually without touching UIDs the fields that are modifiable by user.
     * This will and should only be called if the enrollment has a locally created temp event reference
     * and has previously been saved, so that it has a localId.
     */
    public void updateManually(boolean async) {
        Queriable q = new Update().table(TrackedEntityInstance.class).set(
                Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(fromServer))
                .where(Condition.column(TrackedEntityInstance$Table.LOCALID).is(localId));
        if(async)
            TransactionManager.getInstance().transactQuery(DBTransactionInfo.create(BaseTransaction.PRIORITY_HIGH), q);
        else
            q.query().close();
    }

    @Override
    public void update(boolean async) {
        save(async);
    }

}
