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

package org.hisp.dhis.android.sdk.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.dataelement.DataElement;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class DataElement$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String type;

    @Column
    boolean zeroIsSignificant;

    @Column
    String aggregationOperator;

    @Column
    String formName;

    @Column
    String numberType;

    @Column
    String domainType;

    @Column
    String dimension;

    @Column
    String displayFormName;

    @Column
    String optionSet;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isZeroIsSignificant() {
        return zeroIsSignificant;
    }

    public void setZeroIsSignificant(boolean zeroIsSignificant) {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    public String getAggregationOperator() {
        return aggregationOperator;
    }

    public void setAggregationOperator(String aggregationOperator) {
        this.aggregationOperator = aggregationOperator;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDisplayFormName() {
        return displayFormName;
    }

    public void setDisplayFormName(String displayFormName) {
        this.displayFormName = displayFormName;
    }

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }

    public DataElement$Flow() {
        // empty constructor
    }

    public static DataElement toModel(DataElement$Flow dataElementFlow) {
        if (dataElementFlow == null) {
            return null;
        }

        DataElement dataElement = new DataElement();
        dataElement.setId(dataElementFlow.getId());
        dataElement.setUId(dataElementFlow.getUId());
        dataElement.setCreated(dataElementFlow.getCreated());
        dataElement.setLastUpdated(dataElementFlow.getLastUpdated());
        dataElement.setName(dataElementFlow.getName());
        dataElement.setDisplayName(dataElementFlow.getDisplayName());
        dataElement.setAccess(dataElementFlow.getAccess());
        dataElement.setType(dataElementFlow.getType());
        dataElement.setZeroIsSignificant(dataElementFlow.isZeroIsSignificant());
        dataElement.setAggregationOperator(dataElementFlow.getAggregationOperator());
        dataElement.setFormName(dataElementFlow.getFormName());
        dataElement.setNumberType(dataElementFlow.getNumberType());
        dataElement.setDomainType(dataElementFlow.getDomainType());
        dataElement.setDimension(dataElementFlow.getDimension());
        dataElement.setDisplayFormName(dataElementFlow.getDisplayFormName());
        dataElement.setOptionSet(dataElementFlow.getOptionSet());
        return dataElement;
    }

    public static DataElement$Flow fromModel(DataElement dataElement) {
        if (dataElement == null) {
            return null;
        }

        DataElement$Flow dataElementFlow = new DataElement$Flow();
        dataElementFlow.setId(dataElement.getId());
        dataElementFlow.setUId(dataElement.getUId());
        dataElementFlow.setCreated(dataElement.getCreated());
        dataElementFlow.setLastUpdated(dataElement.getLastUpdated());
        dataElementFlow.setName(dataElement.getName());
        dataElementFlow.setDisplayName(dataElement.getDisplayName());
        dataElementFlow.setAccess(dataElement.getAccess());
        dataElementFlow.setType(dataElement.getType());
        dataElementFlow.setZeroIsSignificant(dataElement.isZeroIsSignificant());
        dataElementFlow.setAggregationOperator(dataElement.getAggregationOperator());
        dataElementFlow.setFormName(dataElement.getFormName());
        dataElementFlow.setNumberType(dataElement.getNumberType());
        dataElementFlow.setDomainType(dataElement.getDomainType());
        dataElementFlow.setDimension(dataElement.getDimension());
        dataElementFlow.setDisplayFormName(dataElement.getDisplayFormName());
        dataElementFlow.setOptionSet(dataElement.getOptionSet());
        return dataElementFlow;
    }

    public static List<DataElement> toModels(List<DataElement$Flow> dataElementFlows) {
        List<DataElement> dataElements = new ArrayList<>();

        if (dataElementFlows != null && !dataElementFlows.isEmpty()) {
            for (DataElement$Flow dataElementFlow : dataElementFlows) {
                dataElements.add(toModel(dataElementFlow));
            }
        }

        return dataElements;
    }

    public static List<DataElement$Flow> fromModels(List<DataElement> dataElements) {
        List<DataElement$Flow> dataElementFlows = new ArrayList<>();

        if (dataElements != null && !dataElements.isEmpty()) {
            for (DataElement dataElement : dataElements) {
                dataElementFlows.add(fromModel(dataElement));
            }
        }

        return dataElementFlows;
    }
}
