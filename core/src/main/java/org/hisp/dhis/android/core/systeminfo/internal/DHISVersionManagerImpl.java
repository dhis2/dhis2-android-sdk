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
package org.hisp.dhis.android.core.systeminfo.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.systeminfo.DHISPatchVersion;
import org.hisp.dhis.android.core.systeminfo.DHISVersion;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SMSVersion;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DHISVersionManagerImpl implements DHISVersionManager {

    private DHISVersion version;
    private DHISPatchVersion patchVersion;
    private SMSVersion smsVersion;
    private final ObjectWithoutUidStore<SystemInfo> systemInfoStore;

    @Inject
    DHISVersionManagerImpl(ObjectWithoutUidStore<SystemInfo> systemInfoStore) {
        this.systemInfoStore = systemInfoStore;
    }

    @Override
    public DHISVersion getVersion() {
        if (version == null) {
            SystemInfo systemInfo = systemInfoStore.selectFirst();

            if (systemInfo != null && systemInfo.version() != null) {
                version = DHISVersion.getValue(systemInfo.version());
            }
        }
        return version;
    }

    @Override
    public DHISPatchVersion getPatchVersion() {
        if (patchVersion == null) {
            SystemInfo systemInfo = systemInfoStore.selectFirst();

            if (systemInfo != null && systemInfo.version() != null) {
                patchVersion = DHISPatchVersion.getValue(systemInfo.version());
            }
        }
        return patchVersion;
    }

    @Override
    public SMSVersion getSmsVersion() {
        if (smsVersion == null) {
            SystemInfo systemInfo = systemInfoStore.selectFirst();

            if (systemInfo != null && systemInfo.version() != null) {
                smsVersion = SMSVersion.getValue(systemInfo.version());
            }
        }
        return smsVersion;
    }

    @Override
    public boolean is2_29() {
        return getVersion() == DHISVersion.V2_29;
    }

    @Override
    public boolean is2_30() {
        return getVersion() == DHISVersion.V2_30;
    }

    @Override
    public boolean is2_31() {
        return getVersion() == DHISVersion.V2_31;
    }

    @Override
    public boolean is2_32() {
        return getVersion() == DHISVersion.V2_32;
    }

    @Override
    public boolean is2_33() {
        return getVersion() == DHISVersion.V2_33;
    }

    @Override
    public boolean is2_34() {
        return getVersion() == DHISVersion.V2_34;
    }

    @Override
    public boolean is2_35() {
        return getVersion() == DHISVersion.V2_35;
    }

    @Override
    public boolean is2_36() {
        return getVersion() == DHISVersion.V2_36;
    }

    @Override
    public boolean is2_37() {
        return getVersion() == DHISVersion.V2_37;
    }

    @Override
    public boolean is2_38() {
        return getVersion() == DHISVersion.V2_38;
    }

    @Override
    public boolean is2_39() {
        return getVersion() == DHISVersion.V2_39;
    }

    @Override
    public boolean isGreaterThan(DHISVersion version) {
        return version.compareTo(getVersion()) < 0;
    }

    @Override
    public boolean isGreaterOrEqualThan(DHISVersion version) {
        return version.compareTo(getVersion()) <= 0;
    }

    void setVersion(String versionStr) {
        this.version = DHISVersion.getValue(versionStr);
        this.patchVersion = DHISPatchVersion.getValue(versionStr);
        this.smsVersion = SMSVersion.getValue(versionStr);
    }
}