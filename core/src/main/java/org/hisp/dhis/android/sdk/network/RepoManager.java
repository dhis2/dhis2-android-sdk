/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.network;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.utils.UiUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static okhttp3.Credentials.basic;


public final class RepoManager {
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 60 * 1000; // 60s
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 60 * 1000; // 60s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 60 * 1000; // 60s

    private RepoManager() {
        // no instances
    }

    public static DhisApi createService(HttpUrl serverUrl, Credentials credentials) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(provideServerUrl(serverUrl))
                .addConverterFactory(provideJacksonConverter())
                .client(provideOkClient(credentials))
                .build();

        return retrofit.create(DhisApi.class);
    }

    private static String provideServerUrl(HttpUrl httpUrl) {
        return httpUrl.toString()+"/api/";
    }

    private static JacksonConverterFactory provideJacksonConverter() {
        return JacksonConverterFactory.create(DhisController.getInstance().getObjectMapper());
    }


    private static okhttp3.OkHttpClient provideOkClient(Credentials credentials) {
        return provideOkHttpClient(credentials);
    }

    public static okhttp3.OkHttpClient provideOkHttpClient(Credentials credentials) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(provideInterceptor(credentials))
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        return client;
    }

    private static Interceptor provideInterceptor(Credentials credentials) {
        return new AuthInterceptor(credentials.getUsername(), credentials.getPassword());
    }

    private static class AuthInterceptor implements Interceptor {
        private final String mUsername;
        private final String mPassword;

        public AuthInterceptor(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            String base64Credentials = basic(mUsername, mPassword);
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", base64Credentials)
                    .build();
            Log.d("RepoManager", request.toString());
            Response response = chain.proceed(request);
            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED &&
                    DhisController.getInstance().isUserLoggedIn()) {
                DhisController.getInstance().invalidateSession();
            }
            return response;
        }

    }



   /* private static class RetrofitErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            cause.printStackTrace();
            Log.d("RepoManager", "there was an error.." + cause.getKind().name());
            try {
                String body = new StringConverter().fromBody(cause.getResponse().getBody(), String.class);
                Log.e("RepoManager", body);
            } catch (ConversionException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            APIException apiException = APIException.fromRetrofitError(cause);
            switch (apiException.getKind()) {
                case CONVERSION:
                case UNEXPECTED: {
                    logResponseBody(cause);
                    Crashlytics.logException(cause.getCause());
                    break;
                }
                case HTTP:
                    if (apiException.getResponse().getStatus() >= 500) {
                        logResponseBody(cause);
                        Crashlytics.logException(cause.getCause());
                    } else if (apiException.getResponse().getStatus() == 409) {
                        logResponseBody(cause);
                        Crashlytics.logException(cause.getCause());
                    }
            }

            return apiException;
        }
    }

    private static void logResponseBody(RetrofitError cause) {
        if (cause.getResponse() != null && cause.getResponse().getBody() != null) {
            Crashlytics.log(cause.getResponse().getBody().toString());
        }
    }*/
}
