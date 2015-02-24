package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 18.02.15.
 */
@Table
public class User extends BaseIdentifiableObject {

    @JsonProperty("firstName")
    @Column
    public String firstName;

    @JsonProperty("surName")
    @Column
    public String surName;

    @JsonProperty("organisationUnits")
    private List<OrganisationUnit> organisationUnits;

    public List<OrganisationUnit> getOrganisationUnits() {
        /*if(organisationUnits == null) {
            organisationUnits = Select.all(OrganisationUnit.class,
                    Condition.column(ProgramStage$Table.PROGRAM_PROGRAM).is(id));
        }*/
        return organisationUnits;
    }

}
