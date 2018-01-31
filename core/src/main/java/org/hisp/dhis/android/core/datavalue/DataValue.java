/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.datavalue;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

import java.util.Date;

@AutoValue
public abstract class DataValue {
    protected static final String DATA_ELEMENT = "dataElement";
    protected static final String PERIOD = "period";
    protected static final String ORGANISATION_UNIT = "organisationUnit";
    protected static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
    protected static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";
    protected static final String VALUE = "value";
    protected static final String STORED_BY = "storedBy";
    protected static final String LAST_UPDATED = "lastUpdated";
    protected static final String COMMENT = "comment";
    protected static final String FOLLOW_UP = "followUp";
    protected static final String DELETED = "deleted";

    private static final Field<DataValue, String> dataElement = Field.create(DATA_ELEMENT);
    private static final Field<DataValue, String> period = Field.create(PERIOD);
    private static final Field<DataValue, String> organisationUnit = Field.create(ORGANISATION_UNIT);
    private static final Field<DataValue, String> categoryOptionCombo = Field.create(CATEGORY_OPTION_COMBO);
    private static final Field<DataValue, String> attributeOptionCombo = Field.create(ATTRIBUTE_OPTION_COMBO);
    private static final Field<DataValue, String> value = Field.create(VALUE);
    private static final Field<DataValue, String> storedBy = Field.create(STORED_BY);
    private static final Field<DataValue, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<DataValue, String> comment = Field.create(COMMENT);
    private static final Field<DataValue, String> followUp = Field.create(FOLLOW_UP);
    private static final Field<DataValue, Boolean> deleted = Field.create(DELETED);

    static final Fields<DataValue> allFields = Fields.<DataValue>builder().fields(
            dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo,
            value, storedBy, lastUpdated, comment, followUp, deleted).build();

    @Nullable
    @JsonProperty(DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @JsonProperty(PERIOD)
    public abstract String period();

    @Nullable
    @JsonProperty(ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty(CATEGORY_OPTION_COMBO)
    public abstract String categoryOptionCombo();

    @Nullable
    @JsonProperty(ATTRIBUTE_OPTION_COMBO)
    public abstract String attributeOptionCombo();

    @Nullable
    @JsonProperty(VALUE)
    public abstract String value();

    @Nullable
    @JsonProperty(STORED_BY)
    public abstract String storedBy();

    @Nullable
    @JsonProperty(LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(COMMENT)
    public abstract String comment();

    @Nullable
    @JsonProperty(FOLLOW_UP)
    public abstract Boolean followUp();

    @Nullable
    @JsonProperty(DELETED)
    public abstract Boolean deleted();

    @JsonCreator
    public static DataValue create(
            @JsonProperty(DATA_ELEMENT) String dataElement,
            @JsonProperty(PERIOD) String period,
            @JsonProperty(ORGANISATION_UNIT) String organisationUnit,
            @JsonProperty(CATEGORY_OPTION_COMBO) String categoryOptionCombo,
            @JsonProperty(ATTRIBUTE_OPTION_COMBO) String attributeOptionCombo,
            @JsonProperty(VALUE) String value,
            @JsonProperty(STORED_BY) String storedBy,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(COMMENT) String comment,
            @JsonProperty(FOLLOW_UP) Boolean followUp,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_DataValue(dataElement, period, organisationUnit, categoryOptionCombo,
                attributeOptionCombo, value, storedBy, lastUpdated, comment, followUp, deleted);
    }
}
