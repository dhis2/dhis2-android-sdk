package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.OkHttpClient;

import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class D2UnitTests {

    @Mock
    private DbOpenHelper dbOpenHelper;

    @Mock
    private ConfigurationModel configuration;

    @Mock
    private OkHttpClient okHttpClient;

    private D2.Builder builder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.builder = new D2.Builder()
                .configuration(configuration)
                .okHttpClient(okHttpClient)
                .dbOpenHelper(dbOpenHelper);
    }

    @Test
    public void build_shouldThrowIllegalArgumentException_ifNoOkHttpClient() {
        try {
            builder.okHttpClient(null).build();

            fail("IllegalArgumentException was expected, but was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void build_shouldThrowIllegalArgumentException_ifNoDbOpenHelper() {
        try {
            builder.dbOpenHelper(null).build();

            fail("IllegalArgumentException was expected, but was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void build_shouldThrowIllegalStateException_ifNoConfiguration() {
        try {
            builder.configuration(null).build();

            fail("IllegalStateException was expected, but was not thrown");
        } catch (IllegalStateException illegalStateException) {
            // swallow exception
        }
    }
}
