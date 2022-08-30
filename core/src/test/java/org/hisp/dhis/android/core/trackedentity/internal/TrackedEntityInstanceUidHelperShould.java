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

package org.hisp.dhis.android.core.trackedentity.internal;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrackedEntityInstanceUidHelperShould {

    @Mock
    private IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;

    @Mock
    private TrackedEntityInstance tei1;

    @Mock
    private TrackedEntityInstance tei2;

    @Mock
    private Enrollment enrollment;

    @Mock
    private Event event;

    private TrackedEntityInstanceUidHelper uidHelper;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(organisationUnitStore.selectUids()).thenReturn(Lists.newArrayList("ou1", "ou2"));
        uidHelper = new TrackedEntityInstanceUidHelperImpl(organisationUnitStore
        );
    }

    @Test
    public void call_organisation_unit_select_uids() {
        uidHelper.getMissingOrganisationUnitUids(new ArrayList<>());
        verify(organisationUnitStore).selectUids();
    }

    @Test
    public void return_tei_org_unit_if_not_in_store() {
        when(tei1.organisationUnit()).thenReturn("ou3");
        Set<String> missingUids = uidHelper.getMissingOrganisationUnitUids(Lists.newArrayList(tei1));
        assertThat(missingUids.size()).isEqualTo(1);
        assertThat(missingUids.iterator().next()).isEqualTo("ou3");
    }

    @Test
    public void not_return_tei_org_unit_if_in_store() {
        when(tei1.organisationUnit()).thenReturn("ou2");
        Set<String> missingUids = uidHelper.getMissingOrganisationUnitUids(Lists.newArrayList(tei1));
        assertThat(missingUids.size()).isEqualTo(0);
    }

    @Test
    public void return_2_tei_org_unit_if_not_in_store() {
        when(tei1.organisationUnit()).thenReturn("ou3");
        when(tei2.organisationUnit()).thenReturn("ou4");
        Set<String> missingUids = uidHelper.getMissingOrganisationUnitUids(Lists.newArrayList(tei1, tei2));
        assertThat(missingUids.size()).isEqualTo(2);
        assertThat(missingUids.contains("ou3")).isTrue();
        assertThat(missingUids.contains("ou4")).isTrue();
    }

    @Test
    public void return_enrollment_org_unit_if_not_in_store() {
        addToEnrollment("ou3");
        Set<String> missingUids = uidHelper.getMissingOrganisationUnitUids(Lists.newArrayList(tei1));
        assertThat(missingUids.size()).isEqualTo(1);
        assertThat(missingUids.iterator().next()).isEqualTo("ou3");
    }

    @Test
    public void not_return_enrollment_org_unit_if_in_store() {
        addToEnrollment("ou2");
        Set<String> missingUids = uidHelper.getMissingOrganisationUnitUids(Lists.newArrayList(tei1));
        assertThat(missingUids.size()).isEqualTo(0);
    }

    @Test
    public void return_event_org_unit_if_not_in_store() {
        addToEvent("ou3");
        Set<String> missingUids = uidHelper.getMissingOrganisationUnitUids(Lists.newArrayList(tei1));
        assertThat(missingUids.size()).isEqualTo(1);
        assertThat(missingUids.iterator().next()).isEqualTo("ou3");
    }

    @Test
    public void not_return_event_org_unit_if_in_store() {
        addToEvent("ou2");
        Set<String> missingUids = uidHelper.getMissingOrganisationUnitUids(Lists.newArrayList(tei1));
        assertThat(missingUids.size()).isEqualTo(0);
    }

    private void addToEnrollment(String organisationUnitId) {
        when(enrollment.organisationUnit()).thenReturn(organisationUnitId);
        when(TrackedEntityInstanceInternalAccessor.accessEnrollments(tei1)).thenReturn(Lists.newArrayList(enrollment));
    }

    private void addToEvent(String organisationUnitId) {
        when(event.organisationUnit()).thenReturn(organisationUnitId);
        when(EnrollmentInternalAccessor.accessEvents(enrollment)).thenReturn(Lists.newArrayList(event));
        when(TrackedEntityInstanceInternalAccessor.accessEnrollments(tei1)).thenReturn(Lists.newArrayList(enrollment));
    }
}
