/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.sms

import android.content.Context
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.sms.data.internal.DeviceStateRepositoryImpl
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.LocalDbRepositoryImpl
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSConfigStore
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSConfigStoreImpl
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSMetadataIdStore
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSMetadataIdStoreImpl
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSOngoingSubmissionStore
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSOngoingSubmissionStoreImpl
import org.hisp.dhis.android.core.sms.data.smsrepository.internal.SmsRepositoryImpl
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.MetadataNetworkHandler
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.WebApiRepositoryImpl
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository
import org.hisp.dhis.android.core.sms.domain.repository.internal.DeviceStateRepository
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@ComponentScan
internal class SmsDIModule {
    @Singleton
    fun deviceStateRepository(context: Context): DeviceStateRepository {
        return DeviceStateRepositoryImpl(context)
    }

    @Singleton
    fun localDbRepository(impl: LocalDbRepositoryImpl): LocalDbRepository {
        return impl
    }

    @Singleton
    fun smsRepository(context: Context): SmsRepository {
        return SmsRepositoryImpl(context)
    }

    @Singleton
    fun webApiRepository(networkHandler: MetadataNetworkHandler): WebApiRepository {
        return WebApiRepositoryImpl(networkHandler)
    }

    @Singleton
    fun smsMetadataIdStore(databaseAdapter: DatabaseAdapter): SMSMetadataIdStore {
        return SMSMetadataIdStoreImpl(databaseAdapter)
    }

    @Singleton
    fun smsConfigStore(databaseAdapter: DatabaseAdapter): SMSConfigStore {
        return SMSConfigStoreImpl(databaseAdapter)
    }

    @Singleton
    fun smsOngoingSubmissionStore(databaseAdapter: DatabaseAdapter): SMSOngoingSubmissionStore {
        return SMSOngoingSubmissionStoreImpl(databaseAdapter)
    }
}
