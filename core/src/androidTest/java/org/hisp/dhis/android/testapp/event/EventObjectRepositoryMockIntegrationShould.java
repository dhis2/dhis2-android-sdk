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

package org.hisp.dhis.android.testapp.event;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.event.EventCreateProjection;
import org.hisp.dhis.android.core.event.EventObjectRepository;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.utils.integration.BaseIntegrationTestFullDispatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class EventObjectRepositoryMockIntegrationShould extends BaseIntegrationTestFullDispatcher {

    @Test
    public void update_organisation_unit() throws D2Error {
        String orgUnitUid = "new_org_unit";
        OrganisationUnitStore.create(databaseAdapter).insert(OrganisationUnit.builder().uid(orgUnitUid).build());

        EventObjectRepository repository = objectRepository();

        repository.setOrganisationUnitUid(orgUnitUid);
        assertThat(repository.get().organisationUnit(), is(orgUnitUid));

        repository.delete();
        OrganisationUnitStore.create(databaseAdapter).delete(orgUnitUid);
    }

    @Test(expected = D2Error.class)
    public void not_update_organisation_unit_if_not_exists() throws D2Error {
        String orgUnitUid = "new_org_unit";

        EventObjectRepository repository = objectRepository();

        try {
            repository.setOrganisationUnitUid(orgUnitUid);
        } finally {
            repository.delete();
        }
    }

    @Test
    public void update_event_date() throws D2Error {
        Date eventDate = new Date();

        EventObjectRepository repository = objectRepository();

        repository.setEventDate(eventDate);
        assertThat(repository.get().eventDate(), is(eventDate));

        repository.delete();
    }
    @Test
    public void update_event_status() throws D2Error {
        EventStatus eventStatus = EventStatus.COMPLETED;

        EventObjectRepository repository = objectRepository();

        repository.setStatus(eventStatus);
        assertThat(repository.get().status(), is(eventStatus));

        repository.delete();
    }

    @Test
    public void update_coordinate() throws D2Error {
        Coordinates coordinate = Coordinates.create(10.00, 11.00);

        EventObjectRepository repository = objectRepository();

        repository.setCoordinate(coordinate);
        assertThat(repository.get().coordinate(), is(coordinate));

        repository.delete();
    }

    @Test
    public void update_completed_date() throws D2Error {
        Date completedDate = new Date();

        EventObjectRepository repository = objectRepository();

        repository.setCompletedDate(completedDate);
        assertThat(repository.get().completedDate(), is(completedDate));

        repository.delete();
    }

    @Test
    public void update_due_date() throws D2Error {
        Date dueDate = new Date();

        EventObjectRepository repository = objectRepository();

        repository.setDueDate(dueDate);
        assertThat(repository.get().dueDate(), is(dueDate));

        repository.delete();
    }

    private EventObjectRepository objectRepository() throws D2Error {
        String eventUid = d2.eventModule().events.add(
                EventCreateProjection.create("enroll1", "lxAQ7Zs9VYR", "dBwrot7S420",
                        "DiszpKrYNg8", "bRowv6yZOF2"));
        return d2.eventModule().events.uid(eventUid);
    }
}