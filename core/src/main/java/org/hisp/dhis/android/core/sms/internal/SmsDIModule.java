package org.hisp.dhis.android.core.sms.internal;

import android.content.Context;

import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.sms.data.internal.DeviceStateRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.internal.SmsVersionRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.LocalDbRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.smsrepository.internal.SmsRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.WebApiRepositoryImpl;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SmsVersionRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
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

    @Provides
    SmsVersionRepository smsVersionRepository(DHISVersionManager dhisVersionManager) {
        return new SmsVersionRepositoryImpl(dhisVersionManager);
    }

    @Provides
    @Reusable
    SmsModule module(SmsModuleImpl impl) {
        return impl;
    }
}
