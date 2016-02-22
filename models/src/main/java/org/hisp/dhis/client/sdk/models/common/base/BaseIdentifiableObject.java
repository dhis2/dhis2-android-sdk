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

package org.hisp.dhis.client.sdk.models.common.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hisp.dhis.client.sdk.models.common.Access;
import org.hisp.dhis.client.sdk.models.common.MergeStrategy;
import org.joda.time.DateTime;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseIdentifiableObject extends BaseModel implements IdentifiableObject {

    @JsonProperty("id")
    private String uId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("created")
    private DateTime created;

    @JsonProperty("lastUpdated")
    private DateTime lastUpdated;

    @JsonProperty("access")
    private Access access;

    @Override
    public String getUId() {
        return uId;
    }

    @Override
    public void setUId(String uId) {
        this.uId = uId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public DateTime getCreated() {
        return created;
    }

    @Override
    public void setCreated(DateTime created) {
        this.created = created;
    }

    @Override
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public Access getAccess() {
        return access;
    }

    @Override
    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public void mergeWith(IdentifiableObject that, MergeStrategy strategy) {
        isNull(that, "BaseIdentifiableObject should not be null");
        isNull(strategy, "MergeStrategy should not be null");

        if (!this.getClass().isInstance(that)) {
            return;
        }

        switch (strategy) {
            case REPLACE: {
                replace(that);
                break;
            }
            case REPLACE_IF_UPDATED: {
                merge(that);
                break;
            }
        }
    }

    private void replace(IdentifiableObject that) {
        this.setId(that.getId());
        this.setUId(that.getUId());
        this.setName(that.getName());
        this.setDisplayName(that.getDisplayName());
        this.setCreated(that.getCreated());
        this.setLastUpdated(that.getLastUpdated());
        this.setAccess(that.getAccess());
    }

    private void merge(IdentifiableObject that) {
        if (this.getLastUpdated() == null || that.getLastUpdated() == null) {
            return;
        }

        if (that.getLastUpdated().isAfter(this.getLastUpdated())) {
            this.setId(that.getId());
            this.setUId(that.getUId());
            this.setName(that.getName());
            this.setDisplayName(that.getDisplayName());
            this.setCreated(that.getCreated());
            this.setLastUpdated(that.getLastUpdated());
            this.setAccess(that.getAccess());
        }
    }
}
