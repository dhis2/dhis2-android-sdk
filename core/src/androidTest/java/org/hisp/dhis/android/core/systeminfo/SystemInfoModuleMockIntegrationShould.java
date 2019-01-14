package org.hisp.dhis.android.core.systeminfo;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class SystemInfoModuleMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        login();
    }

    @Test
    public void allow_access_to_system_info_user() {
        SystemInfo systemInfo = d2.systemInfoModule().systemInfo.get();
        assertThat(systemInfo.version(), is("2.29"));
        assertThat(systemInfo.systemName(), is("DHIS 2 Demo - Sierra Leone"));
    }
}