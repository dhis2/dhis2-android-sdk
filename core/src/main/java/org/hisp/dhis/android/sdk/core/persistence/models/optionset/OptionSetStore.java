package org.hisp.dhis.android.sdk.core.persistence.models.optionset;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Option$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.OptionSet$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.OptionSet$Flow$Table;
import org.hisp.dhis.android.sdk.core.utils.DbUtils;
import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.meta.DbOperation;
import org.hisp.dhis.android.sdk.models.common.meta.IDbOperation;
import org.hisp.dhis.android.sdk.models.option.IOptionStore;
import org.hisp.dhis.android.sdk.models.option.Option;
import org.hisp.dhis.android.sdk.models.optionset.OptionSet;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public final class OptionSetStore implements IIdentifiableObjectStore<OptionSet> {

    private final IOptionStore optionStore;

    public OptionSetStore(IOptionStore optionStore) {
        this.optionStore = optionStore;
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
        List<Option> options = object.getOptions();
        List<IDbOperation> operations;
        if(options != null) {
            operations = new ArrayList<>();
            for(Option option : options) {
                operations.add(DbOperation.with(optionStore).delete(option));
            }
            DbUtils.applyBatch(operations);
        }
        OptionSet$Flow.fromModel(object).delete();
    }

    @Override
    public List<OptionSet> queryAll() {
        List<OptionSet$Flow> optionSetFlows = new Select()
                .from(OptionSet$Flow.class)
                .queryList();
        for(OptionSet$Flow optionSetFlow: optionSetFlows) {
            optionSetFlow.setOptions(Option$Flow.fromModels(optionStore.query(OptionSet$Flow.toModel(optionSetFlow))));
        }
        return OptionSet$Flow.toModels(optionSetFlows);
    }

    @Override
    public OptionSet queryById(long id) {
        OptionSet$Flow optionSetFlow = new Select()
                .from(OptionSet$Flow.class)
                .where(Condition.column(OptionSet$Flow$Table.ID).is(id))
                .querySingle();
        optionSetFlow.setOptions(Option$Flow.fromModels(optionStore.query(OptionSet$Flow.toModel(optionSetFlow))));
        return OptionSet$Flow.toModel(optionSetFlow);
    }

    @Override
    public OptionSet queryByUid(String uid) {
        OptionSet$Flow optionSetFlow = new Select()
                .from(OptionSet$Flow.class)
                .where(Condition.column(OptionSet$Flow$Table.UID).is(uid))
                .querySingle();
        optionSetFlow.setOptions(Option$Flow.fromModels(optionStore.query(OptionSet$Flow.toModel(optionSetFlow))));
        return OptionSet$Flow.toModel(optionSetFlow);
    }
}
