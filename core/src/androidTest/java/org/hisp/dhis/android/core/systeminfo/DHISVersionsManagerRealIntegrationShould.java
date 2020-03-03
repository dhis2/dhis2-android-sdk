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

package org.hisp.dhis.android.core.systeminfo;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Factory;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class DHISVersionsManagerRealIntegrationShould extends BaseRealIntegrationTest {
    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
    }

    //@Test
    public void return_2_29_version_when_connecting_to_2_29_server() throws Exception {
        d2 = D2Factory.forNewDatabase();
        d2.wipeModule().wipeEverything();

        DHISVersionManager versionManager = d2.systemInfoModule().versionManager();

        d2.userModule().logIn(username, password, RealServerMother.url2_29).blockingGet();
        assertThat(versionManager.getVersion()).isEqualTo(DHISVersion.V2_29);
        assertThat(versionManager.is2_29()).isTrue();
        assertThat(versionManager.is2_30()).isFalse();
        assertThat(versionManager.is2_31()).isFalse();
    }

    //@Test
    public void return_2_30_version_when_connecting_to_2_30_server() throws Exception {
        d2 = D2Factory.forNewDatabase();
        d2.wipeModule().wipeEverything();

        DHISVersionManager versionManager = d2.systemInfoModule().versionManager();

        d2.userModule().logIn(username, password, RealServerMother.url2_30).blockingGet();
        assertThat(versionManager.getVersion()).isEqualTo(DHISVersion.V2_30);
        assertThat(versionManager.is2_29()).isFalse();
        assertThat(versionManager.is2_30()).isTrue();
        assertThat(versionManager.is2_31()).isFalse();
    }

    //@Test
    public void return_2_31_version_when_connecting_to_2_31_server() throws Exception {
        d2 = D2Factory.forNewDatabase();
        d2.wipeModule().wipeEverything();

        DHISVersionManager versionManager = d2.systemInfoModule().versionManager();

        d2.userModule().logIn(username, password, RealServerMother.url2_31).blockingGet();
        assertThat(versionManager.getVersion()).isEqualTo(DHISVersion.V2_31);
        assertThat(versionManager.is2_29()).isFalse();
        assertThat(versionManager.is2_30()).isFalse();
        assertThat(versionManager.is2_31()).isTrue();
    }
}
