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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.sdk.utils.StringUtils;

public final class Access {

    public Access() {

    }

    public Access(Access access) {
        this.manage = access.manage;
        this.externalize = access.externalize;
        this.write = access.write;
        this.read = access.read;
        this.update = access.update;
        this.delete = access.delete;
    }

    @JsonProperty("manage")
    boolean manage;

    @JsonProperty("externalize")
    boolean externalize;

    @JsonProperty("write")
    boolean write;

    @JsonProperty("read")
    boolean read;

    @JsonProperty("update")
    boolean update;

    @JsonProperty("delete")
    boolean delete;

    /**
     * Factory method which creates Access object with all rights set to true.
     *
     * @return new Access object.
     */
    static Access provideDefaultAccess() {
        Access access = new Access();
        access.setManage(true);
        access.setExternalize(true);
        access.setWrite(true);
        access.setUpdate(true);
        access.setRead(true);
        access.setDelete(true);
        return access;
    }

    @JsonIgnore
    public boolean isDelete() {
        return delete;
    }

    @JsonIgnore
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @JsonIgnore
    public boolean isExternalize() {
        return externalize;
    }

    @JsonIgnore
    public void setExternalize(boolean externalize) {
        this.externalize = externalize;
    }

    @JsonIgnore
    public boolean isManage() {
        return manage;
    }

    @JsonIgnore
    public void setManage(boolean manage) {
        this.manage = manage;
    }

    @JsonIgnore
    public boolean isRead() {
        return read;
    }

    @JsonIgnore
    public void setRead(boolean read) {
        this.read = read;
    }

    @JsonIgnore
    public boolean isUpdate() {
        return update;
    }

    @JsonIgnore
    public void setUpdate(boolean update) {
        this.update = update;
    }

    @JsonIgnore
    public boolean isWrite() {
        return write;
    }

    @JsonIgnore
    public void setWrite(boolean write) {
        this.write = write;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return StringUtils.create()
                .append("Access {")
                .append("manage=").append(manage)
                .append(", externalize=").append(externalize)
                .append(", write=").append(write)
                .append(", read=").append(read)
                .append(", update=").append(update)
                .append(", delete=").append(delete)
                .append("}")
                .build();
    }
}
