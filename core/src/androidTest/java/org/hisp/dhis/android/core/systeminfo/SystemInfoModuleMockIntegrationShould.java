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
        downloadMetadata();
    }

    @Test
    public void get_no_vulnerabilities_for_high_threshold() {
        assertThat(d2.systemInfoModule().getPerformanceHintsService(100,
                100).areThereVulnerabilities(), is(false));
    }

    @Test
    public void get_vulnerabilities_for_low_threshold() {
        assertThat(d2.systemInfoModule().getPerformanceHintsService(1,
                1).areThereVulnerabilities(), is(true));
    }
}