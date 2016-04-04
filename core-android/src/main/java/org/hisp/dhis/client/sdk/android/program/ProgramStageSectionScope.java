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
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionService;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;

public class ProgramStageSectionScope implements IProgramStageSectionScope {
    private final IProgramStageSectionController programStageSectionController;
    private final IProgramStageSectionService programStageSectionService;

    public ProgramStageSectionScope(IProgramStageSectionController programStageSectionController,
                                    IProgramStageSectionService programStageSectionService) {
        this.programStageSectionController = programStageSectionController;
        this.programStageSectionService = programStageSectionService;
    }

    @Override
    public Observable<List<ProgramStageSection>> pullUpdates() {
        return pullUpdates(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<ProgramStageSection>> pullUpdates(String... uids) {
        return pullUpdates(SyncStrategy.DEFAULT, uids);
    }

    @Override
    public Observable<List<ProgramStageSection>> pullUpdates(final SyncStrategy syncStrategy) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageSection>>() {

            @Override
            public List<ProgramStageSection> call() {
                programStageSectionController.pull(syncStrategy);
                return programStageSectionService.list();
            }
        });
    }

    @Override
    public Observable<List<ProgramStageSection>> pullUpdates(final SyncStrategy syncStrategy,
                                                             final String... uids) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageSection>>() {

            @Override
            public List<ProgramStageSection> call() {
                Set<String> uidSet = new HashSet<>(ModelUtils.asList(uids));
                programStageSectionController.pull(syncStrategy, uidSet);
                return programStageSectionService.list(uidSet);
            }
        });
    }

    @Override
    public Observable<ProgramStageSection> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<ProgramStageSection>() {
            @Override
            public ProgramStageSection call() {
                return programStageSectionService.get(uid);
            }
        });
    }

    @Override
    public Observable<ProgramStageSection> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<ProgramStageSection>() {

            @Override
            public ProgramStageSection call() {
                return programStageSectionService.get(id);
            }
        });
    }

    @Override
    public Observable<List<ProgramStageSection>> list() {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageSection>>() {

            @Override
            public List<ProgramStageSection> call() {
                return programStageSectionService.list();
            }
        });
    }

    @Override
    public Observable<List<ProgramStageSection>> list(final ProgramStage programStage) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageSection>>() {
            @Override
            public List<ProgramStageSection> call() {
                return programStageSectionService.list(programStage);
            }
        });
    }
}
