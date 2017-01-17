package org.hisp.dhis.android.core.configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConfigurationManagerUnitTests {

    @Mock
    private ConfigurationStore configurationStore;

    private ConfigurationManager configurationManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        configurationManager = new ConfigurationManagerImpl(configurationStore);

    }

    @Test
    public void save_shouldCallStoreWithCorrectArguments() {
        when(configurationStore.save("test_server_url")).thenReturn(1L);
        ConfigurationModel savedConfiguration = configurationManager.save("test_server_url");

        verify(configurationStore).save("test_server_url");
        assertThat(savedConfiguration.id()).isEqualTo(1L);
        assertThat(savedConfiguration.serverUrl()).isEqualTo("test_server_url");
    }

    @Test
    public void save_shouldFailOnNullArgument() {
        try {
            configurationManager.save(null);

            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void save_shouldFailOnMalformedServerUrl() {
        try {
            configurationManager.save("");

            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void get_shouldCallQueryOnStore() {
        ConfigurationModel configurationModel = mock(ConfigurationModel.class);
        when(configurationStore.query()).thenReturn(configurationModel);

        ConfigurationModel configuration = configurationManager.get();

        verify(configurationStore).query();
        assertThat(configuration).isEqualTo(configurationModel);
    }

    @Test
    public void get_shouldReturnNull_ifNoConfigurationIsPersisted() {
        ConfigurationModel configuration = configurationManager.get();

        verify(configurationStore).query();
        assertThat(configuration).isNull();
    }

    @Test
    public void remove_shouldReturnOne_ifConfigurationIsPersisted() {
        when(configurationStore.delete()).thenReturn(1);

        int removed = configurationManager.remove();

        verify(configurationStore).delete();
        assertThat(removed).isEqualTo(1);
    }

    @Test
    public void remove_shouldReturnZero_ifNoConfigurationIsPersisted() {
        int removed = configurationManager.remove();

        verify(configurationStore).delete();
        assertThat(removed).isEqualTo(0);
    }
}
