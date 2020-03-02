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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.settings.ProgramSettings;
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TeiQueryBuilderFactoryShould {

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    @Mock
    private LinkStore<OrganisationUnitProgramLink> organisationUnitProgramLinkLinkStore;

    @Mock
    private ProgramStoreInterface programStore;

    @Mock
    private ProgramSettingsObjectRepository programSettingsObjectRepository;

    private ProgramSettings programSettings = null;
    private List<String> programList = Arrays.asList("program1", "program2", "program3");
    private List<String> rootOrgUnits = Arrays.asList("ou1", "ou2");
    private List<String> captureOrgUnits = Arrays.asList("ou1", "ou1.1", "ou2");
    private List<OrganisationUnitProgramLink> links = Arrays.asList(
            OrganisationUnitProgramLink.builder().organisationUnit("ou1.1").program("program1").build(),
            OrganisationUnitProgramLink.builder().organisationUnit("ou1.1").program("program2").build(),
            OrganisationUnitProgramLink.builder().organisationUnit("ou2").program("program2").build()
    );

    // Object to test
    private TrackedEntityInstanceQueryBuilderFactory builderFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(resourceHandler.getLastUpdated(any())).thenReturn(null);
        when(userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids()).thenReturn(rootOrgUnits);
        when(userOrganisationUnitLinkStore.queryOrganisationUnitUidsByScope(any())).thenReturn(captureOrgUnits);
        when(organisationUnitProgramLinkLinkStore.selectWhere(anyString())).thenReturn(links);
        when(programStore.getUidsByProgramType(any())).thenReturn(programList);
        when(programSettingsObjectRepository.blockingGet()).thenReturn(programSettings);

        builderFactory = new TrackedEntityInstanceQueryBuilderFactory(resourceHandler, userOrganisationUnitLinkStore,
                organisationUnitProgramLinkLinkStore, programStore, programSettingsObjectRepository);
    }

    @Test
    public void create_a_single_bundle_when_global() {
        ProgramDataDownloadParams params = ProgramDataDownloadParams.builder().build();

        List<TeiQuery.Builder> builders = builderFactory.getTeiQueryBuilders(params);

        assertThat(builders.size()).isEqualTo(1);

        TeiQuery bundle = builders.get(0).build();
        assertThat(bundle.orgUnits()).isEqualTo(rootOrgUnits);
        assertThat(bundle.ouMode()).isEqualByComparingTo(OrganisationUnitMode.DESCENDANTS);
        assertThat(bundle.program()).isNull();
    }

}
