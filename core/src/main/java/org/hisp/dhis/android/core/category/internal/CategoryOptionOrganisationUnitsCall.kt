/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.internal.UrlLengthHelper
import org.hisp.dhis.android.core.category.CategoryOptionOrganisationUnitLink
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import javax.inject.Inject

@Reusable
internal class CategoryOptionOrganisationUnitsCall @Inject constructor(
    private val handler: LinkHandler<ObjectWithUid, CategoryOptionOrganisationUnitLink>,
    private val service: CategoryOptionService,
    private val dhisVersionManager: DHISVersionManager,
    private val apiDownloader: APIDownloader
) {

    companion object {
        private const val QUERY_WITHOUT_UIDS_LENGTH = ("categoryOptions/orgUnits?categoryOptions=").length
    }

    fun download(uids: Set<String>): Single<Map<String, List<String>>> {
        return if (dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_37)) {
            apiDownloader.downloadPartitionedMap(
                uids,
                UrlLengthHelper.getHowManyUidsFitInURL(QUERY_WITHOUT_UIDS_LENGTH),
                { map: Map<String, List<String?>> -> map.forEach { handleEntry(it) } },
                { partitionUids: Set<String> ->
                    service.getCategoryOptionOrgUnits(
                        CollectionsHelper.commaAndSpaceSeparatedCollectionValues(partitionUids)
                    )
                })
        } else {
            Single.just(emptyMap())
        }
    }

    private fun handleEntry(entry: Map.Entry<String, List<String?>>) {
        handler.handleMany(
            entry.key,
            entry.value.filterNotNull().map { ObjectWithUid.create(it) }
        ) { o ->
            CategoryOptionOrganisationUnitLink.builder()
                .organisationUnit(o.uid())
                .categoryOption(entry.key)
                .build()
        }
    }
}
