/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.client.sdk.ui.bindings.modules;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.utils.LoggerImpl;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.AppPreferencesImpl;
import org.hisp.dhis.client.sdk.ui.SyncDateWrapper;
import org.hisp.dhis.client.sdk.ui.bindings.commons.ApiExceptionHandler;
import org.hisp.dhis.client.sdk.ui.bindings.commons.ApiExceptionHandlerImpl;
import org.hisp.dhis.client.sdk.ui.bindings.commons.AppAccountManager;
import org.hisp.dhis.client.sdk.ui.bindings.commons.SessionPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.commons.SessionPreferencesImpl;
import org.hisp.dhis.client.sdk.utils.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public Logger provideLogger() {
        // if (BuildConfig.DEBUG) {
        // TODO return DebugLoggerImpl
        // }

        // should be changed we need to respect build flavors
        return new LoggerImpl();
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient() {
//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
//
//            return new OkHttpClient.Builder()
//                    // .addNetworkInterceptor(new StethoInterceptor())
//                    .addInterceptor(loggingInterceptor)
//                    .build();
//        }

        return new OkHttpClient();
    }

    @Provides
    @Singleton
    public D2.Flavor providesFlavor(OkHttpClient okHttpClient, Logger logger) {
        return new D2.Builder()
                .okHttp(okHttpClient)
                .logger(logger)
                .build();
    }

    @Inject
    public void initD2(Context context, D2.Flavor flavor) {
        Log.e("Module", "Instantiating D2");
        D2.init(context, flavor);
    }

    @Provides
    @Singleton
    public AppPreferences providesAppPreferences(Context context) {
        return new AppPreferencesImpl(context);
    }

    @Provides
    @Singleton
    public SyncDateWrapper provideSyncManager(Context context, AppPreferences appPreferences) {
        return new SyncDateWrapper(context, appPreferences);
    }

    @Provides
    @Singleton
    public SessionPreferences provideSessionPreferences(Context context) {
        return new SessionPreferencesImpl(context);
    }

    @Provides
    @Singleton
    public AppAccountManager providesAppAccountManager(
            Context context, AppPreferences appPreferences) {
        return new AppAccountManager(context, appPreferences);
    }

    @Provides
    @Singleton
    public ApiExceptionHandler providesApiExceptionHandler(Context context, Logger logger) {
        return new ApiExceptionHandlerImpl(context, logger);
    }
}
