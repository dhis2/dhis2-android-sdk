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

package org.hisp.dhis.client.sdk.android.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.client.sdk.android.common.meta.DbDhis;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


@Table(databaseName = DbDhis.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = UnitToDataSetRelationShip$Flow.UNIQUE_ORGUNIT_DATASET_GROUP, uniqueConflict = ConflictAction.FAIL)
})
public final class UnitToDataSetRelationShip$Flow extends BaseModel {
    static final int UNIQUE_ORGUNIT_DATASET_GROUP = 1;
    static final String ORG_UNIT_KEY = "organisationUnit";
    static final String DATA_SET_KEY = "dataSet";

    @Column
    @PrimaryKey(autoincrement = true)
    int id;

    @Column
    @Unique(unique = false, uniqueGroups = {UNIQUE_ORGUNIT_DATASET_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = ORG_UNIT_KEY, columnType = String.class, foreignColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    OrganisationUnit$Flow organisationUnit;

    @Column
    @Unique(unique = false, uniqueGroups = {UNIQUE_ORGUNIT_DATASET_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DATA_SET_KEY, columnType = String.class, foreignColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    DataSet$Flow dataSet;

    public int getId() {
        return id;
    }

    public void setOrganisationUnit(OrganisationUnit$Flow unit) {
        this.organisationUnit = isNull(unit, "OrganisationUnit object must not be null");
    }

    public void setDataSet(DataSet$Flow dataSet) {
        this.dataSet = isNull(dataSet, "DataSet object must not be null");
    }

    public OrganisationUnit$Flow getOrganisationUnit() {
        return organisationUnit;
    }

    public DataSet$Flow getDataSet() {
        return dataSet;
    }
}