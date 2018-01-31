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
import org.hisp.dhis.android.core.common.ObjectWithUid;
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

    static final Field<Indicator, String> uid = Field.create(UID);
    private static final Field<Indicator, String> code = Field.create(CODE);
    private static final Field<Indicator, String> name = Field.create(NAME);
    private static final Field<Indicator, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<Indicator, String> created = Field.create(CREATED);
    static final Field<Indicator, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<Indicator, String> shortName = Field.create(SHORT_NAME);
    private static final Field<Indicator, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    private static final Field<Indicator, String> description = Field.create(DESCRIPTION);
    private static final Field<Indicator, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    private static final Field<Indicator, Boolean> deleted = Field.create(DELETED);

    private static final Field<Indicator, Boolean> annualized = Field.create(ANNUALIZED);
    private static final Field<Indicator, ObjectWithUid> indicatorType = Field.create(INDICATOR_TYPE);
    private static final Field<Indicator, String> numerator = Field.create(NUMERATOR);
    private static final Field<Indicator, String> numeratorDescription = Field.create(NUMERATOR_DESCRIPTION);
    private static final Field<Indicator, String> denominator = Field.create(DENOMINATOR);
    private static final Field<Indicator, String> denominatorDescription = Field.create(DENOMINATOR_DESCRIPTION);
    private static final Field<Indicator, String> url = Field.create(URL);

    static final Fields<Indicator> allFields = Fields.<Indicator>builder().fields(
            uid, code, name, displayName, created, lastUpdated, shortName, displayShortName,
            description, displayDescription, deleted,
            annualized, indicatorType, numerator, numeratorDescription, denominator,
            denominatorDescription, url).build();

    @Nullable
    @JsonProperty(ANNUALIZED)
    abstract Boolean annualized();

    @Nullable
    @JsonProperty(INDICATOR_TYPE)
    public abstract ObjectWithUid indicatorType();

    String indicatorTypeUid() {
        ObjectWithUid type = indicatorType();
        return type != null ? type.uid() : null;
    }

    @Nullable
    @JsonProperty(NUMERATOR)
    abstract String numerator();

    @Nullable
    @JsonProperty(NUMERATOR_DESCRIPTION)
    abstract String numeratorDescription();

    @Nullable
    @JsonProperty(DENOMINATOR)
    abstract String denominator();

    @Nullable
    @JsonProperty(DENOMINATOR_DESCRIPTION)
    abstract String denominatorDescription();

    @Nullable
    @JsonProperty(URL)
    abstract String url();

    @JsonCreator
    static Indicator create(
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
            @JsonProperty(ANNUALIZED) Boolean annualized,
            @JsonProperty(INDICATOR_TYPE) ObjectWithUid indicatorType,
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
