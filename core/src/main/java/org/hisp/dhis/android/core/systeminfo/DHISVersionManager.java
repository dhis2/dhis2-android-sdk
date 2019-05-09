/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
package org.hisp.dhis.android.core.systeminfo;

import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DHISVersionManager {

    private DHISVersion version;

    @Inject
    DHISVersionManager(ObjectWithoutUidStore<SystemInfo> systemInfoStore) {
        SystemInfo systemInfoModel = systemInfoStore.selectFirst();

        if (systemInfoModel != null && systemInfoModel.version() != null) {
            version = DHISVersion.getValue(systemInfoModel.version());
        }
    }

    public DHISVersion getVersion() {
        return version;
    }

    public boolean is2_29() {
        return version == DHISVersion.V2_29;
    }

    public boolean is2_30() {
        return version == DHISVersion.V2_30;
    }

    public boolean is2_31() {
        return version == DHISVersion.V2_31;
    }

    public boolean is2_32() {
        return version == DHISVersion.V2_32;
    }

    void setVersion(String versionStr) {
        this.version = DHISVersion.getValue(versionStr);
    }
}