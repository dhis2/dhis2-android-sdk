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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis2.android.sdk.persistence.Dhis2Database;

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
    public boolean isUnique;

    @JsonProperty("programScope")
    @Column
    public boolean programScope;

    @JsonProperty("orgunitScope")
    @Column
    public boolean orgunitScope;

    @JsonProperty("displayInListNoProgram")
    @Column
    public boolean displayInListNoProgram;

    @JsonProperty("displayOnVisitSchedule")
    @Column
    public boolean displayOnVisitSchedule;

    @JsonProperty("externalAccess")
    @Column
    public boolean externalAccess;

    @JsonProperty("valueType")
    @Column
    public String valueType;

    @JsonProperty("confidential")
    @Column
    public boolean confidential;

    @JsonProperty("inherit")
    @Column
    public boolean inherit;

    @JsonProperty("sortOrderVisitSchedule")
    @Column
    public int sortOrderVisitSchedule;

    @JsonProperty("dimension")
    @Column
    public String dimension;

    @JsonProperty("displayName")
    @Column
    public String displayName;

    @JsonProperty("sortOrderInListNoProgram")
    @Column
    public int sortOrderInListNoProgram;

    @Column
    public String optionSet;

    @JsonProperty("optionSet")
    public void setOptionSet(Map<String, Object> optionSet) {
        this.optionSet = (String) optionSet.get("id");
    }

    public String getOptionSet() {
        return optionSet;
    }

}
