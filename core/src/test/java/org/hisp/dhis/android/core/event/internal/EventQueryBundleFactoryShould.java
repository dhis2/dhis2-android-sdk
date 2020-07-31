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

import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.settings.DownloadPeriod;
import org.hisp.dhis.android.core.settings.ProgramSetting;
import org.hisp.dhis.android.core.settings.ProgramSettings;
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventQueryBundleFactoryShould {

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

    @Mock
    private ProgramSettings programSettings;

    @Mock
    private EventLastUpdatedManager lastUpdatedManager;

    private String p1 = "program1", p2 = "program2", p3 = "program3";

    private String ou1 = "ou1", ou1c1 = "ou1.1", ou2 = "ou2";

    private List<String> rootOrgUnits = Arrays.asList(ou1, ou2);
    private List<String> captureOrgUnits = Arrays.asList(ou1, ou1c1, ou2);
    private List<OrganisationUnitProgramLink> links = Arrays.asList(
            OrganisationUnitProgramLink.builder().organisationUnit(ou1c1).program(p1).build(),
            OrganisationUnitProgramLink.builder().organisationUnit(ou1c1).program(p2).build(),
            OrganisationUnitProgramLink.builder().organisationUnit(ou2).program(p2).build()
    );

    // Object to test
    private EventQueryBundleFactory bundleFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(resourceHandler.getLastUpdated(any())).thenReturn(null);
        when(userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids()).thenReturn(rootOrgUnits);
        when(userOrganisationUnitLinkStore.queryOrganisationUnitUidsByScope(any())).thenReturn(captureOrgUnits);
        when(organisationUnitProgramLinkLinkStore.selectWhere(anyString())).thenReturn(links);
        when(programStore.getUidsByProgramType(any())).thenReturn(getProgramList());
        when(programSettingsObjectRepository.blockingGet()).thenReturn(programSettings);

        bundleFactory = new EventQueryBundleFactory(userOrganisationUnitLinkStore,
                organisationUnitProgramLinkLinkStore, programStore, programSettingsObjectRepository,
                lastUpdatedManager);
    }

    @Test
    public void create_a_single_bundle_when_global() {
        ProgramDataDownloadParams params = ProgramDataDownloadParams.builder().build();

        List<EventQueryBundle> bundles = bundleFactory.getEventQueryBundles(params);

        assertThat(bundles.size()).isEqualTo(1);

        EventQueryBundle bundle = bundles.get(0);
        assertThat(bundle.orgUnitList()).isEqualTo(rootOrgUnits);
        assertThat(bundle.programList()).isEqualTo(getProgramList());
        assertThat(bundle.ouMode()).isEqualByComparingTo(OrganisationUnitMode.DESCENDANTS);
    }

    @Test
    public void create_separate_bundle_for_program_if_has_specific_settings() {
        ProgramDataDownloadParams params = ProgramDataDownloadParams.builder().build();

        Map<String, ProgramSetting> specifics = new HashMap<>();
        specifics.put(p1, ProgramSetting.builder().uid(p1).eventsDownload(200).build());

        when(programSettings.specificSettings()).thenReturn(specifics);

        List<EventQueryBundle> bundles = bundleFactory.getEventQueryBundles(params);

        assertThat(bundles.size()).isEqualTo(2);

        for (EventQueryBundle bundle : bundles) {
            if (bundle.programList().size() == 1) {
                assertThat(bundle.programList().get(0)).isEqualTo(p1);
                assertThat(bundle.limit()).isEqualTo(200);
            } else if (bundle.programList().size() == 2) {
                assertThat(bundle.programList().contains(p2)).isTrue();
                assertThat(bundle.programList().contains(p3)).isTrue();
            } else {
                throw new RuntimeException("Not a valid bundle");
            }
        }
    }

    @Test
    public void get_event_date_if_defined() {
        ProgramDataDownloadParams params = ProgramDataDownloadParams.builder().build();

        Map<String, ProgramSetting> specifics = new HashMap<>();
        specifics.put(p1, ProgramSetting.builder().uid(p1).eventDateDownload(DownloadPeriod.LAST_3_MONTHS).build());

        when(programSettings.specificSettings()).thenReturn(specifics);

        List<EventQueryBundle> bundles = bundleFactory.getEventQueryBundles(params);

        assertThat(bundles.size()).isEqualTo(2);

        for (EventQueryBundle bundle : bundles) {
            if (bundle.programList().size() == 1) {
                assertThat(bundle.programList().get(0)).isEqualTo(p1);
                assertThat(bundle.eventStartDate()).isNotNull();
            }
        }
    }

    @Test
    public void apply_user_defined_limit_only_to_global_if_no_program() {
        ProgramDataDownloadParams params = ProgramDataDownloadParams.builder().limit(5000).build();

        Map<String, ProgramSetting> specificSettings = new HashMap<>();
        specificSettings.put(p1, ProgramSetting.builder().uid(p1).eventsDownload(100).build());

        when(programSettings.specificSettings()).thenReturn(specificSettings);

        List<EventQueryBundle> bundles = bundleFactory.getEventQueryBundles(params);

        assertThat(bundles.size()).isEqualTo(2);

        for (EventQueryBundle bundle : bundles) {
            if (bundle.programList().size() == 1) {
                assertThat(bundle.programList().get(0)).isEqualTo(p1);
                assertThat(bundle.limit()).isEqualTo(100);
            } else {
                assertThat(bundle.limit()).isEqualTo(5000);
            }
        }
    }

    private List<String> getProgramList() {
        List<String> programList = new ArrayList<>();
        programList.add(p1);
        programList.add(p2);
        programList.add(p3);
        return programList;
    }

}
