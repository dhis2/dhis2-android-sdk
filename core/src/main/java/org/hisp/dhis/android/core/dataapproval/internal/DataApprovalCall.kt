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
package org.hisp.dhis.android.core.dataapproval.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.ArrayList
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCall
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.commaSeparatedCollectionValues
import org.hisp.dhis.android.core.arch.helpers.internal.MultiDimensionalPartitioner
import org.hisp.dhis.android.core.dataapproval.DataApproval

@Reusable
internal class DataApprovalCall @Inject constructor(
    private val service: DataApprovalService,
    private val handler: Handler<DataApproval>,
    private val apiDownloader: APIDownloader,
    private val multiDimensionalPartitioner: MultiDimensionalPartitioner
) : QueryCall<DataApproval, DataApprovalQuery> {

    companion object {
        private const val QUERY_WITHOUT_UIDS_LENGTH = (
            "dataApprovals/multiple?fields=wf,ou,pe,aoc,state&wf=&pe=&ou&aoc="
            ).length
    }

    @Suppress("MagicNumber")
    override fun download(query: DataApprovalQuery): Single<List<DataApproval>> {
        val partitions = multiDimensionalPartitioner.partitionForSize(
            QUERY_WITHOUT_UIDS_LENGTH,
            query.workflowsUids(),
            query.periodIds(),
            query.organisationUnistUids(),
            query.attributeOptionCombosUids()
        )
        return Observable.fromIterable(partitions).flatMapSingle { part ->
            apiDownloader.downloadList(
                handler,
                service.getDataApprovals(
                    DataApprovalFields.allFields,
                    query.lastUpdatedStr(),
                    commaSeparatedCollectionValues(part[0]),
                    commaSeparatedCollectionValues(part[1]),
                    commaSeparatedCollectionValues(part[2]),
                    commaSeparatedCollectionValues(part[3])
                )
            )
        }.reduce(ArrayList(), { t1, t2 -> t1 + t2 })
    }
}
