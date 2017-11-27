/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.HttpUrl;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConfigurationManagerShould {

    @Mock
    private ConfigurationStore configurationStore;

    private ConfigurationManager configurationManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        configurationManager = new ConfigurationManagerImpl(configurationStore);
    }

    @Test
    public void return_correct_values_when_configuration_manager_is_configured_with_saved_store() {
        HttpUrl httpUrl = HttpUrl.parse("http://testserver.org/");
        when(configurationStore.save("http://testserver.org/api/")).thenReturn(1L);
        ConfigurationModel savedConfiguration = configurationManager.configure(httpUrl);

        verify(configurationStore).save("http://testserver.org/api/");
        assertThat(savedConfiguration.id()).isEqualTo(1L);
        assertThat(savedConfiguration.serverUrl().toString()).isEqualTo("http://testserver.org/api/");
    }

    @Test
    public void thrown_illegal_argument_exception_after_configure_with_null_argument() {
        try {
            configurationManager.configure(null);

            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void invoke_configuration_store_query_when_call_configuration_manager_get_method() {
        ConfigurationModel configurationModel = mock(ConfigurationModel.class);
        when(configurationStore.query()).thenReturn(configurationModel);

        ConfigurationModel configuration = configurationManager.get();

        verify(configurationStore).query();
        assertThat(configuration).isEqualTo(configurationModel);
    }

    @Test
    public void return_null_if_configuration_is_not_persisted() {
        ConfigurationModel configuration = configurationManager.get();

        verify(configurationStore).query();
        assertThat(configuration).isNull();
    }

    @Test
    public void invoke_delete_and_return_zero_when_configuration_manager_is_persisted_and_remove_method_is_called() {
        when(configurationStore.delete()).thenReturn(1);

        int removed = configurationManager.remove();

        verify(configurationStore).delete();
        assertThat(removed).isEqualTo(1);
    }

    @Test
    public void invoke_delete_and_return_zero_when_configuration_manager_is_not_persisted_and_remove_method_is_called() {
        int removed = configurationManager.remove();

        verify(configurationStore).delete();
        assertThat(removed).isEqualTo(0);
    }

    @Test
    public void thrown_illegal_argument_exception_when_configure_not_canonized_url() {
        HttpUrl baseUrlWithoutTrailingSlash = HttpUrl.parse("https://play.dhis2.org/demo");

        try {
            configurationManager.configure(baseUrlWithoutTrailingSlash);

            fail("illegalArgumentException was expected but nothing was thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            verify(configurationStore, never()).save(anyString());
        }
    }

    @Test
    public void invoke_save_configuration_store_when_configuration_manager_configure_a_valid_base_url() {
        HttpUrl baseUrl = HttpUrl.parse("https://play.dhis2.org/demo/");

        ConfigurationModel configurationModel = configurationManager.configure(baseUrl);

        assertThat(configurationModel.serverUrl().toString())
                .isEqualTo("https://play.dhis2.org/demo/api/");

        verify(configurationStore).save("https://play.dhis2.org/demo/api/");
    }
}
