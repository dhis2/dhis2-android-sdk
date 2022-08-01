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

package org.hisp.dhis.android.core.systeminfo.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.systeminfo.DHISVersion;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

public class DHISVersionManagerShould {

    @Mock
    private ObjectWithoutUidStore<SystemInfo> systemInfoStore;

    @Mock
    private SystemInfo systemInfo;

    // Object to test
    private DHISVersionManager dhisVersionManager;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(systemInfoStore.selectFirst()).thenReturn(systemInfo);

        this.dhisVersionManager = new DHISVersionManagerImpl(systemInfoStore);
    }

    @Test
    public void compare_version_when_not_null() {
        when(systemInfo.version()).thenReturn("2.31.2");

        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_30)).isTrue();
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_31)).isFalse();
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_32)).isFalse();
        assertThat(dhisVersionManager.isGreaterThan(DHISVersion.V2_33)).isFalse();
    }
}