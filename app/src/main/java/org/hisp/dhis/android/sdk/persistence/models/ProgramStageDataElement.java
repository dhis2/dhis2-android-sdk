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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.Map;

/**
 * @author Simen Skogly Russnes on 18.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramStageDataElement extends BaseModel {

    private static final String CLASS_TAG = "ProgramStageDataElement";

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    @JsonProperty("allowFutureDate")
    @Column
    private boolean allowFutureDate;

    @JsonProperty("sortOrder")
    @Column
    private int sortOrder;

    @JsonProperty("displayInReports")
    @Column
    private boolean displayInReports;

    @JsonProperty("allowProvidedElsewhere")
    @Column
    private boolean allowProvidedElsewhere;

    @JsonProperty("compulsory")
    @Column
    private boolean compulsory;

    @Column
    private String programStageSection;

    @JsonProperty("programStage")
    public void setProgramStage(Map<String, Object> programStage) {
        this.programStage = (String) programStage.get("id");
    }

    @Column
    @PrimaryKey
    protected String programStage;

    @JsonProperty("dataElement")
    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement.id;
        dataElement.async().save();
    }

    @Column
    @PrimaryKey
    protected String dataElement;

    public boolean getAllowFutureDate() {
        return allowFutureDate;
    }

    public void setAllowFutureDate(boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean getAllowProvidedElsewhere() {
        return allowProvidedElsewhere;
    }

    public void setAllowProvidedElsewhere(boolean allowProvidedElsewhere) {
        this.allowProvidedElsewhere = allowProvidedElsewhere;
    }

    public boolean getCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    public String getProgramStage() {
        return programStage;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public DataElement getDataElement() {
        return MetaDataController.getDataElement(dataElement);
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public String getProgramStageSection() {
        return programStageSection;
    }

    public boolean getDisplayInReports() {
        return displayInReports;
    }

    public boolean isAllowProvidedElsewhere() {
        return allowProvidedElsewhere;
    }

    public void setProgramStageSection(String programStageSection) {
        this.programStageSection = programStageSection;
    }

    public void setDisplayInReports(boolean displayInReports) {
        this.displayInReports = displayInReports;
    }

    public String getDataelement()
    {
        return dataElement;
    }
}
