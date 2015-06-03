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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

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

    @JsonProperty("unique")
    @Column
    private boolean isUnique;

    @JsonProperty("programScope")
    @Column
    private boolean programScope;

    @JsonProperty("orgunitScope")
    @Column
    private boolean orgunitScope;

    @JsonProperty("displayInListNoProgram")
    @Column
    private boolean displayInListNoProgram;

    @JsonProperty("displayOnVisitSchedule")
    @Column
    private boolean displayOnVisitSchedule;

    @JsonProperty("externalAccess")
    @Column
    private boolean externalAccess;

    @JsonProperty("valueType")
    @Column
    private String valueType;

    @JsonProperty("confidential")
    @Column
    private boolean confidential;

    @JsonProperty("inherit")
    @Column
    private boolean inherit;

    @JsonProperty("sortOrderVisitSchedule")
    @Column
    private int sortOrderVisitSchedule;

    @JsonProperty("dimension")
    @Column
    private String dimension;

    @JsonProperty("displayName")
    @Column
    private String displayName;

    @JsonProperty("sortOrderInListNoProgram")
    @Column
    private int sortOrderInListNoProgram;

    @Column
    protected String optionSet;

    @JsonProperty("optionSet")
    public void setOptionSet(Map<String, Object> optionSet) {
        this.optionSet = (String) optionSet.get("id");
    }

    public String getOptionSet() {
        return optionSet;
    }

    public boolean getIsUnique() {
        return isUnique;
    }

    public boolean getProgramScope() {
        return programScope;
    }

    public boolean getOrgunitScope() {
        return orgunitScope;
    }

    public boolean getDisplayInListNoProgram() {
        return displayInListNoProgram;
    }

    public boolean getDisplayOnVisitSchedule() {
        return displayOnVisitSchedule;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public String getValueType() {
        return valueType;
    }

    public boolean getConfidential() {
        return confidential;
    }

    public boolean getInherit() {
        return inherit;
    }

    public int getSortOrderVisitSchedule() {
        return sortOrderVisitSchedule;
    }

    public String getDimension() {
        return dimension;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSortOrderInListNoProgram() {
        return sortOrderInListNoProgram;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }

    public void setSortOrderInListNoProgram(int sortOrderInListNoProgram) {
        this.sortOrderInListNoProgram = sortOrderInListNoProgram;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public void setSortOrderVisitSchedule(int sortOrderVisitSchedule) {
        this.sortOrderVisitSchedule = sortOrderVisitSchedule;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public void setDisplayOnVisitSchedule(boolean displayOnVisitSchedule) {
        this.displayOnVisitSchedule = displayOnVisitSchedule;
    }

    public void setDisplayInListNoProgram(boolean displayInListNoProgram) {
        this.displayInListNoProgram = displayInListNoProgram;
    }

    public void setOrgunitScope(boolean orgunitScope) {
        this.orgunitScope = orgunitScope;
    }

    public void setProgramScope(boolean programScope) {
        this.programScope = programScope;
    }

    public void setIsUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }
}
