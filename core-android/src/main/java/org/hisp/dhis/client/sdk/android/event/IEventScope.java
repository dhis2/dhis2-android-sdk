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

import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.List;

import rx.Observable;

public interface IEventScope {

    Observable<Boolean> save(Event event);

    Observable<Boolean> remove(Event event);

    Observable<Event> get(long id);

    Observable<Event> get(String uid);

    Observable<Event> create(String organisationUnitId, String programId, String programStageId, String status);

    Observable<List<Event>> list();

    Observable<List<Event>> list(OrganisationUnit organisationUnit, Program program);

    /**
     * Sends all local event changes to server
     */
    Observable<Void> send();

    /**
     * Loads a list of Events for the given Organisation Unit and Program, limited by limit.
     * to load all set limit to 0.
     *
     * @param organisationUnit
     * @param program
     * @param limit
     */
    Observable<Void> update(OrganisationUnit organisationUnit, Program program, int limit);

    /**
     * Loads a list of Events for the given Organisation Unit and program that have been modified
     * since the last call to update.
     *
     * @param organisationUnit
     * @param program
     */
    Observable<Void> update(OrganisationUnit organisationUnit, Program program);
}
