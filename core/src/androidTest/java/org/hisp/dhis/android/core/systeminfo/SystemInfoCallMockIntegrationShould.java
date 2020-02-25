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

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.data.systeminfo.SystemInfoSamples;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class SystemInfoCallMockIntegrationShould extends BaseMockIntegrationTestEmptyEnqueable {

    private static ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;

    private SystemInfo systemInfoFromAPI = SystemInfoSamples.get1();
    private SystemInfo systemInfoFromDB = SystemInfoSamples.get2();

    private TableInfo tableInfo = SystemInfoTableInfo.TABLE_INFO;

    @BeforeClass
    public static void setUpClass() throws Exception {
        BaseMockIntegrationTestEmptyEnqueable.setUpClass();
        systemInfoRepository = d2.systemInfoModule().systemInfo();
    }

    @Before
    public void setUp() {
        dhis2MockServer.enqueueMockResponse("systeminfo/system_info.json");
        databaseAdapter.delete(tableInfo.name(), "1", new String[]{});
    }

    @Test
    public void persist_system_info_when_call() {
        systemInfoRepository.blockingDownload();
        isSystemInfoInDb(systemInfoFromAPI);
    }

    @Test
    public void update_system_info_when_call() {
        databaseAdapter.insert(tableInfo.name(), null, systemInfoFromDB.toContentValues());
        isSystemInfoInDb(systemInfoFromDB);

        systemInfoRepository.blockingDownload();
        isSystemInfoInDb(systemInfoFromAPI);
    }

    private void isSystemInfoInDb(SystemInfo si) {
        SystemInfo siDb = systemInfoRepository.blockingGet();
        assertThat(si.toBuilder().id(null).build()).isEqualTo(
                siDb.toBuilder().id(null).build()
        );
    }
}
