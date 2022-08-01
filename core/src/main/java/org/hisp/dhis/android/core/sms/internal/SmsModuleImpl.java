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

package org.hisp.dhis.android.core.sms.internal;

import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.sms.domain.interactor.ConfigCase;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.sms.domain.interactor.SmsSubmitCase;
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.DeviceStateRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class SmsModuleImpl implements SmsModule {
    private final DeviceStateRepository deviceStateRepository;
    private final LocalDbRepository localDbRepository;
    private final SmsRepository smsRepository;
    private final WebApiRepository webApiRepository;
    private final DHISVersionManager dhisVersionManager;

    @Inject
    SmsModuleImpl(DeviceStateRepository deviceStateRepository, LocalDbRepository localDbRepository,
                  SmsRepository smsRepository, WebApiRepository webApiRepository,
                  DHISVersionManager dhisVersionManager) {
        this.deviceStateRepository = deviceStateRepository;
        this.localDbRepository = localDbRepository;
        this.smsRepository = smsRepository;
        this.webApiRepository = webApiRepository;
        this.dhisVersionManager = dhisVersionManager;
    }

    @Override
    public ConfigCase configCase() {
        return new ConfigCase(webApiRepository, localDbRepository);
    }

    @Override
    public QrCodeCase qrCodeCase() {
        return new QrCodeCase(localDbRepository, dhisVersionManager);
    }

    @Override
    public SmsSubmitCase smsSubmitCase() {
        return new SmsSubmitCase(localDbRepository, smsRepository, deviceStateRepository, dhisVersionManager);
    }
}
