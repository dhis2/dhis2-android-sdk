/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis2.android.sdk.persistence.models;

import android.database.Cursor;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import org.hisp.dhis2.android.sdk.utils.Utils;

import java.util.UUID;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
@Table
public class DataValue extends BaseValue {

    private static final String CLASS_TAG = DataValue.class.getSimpleName();

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {}

    @JsonIgnore
    @Column(columnType = Column.PRIMARY_KEY)
    public long localEventId; /* reference to local event object */

    @JsonIgnore
    @Column
    public String event;

    @JsonProperty("dataElement")
    @Column(columnType = Column.PRIMARY_KEY)
    public String dataElement;

    @JsonProperty("providedElsewhere")
    @Column
    public boolean providedElsewhere;

    @JsonProperty("storedBy")
    @Column
    public String storedBy;

    public DataValue() {}

    public DataValue(Event event, String value, String dataElement, boolean providedElsewhere,
                      String storedBy) {
        this.localEventId = event.localId;
        this.event = event.event;
        this.value = value;
        this.dataElement = dataElement;
        this.providedElsewhere = providedElsewhere;
        this.storedBy = storedBy;
    }

    private DataValue(long localEventId, String event, String value, String dataElement, boolean providedElsewhere,
                     String storedBy) {
        this.localEventId = localEventId;
        this.event = event;
        this.value = value;
        this.dataElement = dataElement;
        this.providedElsewhere = providedElsewhere;
        this.storedBy = storedBy;
    }

    /**
     * makes a deep copy of the DataValue
     * @return
     */
    public DataValue clone() {
        DataValue dataValue = new DataValue(this.localEventId, this.event, this.value,
                this.dataElement, this.providedElsewhere, this.storedBy);
        return dataValue;
    }

    @Override
    public void save(boolean async) {
        if(Utils.isLocal(event) && DataValueController.getDataValue(localEventId, dataElement)!=null) {

            //DataValue ex = DataValueController.getDataValue(localEventId, dataElement);
            //if(ex == null)
            //    Log.d(CLASS_TAG, "existing is null " );
            //else Log.d(CLASS_TAG, "existing not null! " + ex.localEventId + ": " + ex.event);

            //to avoid overwriting UID from server due to race conditions with autosyncing with server
            //we only update the value (ie not the other fields) if the currently in-memory event UID is locally created
            updateManually(async);
        } else
            super.save(async);
    }

    public void updateManually(boolean async) {
        Queriable q = new Update().table(DataValue.class).set(
                Condition.column(DataValue$Table.VALUE).is(value))
                .where(Condition.column(DataValue$Table.LOCALEVENTID).is(localEventId),
                        Condition.column(DataValue$Table.DATAELEMENT).is(dataElement));
        if(async)
            TransactionManager.getInstance().transactQuery(DBTransactionInfo.create(BaseTransaction.PRIORITY_HIGH), q);
        else
        {
            Log.d(CLASS_TAG, "this is fine");
            Cursor c = q.query();
            c.close();
        }
    }

    @Override
    public void update(boolean async) {
        save(async);
    }

}
