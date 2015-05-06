package org.hisp.dhis2.android.sdk.persistence.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

import org.hisp.dhis2.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.utils.Utils;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@Table
public class TrackedEntityAttributeValue extends BaseValue {
    private static final String CLASS_TAG = TrackedEntityAttributeValue.class.getSimpleName();

    @JsonProperty("attribute")
    @Column(columnType = Column.PRIMARY_KEY)
    public String trackedEntityAttributeId;

    @JsonIgnore
    @Column(columnType = Column.PRIMARY_KEY)
    public String trackedEntityInstanceId;

    @JsonIgnore
    @Column
    public long localTrackedEntityInstanceId;

    /**
     * workaround for sending code if attribute is option set.
     * @return
     */
    @JsonProperty("value")
    public String getValue() {
        TrackedEntityAttribute tea = MetaDataController.
                getTrackedEntityAttribute(trackedEntityAttributeId);
        if(tea.valueType.equals(TrackedEntityAttribute.TYPE_OPTION_SET)) {
            OptionSet optionSet = MetaDataController.getOptionSet(tea.getOptionSet());
            for(Option o: optionSet.getOptions()) {
                if(o.name.equals(value)) {
                    return o.code;
                }
            }
        } else return value;
        return null;
    }

    @Override
    public void save(boolean async) {
        if(Utils.isLocal(trackedEntityInstanceId) && DataValueController.
                getTrackedEntityAttributeValue(trackedEntityAttributeId,
                        localTrackedEntityInstanceId)!=null) {


            //to avoid overwriting UID from server due to race conditions with autosyncing with server
            //we only update the value (ie and not the other fields) if the currently in-memory event UID is locally created
            updateManually(async);
        } else {
            super.save(async);
        }
    }


    public void updateManually(boolean async) {
        Queriable q = new Update().table(TrackedEntityAttributeValue.class).set(
                Condition.column(TrackedEntityAttributeValue$Table.VALUE).is(value))
                .where(Condition.column(TrackedEntityAttributeValue$Table.LOCALTRACKEDENTITYINSTANCEID).is(localTrackedEntityInstanceId),
                        Condition.column(TrackedEntityAttributeValue$Table.TRACKEDENTITYATTRIBUTEID).is(trackedEntityAttributeId));
        if(async)
            TransactionManager.getInstance().transactQuery(DBTransactionInfo.create(BaseTransaction.PRIORITY_HIGH), q);
        else
            q.queryClose();
    }

    @Override
    public void update(boolean async) {
        save(async);
    }
}
