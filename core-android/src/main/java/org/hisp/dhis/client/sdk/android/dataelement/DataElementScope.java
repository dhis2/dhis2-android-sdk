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

package org.hisp.dhis.client.sdk.android.dataelement;


import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.common.controllers.IIdentifiableController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementService;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;

import java.util.List;
import java.util.Set;

import rx.Observable;

public class DataElementScope implements IDataElementScope {
    private final IDataElementService dataElementService;
    private final IIdentifiableController<DataElement> dataElementController;

    public DataElementScope(IDataElementService dataElementService,
                            IIdentifiableController<DataElement> dataElementController) {
        this.dataElementService = dataElementService;
        this.dataElementController = dataElementController;
    }

    @Override
    public Observable<DataElement> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<DataElement>() {
            @Override
            public DataElement call() {
                return dataElementService.get(uid);
            }
        });
    }

    @Override
    public Observable<DataElement> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<DataElement>() {
            @Override
            public DataElement call() {
                return dataElementService.get(id);
            }
        });
    }

    @Override
    public Observable<List<DataElement>> list() {
        return Observable.create(new DefaultOnSubscribe<List<DataElement>>() {
            @Override
            public List<DataElement> call() {
                return dataElementService.list();
            }
        });
    }

    @Override
    public Observable<List<DataElement>> pull() {
        return Observable.create(new DefaultOnSubscribe<List<DataElement>>() {
            @Override
            public List<DataElement> call() {
                dataElementController.pull(SyncStrategy.DEFAULT);
                return dataElementService.list();
            }
        });
    }

    @Override
    public Observable<List<DataElement>> pull(final Set<String> uids) {
        return Observable.create(new DefaultOnSubscribe<List<DataElement>>() {
            @Override
            public List<DataElement> call() {
                dataElementController.pull(SyncStrategy.DEFAULT, uids);
                return dataElementService.list();
            }
        });
    }

    @Override
    public Observable<List<DataElement>> pull(final SyncStrategy strategy) {
        return Observable.create(new DefaultOnSubscribe<List<DataElement>>() {
            @Override
            public List<DataElement> call() {
                dataElementController.pull(strategy);
                return dataElementService.list();
            }
        });
    }

    @Override
    public Observable<List<DataElement>> pull(final SyncStrategy strategy,
                                              final Set<String> uids) {
        return Observable.create(new DefaultOnSubscribe<List<DataElement>>() {
            @Override
            public List<DataElement> call() {
                dataElementController.pull(strategy, uids);
                return dataElementService.list();
            }
        });
    }
}
