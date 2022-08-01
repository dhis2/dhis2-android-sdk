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
package org.hisp.dhis.android.core.category.internal

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.helpers.internal.UrlLengthHelper
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.internal.DataAccessFields

@Reusable
internal class CategoryOptionCall @Inject constructor(
    private val handler: Handler<CategoryOption>,
    private val service: CategoryOptionService,
    private val apiDownloader: APIDownloader
) : UidsCall<CategoryOption> {

    companion object {
        private const val QUERY_WITHOUT_UIDS_LENGTH = (
            "categoryOptions?fields=id,code,name,displayName,created,lastUpdated,deleted,shortName," +
                "displayShortName,description,displayDescription,startDate,endDate,access[data[read,write]]" +
                "&filter=categories.id:in:[]&filter=access.data.read:eq:true&paging=false"
            ).length
    }

    override fun download(uids: Set<String>): Single<List<CategoryOption>> {
        val accessDataReadFilter = "access.data." + DataAccessFields.read.eq(true).generateString()
        return apiDownloader.downloadPartitioned(
            uids,
            UrlLengthHelper.getHowManyUidsFitInURL(QUERY_WITHOUT_UIDS_LENGTH),
            handler
        ) { partitionUids: Set<String> ->
            service.getCategoryOptions(
                CategoryOptionFields.allFields,
                "categories." + ObjectWithUid.uid.`in`(partitionUids).generateString(),
                accessDataReadFilter,
                paging = false
            )
        }
    }
}
