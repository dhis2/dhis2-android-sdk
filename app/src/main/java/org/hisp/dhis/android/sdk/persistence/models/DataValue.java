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

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.utils.Utils;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class DataValue extends BaseValue {

    private static final String CLASS_TAG = DataValue.class.getSimpleName();

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {}

    @JsonIgnore
    @Column
    @PrimaryKey
    protected long localEventId = -1; /* reference to local event object */

    @JsonIgnore
    @Column
    private String event;

    @JsonProperty("dataElement")
    @Column
    @PrimaryKey
    private String dataElement;

    @JsonProperty("providedElsewhere")
    @Column
    private boolean providedElsewhere;

    @JsonProperty("storedBy")
    @Column
    private String storedBy;


    public DataValue() {
    }

    public DataValue(Event event, String value, String dataElement, boolean providedElsewhere,
                      String storedBy) {
        this.localEventId = event.getLocalId();
        this.event = event.getEvent();
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
     *
     * @return DataValue
     */
    @Override
    public DataValue clone() {
        DataValue dataValue = new DataValue(this.localEventId, this.event, this.getValue(),
                this.dataElement, this.providedElsewhere, this.storedBy);
        return dataValue;
    }

    public long getLocalEventId() {
        return localEventId;
    }

    public void setLocalEventId(long localEventId) {
        this.localEventId = localEventId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public boolean getProvidedElsewhere() {
        return providedElsewhere;
    }

    public void setProvidedElsewhere(boolean providedElsewhere) {
        this.providedElsewhere = providedElsewhere;
    }

    public String getStoredBy() {
        return storedBy;
    }

    public void setStoredBy(String storedBy) {
        this.storedBy = storedBy;
    }




    @Override
    public void save() {
        if(Utils.isLocal(event) && DataValueController.getDataValue(localEventId, dataElement)!=null) {

            //to avoid overwriting UID from server due to race conditions with autosyncing with server
            //we only update the value (ie not the other fields) if the currently in-memory event UID is locally created
            updateManually();
        } else
            super.save();
    }

    public void updateManually() {

        new Update(DataValue.class).set(
                Condition.column(DataValue$Table.VALUE).is(this.getValue()))
                .where(Condition.column(DataValue$Table.LOCALEVENTID).is(localEventId),
                        Condition.column(DataValue$Table.DATAELEMENT).is(dataElement)).queryClose();
    }

    @Override
    public void update() {
        save();
    }
}
