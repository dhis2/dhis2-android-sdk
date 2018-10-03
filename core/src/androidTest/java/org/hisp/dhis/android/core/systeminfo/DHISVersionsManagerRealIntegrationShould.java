package org.hisp.dhis.android.core.systeminfo;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class DHISVersionsManagerRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
    }

    //@Test
    public void return_2_29_version_when_connecting_to_2_29_server() throws Exception {
        d2 = D2Factory.create(RealServerMother.url2_29, databaseAdapter());
        d2.wipeModule().wipeEverything();

        DHISVersionManager versionManager = d2.systemInfoModule().versionManager;

        d2.logIn("android", "Android123").call();
        assertThat(versionManager.getVersion()).isEqualTo(DHISVersion.V2_29);
        assertThat(versionManager.is2_29()).isTrue();
        assertThat(versionManager.is2_30()).isFalse();
    }

    //@Test
    public void return_2_30_version_when_connecting_to_2_30_server() throws Exception {
        d2 = D2Factory.create(RealServerMother.url_dev, databaseAdapter());
        d2.wipeModule().wipeEverything();

        DHISVersionManager versionManager = d2.systemInfoModule().versionManager;

        d2.logIn("android", "Android123").call();
        assertThat(versionManager.getVersion()).isEqualTo(DHISVersion.V2_30);
        assertThat(versionManager.is2_29()).isFalse();
        assertThat(versionManager.is2_30()).isTrue();
    }
}
