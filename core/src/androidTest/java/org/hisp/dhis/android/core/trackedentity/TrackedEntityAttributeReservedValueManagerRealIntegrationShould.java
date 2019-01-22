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

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.factories.QueryCallFactory;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.FUTURE_DATE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeReservedValueManagerRealIntegrationShould extends AbsStoreTestCase {

    private TrackedEntityAttributeReservedValueStoreInterface store;
    private String organisationUnitUid = "org_unit_uid";
    private String programUid = "program_uid";
    private String categoryComboUid = "category_combo_uid";
    private String ownerUid = "xs8A6tQJY0s";
    private D2 d2;
    private OrganisationUnit organisationUnit;
    private String pattern;

    @Mock
    private QueryCallFactory<TrackedEntityAttributeReservedValue,
            TrackedEntityAttributeReservedValueQuery> trackedEntityAttributeReservedValueQueryCallFactory;
    @Mock
    private Call<List<TrackedEntityAttributeReservedValue>> trackedEntityAttributeReservedValueCall;

    @Captor
    private ArgumentCaptor<TrackedEntityAttributeReservedValueQuery> trackedEntityAttributeReservedValueQueryCaptor;

    private TrackedEntityAttributeReservedValueManager manager;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
        login();

        store = TrackedEntityAttributeReservedValueStore.create(databaseAdapter());
        IdentifiableObjectStore<OrganisationUnit> organisationUnitStore =
                OrganisationUnitStore.create(databaseAdapter());
        TrackedEntityAttributeStore trackedEntityAttributeStore =
                new TrackedEntityAttributeStoreImpl(databaseAdapter());

        manager = d2.trackedEntityModule().reservedValueManager;


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

        organisationUnit = OrganisationUnit.builder().uid(organisationUnitUid).code("org_unit_code").build();

        organisationUnitStore.insert(organisationUnit);

        pattern = "CURRENT_DATE(YYYYMM) + \"-\" + CURRENT_DATE(ww) + ORG_UNIT_CODE(...)";

        trackedEntityAttributeStore.insert(ownerUid, null, null, null, null, null,
                null, null, null, null, pattern, null,
                null, null, null, null, null, null,
                true, null, null, null, null);

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

        when(trackedEntityAttributeReservedValueQueryCallFactory.create(
                any(TrackedEntityAttributeReservedValueQuery.class)))
                .thenReturn(trackedEntityAttributeReservedValueCall);

        handler.handleMany(trackedEntityAttributeReservedValues,
                new TrackedEntityAttributeReservedValueModelBuilder(organisationUnit, ""));
    }

    @Test
    public void get_one_reserved_value() throws D2Error {

        assertThat(selectAll().size(), is(3));

        String value1 = d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);

        assertThat(value1, is("value1"));
    }

    @Test
    public void get_more_than_one_reserved_value() throws D2Error {
        String value1 = d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);
        String value2 = d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);
        String value3 = d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);

        assertThat(value1, is("value1"));
        assertThat(value2, is("value2"));
        assertThat(value3, is("value3"));
    }

    @Test
    public void sync_reserved_values_for_one_tracked_entity_attribute() {
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, null,100);
        assertThat(selectAll().size(), is(100));
    }

    @Test
    public void sync_20_reserved_values_for_one_tracked_entity_attribute() {
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, null,20);
        assertThat(selectAll().size(), is(20));
    }

    @Test
    public void sync_100_reserved_values_when_not_number_of_values_to_reserve_is_specified() {
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, null,null);
        assertThat(selectAll().size(), is(100));
    }

    @Test
    public void sync_pop_sync_again_and_have_99_reserved_values_when_not_number_of_values_to_reserve_is_specified()
            throws D2Error {
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, null,null);
        assertThat(selectAll().size(), is(100));
        d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(99));
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, organisationUnitUid,null);
        assertThat(selectAll().size(), is(99));
    }

    @Test
    public void fill_up_to_100_values_if_db_does_not_have_at_least_50_values_when_not_number_of_values_to_reserve_is_specified()
            throws D2Error {
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, null,50);
        assertThat(selectAll().size(), is(50));
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, organisationUnitUid,null);
        assertThat(selectAll().size(), is(50));
        d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(49));
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, organisationUnitUid,null);
        assertThat(selectAll().size(), is(100));
    }

    @Test
    public void sync_pop_sync_again_and_have_99_reserved_values_if_less_than_existing_values_are_requested()
            throws D2Error {
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, null,100);
        assertThat(selectAll().size(), is(100));
        d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(99));
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(ownerUid, organisationUnitUid,20);
        assertThat(selectAll().size(), is(99));
    }

    @Test
    public void reserve_100_new_values_and_take_one() throws D2Error {
        d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(99));
    }

    @Test
    public void have_98_values_after_sync_and_take_two() throws D2Error {
        d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);
        d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(98));
    }

