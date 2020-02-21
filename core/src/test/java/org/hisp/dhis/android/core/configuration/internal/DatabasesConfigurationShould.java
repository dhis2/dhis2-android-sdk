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

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DatabasesConfigurationShould extends BaseObjectShould implements ObjectShould {

    public DatabasesConfigurationShould() {
        super("configuration/databases_configuration.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        DatabasesConfiguration configuration = deserialize(DatabasesConfiguration.class);

        assertThat(configuration.loggedServerUrl()).isEqualTo("https://dhis2.org");
        assertThat(configuration.servers().size()).isEqualTo(1);

        DatabaseServerConfiguration server = configuration.servers().get(0);
        assertThat(server.serverUrl()).isEqualTo("https://dhis2.org");
        assertThat(server.users().size()).isEqualTo(1);

        DatabaseUserConfiguration user = server.users().get(0);
        assertThat(user.username()).isEqualTo("user");
        assertThat(user.databaseName()).isEqualTo("dbname.db");
        assertThat(user.encrypted()).isEqualTo(true);
    }

    @Test
    public void equal_when_deserialize_serialize_deserialize() throws IOException {
        DatabasesConfiguration configuration = deserialize(DatabasesConfiguration.class);

        String serialized = serialize(configuration);
        DatabasesConfiguration deserialized = deserialize(serialized, DatabasesConfiguration.class);
        assertThat(deserialized).isEqualTo(configuration);
    }
}