package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.metadata.Constant;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Constant$Flow extends BaseIdentifiableObject$Flow {

    @Column
    double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Constant$Flow() {
        // empty constructor
    }

    public static Constant toModel(Constant$Flow constantFlow) {
        if (constantFlow == null) {
            return null;
        }

        Constant constant = new Constant();
        constant.setId(constantFlow.getId());
        constant.setUId(constantFlow.getUId());
        constant.setCreated(constantFlow.getCreated());
        constant.setLastUpdated(constantFlow.getLastUpdated());
        constant.setName(constantFlow.getName());
        constant.setDisplayName(constantFlow.getDisplayName());
        constant.setAccess(constantFlow.getAccess());
        constant.setValue(constantFlow.getValue());
        return constant;
    }

    public static Constant$Flow fromModel(Constant constant) {
        if (constant == null) {
            return null;
        }

        Constant$Flow constantFlow = new Constant$Flow();
        constantFlow.setId(constant.getId());
        constantFlow.setUId(constant.getUId());
        constantFlow.setCreated(constant.getCreated());
        constantFlow.setLastUpdated(constant.getLastUpdated());
        constantFlow.setName(constant.getName());
        constantFlow.setDisplayName(constant.getDisplayName());
        constantFlow.setAccess(constant.getAccess());
        constantFlow.setValue(constant.getValue());
        return constantFlow;
    }

    public static List<Constant> toModels(List<Constant$Flow> constantFlows) {
        List<Constant> constants = new ArrayList<>();

        if (constantFlows != null && !constantFlows.isEmpty()) {
            for (Constant$Flow constantFlow : constantFlows) {
                constants.add(toModel(constantFlow));
            }
        }

        return constants;
    }

    public static List<Constant$Flow> fromModels(List<Constant> constants) {
        List<Constant$Flow> constantFlows = new ArrayList<>();

        if (constantFlows != null && !constantFlows.isEmpty()) {
            for (Constant constant : constants) {
                constantFlows.add(fromModel(constant));
            }
        }

        return constantFlows;
    }
}
