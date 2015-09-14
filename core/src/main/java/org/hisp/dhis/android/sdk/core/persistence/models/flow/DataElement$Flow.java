package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
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
