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

import org.hisp.dhis.android.core.arch.storage.internal.ObjectSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;

public final class DatabaseConfigurationMigration {

    public static DatabasesConfiguration apply(Context context, SecureStore secureStore, String username) {
        return apply(
                new ConfigurationSecureStoreImpl(secureStore),
                DatabaseConfigurationSecureStore.get(secureStore),
                new DatabaseConfigurationTransformer(),
                new DatabaseNameGenerator(),
                new DatabaseRenamer(context),
                username
        );
    }

    static DatabasesConfiguration apply(ObjectSecureStore<Configuration> oldConfigurationStore,
                                        ObjectSecureStore<DatabasesConfiguration> newConfigurationStore,
                                        DatabaseConfigurationTransformer transformer,
                                        DatabaseNameGenerator nameGenerator,
                                        DatabaseRenamer renamer,
                                        String username) {
        Configuration oldConfiguration = oldConfigurationStore.get();
        if (oldConfiguration != null) {
            oldConfigurationStore.remove();
            String databaseName = nameGenerator.getDatabaseName(oldConfiguration.serverUrl().toString(), username, false);
            renamer.renameDatabase("dhis.db", databaseName);
            DatabasesConfiguration newConfiguration = transformer.transform(oldConfiguration, databaseName);
            newConfigurationStore.set(newConfiguration);
            return newConfiguration;
        } else {
            return newConfigurationStore.get();
        }
    }

    private DatabaseConfigurationMigration() {

    }
}