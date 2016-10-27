/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.models.dataelement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseNameableObject;
import org.hisp.dhis.client.models.common.ValueType;
import org.hisp.dhis.client.models.option.OptionSet;

import javax.annotation.Nullable;

// TODO: Unit tests
@AutoValue
@JsonDeserialize(builder = AutoValue_DataElement.Builder.class)
public abstract class DataElement extends BaseNameableObject {
    private final static String JSON_PROPERTY_VALUE_TYPE = "valueType";
    private final static String JSON_PROPERTY_ZERO_IS_SIGNIFICANT = "zeroIsSignificant";
    private final static String JSON_PROPERTY_AGGREGATION_OPERATOR = "aggregationOperator";
    private final static String JSON_PROPERTY_FORM_NAME = "formName";
    private final static String JSON_PROPERTY_NUMBER_TYPE = "numberType";
    private final static String JSON_PROPERTY_DOMAIN_TYPE = "domainType";
    private final static String JSON_PROPERTY_DIMENSION = "dimension";
    private final static String JSON_PROPERTY_DISPLAY_FORM_NAME = "displayFormName";
    private final static String JSON_PROPERTY_OPTION_SET = "optionSet";

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ZERO_IS_SIGNIFICANT)
    public abstract Boolean zeroIsSignificant();

    @Nullable
    @JsonProperty(JSON_PROPERTY_AGGREGATION_OPERATOR)
    public abstract String aggregationOperator();

    @Nullable
    @JsonProperty(JSON_PROPERTY_FORM_NAME)
    public abstract String formName();

    @Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_TYPE)
    public abstract String numberType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DOMAIN_TYPE)
    public abstract String domainType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DIMENSION)
    public abstract String dimension();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_FORM_NAME)
    public abstract String displayFormName();

    @Nullable
    @JsonProperty(JSON_PROPERTY_OPTION_SET)
    public abstract OptionSet optionSet();

    public static Builder builder() {
        return new AutoValue_DataElement.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
        public abstract Builder valueType(@Nullable ValueType valueType);

        @JsonProperty(JSON_PROPERTY_ZERO_IS_SIGNIFICANT)
        public abstract Builder zeroIsSignificant(@Nullable Boolean zeroIsSignificant);

        @JsonProperty(JSON_PROPERTY_AGGREGATION_OPERATOR)
        public abstract Builder aggregationOperator(@Nullable String aggregationOperator);

        @JsonProperty(JSON_PROPERTY_FORM_NAME)
        public abstract Builder formName(@Nullable String formName);

        @JsonProperty(JSON_PROPERTY_NUMBER_TYPE)
        public abstract Builder numberType(@Nullable String numberType);

        @JsonProperty(JSON_PROPERTY_DOMAIN_TYPE)
        public abstract Builder domainType(@Nullable String domainType);

        @JsonProperty(JSON_PROPERTY_DIMENSION)
        public abstract Builder dimension(@Nullable String dimension);

        @JsonProperty(JSON_PROPERTY_DISPLAY_FORM_NAME)
        public abstract Builder displayFormName(@Nullable String displayFormName);

        @JsonProperty(JSON_PROPERTY_OPTION_SET)
        public abstract Builder optionSet(@Nullable OptionSet optionSet);

        public abstract DataElement build();
    }
}
