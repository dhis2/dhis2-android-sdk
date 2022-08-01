/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.api.internal;

import android.os.Build;
import android.util.Log;

import org.hisp.dhis.android.BuildConfig;
import org.hisp.dhis.android.core.D2Configuration;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;

final class OkHttpClientFactory {

    static OkHttpClient okHttpClient(D2Configuration d2Configuration,
                                     Interceptor authenticator) {

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(new DynamicServerURLInterceptor())
                .addInterceptor(new ServerURLVersionRedirectionInterceptor())
                .addInterceptor(authenticator)
                .addInterceptor(new PreventURLDecodeInterceptor())
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request withUserAgent = originalRequest.newBuilder()
                            .header("User-Agent", getUserAgent(d2Configuration))
                            .build();
                    return chain.proceed(withUserAgent);
                })
                .readTimeout(d2Configuration.readTimeoutInSeconds(), TimeUnit.SECONDS)
                .connectTimeout(d2Configuration.connectTimeoutInSeconds(), TimeUnit.SECONDS)
                .writeTimeout(d2Configuration.writeTimeoutInSeconds(), TimeUnit.SECONDS)
                .followRedirects(false);

        for (Interceptor interceptor : d2Configuration.networkInterceptors()) {
            client.addNetworkInterceptor(interceptor);
        }

        for (Interceptor interceptor : d2Configuration.interceptors()) {
            client.addInterceptor(interceptor);
        }

        setTLSParameters(client);

        return client.build();
    }

    private static String getUserAgent(D2Configuration d2Configuration) {
        return String.format("%s/%s/%s/Android_%s",
                d2Configuration.appName(),
                BuildConfig.VERSION_NAME, //SDK version
                d2Configuration.appVersion(),
                Build.VERSION.SDK_INT //Android Version
        );
    }

    private static void setTLSParameters(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {

                SSLContext sc = SSLContext.getInstance(TlsVersion.TLS_1_2.javaName());
                sc.init(null, null, null);

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }

                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
                client.sslSocketFactory(new TLSSocketFactory(sc.getSocketFactory()), trustManager);

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_0, TlsVersion.TLS_1_1, TlsVersion.TLS_1_2)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);

            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                Log.e(OkHttpClientFactory.class.getSimpleName(), e.getMessage(), e);
            }
        }
    }

    private OkHttpClientFactory() {
    }
}