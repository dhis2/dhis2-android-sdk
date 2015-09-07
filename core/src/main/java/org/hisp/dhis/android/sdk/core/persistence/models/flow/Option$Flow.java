package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.metadata.Option;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Option$Flow extends BaseIdentifiableObject$Flow {

    @Column
    int sortIndex;

    @Column
    String optionSet;

    @Column
    String code;

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Option$Flow() {
        // empty constructor
    }

    public static Option toModel(Option$Flow optionFlow) {
        if (optionFlow == null) {
            return null;
        }

        Option option = new Option();
        option.setId(optionFlow.getId());
        option.setUId(optionFlow.getUId());
        option.setCreated(optionFlow.getCreated());
        option.setLastUpdated(optionFlow.getLastUpdated());
        option.setName(optionFlow.getName());
        option.setDisplayName(optionFlow.getDisplayName());
        option.setAccess(optionFlow.getAccess());
        option.setSortOrder(optionFlow.getSortIndex());
        option.setOptionSet(optionFlow.getOptionSet());
        option.setCode(optionFlow.getCode());
        return option;
    }

    public static Option$Flow fromModel(Option option) {
        if (option == null) {
            return null;
        }

        Option$Flow optionFlow = new Option$Flow();
        optionFlow.setId(option.getId());
        optionFlow.setUId(option.getUId());
        optionFlow.setCreated(option.getCreated());
        optionFlow.setLastUpdated(option.getLastUpdated());
        optionFlow.setName(option.getName());
        optionFlow.setDisplayName(option.getDisplayName());
        optionFlow.setAccess(option.getAccess());
        optionFlow.setSortIndex(option.getSortOrder());
        optionFlow.setOptionSet(option.getOptionSet());
        optionFlow.setCode(option.getCode());
        return optionFlow;
    }

    public static List<Option> toModels(List<Option$Flow> optionFlows) {
        List<Option> options = new ArrayList<>();

        if (optionFlows != null && !optionFlows.isEmpty()) {
            for (Option$Flow optionFlow : optionFlows) {
                options.add(toModel(optionFlow));
            }
        }

        return options;
    }

    public static List<Option$Flow> fromModels(List<Option> options) {
        List<Option$Flow> optionFlows = new ArrayList<>();

        if (optionFlows != null && !optionFlows.isEmpty()) {
            for (Option option : options) {
                optionFlows.add(fromModel(option));
            }
        }

        return optionFlows;
    }
}
