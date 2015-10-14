/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.java.sdk.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.java.sdk.persistence.Dhis2Database;

import java.util.Map;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class TrackedEntityAttribute extends BaseNameableObject {

    public static final String TYPE_DATE = "date";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_LETTER = "letter";
    public static final String TYPE_BOOL = "bool";
    public static final String TYPE_TRUE_ONLY = "trueOnly";
    public static final String TYPE_OPTION_SET = "optionSet";
    public static final String TYPE_PHONE_NUMBER = "phoneNumber";
    public static final String TYPE_TRACKER_ASSOCIATE = "trackerAssociate";
    public static final String TYPE_USERS = "users";
    public static final String TYPE_EMAIL = "email";

    @Column(name = "optionSet")
    String optionSet;

    @JsonProperty("unique")
    @Column(name = "isUnique")
    boolean isUnique;

    @JsonProperty("programScope")
    @Column(name = "programScope")
    boolean programScope;

    @JsonProperty("orgunitScope")
    @Column(name = "orgunitScope")
    boolean orgunitScope;

    @JsonProperty("displayInListNoProgram")
    @Column(name = "displayInListNoProgram")
    boolean displayInListNoProgram;

    @JsonProperty("displayOnVisitSchedule")
    @Column(name = "displayOnVisitSchedule")
    boolean displayOnVisitSchedule;

    @JsonProperty("externalAccess")
    @Column(name = "externalAccess")
    boolean externalAccess;

    @JsonProperty("valueType")
    @Column(name = "valueType")
    String valueType;

    @JsonProperty("confidential")
    @Column(name = "confidential")
    boolean confidential;

    @JsonProperty("inherit")
    @Column(name = "inherit")
    boolean inherit;

    @JsonProperty("sortOrderVisitSchedule")
    @Column(name = "sortOrderVisitSchedule")
    int sortOrderVisitSchedule;

    @JsonProperty("dimension")
    @Column(name = "dimension")
    String dimension;

    @JsonProperty("sortOrderInListNoProgram")
    @Column(name = "sortOrderInListNoProgram")
    int sortOrderInListNoProgram;

    @JsonProperty("optionSet")
    public void setOptionSet(Map<String, Object> optionSet) {
        this.optionSet = (String) optionSet.get("id");
    }

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }

    public boolean getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public boolean getProgramScope() {
        return programScope;
    }

    public void setProgramScope(boolean programScope) {
        this.programScope = programScope;
    }

    public boolean getOrgunitScope() {
        return orgunitScope;
    }

    public void setOrgunitScope(boolean orgunitScope) {
        this.orgunitScope = orgunitScope;
    }

    public boolean getDisplayInListNoProgram() {
        return displayInListNoProgram;
    }

    public void setDisplayInListNoProgram(boolean displayInListNoProgram) {
        this.displayInListNoProgram = displayInListNoProgram;
    }

    public boolean getDisplayOnVisitSchedule() {
        return displayOnVisitSchedule;
    }

    public void setDisplayOnVisitSchedule(boolean displayOnVisitSchedule) {
        this.displayOnVisitSchedule = displayOnVisitSchedule;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }

    public boolean getInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public int getSortOrderVisitSchedule() {
        return sortOrderVisitSchedule;
    }

    public void setSortOrderVisitSchedule(int sortOrderVisitSchedule) {
        this.sortOrderVisitSchedule = sortOrderVisitSchedule;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public int getSortOrderInListNoProgram() {
        return sortOrderInListNoProgram;
    }

    public void setSortOrderInListNoProgram(int sortOrderInListNoProgram) {
        this.sortOrderInListNoProgram = sortOrderInListNoProgram;
    }
}
