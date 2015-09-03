package org.hisp.dhis.android.sdk.core.persistence.models.metadata;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.OptionSet$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.OptionSet$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.metadata.OptionSet;

import java.util.List;

public final class OptionSetStore implements IStore<OptionSet>{

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
        return OptionSet$Flow.toModels(optionSetFlows);
    }

    @Override
    public OptionSet query(long id) {
        OptionSet$Flow optionSetFlow = new Select()
                .from(OptionSet$Flow.class)
                .where(Condition.column(OptionSet$Flow$Table.ID).is(id))
                .querySingle();
        return OptionSet$Flow.toModel(optionSetFlow);
    }

    @Override
    public OptionSet query(String uid) {
        OptionSet$Flow optionSetFlow = new Select()
                .from(OptionSet$Flow.class)
                .where(Condition.column(OptionSet$Flow$Table.UID).is(uid))
                .querySingle();
        return OptionSet$Flow.toModel(optionSetFlow);
    }
}
