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

package org.hisp.dhis.client.sdk.models.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.sdk.models.common.BaseDataModel;
import org.hisp.dhis.client.sdk.models.common.Coordinates;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_Event.Builder.class)
public abstract class Event extends BaseDataModel {
    private static final String JSON_PROPERTY_EVENT_UID = "event";
    private static final String JSON_PROPERTY_ENROLLMENT_UID = "enrollment";
    private static final String JSON_PROPERTY_CREATED = "created";
    private static final String JSON_PROPERTY_LAST_UPDATED = "lastUpdated";
    private static final String JSON_PROPERTY_STATUS = "status";
    private static final String JSON_PROPERTY_COORDINATE = "coordinate";
    private static final String JSON_PROPERTY_PROGRAM = "program";
    private static final String JSON_PROPERTY_PROGRAM_STAGE = "programStage";
    private static final String JSON_PROPERTY_ORGANISATION_UNIT = "organisationUnit";
    private static final String JSON_PROPERTY_EVENT_DATE = "eventDate";
    private static final String JSON_PROPERTY_COMPLETE_DATE = "completedDate";
    private static final String JSON_PROPERTY_DUE_DATE = "dueDate";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES = "trackedEntityDataValues";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";

    public static final Comparator<Event> DESCENDING_EVENT_DATE_COMPARATOR = new DescendingEventDateComparator();
    public static final Comparator<Event> ASCENDING_DATE_COMPARATOR = new AscendingEventDateComparator();

    // Mandatory, non-null properties

    @JsonProperty(JSON_PROPERTY_EVENT_UID)
    public abstract String uid();

    @JsonProperty(JSON_PROPERTY_STATUS)
    public abstract EventStatus status();

    @JsonProperty(JSON_PROPERTY_PROGRAM)
    public abstract String program();

    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
    public abstract String programStage();

    @JsonProperty(JSON_PROPERTY_ORGANISATION_UNIT)
    public abstract String organisationUnit();

    // Nullable properties

    @Nullable
    @JsonProperty(JSON_PROPERTY_EVENT_DATE)
    public abstract Date eventDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ENROLLMENT_UID)
    public abstract String enrollmentUid();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(JSON_PROPERTY_COORDINATE)
    public abstract Coordinates coordinates();

    @Nullable
    @JsonProperty(JSON_PROPERTY_COMPLETE_DATE)
    public abstract Date completedDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DUE_DATE)
    public abstract Date dueDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES)
    public abstract List<TrackedEntityDataValue> trackedEntityDataValues();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE)
    public abstract String trackedEntityInstance();

    public abstract Builder toBuilder();

    @Override
    public boolean isValid() {
        super.isValid();

        if (uid() == null || created() == null) {
            return false;
        }

        return true;
    }

    public static Builder builder() {
        return new AutoValue_Event.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {
        @JsonProperty(JSON_PROPERTY_EVENT_UID)
        public abstract Builder uid(@Nullable String uid);

        @JsonProperty(JSON_PROPERTY_CREATED)
        public abstract Builder created(@Nullable Date created);

        @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        @JsonProperty(JSON_PROPERTY_STATUS)
        public abstract Builder status(EventStatus eventStatus);

        @JsonProperty(JSON_PROPERTY_COORDINATE)
        public abstract Builder coordinates(@Nullable Coordinates coordinates);

        @JsonProperty(JSON_PROPERTY_ENROLLMENT_UID)
        public abstract Builder enrollmentUid(@Nullable String enrollmentUid);

        @JsonProperty(JSON_PROPERTY_PROGRAM)
        public abstract Builder program(String program);

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
        public abstract Builder programStage(String programStage);

        @JsonProperty(JSON_PROPERTY_ORGANISATION_UNIT)
        public abstract Builder organisationUnit(String organisationUnit);

        @JsonProperty(JSON_PROPERTY_EVENT_DATE)
        public abstract Builder eventDate(@Nullable Date eventDate);

        @JsonProperty(JSON_PROPERTY_COMPLETE_DATE)
        public abstract Builder completedDate(@Nullable Date completedDate);

        @JsonProperty(JSON_PROPERTY_DUE_DATE)
        public abstract Builder dueDate(@Nullable Date dueDate);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES)
        public abstract Builder trackedEntityDataValues(@Nullable List<TrackedEntityDataValue> trackedEntityDataValues);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE)
        public abstract Builder trackedEntityInstance(@Nullable String trackedEntityInstance);

        abstract List<TrackedEntityDataValue> trackedEntityDataValues();

        abstract Event autoBuild();

        public Event build() {
            if (trackedEntityDataValues() != null) {
                trackedEntityDataValues(Collections.unmodifiableList(trackedEntityDataValues()));
            }

            return autoBuild();
        }
    }


    /**
     * Comparator that returns the Event with the latest EventDate
     * as the greater of the two given.
     */
    private static class DescendingEventDateComparator implements Comparator<Event> {

        @Override
        public int compare(Event first, Event second) {
            if (first != null && second != null && first.eventDate() != null && second.eventDate() != null) {
                return first.eventDate().compareTo(second.eventDate());
            }

            return 0;
        }
    }

    /**
     * Comparator that returns the Event with the latest EventDate
     * as the greater of the two given.
     */
    private static class AscendingEventDateComparator implements Comparator<Event> {

        @Override
        public int compare(Event first, Event second) {
            if (first != null && second != null && first.eventDate() != null) {
                return second.eventDate().compareTo(first.eventDate());
            }

            return 0;
        }
    }
}
