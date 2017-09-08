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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 24.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ImportSummary extends BaseModel {

    public static final String SUCCESS = "SUCCESS";
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    int id;

    @JsonProperty("status")
    @Column(name = "status")
    String status;

    @JsonProperty("description")
    @Column(name = "description")
    String description;

    @JsonProperty("importCount")
    @Column
    @ForeignKey(references = {
            @ForeignKeyReference(columnName = "importCount", columnType = int.class, foreignColumnName = "id")
    })
    ImportCount importCount;

    @JsonProperty("reference")
    @Column(name = "reference")
    String reference;

    @JsonProperty("href")
    @Column(name = "href")
    String href;

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    @JsonProperty("conflicts")
    List<Conflict> conflicts;

    public List<Conflict> getConflicts() {
        if( conflicts == null ) {
            conflicts = new Select().from(Conflict.class).where(Condition.column(Conflict$Table.IMPORTSUMMARY).is(id)).queryList();
        }
        return conflicts;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public ImportCount getImportCount() {
        return importCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public boolean isSuccessOrOK() {
        //When a batch list have conflicts, the response is success but the api response should be error.
        return ((getStatus().equals(SUCCESS) && ((getConflicts()==null) || getConflicts().size()==0)) || getStatus().equals(OK));
    }

    public boolean isError() {
        //When a batch list have conflicts, the response is success but the api response should be error.
        return ImportSummary.ERROR.equals(getStatus()) || (getConflicts()!=null && getConflicts().size()>0);
    }

    public boolean isConflictOnBatchPush() {
        //When a batch list have conflicts, the response is success but the api response should be error.
        return (getStatus().equals(SUCCESS) && ((getConflicts()!=null) && getConflicts().size()>0));
    }


}
