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

import androidx.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.event.EventDownloader;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.event.EventService;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class EventModuleImpl implements EventModule {

    private final EventCollectionRepository events;
    private final EventDownloader eventDownloader;
    private final EventService eventService;

    @VisibleForTesting
    final EventPersistenceCallFactory eventPersistenceCallFactory;

    @Inject
    EventModuleImpl(EventCollectionRepository events,
                    EventPersistenceCallFactory eventPersistenceCallFactory,
                    EventDownloader eventDownloader,
                    EventService eventService) {
        this.events = events;
        this.eventPersistenceCallFactory = eventPersistenceCallFactory;
        this.eventDownloader = eventDownloader;
        this.eventService = eventService;
    }

    @Override
    public EventCollectionRepository events() {
        return events;
    }

    @Override
    public EventDownloader eventDownloader() {
        return eventDownloader;
    }

    @Override
    public EventService eventService() {
        return eventService;
    }
}
