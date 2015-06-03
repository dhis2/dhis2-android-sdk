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

import static android.text.TextUtils.isEmpty;

/**
 * @author Simen Skogly Russnes on 18.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class DataElement extends BaseNameableObject {

    @JsonProperty("type")
    @Column
    private String type;

    @JsonProperty("zeroIsSignificant")
    @Column
    private boolean zeroIsSignificant;

    @JsonProperty("externalAccess")
    @Column
    private boolean externalAccess;

    @JsonProperty("aggregationOperator")
    @Column
    private String aggregationOperator;

    @JsonProperty("formName")
    @Column
    private String formName;

    @JsonProperty("numberType")
    @Column
    private String numberType;

    @JsonProperty("domainType")
    @Column
    private String domainType;

    @JsonProperty("dimension")
    @Column
    private String dimension;

    @JsonProperty("displayName")
    @Column
    private String displayName;

    @JsonProperty("displayFormName")
    @Column
    private String displayFormName;

    @Column
    protected String optionSet;

    @JsonProperty("optionSet")
    public void setOptionSet(Map<String, Object> optionSet) {
        this.optionSet = (String) optionSet.get("id");
    }

    public String getDisplayName() {
        if (!isEmpty(displayFormName)) {
            return displayFormName;
        } else {
            return displayName;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }

    public boolean getZeroIsSignificant() {
        return zeroIsSignificant;
    }

    public void setZeroIsSignificant(boolean zeroIsSignificant) {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public String getAggregationOperator() {
        return aggregationOperator;
    }

    public void setAggregationOperator(String aggregationOperator) {
        this.aggregationOperator = aggregationOperator;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayFormName() {
        return displayFormName;
    }

    public void setDisplayFormName(String displayFormName) {
        this.displayFormName = displayFormName;
    }

    /**
     *
     */
    public static final String VALUE_TYPE_INT = "int";

    public static final String VALUE_TYPE_STRING = "string";

    public static final String VALUE_TYPE_USER_NAME = "username";

    public static final String VALUE_TYPE_BOOL = "bool";

    public static final String VALUE_TYPE_TRUE_ONLY = "trueOnly";

    public static final String VALUE_TYPE_DATE = "date";

    public static final String VALUE_TYPE_UNIT_INTERVAL = "unitInterval";

    public static final String VALUE_TYPE_PERCENTAGE = "percentage";

    public static final String VALUE_TYPE_NUMBER = "number";

    public static final String VALUE_TYPE_POSITIVE_INT = "posInt";

    public static final String VALUE_TYPE_NEGATIVE_INT = "negInt";

    public static final String VALUE_TYPE_ZERO_OR_POSITIVE_INT = "zeroPositiveInt";

    public static final String VALUE_TYPE_TEXT = "text";

    public static final String VALUE_TYPE_LONG_TEXT = "longText";
}
