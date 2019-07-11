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

package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.event.internal.EventPersistenceCallFactory;
import org.hisp.dhis.android.core.event.internal.EventWithLimitCallFactory;

import javax.inject.Inject;

import androidx.annotation.VisibleForTesting;
import dagger.Reusable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.reactivex.Observable;

@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
@Reusable
public final class EventModule {

    private final EventWithLimitCallFactory eventWithLimitCallFactory;
    public final EventCollectionRepository events;

    @VisibleForTesting
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    final EventPersistenceCallFactory eventPersistenceCallFactory;

    @Inject
    EventModule(EventWithLimitCallFactory eventWithLimitCallFactory,
                EventCollectionRepository events,
                EventPersistenceCallFactory eventPersistenceCallFactory) {
        this.eventWithLimitCallFactory = eventWithLimitCallFactory;
        this.events = events;
        this.eventPersistenceCallFactory = eventPersistenceCallFactory;
    }

    /**
     * Downloads and persists Events from the server. Only instances in capture scope are downloaded.
     * This method keeps track of the latest successful download in order to void downloading unmodified data.
     *
     * It makes use of paging with a best effort strategy: in case a page fails to be downloaded or persisted, it is
     * skipped and the rest of pages are persisted.
     *
     * @param eventLimit Max number of events to download
     * @param limitByOrgUnit If true, the limit of Events is considered per organisation unit.
     * @param limitByProgram If true, the limit of Events is considered per program.
     * @return -
     */
    public Observable<D2Progress> downloadSingleEvents(int eventLimit, boolean limitByOrgUnit, boolean limitByProgram) {
        return eventWithLimitCallFactory.downloadSingleEvents(eventLimit, limitByOrgUnit, limitByProgram);
    }
}
