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

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Factory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TrackedEntityAttributeReservedValueEndpointCallRealIntegrationShould extends BaseRealIntegrationTest {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private Integer numberToReserve = 5;
    private String orgunitUid = "DiszpKrYNg8";

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.forNewDatabase();
    }

    private void reserveValues() {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues("xs8A6tQJY0s", numberToReserve);
    }

    private String getValue() {
        return d2.trackedEntityModule().reservedValueManager().blockingGetValue("xs8A6tQJY0s", orgunitUid);
    }

    // @Test
    public void reserve_and_download() {
        login();
        syncMetadata();
        reserveValues();
        String value = getValue();
    }

    // @Test
    public void download_and_persist_reserved_values() {
        login();
        syncMetadata();
        reserveValues();

        List<TrackedEntityAttributeReservedValue> reservedValues = TrackedEntityAttributeReservedValueStore.create(
                databaseAdapter()).selectAll();

        assertThat(reservedValues.size()).isEqualTo(numberToReserve);
    }

    // @Test
    public void download_and_persist_all_reserved_values() {
        login();
        syncMetadata();
        d2.trackedEntityModule().reservedValueManager().blockingDownloadAllReservedValues(20);

        List<TrackedEntityAttributeReservedValue> reservedValues = TrackedEntityAttributeReservedValueStore.create(
                databaseAdapter()).selectAll();

        String value = d2.trackedEntityModule().reservedValueManager().blockingGetValue("xs8A6tQJY0s", orgunitUid);
    }

    private void login() {
        if (!d2.userModule().isLogged().blockingGet()) {
            d2.userModule().logIn(username, password, url).blockingGet();
        }
    }

    private void syncMetadata() {
        d2.metadataModule().blockingDownload();
    }
}