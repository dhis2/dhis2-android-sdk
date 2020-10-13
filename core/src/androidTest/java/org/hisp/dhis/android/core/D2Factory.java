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

package org.hisp.dhis.android.core;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.hisp.dhis.android.core.arch.storage.internal.AndroidInsecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.AndroidSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.InMemoryUnsecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.Collections;

import okhttp3.logging.HttpLoggingInterceptor;

public class D2Factory {

    public static D2 forNewDatabase() {
        return forNewDatabaseInternal(new InMemorySecureStore(), new InMemoryUnsecureStore());
    }

    public static D2 forNewDatabaseWithAndroidSecureStore() throws D2Error {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        return forNewDatabaseInternal(new AndroidSecureStore(context), new AndroidInsecureStore(context));
    }

    private static D2 forNewDatabaseInternal(SecureStore secureStore, InsecureStore insecureStore) {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        D2Configuration d2Configuration = d2Configuration(context);

        D2Manager.setTestMode(true);
        D2Manager.setTestingSecureStore(secureStore);
        D2Manager.setTestingInsecureStore(insecureStore);
        D2 d2 = D2Manager.blockingInstantiateD2(d2Configuration);

        D2Manager.clear();

        return d2;
    }

    public static D2Configuration d2Configuration(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return D2Configuration.builder()
                .appVersion("1.0.0")
                .readTimeoutInSeconds(30)
                .connectTimeoutInSeconds(30)
                .writeTimeoutInSeconds(30)
                .networkInterceptors(Collections.singletonList(new StethoInterceptor()))
                .interceptors(Collections.singletonList(loggingInterceptor))
                .context(context)
                .build();
    }
}