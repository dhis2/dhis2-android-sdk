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

import java.util.ArrayList;
import java.util.List;

public final class DatabaseConfigurationHelper {

    private final DatabaseNameGenerator databaseNameGenerator;

    public DatabaseConfigurationHelper(DatabaseNameGenerator databaseNameGenerator) {
        this.databaseNameGenerator = databaseNameGenerator;
    }

    public DatabaseUserConfiguration getUserConfiguration(DatabasesConfiguration configuration, String serverUrl,
                                                          String username) {
        if (configuration != null) {
            DatabaseServerConfiguration serverConfiguration = getServerConfiguration(configuration, serverUrl);
            if (serverConfiguration != null) {
                for (DatabaseUserConfiguration user: serverConfiguration.users()) {
                    if (user.username().equals(username)) {
                        return user;
                    }
                }
            }
        }
        return null;
    }

    public DatabaseUserConfiguration getLoggedUserConfiguration(DatabasesConfiguration configuration, String username) {
        DatabaseUserConfiguration userConfiguration = getUserConfiguration(configuration,
                configuration.loggedServerUrl(), username);
        if (userConfiguration == null) {
            throw new RuntimeException("Malformed configuration: user configuration not found for logged server");
        } else {
            return userConfiguration;
        }
    }

    private DatabaseServerConfiguration getServerConfiguration(DatabasesConfiguration configuration, String serverUrl) {
        for (DatabaseServerConfiguration server: configuration.servers()) {
            if (server.serverUrl().equals(serverUrl)) {
                return server;
            }
        }
        return null;
    }

    public DatabasesConfiguration addConfiguration(DatabasesConfiguration configuration, String serverUrl,
                                                   String username, boolean encrypt) {
        DatabaseServerConfiguration existingServerConf = configuration == null ? null
                : getServerConfiguration(configuration, serverUrl);

        List<DatabaseUserConfiguration> newUsers = new ArrayList<>();
        if (existingServerConf != null) {
            for (DatabaseUserConfiguration userConf : existingServerConf.users()) {
                if (!userConf.username().equals(username)) {
                    newUsers.add(userConf);
                }
            }
        }

        DatabaseUserConfiguration newUserConf = DatabaseUserConfiguration.builder()
                .username(username)
                .databaseName(databaseNameGenerator.getDatabaseName(serverUrl, username, encrypt))
                .encrypted(encrypt)
                .build();

        newUsers.add(newUserConf);

        DatabaseServerConfiguration serverConfiguration = DatabaseServerConfiguration.builder()
                .serverUrl(serverUrl)
                .users(newUsers)
                .build();

        List<DatabaseServerConfiguration> newServers = new ArrayList<>();
        if (configuration != null) {
            for (DatabaseServerConfiguration serverConf : configuration.servers()) {
                if (!serverConf.serverUrl().equals(serverUrl)) {
                    newServers.add(serverConf);
                }
            }
        }
        newServers.add(serverConfiguration);
        return DatabasesConfiguration.builder()
                .loggedServerUrl(serverUrl)
                .servers(newServers)
                .build();
    }

    public DatabasesConfiguration setServerUrl(DatabasesConfiguration configuration, String serverUrl) {
        return configuration.toBuilder().loggedServerUrl(serverUrl).build();
    }
}