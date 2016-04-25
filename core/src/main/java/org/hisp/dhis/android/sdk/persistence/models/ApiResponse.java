/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import org.hisp.dhis.android.sdk.controllers.DhisController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 24.02.15.
 */
public class ApiResponse {

    public static final String RESPONSETYPE_IMPORTSUMMARIES = "ImportSummaries";
    public static final String RESPONSETYPE_IMPORTSUMMARY = "ImportSummary";

    @JsonProperty("imported")
    int imported;

    @JsonProperty("ignored")
    int ignored;

    @JsonIgnore
    List<ImportSummary> importSummaries;

    @JsonProperty("response")
    public void setResponse(Map<String, Object> response) {
        try {
            String responseType = (String) response.get("responseType");
            if (responseType.equals( RESPONSETYPE_IMPORTSUMMARIES )) {
                TypeReference<List<ImportSummary>> typeRef =
                        new TypeReference<List<ImportSummary>>() {
                        };
                List<ImportSummary> importSummaries = DhisController.getInstance().getObjectMapper().
                        convertValue(response.get("importSummaries"), typeRef);
                this.importSummaries = importSummaries;
            } else if (responseType.equals( RESPONSETYPE_IMPORTSUMMARY )) {
                ImportSummary importSummary = DhisController.getInstance().getObjectMapper().convertValue(response, ImportSummary.class);
                importSummaries = new ArrayList<>();
                importSummaries.add(importSummary);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    public List<ImportSummary> getImportSummaries() {
        return importSummaries;
    }

    @JsonIgnore
    public void setImportSummaries(List<ImportSummary> importSummaries) {
        this.importSummaries = importSummaries;
    }
}
