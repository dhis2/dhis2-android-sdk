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

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;
import java.util.UUID;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
@Table
public class Event extends BaseModel {

    public static String STATUS_ACTIVE = "ACTIVE";

    public static String STATUS_COMPLETED = "COMPLETED";

    public static String STATUS_VISITED = "VISITED";

    public static String STATUS_FUTURE_VISIT = "FUTURE_VISIT";

    public static String STATUS_LATE_VISIT = "LATE_VISIT";

    public static String STATUS_SKIPPED = "SKIPPED";

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {}

    public Event() {
    }

    @JsonIgnore
    @Column
    public boolean fromServer = true;

    @JsonIgnore
    @Column(columnType = Column.PRIMARY_KEY)
    public String event;

    @JsonIgnore
    public String getEvent() {
        return event;
    }

    @JsonProperty("event")
    public void setEvent(String event) {
        this.event = event;
    }

    @JsonProperty("status")
    @Column
    public String status;

    @JsonProperty("trackedEntityInstance")
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    @Column
    public String trackedEntityInstance;

    @JsonProperty("enrollment")
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    @Column
    public String enrollment;

    @JsonProperty("program")
    @Column
    public String programId;

    @JsonProperty("programStage")
    @Column
    public String programStageId;

    @JsonProperty("orgUnit")
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    @Column
    public String organisationUnitId;

    @JsonProperty("eventDate")
    @Column
    public String eventDate;

    @JsonProperty("dueDate")
    @Column
    public String dueDate;

    @JsonProperty("dataValues")
    public List<DataValue> dataValues;

    public List<DataValue> getDataValues() {
        if( dataValues == null) dataValues = Select.all(DataValue.class,
                Condition.column(DataValue$Table.EVENT).is(event));
        return dataValues;
    }

    @Override
    public void delete(boolean async) {
        if(dataValues != null) {
            for(DataValue dataValue: dataValues)
                dataValue.delete(async);
        }
        super.delete(async);
    }
}
