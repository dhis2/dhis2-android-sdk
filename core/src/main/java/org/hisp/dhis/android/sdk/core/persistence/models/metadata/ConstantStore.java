package org.hisp.dhis.android.sdk.core.persistence.models.metadata;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Constant$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Constant$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.metadata.Constant;

import java.util.List;

public final class ConstantStore implements IIdentifiableObjectStore<Constant> {

    public ConstantStore() {
        //empty constructor
    }

    @Override
    public void insert(Constant object) {
        Constant$Flow constantFlow = Constant$Flow.fromModel(object);
        constantFlow.insert();

        object.setId(constantFlow.getId());
    }

    @Override
    public void update(Constant object) {
        Constant$Flow.fromModel(object).update();
    }

    @Override
    public void save(Constant object) {
        Constant$Flow constantFlow =
                Constant$Flow.fromModel(object);
        constantFlow.save();

        object.setId(constantFlow.getId());
    }

    @Override
    public void delete(Constant object) {
        Constant$Flow.fromModel(object).delete();
    }

    @Override
    public List<Constant> query() {
        List<Constant$Flow> constantFlows = new Select()
                .from(Constant$Flow.class)
                .queryList();
        return Constant$Flow.toModels(constantFlows);
    }

    @Override
    public Constant query(long id) {
        Constant$Flow constantFlow = new Select()
                .from(Constant$Flow.class)
                .where(Condition.column(Constant$Flow$Table.ID).is(id))
                .querySingle();
        return Constant$Flow.toModel(constantFlow);
    }

    @Override
    public Constant query(String uid) {
        Constant$Flow constantFlow = new Select()
                .from(Constant$Flow.class)
                .where(Condition.column(Constant$Flow$Table.UID).is(uid))
                .querySingle();
        return Constant$Flow.toModel(constantFlow);
    }
}
