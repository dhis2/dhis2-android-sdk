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

package org.hisp.dhis.android.core.trackedentity.search;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Factory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

public class TrackedEntityInstanceQueryAndDownloadRealIntegrationShould extends BaseRealIntegrationTest {
    private D2 d2;
    private TrackedEntityInstanceQueryCollectionRepository repository;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.forNewDatabase();


        List<String> orgUnits = new ArrayList<>();
        orgUnits.add("O6uvpzGd5pu");

        repository = d2.trackedEntityModule().trackedEntityInstanceQuery()
                .byOrgUnits().in(orgUnits).byOrgUnitMode().eq(OrganisationUnitMode.ACCESSIBLE);
    }

    //@Test
    public void query_and_download_tracked_entity_instances() {
        login();

        d2.metadataModule().blockingDownload();

        List<TrackedEntityInstance> queriedTeis = repository.onlineOnly().blockingGet();
        assertThat(queriedTeis).isNotEmpty();

        Set<String> uids = new HashSet<>(queriedTeis.size());

        for(TrackedEntityInstance tei: queriedTeis) {
            uids.add(tei.uid());
        }

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().in(uids).blockingDownload();
        List<TrackedEntityInstance> downloadedTeis = d2.trackedEntityModule().trackedEntityInstances().byUid().in(uids).blockingGet();

        assertThat(queriedTeis.size()).isEqualTo(downloadedTeis.size());
    }

    private void login() {
        d2.userModule().logIn(username, password, url).blockingGet();
    }
}
