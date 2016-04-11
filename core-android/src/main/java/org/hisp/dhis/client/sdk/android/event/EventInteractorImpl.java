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

package org.hisp.dhis.client.sdk.android.event;

import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.event.EventController;
import org.hisp.dhis.client.sdk.core.event.EventService;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.List;
import java.util.Set;

import rx.Observable;

public class EventInteractorImpl implements EventInteractor {
    private final EventService eventService;
    private final EventController eventController;

    public EventInteractorImpl(EventService eventService, EventController eventController) {
        this.eventService = eventService;
        this.eventController = eventController;
    }

    @Override
    public Observable<List<Event>> sync(final Set<String> uids) {
        return sync(SyncStrategy.DEFAULT, uids);
    }

    @Override
    public Observable<List<Event>> sync(final SyncStrategy strategy, final Set<String> uids) {
        return Observable.create(new DefaultOnSubscribe<List<Event>>() {
            @Override
            public List<Event> call() {
                eventController.sync(strategy, uids);
                return eventService.list(uids);
            }
        });
    }

    @Override
    public Observable<List<Event>> pull(Set<String> uids) {
        return pull(SyncStrategy.DEFAULT, uids);
    }

    @Override
    public Observable<List<Event>> pull(final SyncStrategy strategy, final Set<String> uids) {
        return Observable.create(new DefaultOnSubscribe<List<Event>>() {
            @Override
            public List<Event> call() {
                eventController.pull(strategy, uids);
                return eventService.list(uids);
            }
        });
    }

    @Override
    public Observable<List<Event>> push(final Set<String> uids) {
        return Observable.create(new DefaultOnSubscribe<List<Event>>() {
            @Override
            public List<Event> call() {
                eventController.push(uids);
                return eventService.list(uids);
            }
        });
    }

    @Override
    public Observable<Boolean> save(final Event event) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return eventService.save(event);
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final Event event) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return eventService.remove(event);
            }
        });
    }

    @Override
    public Observable<Event> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<Event>() {
            @Override
            public Event call() {
                return eventService.get(id);
            }
        });
    }

    @Override
    public Observable<Event> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<Event>() {
            @Override
            public Event call() {
                return eventService.get(uid);
            }
        });
    }

    @Override
    public Observable<List<Event>> list() {
        return Observable.create(new DefaultOnSubscribe<List<Event>>() {
            @Override
            public List<Event> call() {
                return eventService.list();
            }
        });
    }

    @Override
    public Observable<List<Event>> list(final OrganisationUnit organisationUnit,
                                        final Program program) {
        return Observable.create(new DefaultOnSubscribe<List<Event>>() {
            @Override
            public List<Event> call() {
                return eventService.list(organisationUnit, program);
            }
        });
    }
}
