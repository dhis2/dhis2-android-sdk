package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.metadata.Constant;
import org.hisp.dhis.android.sdk.models.metadata.Option;
import org.hisp.dhis.android.sdk.models.metadata.OptionSet;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class OptionSet$Flow extends BaseIdentifiableObject$Flow {

    @Column
    int version;

    List<Option$Flow> options;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Option$Flow> getOptions() {
        return options;
    }

    public void setOptions(List<Option$Flow> options) {
        this.options = options;
    }

    public OptionSet$Flow() {
        // empty constructor
    }

    public static OptionSet toModel(OptionSet$Flow optionSetFlow) {
        if (optionSetFlow == null) {
            return null;
        }

        OptionSet optionSet = new OptionSet();
        optionSet.setId(optionSetFlow.getId());
        optionSet.setUId(optionSetFlow.getUId());
        optionSet.setCreated(optionSetFlow.getCreated());
        optionSet.setLastUpdated(optionSetFlow.getLastUpdated());
        optionSet.setName(optionSetFlow.getName());
        optionSet.setDisplayName(optionSetFlow.getDisplayName());
        optionSet.setAccess(optionSetFlow.getAccess());
        optionSet.setVersion(optionSetFlow.getVersion());
        optionSet.setOptions(Option$Flow.toModels(optionSetFlow.getOptions()));
        return optionSet;
    }

    public static OptionSet$Flow fromModel(OptionSet optionSet) {
        if (optionSet == null) {
            return null;
        }

        OptionSet$Flow optionSetFlow = new OptionSet$Flow();
        optionSetFlow.setId(optionSet.getId());
        optionSetFlow.setUId(optionSet.getUId());
        optionSetFlow.setCreated(optionSet.getCreated());
        optionSetFlow.setLastUpdated(optionSet.getLastUpdated());
        optionSetFlow.setName(optionSet.getName());
        optionSetFlow.setDisplayName(optionSet.getDisplayName());
        optionSetFlow.setAccess(optionSet.getAccess());
        optionSetFlow.setVersion(optionSet.getVersion());
        return optionSetFlow;
    }

    public static List<OptionSet> toModels(List<OptionSet$Flow> optionSetFlows) {
        List<OptionSet> optionSets = new ArrayList<>();

        if (optionSetFlows != null && !optionSetFlows.isEmpty()) {
            for (OptionSet$Flow optionSetFlow : optionSetFlows) {
                optionSets.add(toModel(optionSetFlow));
            }
        }

        return optionSets;
    }

    public static List<OptionSet$Flow> fromModels(List<OptionSet> optionSets) {
        List<OptionSet$Flow> optionSetFlows = new ArrayList<>();

        if (optionSetFlows != null && !optionSetFlows.isEmpty()) {
            for (OptionSet optionSet : optionSets) {
                optionSetFlows.add(fromModel(optionSet));
            }
        }

        return optionSetFlows;
    }
}
