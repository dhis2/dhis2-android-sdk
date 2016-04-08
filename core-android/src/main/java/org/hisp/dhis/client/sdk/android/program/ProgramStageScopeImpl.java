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

package org.hisp.dhis.client.sdk.android.program;


import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.program.IProgramStageController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageService;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;

import java.util.List;
import java.util.Set;

import rx.Observable;

public class ProgramStageScopeImpl implements ProgramStageScope {
    private final IProgramStageService programStageService;
    private final IProgramStageController programStageController;

    public ProgramStageScopeImpl(IProgramStageService programStageService,
                                 IProgramStageController programStageController) {
        this.programStageService = programStageService;
        this.programStageController = programStageController;
    }

    @Override
    public Observable<List<ProgramStage>> pull() {
        return pull(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<ProgramStage>> pull(Set<String> programStageIds) {
        return pull(SyncStrategy.DEFAULT, programStageIds);
    }

    @Override
    public Observable<List<ProgramStage>> pull(final SyncStrategy syncStrategy) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStage>>() {
            @Override
            public List<ProgramStage> call() {
                programStageController.pull(syncStrategy);
                return programStageService.list();
            }
        });
    }

    @Override
    public Observable<List<ProgramStage>> pull(final SyncStrategy syncStrategy,
                                               final Set<String> programStageIds) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStage>>() {
            @Override
            public List<ProgramStage> call() {
                programStageController.pull(syncStrategy, programStageIds);
                return programStageService.list(programStageIds);
            }
        });
    }

    @Override
    public Observable<ProgramStage> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<ProgramStage>() {
            @Override
            public ProgramStage call() {
                return programStageService.get(uid);
            }
        });
    }

    @Override
    public Observable<ProgramStage> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<ProgramStage>() {
            @Override
            public ProgramStage call() {
                return programStageService.get(id);
            }
        });
    }

    @Override
    public Observable<List<ProgramStage>> list() {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStage>>() {
            @Override
            public List<ProgramStage> call() {
                return programStageService.list();
            }
        });
    }

    @Override
    public Observable<List<ProgramStage>> list(final Program program) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStage>>() {
            @Override
            public List<ProgramStage> call() {
                return programStageService.list(program);
            }
        });
    }
}
