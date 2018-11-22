/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboTableInfo;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.FUTURE_DATE;

public class TrackedEntityAttributeReservedValueManagerRealIntegrationShould extends AbsStoreTestCase {

    private TrackedEntityAttributeReservedValueStoreInterface store;
    private String organisationUnitUid = "org_unit_uid";
    private String programUid = "program_uid";
    private String categoryComboUid = "category_combo_uid";
    private String ownerUid = "xs8A6tQJY0s";
    private D2 d2;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        login();

        store = TrackedEntityAttributeReservedValueStore.create(databaseAdapter());

        SyncHandlerWithTransformer<TrackedEntityAttributeReservedValue> handler =
                TrackedEntityAttributeReservedValueHandler.create(databaseAdapter());

        List<TrackedEntityAttributeReservedValue> trackedEntityAttributeReservedValues = new ArrayList<>();
        TrackedEntityAttributeReservedValue.Builder reservedValueBuilder =
                TrackedEntityAttributeReservedValue.builder()
                .ownerObject("owner_obj")
                .ownerUid(ownerUid)
                .key("key")
                .created(CREATED)
                .expiryDate(FUTURE_DATE);

        TrackedEntityAttributeReservedValue reservedValue1 = reservedValueBuilder.value("value1").build();
        TrackedEntityAttributeReservedValue reservedValue2 = reservedValueBuilder.value("value2").build();
        TrackedEntityAttributeReservedValue reservedValue3 = reservedValueBuilder.value("value3").build();

        trackedEntityAttributeReservedValues.add(reservedValue1);
        trackedEntityAttributeReservedValues.add(reservedValue2);
        trackedEntityAttributeReservedValues.add(reservedValue3);

        OrganisationUnit organisationUnit = OrganisationUnit.builder()
                .uid(organisationUnitUid).code("org_unit_code").build();
        IdentifiableObjectStore<OrganisationUnit> organisationUnitStore =
                OrganisationUnitStore.create(databaseAdapter());
        organisationUnitStore.insert(organisationUnit);

        String pattern = "CURRENT_DATE(YYYYMM) + \"-\" + CURRENT_DATE(ww) + ORG_UNIT_CODE(...)";

        TrackedEntityAttributeStore trackedEntityAttributeStore =
                new TrackedEntityAttributeStoreImpl(databaseAdapter());
        trackedEntityAttributeStore.insert(ownerUid, null, null, null, null, null,
                null, null, null, null, pattern, null,
                null, null, null, null, null, null,
                null, null, null, null, null);

        CategoryCombo categoryCombo = CategoryCombo.builder().uid(categoryComboUid).build();
        database().insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo.toContentValues());

        Program program = Program.builder().uid(programUid).categoryCombo(categoryCombo)
                .access(Access.create(null, null, null, null, null,null,
                        DataAccess.create(true, true))).build();
        ProgramStore.create(databaseAdapter()).insert(program);

        ProgramTrackedEntityAttributeModel programTrackedEntityAttributeModel =
                ProgramTrackedEntityAttributeModel.builder()
                        .uid("ptea_uid").trackedEntityAttribute(ownerUid).program(programUid).build();
        ProgramTrackedEntityAttributeStore.create(databaseAdapter()).insert(programTrackedEntityAttributeModel);

        OrganisationUnitProgramLinkModel organisationUnitProgramLinkModel =
                OrganisationUnitProgramLinkModel.builder().organisationUnit(organisationUnitUid).program(programUid).build();
        OrganisationUnitProgramLinkStore.create(databaseAdapter()).insert(organisationUnitProgramLinkModel);

        handler.handleMany(trackedEntityAttributeReservedValues,
                new TrackedEntityAttributeReservedValueModelBuilder(organisationUnit, ""));
    }

    //@Test
    public void get_one_reserved_value() throws D2Error {
        assertThat(selectAll().size(), is(3));
        String value1 = d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(value1, is("value1"));
    }

    //@Test
    public void get_more_than_one_reserved_value() throws D2Error {
        String value1 = d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        String value2 = d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        String value3 = d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);

        assertThat(value1, is("value1"));
        assertThat(value2, is("value2"));
        assertThat(value3, is("value3"));
    }

    //@Test
    public void sync_reserved_values_for_one_tracked_entity_attribute() {
        d2.syncTrackedEntityAttributeReservedValues(ownerUid, null,100);
        assertThat(selectAll().size(), is(100));
    }

    //@Test
    public void sync_20_reserved_values_for_one_tracked_entity_attribute() {
        d2.syncTrackedEntityAttributeReservedValues(ownerUid, null,20);
        assertThat(selectAll().size(), is(20));
    }

    //@Test
    public void sync_100_reserved_values_when_not_number_of_values_to_reserve_is_specified() {
        d2.syncTrackedEntityAttributeReservedValues(ownerUid, null,null);
        assertThat(selectAll().size(), is(100));
    }

    //@Test
    public void sync_pop_sync_again_and_have_100_reserved_values_when_not_number_of_values_to_reserve_is_specified()
            throws D2Error {
        d2.syncTrackedEntityAttributeReservedValues(ownerUid, null, null);
        assertThat(selectAll().size(), is(100));
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(99));
        d2.syncTrackedEntityAttributeReservedValues(ownerUid, organisationUnitUid, null);
        assertThat(selectAll().size(), is(100));
    }

    //@Test
    public void sync_pop_sync_again_and_have_99_reserved_values_if_less_than_existing_values_are_requested()
            throws D2Error {
        d2.syncTrackedEntityAttributeReservedValues(ownerUid, null, 100);
        assertThat(selectAll().size(), is(100));
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(99));
        d2.syncTrackedEntityAttributeReservedValues(ownerUid, organisationUnitUid, 20);
        assertThat(selectAll().size(), is(99));
    }

    //@Test
    public void reserve_100_new_values_and_take_one() throws D2Error {
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(99));
    }

    //@Test
    public void have_98_values_after_sync_and_take_two() throws D2Error {
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(98));
    }

    //@Test
    public void sync_all_tracked_entity_instances() throws Exception {
        assertThat(selectAll().size(), is(3));
        d2.syncMetaData().call();
        d2.syncTrackedEntityAttributeReservedValues(null, null, null);

        /* 100 Reserved values by default * 2 TEA with generated property true on server = 200 */
        assertThat(selectAll().size(), is(200));
    }

    //@Test (expected = D2Error.class)
    public void return_d2_call_exception_if_no_valid_org_unit() throws D2Error {
        d2.popTrackedEntityAttributeReservedValue(ownerUid, "not_stored_organisation_unit_uid");
    }

    private List<TrackedEntityAttributeReservedValue> selectAll() {
        return store.selectAll();
    }

    private void login() {
        try {
            if (!d2.isUserLoggedIn().call()) {
                d2.logIn(RealServerMother.user, RealServerMother.password).call();
            }
        } catch (Exception ignored) {
        }
    }
}