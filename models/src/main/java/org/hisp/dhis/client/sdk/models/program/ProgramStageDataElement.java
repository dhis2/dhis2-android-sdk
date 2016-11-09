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

package org.hisp.dhis.client.sdk.models.program;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;

import java.util.Comparator;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramStageDataElement.Builder.class)
//TODO: ProgramStageDataElement is not a true BaseIdentifiableObject. It lacks name and displayName in API. Override those properties and return dataElement.getName() and dataElement.getDisplayName()
public abstract class ProgramStageDataElement extends BaseIdentifiableObject {
    public static final Comparator<ProgramStageDataElement> DESCENDING_SORT_ORDER_COMPARATOR = new DescendingSortOrderComparator();

    private static final String JSON_PROPERTY_DISPLAY_IN_REPORTS = "displayInReports";
    private static final String JSON_PROPERTY_DATA_ELEMENT = "dataElement";
    private static final String JSON_PROPERTY_COMPULSORY = "compulsory";
    private static final String JSON_PROPERTY_ALLOW_PROVIDED_ELSEWHERE = "allowProvidedElsewhere";
    private static final String JSON_PROPERTY_SORT_ORDER = "sortOrder";
    private static final String JSON_PROPERTY_ALLOW_FUTURE_DATE = "allowFutureDate";

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_IN_REPORTS)
    public abstract Boolean displayInReports();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
    public abstract DataElement dataElement();

    @Nullable
    @JsonProperty(JSON_PROPERTY_COMPULSORY)
    public abstract Boolean compulsory();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ALLOW_PROVIDED_ELSEWHERE)
    public abstract Boolean allowProvidedElsewhere();

    @Nullable
    @JsonProperty(JSON_PROPERTY_SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    public static ProgramStageDataElement.Builder builder() {
        return new AutoValue_ProgramStageDataElement.Builder();
    }

    /**
     * Comparator that returns the ProgramStageDataElement with the sortOrder
     * as the greater of the two given.
     */
    private static class DescendingSortOrderComparator implements Comparator<ProgramStageDataElement> {

        @Override
        public int compare(ProgramStageDataElement first, ProgramStageDataElement second) {
            if (first != null && second != null && first.sortOrder() != null) {
                return first.sortOrder().compareTo(second.sortOrder());
            }

            return 0;
        }
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_DISPLAY_IN_REPORTS)
        public abstract Builder displayInReports(@Nullable Boolean displayInReports);

        @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
        public abstract Builder dataElement(@Nullable DataElement dataElement);

        @JsonProperty(JSON_PROPERTY_COMPULSORY)
        public abstract Builder compulsory(@Nullable Boolean compulsory);

        @JsonProperty(JSON_PROPERTY_ALLOW_PROVIDED_ELSEWHERE)
        public abstract Builder allowProvidedElsewhere(@Nullable Boolean allowProvidedElsewhere);

        @JsonProperty(JSON_PROPERTY_SORT_ORDER)
        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        @JsonProperty(JSON_PROPERTY_ALLOW_FUTURE_DATE)
        public abstract Builder allowFutureDate(@Nullable Boolean allowFutureDate);

        public abstract ProgramStageDataElement build();
    }
}
