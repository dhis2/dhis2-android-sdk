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

package org.hisp.dhis.android.core.validation.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.validation.DataSetValidationRuleLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Reusable
final class ValidationRuleUidsCallImpl implements ValidationRuleUidsCall {

    private final ValidationRuleService service;
    private final LinkHandler<ObjectWithUid, DataSetValidationRuleLink> linkHandler;
    private final APIDownloader apiDownloader;

    @Inject
    ValidationRuleUidsCallImpl(ValidationRuleService service,
                               LinkHandler<ObjectWithUid, DataSetValidationRuleLink> linkHandler,
                               APIDownloader apiDownloader) {
        this.service = service;
        this.linkHandler = linkHandler;
        this.apiDownloader = apiDownloader;
    }

    @Override
    public Single<List<ObjectWithUid>> download(Set<String> dataSetUids) {
        return Observable.fromIterable(dataSetUids)
                .flatMapSingle(dataSetUid -> apiDownloader.downloadLink(dataSetUid, linkHandler, dataSetPartitionUids ->
                                service.getDataSetValidationRuleUids(
                                        dataSetUid, BaseIdentifiableObject.UID, Boolean.FALSE),
                        validationRule -> DataSetValidationRuleLink.builder()
                                .dataSet(dataSetUid)
                                .validationRule(validationRule.uid()).build()))
                .reduce(new ArrayList<>(), (items1, items2) -> {
                    items1.addAll(items2);
                    return items1;
                });
    }
}