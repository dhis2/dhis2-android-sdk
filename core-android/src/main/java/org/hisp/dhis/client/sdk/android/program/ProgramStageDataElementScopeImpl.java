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
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementService;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

import java.util.List;
import java.util.Set;

import rx.Observable;

public class ProgramStageDataElementScopeImpl implements ProgramStageDataElementScope {
    private final IProgramStageDataElementService programStageDataElementService;
    private final IProgramStageDataElementController programStageDataElementController;

    public ProgramStageDataElementScopeImpl(
            IProgramStageDataElementService programStageDataElementService,
            IProgramStageDataElementController programStageDataElementController) {
        this.programStageDataElementService = programStageDataElementService;
        this.programStageDataElementController = programStageDataElementController;

    }

    @Override
    public Observable<List<ProgramStageDataElement>> pull() {
        return pull(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<ProgramStageDataElement>> pull(Set<String> uids) {
        return pull(SyncStrategy.DEFAULT, uids);
    }

    @Override
    public Observable<List<ProgramStageDataElement>> pull(final SyncStrategy syncStrategy,
                                                          final Set<String> uids) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageDataElement>>() {

            @Override
            public List<ProgramStageDataElement> call() {
                programStageDataElementController.pull(syncStrategy, uids);
                return programStageDataElementService.list(uids);
            }
        });
    }

    @Override
    public Observable<List<ProgramStageDataElement>> pull(final SyncStrategy syncStrategy) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageDataElement>>() {

            @Override
            public List<ProgramStageDataElement> call() {
                programStageDataElementController.pull(syncStrategy);
                return programStageDataElementService.list();
            }
        });
    }

    @Override
    public Observable<ProgramStageDataElement> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<ProgramStageDataElement>() {
            @Override
            public ProgramStageDataElement call() {
                return programStageDataElementService.get(id);
            }
        });
    }

    @Override
    public Observable<List<ProgramStageDataElement>> list() {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageDataElement>>() {
            @Override
            public List<ProgramStageDataElement> call() {
                return programStageDataElementService.list();
            }
        });
    }

    @Override
    public Observable<List<ProgramStageDataElement>> list(final ProgramStage programStage) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageDataElement>>() {
            @Override
            public List<ProgramStageDataElement> call() {
                return programStageDataElementService.list(programStage);
            }
        });
    }

    @Override
    public Observable<ProgramStageDataElement> list(final ProgramStage programStage,
                                                    final DataElement dataElement) {
        return Observable.create(new DefaultOnSubscribe<ProgramStageDataElement>() {
            @Override
            public ProgramStageDataElement call() {
                return programStageDataElementService.query(programStage, dataElement);
            }
        });
    }

    @Override
    public Observable<List<ProgramStageDataElement>> list(
            final ProgramStageSection programStageSection) {
        return Observable.create(new DefaultOnSubscribe<List<ProgramStageDataElement>>() {
            @Override
            public List<ProgramStageDataElement> call() {
                return programStageDataElementService.list(programStageSection);
            }
        });
    }
}
