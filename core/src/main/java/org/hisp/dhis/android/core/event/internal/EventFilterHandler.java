/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.event.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.event.EventDataFilter;
import org.hisp.dhis.android.core.event.EventFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class EventFilterHandler extends IdentifiableHandlerImpl<EventFilter> {

    private final HandlerWithTransformer<EventDataFilter> eventDataFilterHandler;
    private final OrphanCleaner<ObjectWithUid, EventFilter> orphanCleaner;

    @Inject
    EventFilterHandler(
            IdentifiableObjectStore<EventFilter> eventFilterStore,
            HandlerWithTransformer<EventDataFilter> eventDataFilterHandler,
            OrphanCleaner<ObjectWithUid, EventFilter> orphanCleaner) {
        super(eventFilterStore);
        this.eventDataFilterHandler = eventDataFilterHandler;
        this.orphanCleaner = orphanCleaner;
    }

    @Override
    protected Collection<EventFilter> beforeCollectionHandled(Collection<EventFilter> eventFilters) {
        for (Map.Entry<String, List<EventFilter>> entry :
                EventFilterHelper.groupFiltersByProgram(eventFilters).entrySet()) {
            this.orphanCleaner.deleteOrphan(ObjectWithUid.create(entry.getKey()), entry.getValue());
        }

        return super.beforeCollectionHandled(eventFilters);
    }

    @Override
    protected void afterObjectHandled(EventFilter eventFilter, HandleAction action) {
        if (action != HandleAction.Delete && eventFilter.eventQueryCriteria() != null) {
            this.eventDataFilterHandler.handleMany(eventFilter.eventQueryCriteria().dataFilters(), o ->
                    o.toBuilder().eventFilter(eventFilter.uid()).build());
        }
    }
}