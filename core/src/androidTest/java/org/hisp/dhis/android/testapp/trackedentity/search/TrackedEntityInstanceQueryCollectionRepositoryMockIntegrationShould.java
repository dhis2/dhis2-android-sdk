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

package org.hisp.dhis.android.testapp.trackedentity.search;

import org.hisp.dhis.android.core.arch.helpers.DateUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryRepositoryScope;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class TrackedEntityInstanceQueryCollectionRepositoryMockIntegrationShould
        extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_by_program() {
        List<TrackedEntityInstance> trackedEntityInstances =
                d2.trackedEntityModule().trackedEntityInstanceQuery()
                        .byProgram().eq("lxAQ7Zs9VYR")
                        .blockingGet();

        assertThat(trackedEntityInstances.size()).isEqualTo(2);
    }

    @Test
    public void find_uids_by_program() {
        List<String> trackedEntityInstanceUids =
                d2.trackedEntityModule().trackedEntityInstanceQuery()
                        .byProgram().eq("lxAQ7Zs9VYR")
                        .blockingGetUids();

        assertThat(trackedEntityInstanceUids.size()).isEqualTo(2);
    }

    @Test
    public void find_by_enrollment_date() throws ParseException {
        Date refDate = DateUtils.DATE_FORMAT.parse("2018-01-10T00:00:00.000");

        List<TrackedEntityInstance> trackedEntityInstances =
                d2.trackedEntityModule().trackedEntityInstanceQuery()
                        .byProgram().eq("lxAQ7Zs9VYR")
                        .byProgramDate().afterOrEqual(refDate)
                        .byProgramDate().beforeOrEqual(refDate)
                        .blockingGet();

        assertThat(trackedEntityInstances.size()).isEqualTo(1);
    }

    @Test
    public void find_by_incident_date() throws ParseException {
        Date refDate = DateUtils.DATE_FORMAT.parse("2018-01-10T00:00:00.000");

        List<TrackedEntityInstance> trackedEntityInstances =
                d2.trackedEntityModule().trackedEntityInstanceQuery()
                        .byProgram().eq("lxAQ7Zs9VYR")
                        .byIncidentDate().afterOrEqual(refDate)
                        .byIncidentDate().beforeOrEqual(refDate)
                        .blockingGet();

        assertThat(trackedEntityInstances.size()).isEqualTo(1);
    }

    @Test
    public void find_by_event_date() throws ParseException {
        Date refDate = DateUtils.DATE_FORMAT.parse("2015-05-01T00:00:00.000");

        List<TrackedEntityInstance> trackedEntityInstances =
                d2.trackedEntityModule().trackedEntityInstanceQuery()
                        .byProgram().eq("lxAQ7Zs9VYR")
                        .byEventDate().afterOrEqual(refDate)
                        .byEventDate().beforeOrEqual(refDate)
                        .blockingGet();

        assertThat(trackedEntityInstances.size()).isEqualTo(1);
    }

    @Test
    public void exclude_uids() {
        List<TrackedEntityInstance> trackedEntityInstances =
                d2.trackedEntityModule().trackedEntityInstanceQuery()
                        .excludeUids().in("nWrB0TfWlvh")
                        .blockingGet();

        assertThat(trackedEntityInstances.size()).isEqualTo(1);
    }

    @Test
    public void get_scope() {
        TrackedEntityInstanceQueryRepositoryScope scope =
                d2.trackedEntityModule().trackedEntityInstanceQuery().getScope();

        assertThat(scope.attribute()).isNotNull();
    }

    @Test
    public void find_by_transferred_orgunit() {
        TrackedEntityInstanceQueryCollectionRepository originalOu = d2.trackedEntityModule()
                .trackedEntityInstanceQuery()
                .byProgram().eq("lxAQ7Zs9VYR")
                .byOrgUnits().eq("DiszpKrYNg8");

        TrackedEntityInstanceQueryCollectionRepository transferredOu = d2.trackedEntityModule()
                .trackedEntityInstanceQuery()
                .byProgram().eq("lxAQ7Zs9VYR")
                .byOrgUnits().eq("g8upMTyEZGZ");

        assertThat(originalOu.blockingCount()).isEqualTo(2);
        assertThat(transferredOu.blockingCount()).isEqualTo(0);

        // Transfer ownership
        String teiUid = originalOu.blockingGet().get(0).uid();
        d2.trackedEntityModule().ownershipManager().blockingTransfer(teiUid, "lxAQ7Zs9VYR", "g8upMTyEZGZ");

        assertThat(originalOu.blockingCount()).isEqualTo(1);
        assertThat(transferredOu.blockingCount()).isEqualTo(1);

        // Undo change
        d2.trackedEntityModule().ownershipManager().blockingTransfer(teiUid, "lxAQ7Zs9VYR", "DiszpKrYNg8");
    }
}
