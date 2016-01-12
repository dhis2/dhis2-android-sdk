/*
 *  Copyright (c) 2016, University of Oslo
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
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramTrackedEntityAttribute extends BaseModel {

    private static final String CLASS_TAG = ProgramTrackedEntityAttribute.class.getSimpleName();

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    }

    @Column(name = "trackedEntityAttribute")
    @PrimaryKey
    String trackedEntityAttribute;

    @Column(name = "sortOrder")
    int sortOrder;

    @JsonProperty("allowFutureDate")
    @Column(name = "allowFutureDate")
    boolean allowFutureDate;

    @JsonProperty("displayInList")
    @Column(name = "displayInList")
    boolean displayInList;

    @JsonProperty("mandatory")
    @Column(name = "mandatory")
    boolean mandatory;

    @Column(name = "program")
    @JsonIgnore
    @PrimaryKey
    String program;

    @JsonProperty("trackedEntityAttribute")
    public void setTrackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        trackedEntityAttribute.async().save();
        this.trackedEntityAttribute = trackedEntityAttribute.id;
    }

    public boolean getAllowFutureDate() {
        return allowFutureDate;
    }

    public void setAllowFutureDate(boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    public boolean getDisplayInList() {
        return displayInList;
    }

    public void setDisplayInList(boolean displayInList) {
        this.displayInList = displayInList;
    }

    public boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public TrackedEntityAttribute getTrackedEntityAttribute() {
        return MetaDataController.getTrackedEntityAttribute(trackedEntityAttribute);
    }

    public void setTrackedEntityAttribute(String trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    public String getTrackedEntityAttributeId() {
        return trackedEntityAttribute;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
