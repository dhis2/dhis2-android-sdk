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

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.util.Date;

public class MultiUserDatabaseManagerForD2Manager {

    private final DatabaseAdapter databaseAdapter;
    private final DatabaseConfigurationHelper configurationHelper;
    private final DatabaseConfigurationMigration migration;
    private final DatabaseAdapterFactory databaseAdapterFactory;

    MultiUserDatabaseManagerForD2Manager(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull DatabaseConfigurationHelper configurationHelper,
            @NonNull DatabaseConfigurationMigration migration,
            @NonNull DatabaseAdapterFactory databaseAdapterFactory) {
        this.databaseAdapter = databaseAdapter;
        this.configurationHelper = configurationHelper;
        this.migration = migration;
        this.databaseAdapterFactory = databaseAdapterFactory;
    }

    public static MultiUserDatabaseManagerForD2Manager create(DatabaseAdapter databaseAdapter, Context context,
                                                              InsecureStore insecureStore,
                                                              DatabaseAdapterFactory databaseAdapterFactory) {
        return new MultiUserDatabaseManagerForD2Manager(databaseAdapter,
                DatabaseConfigurationHelper.create(),
                DatabaseConfigurationMigration.create(context, insecureStore, databaseAdapterFactory),
                databaseAdapterFactory);
    }

    public void loadIfLogged(Credentials credentials) {
        DatabasesConfiguration databaseConfiguration = migration.apply();

        if (databaseConfiguration != null && credentials != null) {
            ServerURLWrapper.setServerUrl(databaseConfiguration.loggedServerUrl());
            DatabaseUserConfiguration userConfiguration = configurationHelper.getLoggedUserConfiguration(
                    databaseConfiguration, credentials.username());
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, userConfiguration);
        }
    }

    @VisibleForTesting
    public void loadDbForTesting(String name, boolean encrypt, String username) {
        DatabaseUserConfiguration config = DatabaseUserConfiguration.builder()
                .databaseName(name)
                .encrypted(encrypt)
                .username(username)
                .databaseCreationDate(BaseIdentifiableObject.dateToDateStr(new Date()))
                .build();

        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, config);
    }
}