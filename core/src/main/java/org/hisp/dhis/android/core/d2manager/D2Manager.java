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

package org.hisp.dhis.android.core.d2manager;

import android.content.Context;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.Configuration;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;

import io.reactivex.annotations.NonNull;
import okhttp3.OkHttpClient;

public final class D2Manager {
    private D2 d2;
    private Configuration configuration;
    private OkHttpClient client;
    private Context context;

    D2Manager(@NonNull Configuration configuration,
              @NonNull OkHttpClient client,
              @NonNull Context context) {
        this.configuration = configuration;
        this.client = client;
        this.context = context;
    }

    public boolean isD2Configured() {
        return d2 != null;
    }

    public void configureD2(@NonNull D2Configuration d2Configuration) {
        d2 = new D2.Builder()
                .configuration(configuration)
                .databaseAdapter(databaseAdapter(d2Configuration.databaseName()))
                .okHttpClient(client)
                .context(context)
                .build();
    }

    public D2 getD2() {
        return d2;
    }

    private DatabaseAdapter databaseAdapter(@NonNull String databaseName) {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context, databaseName);
        return new SqLiteDatabaseAdapter(dbOpenHelper);
    }
}