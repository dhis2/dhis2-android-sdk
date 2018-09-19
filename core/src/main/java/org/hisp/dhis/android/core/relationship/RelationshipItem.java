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

package org.hisp.dhis.android.core.relationship;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_RelationshipItem.Builder.class)
public abstract class RelationshipItem {

    @Nullable
    @JsonProperty()
    public abstract RelationshipItemTrackedEntityInstance trackedEntityInstance();

    @Nullable
    @JsonProperty()
    public abstract RelationshipItemEnrollment enrollment();

    @Nullable
    @JsonProperty()
    public abstract RelationshipItemEvent event();

    public boolean hasTrackedEntityInstance() {
        return trackedEntityInstance() != null;
    }

    public boolean hasEnrollment() {
        return enrollment() != null;
    }

    public boolean hasEvent() {
        return event() != null;
    }

    public String elementUid() {
        if (hasTrackedEntityInstance()) {
            return trackedEntityInstance().trackedEntityInstance();
        } else if (hasEnrollment()) {
            return enrollment().enrollment();
        } else {
            return event().event();
        }
    }

    public static Builder builder() {
        return new AutoValue_RelationshipItem.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder trackedEntityInstance(RelationshipItemTrackedEntityInstance trackedEntityInstance);

        public abstract Builder enrollment(RelationshipItemEnrollment enrollment);

        public abstract Builder event(RelationshipItemEvent event);

        protected abstract RelationshipItem autoBuild();

        @SuppressWarnings("PMD.NPathComplexity")
        public RelationshipItem build() {
            RelationshipItem item = autoBuild();
            int teiCount = item.trackedEntityInstance() == null ? 0 : 1;
            int enrollmentCount = item.enrollment() == null ? 0 : 1;
            int eventCount = item.event() == null ? 0 : 1;
            if (teiCount + enrollmentCount + eventCount == 1) {
                return item;
            } else {
                throw new IllegalArgumentException("Item must have either a TEI, enrollment or event");
            }
        }
    }
}
