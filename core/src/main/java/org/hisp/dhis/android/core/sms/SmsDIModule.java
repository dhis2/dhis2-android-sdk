package org.hisp.dhis.android.core.sms;

import android.content.Context;

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepository;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.sms.data.DeviceStateRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.LocalDbRepositoryImpl;
import org.hisp.dhis.android.core.sms.data.smsrepository.SmsRepositoryImpl;
import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.user.UserModule;

import dagger.Module;
import dagger.Provides;

@Module
public class SmsDIModule {

    @Provides
    DeviceStateRepository deviceStateRepository(Context context) {
        return new DeviceStateRepositoryImpl(context);
    }

    @Provides
    LocalDbRepository localDbRepository(UserModule userModule, ReadOnlyIdentifiableCollectionRepository<CategoryOptionCombo> categoryOptionCombos) {
        return new LocalDbRepositoryImpl(userModule, categoryOptionCombos);
    }

    @Provides
    SmsRepository smsRepository(Context context) {
        return new SmsRepositoryImpl(context);
    }
}
