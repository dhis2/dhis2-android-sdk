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

package org.hisp.dhis.android.core.configuration.internal;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import org.hisp.dhis.android.core.arch.storage.internal.AndroidSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectSecureStore;
import org.hisp.dhis.android.core.data.configuration.ConfigurationSamples;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.HttpUrl;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class ConfigurationSecureStoreIntegrationShould {

    private final Configuration configuration;
    private final ObjectSecureStore<Configuration> configurationSecureStore;

    public ConfigurationSecureStoreIntegrationShould() {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        this.configurationSecureStore = new ConfigurationSecureStoreImpl(new AndroidSecureStore(context));
        this.configuration = buildObject();
    }

    @Before
    public void setUp() throws IOException {
        configurationSecureStore.remove();
    }

    @Test
    public void configure_and_get() {
        configurationSecureStore.set(configuration);
        Configuration objectFromDb = configurationSecureStore.get();
        assertThat(objectFromDb.serverUrl()).isEqualTo(HttpUrl.parse("http://testserver.org/api/"));
    }

    @Test
    public void configure_and_remove() {
        configurationSecureStore.set(configuration);
        configurationSecureStore.remove();
        assertThat(configurationSecureStore.get()).isNull();
    }

    @Test
    public void overwrite_and_not_fail_in_a_consecutive_configuration() {
        configurationSecureStore.set(configuration);
        configurationSecureStore.set(configuration);
        assertThat(configurationSecureStore.get()).isNotNull();
    }

    protected Configuration buildObject() {
        return ConfigurationSamples.getConfiguration();
    }
}