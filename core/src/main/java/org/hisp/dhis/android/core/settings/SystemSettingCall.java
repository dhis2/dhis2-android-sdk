/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.CallException;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.SimpleCallFactory;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.Transaction;

import retrofit2.Response;

public final class SystemSettingCall extends SyncCall<SystemSetting> {
    private final GenericCallData data;
    private final SystemSettingHandler handler;
    private final SystemSettingService service;
    private final SystemSettingModelBuilder modelBuilder;

    private SystemSettingCall(GenericCallData data,
                             SystemSettingHandler handler,
                             SystemSettingService service,
                             SystemSettingModelBuilder modelBuilder) {
        this.data = data;
        this.handler = handler;
        this.service = service;
        this.modelBuilder = modelBuilder;
    }

    @Override
    public final Response<SystemSetting> call() throws Exception {
        super.setExecuted();

        Response<SystemSetting> response = service.getSystemSettings(SystemSetting.allFields).execute();

        if (isValidResponse(response)) {
            persist(response);
            return response;
        } else {
            throw CallException.create(response);
        }
    }

    private void persist(Response<SystemSetting> response) {
        SystemSetting pojo = response.body();
        if (pojo != null) {
            Transaction transaction = data.databaseAdapter().beginNewTransaction();

            try {
                handler.handle(pojo, modelBuilder);
                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
    }

    private boolean isValidResponse(Response<SystemSetting> response) {
        return response.isSuccessful() && response.body() != null;
    }

    public static final SimpleCallFactory<SystemSetting> FACTORY =
            new SimpleCallFactory<SystemSetting>() {

                @Override
                public Call<Response<SystemSetting>> create(GenericCallData genericCallData) {
                    return new SystemSettingCall(
                            genericCallData,
                            SystemSettingHandlerImpl.create(genericCallData.databaseAdapter()),
                            genericCallData.retrofit().create(SystemSettingService.class),
                            new SystemSettingModelBuilder()
                    );
                }
            };
}
