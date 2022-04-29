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
package org.hisp.dhis.android.core.visualization.internal

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.visualization.Visualization

@Reusable
internal class VisualizationCall @Inject constructor(
    private val handler: Handler<Visualization>,
    private val service: VisualizationService,
    private val dhis2VersionManager: DHISVersionManager,
    private val apiDownloader: APIDownloader
) : UidsCall<Visualization> {

    companion object {
        private const val MAX_UID_LIST_SIZE = 90
    }

    override fun download(uids: Set<String>): Single<List<Visualization>> {
        return if (dhis2VersionManager.isGreaterOrEqualThan(DHISVersion.V2_34)) {
            if (dhis2VersionManager.isGreaterOrEqualThan(DHISVersion.V2_37)) {
                apiDownloader.downloadPartitioned(
                    uids,
                    MAX_UID_LIST_SIZE,
                    handler
                ) { partitionUids: Set<String> ->
                    service.getVisualizations(
                        VisualizationFields.allFields,
                        VisualizationFields.uid.`in`(partitionUids),
                        paging = false
                    )
                }
            } else {
                apiDownloader.downloadPartitioned(
                    uids,
                    MAX_UID_LIST_SIZE,
                    handler,
                    { partitionUids: Set<String> ->
                        service.getVisualizations36(
                            VisualizationFields.allFieldsAPI36,
                            VisualizationFields.uid.`in`(partitionUids),
                            paging = false
                        )
                    },
                    { it.toVisualization() }
                )
            }
        } else {
            Single.just(listOf())
        }
    }
}
