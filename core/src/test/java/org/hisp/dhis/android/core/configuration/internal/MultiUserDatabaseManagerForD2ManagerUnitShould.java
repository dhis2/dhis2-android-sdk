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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class MultiUserDatabaseManagerForD2ManagerUnitShould extends BaseCallShould {

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private DatabaseConfigurationHelper configurationHelper;

    @Mock
    private DatabaseAdapterFactory databaseAdapterFactory;

    @Mock
    private DatabaseConfigurationMigration migration;

    private final String USERNAME = "username";
    private final String SERVER_URL = "https://dhis2.org";

    private final Credentials credentials = Credentials.create(USERNAME, "password");

    private final String UNENCRYPTED_DB_NAME = "un.db";

    private static final String DATE = "2014-06-06T20:44:21.375";

    private DatabaseUserConfiguration userConfigurationUnencrypted = DatabaseUserConfiguration.builder()
            .databaseName(UNENCRYPTED_DB_NAME)
            .username(USERNAME)
            .encrypted(false)
            .databaseCreationDate(DATE)
            .build();

    @Mock
    private DatabasesConfiguration databasesConfiguration;

    private MultiUserDatabaseManagerForD2Manager manager;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        manager = new MultiUserDatabaseManagerForD2Manager(databaseAdapter, configurationHelper,
                migration, databaseAdapterFactory);
    }

    @Test
    public void call_migration_when_calling_loadIfLogged() {
        manager.loadIfLogged(credentials);
        verify(migration).apply();
    }

    @Test
    public void not_try_to_load_db_if_not_logged_when_calling_loadIfLogged() {
        manager.loadIfLogged(null);
        verifyNoMoreInteractions(databaseAdapterFactory);
    }

    @Test
    public void load_db_if_logged_when_calling_loadIfLogged() {
        when(migration.apply()).thenReturn(databasesConfiguration);
        when(databasesConfiguration.loggedServerUrl()).thenReturn(SERVER_URL);
        when(configurationHelper.getLoggedUserConfiguration(databasesConfiguration, USERNAME)).thenReturn(userConfigurationUnencrypted);

        manager.loadIfLogged(credentials);

        verify(databaseAdapterFactory).createOrOpenDatabase(databaseAdapter, userConfigurationUnencrypted);
    }
}
