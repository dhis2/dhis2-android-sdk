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

package org.hisp.dhis.android.testapp.trackedentity;

import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceObjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstanceObjectRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void update_organisation_unit() throws D2Error {
        String orgUnitUid = "new_org_unit";
        OrganisationUnitStore.create(databaseAdapter).insert(OrganisationUnit.builder().uid(orgUnitUid).build());

        TrackedEntityInstanceObjectRepository repository = objectRepository();

        repository.setOrganisationUnitUid(orgUnitUid);
        assertThat(repository.get().organisationUnit(), is(orgUnitUid));

        repository.delete();
        OrganisationUnitStore.create(databaseAdapter).delete(orgUnitUid);
    }

    @Test(expected = D2Error.class)
    public void not_update_organisation_unit_if_not_exists() throws D2Error {
        String orgUnitUid = "new_org_unit";

        TrackedEntityInstanceObjectRepository repository = objectRepository();

        try {
            repository.setOrganisationUnitUid(orgUnitUid);
        } finally {
            repository.delete();
        }
    }

    @Test
    public void update_coordinates() throws D2Error {
        String coordinates = "[11, 10]";

        TrackedEntityInstanceObjectRepository repository = objectRepository();

        repository.setCoordinates(coordinates);
        assertThat(repository.get().coordinates(), is(coordinates));

        repository.delete();
    }

    private TrackedEntityInstanceObjectRepository objectRepository() throws D2Error {
        String teiUid = d2.trackedEntityModule().trackedEntityInstances.add(
                TrackedEntityInstanceCreateProjection.create("DiszpKrYNg8", "nEenWmSyUEp"));
        return d2.trackedEntityModule().trackedEntityInstances.uid(teiUid);
    }
}