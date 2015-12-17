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

    Observable<List<Event>> list();

    /**
     * Sends all local event changes to server
     */
    void send();

    /**
     * Loads a list of Events for the given Organisation Unit and Program, limited by limit.
     * to load all set limit to 0.
     *
     * @param organisationUnit
     * @param program
     * @param limit
     */
    void update(OrganisationUnit organisationUnit, Program program, int limit);

    /**
     * Loads a list of Events for the given Organisation Unit and program that have been modified
     * since the last call to update.
     *
     * @param organisationUnit
     * @param program
     */
    void update(OrganisationUnit organisationUnit, Program program);
}
