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

package org.hisp.dhis.android.core.configuration;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.db.TableInfo;
import org.hisp.dhis.android.core.data.configuration.ConfigurationSamples;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapterFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ConfigurationStoreIntegrationShould {

    private final Configuration configuration;
    private final Configuration configurationWithId;
    private final ConfigurationStore store;
    private final TableInfo tableInfo;
    private final DatabaseAdapter databaseAdapter;

    public ConfigurationStoreIntegrationShould() {
        this.store = ConfigurationStoreImpl.create(DatabaseAdapterFactory.get(false));
        this.configuration = buildObject();
        this.configurationWithId = buildObjectWithId();
        this.tableInfo = ConfigurationTableInfo.TABLE_INFO;
        this.databaseAdapter = DatabaseAdapterFactory.get(false);
    }

    @Before
    public void setUp() throws IOException {
        store.delete();
    }

    @After
    public void tearDown() throws IOException {
        DatabaseAdapterFactory.get(false).database().close();
    }

    @Test
    public void insert_and_select_first_object() {
        store.insert(configuration);
        Configuration objectFromDb = store.selectFirst();
        assertThat(objectFromDb.serverUrl()).isEqualTo(HttpUrl.parse("http://testserver.org/api/"));
    }

    @Test
    public void insert_as_content_values_and_select_first_object() {
        long rowsInserted = databaseAdapter.database()
                .insert(tableInfo.name(), null, configuration.toContentValues());
        assertThat(rowsInserted).isEqualTo(1);
        Configuration objectFromDb = store.selectFirst();
        assertThat(objectFromDb).isEqualTo(configuration);
    }

    @Test
    public void insert_and_select_all_objects() {
        store.insert(configuration);
        List<Configuration> objectsFromDb = store.selectAll();
        assertThat(objectsFromDb.iterator().next().serverUrl()).isEqualTo(HttpUrl.parse("http://testserver.org/api/"));
    }

    @Test
    public void delete_inserted_object_by_id() {
        store.insert(configurationWithId);
        store.deleteById(configurationWithId);
        assertThat(store.selectFirst()).isEqualTo(null);
    }

    @Test
    public void save_configuration_properly() {
        store.save(configuration);
        Configuration objectFromDb = store.selectFirst();
        assertThat(objectFromDb.serverUrl()).isEqualTo(HttpUrl.parse("http://testserver.org/api/"));
    }

    @Test
    public void delete_old_configuration_before_save_the_new_one() {
        store.save(configuration);
        store.save(configuration);
        store.save(configuration);
        assertThat(store.count()).isEqualTo(1);
    }

    protected Configuration buildObject() {
        return ConfigurationSamples.getConfiguration();
    }

    protected Configuration buildObjectWithId() {
        return ConfigurationSamples.getConfiguration().toBuilder()
                .id(1L)
                .build();
    }
}