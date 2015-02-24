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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.persistence.Dhis2Database;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 17.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class Program extends BaseIdentifiableObject {

    public static final String SINGLE_EVENT_WITH_REGISTRATION = "SINGLE_EVENT_WITH_REGISTRATION";

    public static final String SINGLE_EVENT_WITHOUT_REGISTRATION = "SINGLE_EVENT_WITHOUT_REGISTRATION";

    public static final String MULTIPLE_EVENTS_WITH_REGISTRATION = "MULTIPLE_EVENTS_WITH_REGISTRATION";

    @JsonProperty("type")
    @Column
    public int type;

    @JsonProperty("kind")
    @Column
    public String kind;

    @JsonProperty("version")
    @Column
    public int version;

    @JsonProperty("dateOfEnrollmentDescription")
    @Column
    public String dateOfEnrollmentDescription;

    @JsonProperty("description")
    @Column
    public String description;

    @JsonProperty("onlyEnrollOnce")
    @Column
    public boolean onlyEnrollOnce;

    @JsonProperty("externalAccess")
    @Column
    public boolean extenalAccess;

    @JsonProperty("displayIncidentDate")
    @Column
    public boolean displayIncidentDate;

    @JsonProperty("dateOfIncidentDateDescription")
    @Column
    public boolean dateOfIncidentDateDescription;

    @JsonProperty("registration")
    @Column
    public boolean registration;

    @JsonProperty("selectEnrollmentDatesInFuture")
    @Column
    public boolean selectEnrollmentDatesInFuture;

    @JsonProperty("dataEntryMethod")
    @Column
    public boolean dataEntryMethod;

    @JsonProperty("singleEvent")
    @Column
    public boolean singleEvent;

    @JsonProperty("ignoreOverdueEvents")
    @Column
    public boolean ignoreOverdueEvents;

    @JsonProperty("relationshipFromA")
    @Column
    public boolean relationshipFromA;

    @JsonProperty("displayName")
    @Column
    public String displayName;

    @JsonProperty("selectIncidentDatesInFuture")
    @Column
    public boolean selectIncidentDatesInFuture;

    @JsonProperty("trackedEntity")
    @Column(columnType = Column.FOREIGN_KEY,
            references = {@ForeignKeyReference(columnName = "trackedEntity",
            columnType = String.class, foreignColumnName = "id")})
    public TrackedEntity trackedEntity;

    //@JsonProperty("programStages")
    private List<ProgramStage> programStages;

    @JsonProperty("programTrackedEntityAttributes")
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    public Program() {}

    public TrackedEntity getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(TrackedEntity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    /**
     * Using lazy loading for one to many relationships
     * @return
     */
    public List<ProgramStage> getProgramStages() {
        if(programStages == null) {
            programStages = Select.all(ProgramStage.class,
                    Condition.column(ProgramStage$Table.PROGRAM).is(id));
        }
        return programStages;
    }

    public List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes() {
        if(programTrackedEntityAttributes == null) {
            programTrackedEntityAttributes = Select.all(ProgramTrackedEntityAttribute.class,
                    Condition.column(ProgramTrackedEntityAttribute$Table.PROGRAM).is(id));
        }
        return programTrackedEntityAttributes;
    }


}
