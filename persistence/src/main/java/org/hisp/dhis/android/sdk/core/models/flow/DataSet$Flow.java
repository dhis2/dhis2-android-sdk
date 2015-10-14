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

package org.hisp.dhis.android.sdk.core.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.dataset.DataSet;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class DataSet$Flow extends BaseIdentifiableObject$Flow {
    private static final String CATEGORY_COMBO_KEY = "categoryComboKey";

    @Column
    int version;

    @Column
    int expiryDays;

    @Column
    boolean allowFuturePeriods;

    @Column
    String periodType;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = CATEGORY_COMBO_KEY, columnType = String.class, foreignColumnName = "uId")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    CategoryCombo$Flow categoryCombo;

    public DataSet$Flow() {
    }

    public CategoryCombo$Flow getCategoryCombo() {
        return categoryCombo;
    }

    public void setCategoryCombo(CategoryCombo$Flow categoryCombo) {
        this.categoryCombo = categoryCombo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(int expiryDays) {
        this.expiryDays = expiryDays;
    }

    public boolean isAllowFuturePeriods() {
        return allowFuturePeriods;
    }

    public void setAllowFuturePeriods(boolean allowFuturePeriods) {
        this.allowFuturePeriods = allowFuturePeriods;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public static DataSet$Flow fromModel(DataSet dataSet) {
        if (dataSet == null) {
            return null;
        }

        DataSet$Flow dataSetFlow = fromModel(dataSet);
        dataSetFlow.setVersion(dataSet.getVersion());
        dataSetFlow.setExpiryDays(dataSet.getExpiryDays());
        dataSetFlow.setAllowFuturePeriods(dataSet.isAllowFuturePeriods());
        dataSetFlow.setPeriodType(dataSet.getPeriodType());

        dataSetFlow.setCategoryCombo(null);

        return dataSetFlow;
    }

    public static DataSet toModel(DataSet$Flow dataSetFlow) {
        if (dataSetFlow == null) {
            return null;
        }

        DataSet dataSet = toModel(dataSetFlow);
        dataSet.setVersion(dataSetFlow.getVersion());
        dataSet.setExpiryDays(dataSetFlow.getExpiryDays());
        dataSet.setAllowFuturePeriods(dataSetFlow.isAllowFuturePeriods());
        dataSet.setPeriodType(dataSetFlow.getPeriodType());

        dataSet.setCategoryCombo(null);

        return dataSet;
    }

    public static List<DataSet> toModels(List<DataSet$Flow> dataSetFlows) {
        List<DataSet> dataSets = new ArrayList<>();

        if (dataSetFlows != null && !dataSetFlows.isEmpty()) {
            for (DataSet$Flow dataSetFlow : dataSetFlows) {
                dataSets.add(toModel(dataSetFlow));
            }
        }

        return dataSets;
    }

    public static List<DataSet$Flow> fromModels(List<DataSet> dataSets) {
        List<DataSet$Flow> dataSetFlows = new ArrayList<>();

        if (dataSets != null && !dataSets.isEmpty()) {
            for (DataSet dataSet : dataSets) {
                dataSetFlows.add(fromModel(dataSet));
            }
        }

        return dataSetFlows;
    }
}
