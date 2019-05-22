package org.hisp.dhis.android.core.sms;

import android.content.Context;

import org.hisp.dhis.android.core.sms.data.DeviceStateRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.LocalDbRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.smsrepository.SmsRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.webapirepository.WebApiRepositoryImpl;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class SmsDIModule {

    @Provides
    DeviceStateRepository deviceStateRepository(Context context) {
        return new DeviceStateRepositoryImpl(context);
    }

    @Provides
    LocalDbRepository localDbRepository(LocalDbRepositoryImpl impl) {
        return impl;
    }

    @Provides
    SmsRepository smsRepository(Context context) {
        return new SmsRepositoryImpl(context);
    }

    @Provides
    WebApiRepository webApiRepository(Retrofit retrofit) {
        return new WebApiRepositoryImpl(retrofit);
    }
}
