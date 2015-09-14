package org.hisp.dhis.android.sdk.core.persistence.models.organisationunit;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.OrganisationUnit$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.OrganisationUnit$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

public final class OrganisationUnitStore implements IIdentifiableObjectStore<OrganisationUnit> {

    public OrganisationUnitStore() {
        //empty constructor
    }

    @Override
    public void insert(OrganisationUnit object) {
        OrganisationUnit$Flow organisationUnitFlow
                = OrganisationUnit$Flow.fromModel(object);
        organisationUnitFlow.insert();

        object.setId(organisationUnitFlow.getId());
    }

    @Override
    public void update(OrganisationUnit object) {
        OrganisationUnit$Flow.fromModel(object).update();
    }

    @Override
    public void save(OrganisationUnit object) {
        OrganisationUnit$Flow organisationUnitFlow
                = OrganisationUnit$Flow.fromModel(object);
        organisationUnitFlow.save();

        object.setId(organisationUnitFlow.getId());
    }

    @Override
    public void delete(OrganisationUnit object) {
        OrganisationUnit$Flow.fromModel(object).delete();
    }

    @Override
    public List<OrganisationUnit> query() {
        List<OrganisationUnit$Flow> organisationUnitFlow = new Select()
                .from(OrganisationUnit$Flow.class)
                .queryList();
        return OrganisationUnit$Flow.toModels(organisationUnitFlow);
    }

    @Override
    public OrganisationUnit query(long id) {
        OrganisationUnit$Flow organisationUnitFlow = new Select()
                .from(OrganisationUnit$Flow.class)
                .where(Condition.column(OrganisationUnit$Flow$Table.ID).is(id))
                .querySingle();
        return OrganisationUnit$Flow.toModel(organisationUnitFlow);
    }

    @Override
    public OrganisationUnit query(String uid) {
        OrganisationUnit$Flow organisationUnitFlow = new Select()
                .from(OrganisationUnit$Flow.class)
                .where(Condition.column(OrganisationUnit$Flow$Table.UID).is(uid))
                .querySingle();
        return OrganisationUnit$Flow.toModel(organisationUnitFlow);
    }
}
