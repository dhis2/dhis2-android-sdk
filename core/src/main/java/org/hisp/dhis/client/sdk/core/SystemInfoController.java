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

package org.hisp.dhis.client.sdk.core;

import android.util.Log;

import org.hisp.dhis.client.sdk.models.SystemInfo;

import java.util.HashMap;
import java.util.Map;

import retrofit2.*;
import retrofit2.Callback;

public class SystemInfoController {
    private final SystemInfoApi systemInfoApi;
    private final SystemInfoStore systemInfoStore;
    private Call<SystemInfo> systemInfoCall;

    public SystemInfoController(SystemInfoApi systemInfoApi, SystemInfoStore systemInfoStore) {
        this.systemInfoApi = systemInfoApi;
        this.systemInfoStore = systemInfoStore;
    }

    public void pull() {
        systemInfoCall = getSystemInfo();
        systemInfoCall.enqueue(new Callback<SystemInfo>() {
            @Override
            public void onResponse(Call<SystemInfo> call, Response<SystemInfo> response) {
                SystemInfo systemInfo = response.body();

                systemInfoStore.save(systemInfo);

                Log.d(SystemInfoController.class.getSimpleName(),
                        "serverDateTime: " + systemInfo.getServerDateTime().toString());

            }

            @Override
            public void onFailure(Call<SystemInfo> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private Call<SystemInfo> getSystemInfo() {
        Map<String, String> queryMap = new HashMap<>();
        return systemInfoApi.systemInfo(queryMap);
    }

    public void cancel() {
        if (systemInfoCall != null && (!systemInfoCall.isCanceled() ||
                !systemInfoCall.isExecuted())) {
            systemInfoCall.cancel();
        }
    }
}
