package org.hisp.dhis.android.core.configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConfigurationManagerUnitTests {

    @Mock
    private ConfigurationStore configurationStore;

    private ConfigurationModel configurationModel;
    private ConfigurationManager configurationManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        configurationManager = new ConfigurationManagerImpl(configurationStore);
        configurationModel = ConfigurationModel.builder()
                .serverUrl("test_server_url")
                .build();
    }

    @Test
    public void configure_shouldCallStoreWithCorrectArguments() {
        configurationManager.configure(configurationModel);

        verify(configurationStore).save(configurationModel);
    }

    @Test
    public void configure_shouldFailOnNullArgument() {
        try {
            configurationManager.configure(null);

            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void configure_shouldFailOnMalformedServerUrl() {
        try {
            ConfigurationModel configurationModel = ConfigurationModel.builder()
                    .serverUrl("")
                    .build();
            configurationManager.configure(configurationModel);

            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void configure_shouldFailIfConfigurationIsAlreadyPersisted() {
        when(configurationStore.query()).thenReturn(Arrays.asList(configurationModel));

        try {
            configurationManager.configure(configurationModel);

            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }
}
