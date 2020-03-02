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
package org.hisp.dhis.android.core.settings.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.settings.ProgramSetting;
import org.hisp.dhis.android.core.settings.ProgramSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Single;

@Reusable
public class ProgramSettingCall implements CompletableProvider {
    private final DatabaseAdapter databaseAdapter;
    private final Handler<ProgramSetting> programSettingHandler;
    private final SettingService androidSettingService;
    private final RxAPICallExecutor apiCallExecutor;

    @Inject
    ProgramSettingCall(DatabaseAdapter databaseAdapter,
                       Handler<ProgramSetting> programSettingHandler,
                       SettingService androidSettingService,
                       RxAPICallExecutor apiCallExecutor) {
        this.databaseAdapter = databaseAdapter;
        this.programSettingHandler = programSettingHandler;
        this.androidSettingService = androidSettingService;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public Completable getCompletable(boolean storeError) {
        return Completable
                .fromSingle(downloadAndPersist(storeError))
                .onErrorComplete();
    }

    private Single<ProgramSettings> downloadAndPersist(boolean storeError) {
        return apiCallExecutor.wrapSingle(androidSettingService.getProgramSettings(), storeError)
                .map(programSettings -> {
                    Transaction transaction = databaseAdapter.beginNewTransaction();
                    try {
                        List<ProgramSetting> programSettingList = getProgramSettingList(programSettings);
                        programSettingHandler.handleMany(programSettingList);
                        transaction.setSuccessful();
                    } finally {
                        transaction.end();
                    }
                    return programSettings;
                });
    }

    private List<ProgramSetting> getProgramSettingList(ProgramSettings programSettings) {
        List<ProgramSetting> programSettingList = new ArrayList<>();

        if (programSettings != null) {
            programSettingList.add(programSettings.globalSettings());

            for (Map.Entry<String, ProgramSetting> entry : programSettings.specificSettings().entrySet()) {
                programSettingList.add(entry.getValue());
            }
        }
        return programSettingList;
    }
}
