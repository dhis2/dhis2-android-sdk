/*
 *  Copyright (c) 2004-2024, University of Oslo
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
package org.hisp.dhis.android.network.visualization

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.internal.VisualizationNetworkHandler
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.fields.AccessFields
import org.koin.core.annotation.Singleton

@Singleton
internal class VisualizationNetworkHandlerImpl(
    httpClient: HttpServiceClient,
    private val dhis2VersionManager: DHISVersionManager,
) : VisualizationNetworkHandler {
    private val service: VisualizationService = VisualizationService(httpClient)

    override suspend fun getVisualizations(partitionUids: Set<String>): Payload<Visualization> {
        val accessFilter = "access." + AccessFields.read.eq(true).generateString()
        val visualizations =
            if (dhis2VersionManager.isGreaterOrEqualThan(DHISVersion.V2_34)) {
                // Workaround for DHIS2-15322. Request visualizations using the entity endpoint.
                partitionUids.mapNotNull { visualizationUid ->
                    try {
                        if (dhis2VersionManager.isGreaterOrEqualThan(DHISVersion.V2_37)) {
                            getVisualization(visualizationUid, accessFilter)
                        } else {
                            getVisualization36(visualizationUid, accessFilter)
                        }
                    } catch (ignored: Exception) {
                        null
                    }
                }
            } else {
                emptyList()
            }
        return PayloadJson(visualizations)
    }

    private suspend fun getVisualization(uid: String, accessFilter: String): Visualization {
        return service.getSingleVisualization(
            uid = uid,
            fields = VisualizationFields.allFields,
            accessFilter = accessFilter,
            paging = false,
        ).toDomain()
    }

    private suspend fun getVisualization36(uid: String, accessFilter: String): Visualization {
        return service.getSingleVisualizations36(
            uid = uid,
            fields = VisualizationFields.allFieldsAPI36,
            accessFilter = accessFilter,
            paging = false,
        ).toDomain()
    }
}
