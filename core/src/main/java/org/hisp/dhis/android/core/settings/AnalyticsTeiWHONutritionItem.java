/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_AnalyticsTeiWHONutritionItem.Builder.class)
public abstract class AnalyticsTeiWHONutritionItem {

    public abstract List<AnalyticsTeiDataElement> dataElements();

    public abstract List<AnalyticsTeiIndicator> indicators();

    public static Builder builder() {
        return new AutoValue_AnalyticsTeiWHONutritionItem.Builder()
                .dataElements(Collections.emptyList())
                .indicators(Collections.emptyList());
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder dataElements(List<AnalyticsTeiDataElement> dataElements);

        @JsonAlias("programIndicators")
        public abstract Builder indicators(List<AnalyticsTeiIndicator> indicators);


        //Auxiliary fields
        abstract AnalyticsTeiWHONutritionItem autoBuild();

        abstract List<AnalyticsTeiDataElement> dataElements();
        abstract List<AnalyticsTeiIndicator> indicators();

        public AnalyticsTeiWHONutritionItem build() {
            try {
                dataElements();
            } catch (IllegalStateException e) {
                dataElements(Collections.emptyList());
            }

            try {
                indicators();
            } catch (IllegalStateException e) {
                indicators(Collections.emptyList());
            }

            return autoBuild();
        }
    }
}