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

package org.hisp.dhis.android.core.event.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.repositories.scope.BaseScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventDataFilter;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class EventQueryRepositoryScope implements BaseScope {

    @NonNull
    public abstract RepositoryMode mode();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract String programStage();

    @Nullable
    public abstract Boolean followUp();

    @Nullable
    public abstract String trackedEntityInstance();

    @Nullable
    public abstract List<String> orgUnits();

    @NonNull
    public abstract OrganisationUnitMode orgUnitMode();

    @Nullable
    public abstract AssignedUserMode assignedUserMode();

    @NonNull
    public abstract List<EventQueryScopeOrderByItem> order();

    @NonNull
    public abstract List<EventDataFilter> dataFilters();

    @Nullable
    public abstract List<String> events();

    @Nullable
    public abstract List<EventStatus> eventStatus();

    @Nullable
    public abstract DateFilterPeriod eventDate();

    @Nullable
    public abstract DateFilterPeriod dueDate();

    @Nullable
    public abstract DateFilterPeriod lastUpdatedDate();

    // Check if it is implemented in backend
    @Nullable
    public abstract DateFilterPeriod completedDate();

    @NonNull
    public abstract Boolean includeDeleted();

    @Nullable
    public abstract List<State> states();

    @Nullable
    public abstract List<String> attributeOptionCombos();

    abstract Builder toBuilder();

    static Builder builder() {
        return new AutoValue_EventQueryRepositoryScope.Builder()
                .order(Collections.emptyList())
                .dataFilters(Collections.emptyList())
                .mode(RepositoryMode.OFFLINE_ONLY)
                .orgUnitMode(OrganisationUnitMode.SELECTED)
                .includeDeleted(false);
    }

    static EventQueryRepositoryScope empty() {
        return builder().build();
    }

    @AutoValue.Builder
    abstract static class Builder {

        public abstract Builder mode(RepositoryMode mode);

        public abstract Builder program(String program);

        public abstract Builder programStage(String programStage);

        public abstract Builder followUp(Boolean followUp);

        public abstract Builder trackedEntityInstance(String trackedEntityInstance);

        public abstract Builder orgUnits(List<String> orgUnits);

        public abstract Builder orgUnitMode(OrganisationUnitMode orgUnitMode);

        public abstract Builder assignedUserMode(AssignedUserMode assignedUserMode);

        public abstract Builder order(List<EventQueryScopeOrderByItem> order);

        public abstract Builder dataFilters(List<EventDataFilter> dataFilters);

        public abstract Builder events(List<String> events);

        public abstract Builder eventStatus(List<EventStatus> eventStatus);

        public abstract Builder eventDate(DateFilterPeriod eventDate);

        public abstract Builder dueDate(DateFilterPeriod dueDate);

        public abstract Builder lastUpdatedDate(DateFilterPeriod lastUpdatedDate);

        public abstract Builder completedDate(DateFilterPeriod completedDate);

        public abstract Builder includeDeleted(Boolean includeDeleted);

        public abstract Builder states(List<State> states);

        public abstract Builder attributeOptionCombos(List<String> attributeOptionCombos);

        abstract EventQueryRepositoryScope autoBuild();

        // Auxiliary fields to access values
        abstract List<State> states();

        public EventQueryRepositoryScope build()  {
            if (states() != null) {
                mode(RepositoryMode.OFFLINE_ONLY);
            }

            return autoBuild();
        }
    }
}