//    @Test
    public void sync_all_tracked_entity_instances() throws Exception {
        assertThat(selectAll().size(), is(3));
        d2.syncMetaData().call();
        d2.trackedEntityModule().reservedValueManager.syncReservedValues(null, null,null);

        /* 100 Reserved values by default * 2 TEA with generated property true on server = 200 */
        assertThat(selectAll().size(), is(200));
    }

//    @Test
    public void create_the_right_query_when_nothing_is_passed() {
        manager.syncReservedValues(null, null, null);
        assertQueryIsCreatedRight(97);
    }

//    @Test
    public void create_the_right_query_when_only_an_attribute_is_passed() {
        manager.syncReservedValues(ownerUid, null, null);
        assertQueryIsCreatedRight(97);
    }

//    @Test
    public void create_the_right_query_when_only_a_organisation_unit_is_passed() {
        manager.syncReservedValues(null, organisationUnit.uid(), null);
        assertQueryIsCreatedRight(97);
    }

//    @Test
    public void create_the_right_query_when_an_attribute_and_a_organisation_unit_is_passed() {
        manager.syncReservedValues(ownerUid, organisationUnitUid, null);
        assertQueryIsCreatedRight(97);
    }

//    @Test
    public void create_the_right_query_when_a_number_of_values_to_fill_up_is_passed() {
        manager.syncReservedValues(null, null, 20);
        assertQueryIsCreatedRight(17);
    }

//    @Test
    public void create_the_right_query_when_a_number_of_values_to_fill_up_and_an_attribute_is_passed() {
        manager.syncReservedValues(ownerUid, null, 20);
        assertQueryIsCreatedRight(17);
    }

//    @Test
    public void create_the_right_query_when_a_number_of_values_to_fill_up_and_a_organisation_unit_is_passed() {
        manager.syncReservedValues(null, organisationUnitUid, 20);
        assertQueryIsCreatedRight(17);
    }

//    @Test
    public void create_the_right_query_when_all_arguments_are_passed() {
        manager.syncReservedValues(ownerUid, organisationUnitUid, 20);
        assertQueryIsCreatedRight(17);
    }

    @Test (expected = D2Error.class)
    public void return_d2_call_exception_if_no_valid_org_unit() throws D2Error {
        d2.trackedEntityModule().reservedValueManager.getValue(ownerUid, "not_stored_organisation_unit_uid");
    }

    private List<TrackedEntityAttributeReservedValue> selectAll() {
        return store.selectAll();
    }

    private void login() {
        try {
            if (!d2.userModule().isLogged().call()) {
                d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();
            }
        } catch (Exception ignored) {
        }
    }

    private void assertQueryIsCreatedRight(Integer numberOfValuesExpected) {
        verify(trackedEntityAttributeReservedValueQueryCallFactory).create(trackedEntityAttributeReservedValueQueryCaptor.capture());

        TrackedEntityAttributeReservedValueQuery query = trackedEntityAttributeReservedValueQueryCaptor.getValue();
        assertThat(query.organisationUnit().uid(), is(organisationUnit.uid()));
        assertThat(query.numberToReserve(), is(numberOfValuesExpected)); // values expected - 3 that it had before.
        assertThat(query.trackedEntityAttributePattern(), is(pattern));
        assertThat(query.trackedEntityAttributeUid(), is(ownerUid));
    }
}