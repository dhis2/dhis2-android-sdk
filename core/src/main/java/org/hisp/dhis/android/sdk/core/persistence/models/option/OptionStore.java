package org.hisp.dhis.android.sdk.core.persistence.models.option;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Option$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Option$Flow$Table;
import org.hisp.dhis.android.sdk.models.option.IOptionStore;
import org.hisp.dhis.android.sdk.models.option.Option;
import org.hisp.dhis.android.sdk.models.optionset.OptionSet;

import java.util.List;

public final class OptionStore implements IOptionStore{

    public OptionStore() {
        //empty constructor
    }

    @Override
    public void insert(Option object) {
        Option$Flow optionFlow = Option$Flow.fromModel(object);
        optionFlow.insert();

        object.setId(optionFlow.getId());
    }

    @Override
    public void update(Option object) {
        Option$Flow.fromModel(object).update();
    }

    @Override
    public void save(Option object) {
        Option$Flow optionFlow =
                Option$Flow.fromModel(object);
        optionFlow.save();

        object.setId(optionFlow.getId());
    }

    @Override
    public void delete(Option object) {
        Option$Flow.fromModel(object).delete();
    }

    @Override
    public List<Option> query() {
        List<Option$Flow> optionFlows = new Select()
                .from(Option$Flow.class)
                .queryList();
        return Option$Flow.toModels(optionFlows);
    }

    @Override
    public Option query(long id) {
        Option$Flow optionFlow = new Select()
                .from(Option$Flow.class)
                .where(Condition.column(Option$Flow$Table.ID).is(id))
                .orderBy(Option$Flow$Table.SORTINDEX)
                .querySingle();
        return Option$Flow.toModel(optionFlow);
    }

    @Override
    public Option query(String uid) {
        Option$Flow optionFlow = new Select()
                .from(Option$Flow.class)
                .where(Condition.column(Option$Flow$Table.UID).is(uid))
                .querySingle();
        return Option$Flow.toModel(optionFlow);
    }

    @Override
    public List<Option> query(OptionSet optionSet) {
        List<Option$Flow> optionFlows = new Select()
                .from(Option$Flow.class).where(Condition.column(Option$Flow$Table.OPTIONSET).is(optionSet.getUId()))
                .queryList();
        return Option$Flow.toModels(optionFlows);
    }
}
