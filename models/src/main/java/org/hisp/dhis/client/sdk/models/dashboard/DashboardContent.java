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

package org.hisp.dhis.client.sdk.models.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hisp.dhis.client.sdk.models.common.base.BaseIdentifiableObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class DashboardContent extends BaseIdentifiableObject {
    public static final String TYPE_CHART = "CHART";
    public static final String TYPE_EVENT_CHART = "EVENT_CHART";
    public static final String TYPE_MAP = "MAP";
    public static final String TYPE_REPORT_TABLE = "REPORT_TABLE";

    /* have to implement user fragment for this */
    public static final String TYPE_USERS = "USERS";

    /* we can use data entry fragment for this: have to implement read-only mode */
    public static final String TYPE_REPORTS = "REPORTS";

    /* not supported on server side */
    public static final String TYPE_EVENT_REPORT = "EVENT_REPORT";

    /* resource can be anything (like pdf or binary file. Will look into this later */
    public static final String TYPE_RESOURCES = "RESOURCES";

    /* won't be supported until implementation of messaging application */
    public static final String TYPE_MESSAGES = "MESSAGES";


    @JsonIgnore
    String type;

    public DashboardContent() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}