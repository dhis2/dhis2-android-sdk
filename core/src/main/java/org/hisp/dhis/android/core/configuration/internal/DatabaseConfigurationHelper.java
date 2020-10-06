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

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseConfigurationHelper {

    private final DatabaseNameGenerator databaseNameGenerator;
    private final DateProvider dateProvider;

    DatabaseConfigurationHelper(DatabaseNameGenerator databaseNameGenerator, DateProvider dateProvider) {
        this.databaseNameGenerator = databaseNameGenerator;
        this.dateProvider = dateProvider;
    }

    static DatabaseConfigurationHelper create() {
        return new DatabaseConfigurationHelper(new DatabaseNameGenerator(),
                () -> BaseIdentifiableObject.dateToDateStr(new Date()));
    }

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    DatabaseUserConfiguration getUserConfiguration(DatabasesConfiguration configuration, String serverUrl,
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

    public DatabaseUserConfiguration changeEncryption(String serverUrl, DatabaseUserConfiguration userConfiguration) {
        return userConfiguration.toBuilder()
                .encrypted(!userConfiguration.encrypted())
                .databaseName(databaseNameGenerator.getDatabaseName(serverUrl, userConfiguration.username(),
                        !userConfiguration.encrypted()))
                .build();
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    DatabasesConfiguration removeServerUserConfiguration(DatabasesConfiguration configuration,
                                                         DatabaseUserConfiguration userToRemove) {
        List<DatabaseServerConfiguration> servers = new ArrayList<>(configuration.servers().size());
        for (DatabaseServerConfiguration server: configuration.servers()) {
            List<DatabaseUserConfiguration> users = new ArrayList<>(server.users().size());
            for (DatabaseUserConfiguration user: server.users()) {
                if (!user.databaseName().equals(userToRemove.databaseName())) {
                    users.add(user);
                }
            }

            if (!users.isEmpty()) {
                servers.add(server.toBuilder().users(users).build());
            }
        }
        return configuration.toBuilder().servers(servers).build();
    }

    DatabaseUserConfiguration getLoggedUserConfiguration(DatabasesConfiguration configuration, String username) {
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
            if (equalsIgnoreProtocol(server.serverUrl(), serverUrl)) {
                return server;
            }
        }
        return null;
    }

    private String removeProtocol(String s) {
        return s.replace("https://", "").replace("http://", "");
    }

    private boolean equalsIgnoreProtocol(String s1, String s2) {
        return removeProtocol(s1).equals(removeProtocol(s2));
    }

    DatabasesConfiguration setConfiguration(DatabasesConfiguration configuration, String serverUrl,
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
                .databaseCreationDate(dateProvider.getDateStr())
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

    int countServerUserPairs(DatabasesConfiguration configuration) {
        if (configuration == null) {
            return 0;
        } else {
            int count = 0;
            for (DatabaseServerConfiguration server: configuration.servers()) {
                count += server.users().size();
            }
            return count;
        }
    }

    DatabaseUserConfiguration getOldestServerUser(DatabasesConfiguration configuration) {
        if (configuration == null) {
            return null;
        } else {
            DatabaseUserConfiguration oldestUser = null;
            for (DatabaseServerConfiguration server: configuration.servers()) {
                for (DatabaseUserConfiguration user: server.users()) {
                    try {
                        oldestUser = getOlderUserInternal(oldestUser, user);
                    } catch (ParseException e) {
                        Log.e("DbConfigHelper", "Error parsing databaseCreationDate");
                        throw new RuntimeException(e);
                    }
                }
            }

            return oldestUser;
        }
    }

    private DatabaseUserConfiguration getOlderUserInternal(
            @Nullable DatabaseUserConfiguration oldestUser,
            @NonNull DatabaseUserConfiguration user) throws ParseException {
        if (oldestUser == null) {
            return user;
        } else {
            Date oldestUserDate = BaseIdentifiableObject.parseDate(oldestUser.databaseCreationDate());
            Date userDate = BaseIdentifiableObject.parseDate(user.databaseCreationDate());
            if (userDate.compareTo(oldestUserDate) < 0) {
                return user;
            } else {
                return oldestUser;
            }
        }
    }
}