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

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.authentication.internal.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.arch.api.internal.PreventURLDecodeInterceptor;
import org.hisp.dhis.android.core.configuration.Configuration;
import org.hisp.dhis.android.core.configuration.ServerUrlParser;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import androidx.test.InstrumentationRegistry;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class D2Factory {

    public static D2 create(String serverUrl, String databaseName) {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        D2Configuration d2Configuration = D2Configuration.builder()
                .appName("d2_integration_tests")
                .appVersion("1.0.0")
                .readTimeoutInSeconds(30)
                .connectTimeoutInSeconds(30)
                .writeTimeoutInSeconds(30)
                .networkInterceptors(Collections.singletonList(new StethoInterceptor()))
                .interceptors(Collections.singletonList(loggingInterceptor))
                .context(context)
                .build();

        D2Manager.setDatabaseName(databaseName);

        D2 d2 = D2Manager.setUp(d2Configuration)
                .andThen(D2Manager.setServerUrl(serverUrl))
                .andThen(D2Manager.instantiateD2())
                .blockingGet();

        D2Manager.clear();
        D2Manager.setDatabaseName(null);

        return d2;
    }

    public static D2 create(String url, DatabaseAdapter databaseAdapter) {
        return new D2.Builder()
                .configuration(Configuration.forServerUrl(ServerUrlParser.parse(url)))
                .databaseAdapter(databaseAdapter)
                .okHttpClient(okHttpClient(databaseAdapter))
                .context(InstrumentationRegistry.getTargetContext().getApplicationContext())
                .build();
    }

    private static OkHttpClient okHttpClient(DatabaseAdapter databaseAdapter) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
                .addInterceptor(new PreventURLDecodeInterceptor())
                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter))
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}