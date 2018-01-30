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

package org.hisp.dhis.android.core.indicator;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

import java.util.Date;

@AutoValue
public abstract class Indicator extends BaseNameableObject {
    private final static String ANNUALIZED = "annualized";
    private final static String INDICATOR_TYPE = "indicatorType";
    private final static String NUMERATOR = "numerator";
    private final static String NUMERATOR_DESCRIPTION = "numeratorDescription";
    private final static String DENOMINATOR = "denominator";
    private final static String DENOMINATOR_DESCRIPTION = "denominatorDescription";
    private final static String URL = "url";

    public static final Field<Indicator, String> uid = Field.create(UID);
    public static final Field<Indicator, String> code = Field.create(CODE);
    public static final Field<Indicator, String> name = Field.create(NAME);
    public static final Field<Indicator, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<Indicator, String> created = Field.create(CREATED);
    public static final Field<Indicator, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<Indicator, String> shortName = Field.create(SHORT_NAME);
    public static final Field<Indicator, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<Indicator, String> description = Field.create(DESCRIPTION);
    public static final Field<Indicator, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    public static final Field<Indicator, Boolean> deleted = Field.create(DELETED);

    public static final Field<Indicator, Integer> annualized = Field.create(ANNUALIZED);
    public static final Field<Indicator, String> indicatorType = Field.create(INDICATOR_TYPE);
    public static final Field<Indicator, String> numerator = Field.create(NUMERATOR);
    public static final Field<Indicator, String> numeratorDescription = Field.create(NUMERATOR_DESCRIPTION);
    public static final Field<Indicator, String> denominator = Field.create(DENOMINATOR);
    public static final Field<Indicator, String> denominatorDescription = Field.create(DENOMINATOR_DESCRIPTION);
    public static final Field<Indicator, String> url = Field.create(URL);

    public static final Fields<Indicator> allFields = Fields.<Indicator>builder().fields(
            uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, deleted,
            annualized, indicatorType, numerator, numeratorDescription, denominator,
            denominatorDescription, url).build();

    @Nullable
    @JsonProperty(ANNUALIZED)
    public abstract Integer annualized();

    /* TODO */
    @Nullable
    @JsonProperty(INDICATOR_TYPE)
    public abstract String indicatorType();


    @Nullable
    @JsonProperty(NUMERATOR)
    public abstract String numerator();

    @Nullable
    @JsonProperty(NUMERATOR_DESCRIPTION)
    public abstract String numeratorDescription();

    @Nullable
    @JsonProperty(DENOMINATOR)
    public abstract String denominator();

    @Nullable
    @JsonProperty(DENOMINATOR_DESCRIPTION)
    public abstract String denominatorDescription();

    @Nullable
    @JsonProperty(URL)
    public abstract String url();

    @JsonCreator
    public static Indicator create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(SHORT_NAME) String shortName,
            @JsonProperty(DISPLAY_SHORT_NAME) String displayShortName,
            @JsonProperty(DESCRIPTION) String description,
            @JsonProperty(DISPLAY_DESCRIPTION) String displayDescription,
            @JsonProperty(ANNUALIZED) Integer annualized,
            @JsonProperty(INDICATOR_TYPE) String indicatorType,
            @JsonProperty(NUMERATOR) String numerator,
            @JsonProperty(NUMERATOR_DESCRIPTION) String numeratorDescription,
            @JsonProperty(DENOMINATOR) String denominator,
            @JsonProperty(DENOMINATOR_DESCRIPTION) String denominatorDescription,
            @JsonProperty(URL) String url,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_Indicator(uid, code, name,
                displayName, created, lastUpdated, deleted,
                shortName, displayShortName, description, displayDescription,
                annualized, indicatorType, numerator, numeratorDescription, denominator,
                denominatorDescription, url);
    }
}
