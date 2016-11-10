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

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionApiClient;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class ProgramStageSectionApiClientImpl implements ProgramStageSectionApiClient {
    private final ProgramStageSectionApiClientRetrofit programStageSectionApiClientRetrofit;

    public ProgramStageSectionApiClientImpl(ProgramStageSectionApiClientRetrofit retrofitClient) {
        this.programStageSectionApiClientRetrofit = retrofitClient;
    }

    @Override
    public List<ProgramStageSection> getProgramStageSections(
            Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {

        return getCollection(apiResource, fields, lastUpdated, uids);
    }

    @Override
    public List<ProgramStageSection> getProgramStageSections(
            Fields fields, Set<String> stageDataElementUids) throws ApiException {

        return getCollection(apiResource, "programStageDataElements.id",
                fields, null, stageDataElementUids);
    }

    private ApiResource<ProgramStageSection> apiResource = new ApiResource<ProgramStageSection>() {

        @Override
        public String getResourceName() {
            return "programStageSections";
        }

        @Override
        public String getBasicProperties() {
            return "id,programStageDataElements[id]";
        }

        @Override
        public String getAllProperties() {
            return "id,name,displayName,created,lastUpdated,access," +
                    "sortOrder,programStage[id],programStageDataElements[id]";
        }

        @Override
        public String getDescendantProperties() {
            throw new UnsupportedOperationException();
        }

        public Call<Map<String, List<ProgramStageSection>>> getEntities(
                Map<String, String> queryMap, List<String> filters) throws ApiException {
            return programStageSectionApiClientRetrofit
                    .getProgramStageSections(queryMap, filters);
        }
    };
}
