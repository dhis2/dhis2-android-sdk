package org.hisp.dhis.android.sdk.core.persistence.models.optionset;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.api.Models;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Option$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.OptionSet$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.OptionSet$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.optionset.OptionSet;

import java.util.List;

public final class OptionSetStore implements IIdentifiableObjectStore<OptionSet> {

    public OptionSetStore() {
        //empty constructor
    }

    @Override
    public void insert(OptionSet object) {
        OptionSet$Flow optionSetFlow = OptionSet$Flow.fromModel(object);
        optionSetFlow.insert();

        object.setId(optionSetFlow.getId());
    }

    @Override
    public void update(OptionSet object) {
        OptionSet$Flow.fromModel(object).update();
    }

    @Override
    public void save(OptionSet object) {
        OptionSet$Flow optionSetFlow =
                OptionSet$Flow.fromModel(object);
        optionSetFlow.save();

        object.setId(optionSetFlow.getId());
    }

    @Override
    public void delete(OptionSet object) {
        OptionSet$Flow.fromModel(object).delete();
    }

    @Override
    public List<OptionSet> query() {
        List<OptionSet$Flow> optionSetFlows = new Select()
                .from(OptionSet$Flow.class)
                .queryList();
        for(OptionSet$Flow optionSetFlow: optionSetFlows) {
            optionSetFlow.setOptions(Option$Flow.fromModels(Models.options().query(OptionSet$Flow.toModel(optionSetFlow))));
        }
        return OptionSet$Flow.toModels(optionSetFlows);
    }

    @Override
    public OptionSet query(long id) {
        OptionSet$Flow optionSetFlow = new Select()
                .from(OptionSet$Flow.class)
                .where(Condition.column(OptionSet$Flow$Table.ID).is(id))
                .querySingle();
        optionSetFlow.setOptions(Option$Flow.fromModels(Models.options().query(OptionSet$Flow.toModel(optionSetFlow))));
        return OptionSet$Flow.toModel(optionSetFlow);
    }

    @Override
    public OptionSet query(String uid) {
        OptionSet$Flow optionSetFlow = new Select()
                .from(OptionSet$Flow.class)
                .where(Condition.column(OptionSet$Flow$Table.UID).is(uid))
                .querySingle();
        optionSetFlow.setOptions(Option$Flow.fromModels(Models.options().query(OptionSet$Flow.toModel(optionSetFlow))));
        return OptionSet$Flow.toModel(optionSetFlow);
    }
}
