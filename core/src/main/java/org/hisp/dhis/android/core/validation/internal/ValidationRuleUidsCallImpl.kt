/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.validation.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.validation.DataSetValidationRuleLink
import java.lang.Boolean
import javax.inject.Inject

@Reusable
internal class ValidationRuleUidsCallImpl @Inject constructor(
    private val service: ValidationRuleService,
    private val linkHandler: DataSetValidationRuleLinkHandler,
    private val apiDownloader: APIDownloader,
) : ValidationRuleUidsCall {

    override fun download(uids: Set<String>): Single<List<ObjectWithUid>> {
        return Observable.fromIterable(uids)
            .flatMapSingle { dataSetUid: String ->
                apiDownloader.downloadLink(
                    dataSetUid,
                    linkHandler,
                    { _: String ->
                        service.getDataSetValidationRuleUids(
                            dataSetUid,
                            BaseIdentifiableObject.UID,
                            Boolean.FALSE,
                        )
                    },
                ) { validationRule: ObjectWithUid ->
                    DataSetValidationRuleLink.builder()
                        .dataSet(dataSetUid)
                        .validationRule(validationRule.uid()).build()
                }
            }
            .reduce(ArrayList()) { items1: List<ObjectWithUid>, items2: List<ObjectWithUid> ->
                items1 + items2 // Use the + operator to concatenate lists
            }
    }
}