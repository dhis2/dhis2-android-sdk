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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

@AutoValue
public abstract class RelationshipItem {
    private static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
    private static final String ENROLLMENT = "enrollment";
    private static final String EVENT = "event";

    public static final Field<RelationshipItem, RelationshipItemTrackedEntityInstance> trackedEntityInstance =
            Field.create(TRACKED_ENTITY_INSTANCE);
    public static final Field<RelationshipItem, RelationshipItemEnrollment> enrollment = Field.create(ENROLLMENT);
    public static final Field<RelationshipItem, RelationshipItemEvent> event = Field.create(EVENT);

    public static final Fields<RelationshipItem> allFields = Fields.<RelationshipItem>builder().fields(
            trackedEntityInstance, enrollment, event).build();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_INSTANCE)
    public abstract RelationshipItemTrackedEntityInstance trackedEntityInstance();

    @Nullable
    @JsonProperty(ENROLLMENT)
    public abstract RelationshipItemEnrollment enrollment();

    @Nullable
    @JsonProperty(EVENT)
    public abstract RelationshipItemEvent event();

    @Nullable
    @JsonCreator
    public static RelationshipItem create(
            @JsonProperty(TRACKED_ENTITY_INSTANCE) RelationshipItemTrackedEntityInstance trackedEntityInstance,
            @JsonProperty(ENROLLMENT) RelationshipItemEnrollment enrollment,
            @JsonProperty(EVENT) RelationshipItemEvent event) {

        return new AutoValue_RelationshipItem(trackedEntityInstance, enrollment, event);
    }
}
