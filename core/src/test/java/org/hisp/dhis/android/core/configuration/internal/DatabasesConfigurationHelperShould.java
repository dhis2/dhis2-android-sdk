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

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DatabasesConfigurationHelperShould {

    private DatabaseNameGenerator nameGenerator = new DatabaseNameGenerator();

    private String URL_1 = "https://url1.org";
    private String URL_2 = "https://url2.org";
    private String USERNAME_1 = "u1";
    private String USERNAME_2 = "u2";
    private String DB_NAME_11 = nameGenerator.getDatabaseName(URL_1, USERNAME_1, false);
    private String DB_NAME_12 = nameGenerator.getDatabaseName(URL_1, USERNAME_2, false);
    private String DB_NAME_22 = nameGenerator.getDatabaseName(URL_2, USERNAME_2, false);
    private static final String DATE = "2014-06-06T20:44:21.375";

    private DatabaseUserConfiguration USER_CONFIG_11 = DatabaseUserConfiguration.builder()
            .username(USERNAME_1)
            .databaseName(DB_NAME_11)
            .encrypted(false)
            .databaseCreationDate(DATE)
            .build();

    private DatabaseUserConfiguration USER_CONFIG_12 = DatabaseUserConfiguration.builder()
            .username(USERNAME_2)
            .databaseName(DB_NAME_12)
            .encrypted(false)
            .databaseCreationDate(DATE)
            .build();

    private DatabaseUserConfiguration USER_CONFIG_22 = DatabaseUserConfiguration.builder()
            .username(USERNAME_2)
            .databaseName(DB_NAME_22)
            .encrypted(false)
            .databaseCreationDate(DATE)
            .build();

    private DatabasesConfiguration SINGLE_SERVER_SINGLE_USER_CONFIG = DatabasesConfiguration.builder()
            .loggedServerUrl(URL_1)
                .servers(Collections.singletonList(DatabaseServerConfiguration.builder()
                        .serverUrl(URL_1)
                        .users(Collections.singletonList(USER_CONFIG_11))
            .build()
        )).build();

    private DatabasesConfiguration SINGLE_SERVER_2_USERS_CONFIG = DatabasesConfiguration.builder()
            .loggedServerUrl(URL_1)
            .servers(Collections.singletonList(DatabaseServerConfiguration.builder()
                    .serverUrl(URL_1)
                    .users(Arrays.asList(USER_CONFIG_11, USER_CONFIG_12))
                    .build()
            )).build();

    private DatabasesConfiguration TWO_SERVERS_ONE_USER_EACH = DatabasesConfiguration.builder()
            .loggedServerUrl(URL_1)
            .servers(Arrays.asList(DatabaseServerConfiguration.builder()
                            .serverUrl(URL_1)
                            .users(Collections.singletonList(USER_CONFIG_11))
                            .build(),
                    DatabaseServerConfiguration.builder()
                            .serverUrl(URL_2)
                            .users(Collections.singletonList(USER_CONFIG_22))
                            .build()
            )).build();

    private DatabasesConfiguration TWO_SERVERS_TWO_USERS_FIRST = DatabasesConfiguration.builder()
            .loggedServerUrl(URL_1)
            .servers(Arrays.asList(DatabaseServerConfiguration.builder()
                            .serverUrl(URL_1)
                            .users(Arrays.asList(USER_CONFIG_11, USER_CONFIG_12))
                            .build(),
                    DatabaseServerConfiguration.builder()
                            .serverUrl(URL_2)
                            .users(Collections.singletonList(USER_CONFIG_22))
                            .build()
            )).build();

    private DatabaseConfigurationHelper helper = new DatabaseConfigurationHelper(nameGenerator, () -> DATE);

    @Test
    public void get_logged_configuration_when_one_server_user() {
        assertThat(helper.getLoggedUserConfiguration(SINGLE_SERVER_SINGLE_USER_CONFIG, USERNAME_1)).isSameAs(USER_CONFIG_11);
    }

    @Test
    public void get_logged_configuration_when_one_server_and_2_users() {
        assertThat(helper.getLoggedUserConfiguration(SINGLE_SERVER_2_USERS_CONFIG, USERNAME_1)).isSameAs(USER_CONFIG_11);
        assertThat(helper.getLoggedUserConfiguration(SINGLE_SERVER_2_USERS_CONFIG, USERNAME_2)).isSameAs(USER_CONFIG_12);
    }

    @Test
    public void get_logged_configuration_when_2_servers_with_one_user_on_each_1() {
        DatabasesConfiguration configuration = DatabasesConfiguration.builder()
                .loggedServerUrl(URL_1)
                .servers(Arrays.asList(DatabaseServerConfiguration.builder()
                        .serverUrl(URL_1)
                        .users(Collections.singletonList(USER_CONFIG_11))
                        .build(),
                        DatabaseServerConfiguration.builder()
                                .serverUrl(URL_2)
                                .users(Collections.singletonList(USER_CONFIG_12))
                                .build()
        )).build();

        assertThat(helper.getLoggedUserConfiguration(configuration, USERNAME_1)).isSameAs(USER_CONFIG_11);
    }

    @Test(expected = RuntimeException.class)
    public void get_logged_configuration_when_2_servers_with_one_user_on_each_2() {
        helper.getLoggedUserConfiguration(TWO_SERVERS_ONE_USER_EACH, USERNAME_2);
    }

    @Test
    public void get_logged_configuration_when_2_servers_with_two_users_in_first() {
        assertThat(helper.getLoggedUserConfiguration(TWO_SERVERS_TWO_USERS_FIRST, USERNAME_1)).isSameAs(USER_CONFIG_11);
        assertThat(helper.getLoggedUserConfiguration(TWO_SERVERS_TWO_USERS_FIRST, USERNAME_2)).isSameAs(USER_CONFIG_12);
    }

    @Test
    public void add_new_configuration_to_empty() {
        DatabasesConfiguration config = helper.setConfiguration(null, URL_1, USERNAME_1, false);
        assertThat(config).isEqualTo(SINGLE_SERVER_SINGLE_USER_CONFIG);
    }

    @Test
    public void add_new_configuration_to_single_server_single_user_in_same_server() {
        DatabasesConfiguration config = helper.setConfiguration(SINGLE_SERVER_SINGLE_USER_CONFIG, URL_1, USERNAME_2, false);
        assertThat(config).isEqualTo(SINGLE_SERVER_2_USERS_CONFIG);
    }

    @Test
    public void add_new_configuration_to_single_server_single_user_in_other_server() {
        DatabasesConfiguration config = helper.setConfiguration(SINGLE_SERVER_SINGLE_USER_CONFIG, URL_2, USERNAME_2, false);
        assertThat(config.loggedServerUrl()).isEqualTo(URL_2);
        assertThat(helper.getLoggedUserConfiguration(config, USERNAME_2)).isEqualTo(USER_CONFIG_22);
    }
}