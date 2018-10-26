package org.hisp.dhis.android.core.common;

import android.support.test.InstrumentationRegistry;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.arch.db.implementations.DatabaseAdapter;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class D2Factory {
    public static D2 create(String url, DatabaseAdapter databaseAdapter) {
        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse(url))
                .build();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new D2.Builder()
                .configuration(config)
                .databaseAdapter(databaseAdapter)
                .okHttpClient(
                        new OkHttpClient.Builder()
                                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter))
                                .addInterceptor(loggingInterceptor)
                                .addNetworkInterceptor(new StethoInterceptor())
                                .readTimeout(30, TimeUnit.SECONDS)
                                .build()
                )
                .context(InstrumentationRegistry.getTargetContext().getApplicationContext())
                .build();
    }
}