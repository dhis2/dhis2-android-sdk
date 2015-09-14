/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.datacapture.sdk.persistence.DbDhis;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Category$Flow extends BaseIdentifiableObject implements DisplayNameModel {
    @JsonProperty("dataDimension") @Column
    String dataDimension;
    @JsonProperty("dataDimensionType") @Column
    String dataDimensionType;
    @JsonProperty("dimension") @Column
    String dimension;
    @JsonProperty("displayName") @Column
    String displayName;
    @JsonProperty("categoryOptions")
    List<CategoryOption> categoryOptions;

    public Category$Flow() {
    }

    @JsonIgnore
    public String getDataDimension() {
        return dataDimension;
    }

    @JsonIgnore
    public void setDataDimension(String dataDimension) {
        this.dataDimension = dataDimension;
    }

    @JsonIgnore
    public String getDataDimensionType() {
        return dataDimensionType;
    }

    @JsonIgnore
    public void setDataDimensionType(String dataDimensionType) {
        this.dataDimensionType = dataDimensionType;
    }

    @JsonIgnore
    public String getDimension() {
        return dimension;
    }

    @JsonIgnore
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @JsonIgnore
    public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public List<CategoryOption> getCategoryOptions() {
        return categoryOptions;
    }

    @JsonIgnore
    public void setCategoryOptions(List<CategoryOption> categoryOptions) {
        this.categoryOptions = categoryOptions;
    }

    public static List<CategoryOption> getRelatedOptions(String catId) {
        List<CategoryToCategoryOptionRelation> relations = new Select()
                .from(CategoryToCategoryOptionRelation.class)
                .where(Condition.column(CategoryToCategoryOptionRelation$Table
                        .CATEGORY_CATEGORY).is(catId))
                .queryList();
        List<CategoryOption> categoryOptions = new ArrayList<>();
        if (relations != null && !relations.isEmpty()) {
            for (CategoryToCategoryOptionRelation relation : relations) {
                categoryOptions.add(relation.getCategoryOption());
            }
        }
        return categoryOptions;
    }
}
