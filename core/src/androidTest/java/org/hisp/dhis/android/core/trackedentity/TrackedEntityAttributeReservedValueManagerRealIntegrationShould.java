/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.FUTURE_DATE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboTableInfo;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitProgramLinkStore;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.internal.ProgramStore;
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueQuery;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueStoreInterface;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TrackedEntityAttributeReservedValueManagerRealIntegrationShould extends BaseRealIntegrationTest {

    private TrackedEntityAttributeReservedValueStoreInterface store;
    private String organisationUnitUid = "org_unit_uid";
    private String programUid = "program_uid";
    private String categoryComboUid = "category_combo_uid";
    private String ownerUid = "xs8A6tQJY0s";
    private OrganisationUnit organisationUnit;
    private String pattern;

    @Mock
    QueryCallFactory<TrackedEntityAttributeReservedValue,
            TrackedEntityAttributeReservedValueQuery> trackedEntityAttributeReservedValueQueryCallFactory;
    @Mock
    Callable<List<TrackedEntityAttributeReservedValue>> trackedEntityAttributeReservedValueCall;

    @Captor
    private ArgumentCaptor<TrackedEntityAttributeReservedValueQuery> trackedEntityAttributeReservedValueQueryCaptor;

    private TrackedEntityAttributeReservedValueManager manager;

    @Before
    public void setUp() {
        super.setUp();

        login();

        store = TrackedEntityAttributeReservedValueStore.create(d2.databaseAdapter());

        IdentifiableObjectStore<OrganisationUnit> organisationUnitStore =
                OrganisationUnitStore.create(d2.databaseAdapter());

        IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore =
                TrackedEntityAttributeStore.create(d2.databaseAdapter());

        manager = d2.trackedEntityModule().reservedValueManager();

        Handler<TrackedEntityAttributeReservedValue> handler = new ObjectWithoutUidHandlerImpl<>(store);

        List<TrackedEntityAttributeReservedValue> trackedEntityAttributeReservedValues = new ArrayList<>();

        TrackedEntityAttributeReservedValue.Builder reservedValueBuilder =
                TrackedEntityAttributeReservedValue.builder()
                        .ownerObject("owner_obj")
                        .ownerUid(ownerUid)
                        .key("key")
                        .created(CREATED)
                        .expiryDate(FUTURE_DATE)
                        .organisationUnit(organisationUnitUid);

        TrackedEntityAttributeReservedValue reservedValue1 = reservedValueBuilder.value("value1").build();
        TrackedEntityAttributeReservedValue reservedValue2 = reservedValueBuilder.value("value2").build();
        TrackedEntityAttributeReservedValue reservedValue3 = reservedValueBuilder.value("value3").build();

        trackedEntityAttributeReservedValues.add(reservedValue1);
        trackedEntityAttributeReservedValues.add(reservedValue2);
        trackedEntityAttributeReservedValues.add(reservedValue3);

        organisationUnit = OrganisationUnit.builder().uid(organisationUnitUid).code("org_unit_code").build();
        organisationUnitStore.insert(organisationUnit);

        pattern = "CURRENT_DATE(YYYYMM) + \"-\" + CURRENT_DATE(ww) + ORG_UNIT_CODE(...)";

        trackedEntityAttributeStore.updateOrInsert(TrackedEntityAttribute.builder().uid(ownerUid).pattern(pattern).build());

        CategoryCombo categoryCombo = CategoryCombo.builder().uid(categoryComboUid).build();
        d2.databaseAdapter().insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo.toContentValues());

        Program program = Program.builder().uid(programUid).categoryCombo(ObjectWithUid.create(categoryCombo.uid()))
                .access(Access.create(null, null, DataAccess.create(true, true))).build();
        ProgramStore.create(d2.databaseAdapter()).insert(program);

        ProgramTrackedEntityAttribute programTrackedEntityAttribute =
                ProgramTrackedEntityAttribute.builder()
                        .uid("ptea_uid")
                        .trackedEntityAttribute(ObjectWithUid.create(ownerUid))
                        .program(ObjectWithUid.create(programUid))
                        .build();
        ProgramTrackedEntityAttributeStore.create(d2.databaseAdapter()).insert(programTrackedEntityAttribute);

        OrganisationUnitProgramLink organisationUnitProgramLink =
                OrganisationUnitProgramLink.builder().organisationUnit(organisationUnitUid).program(programUid).build();
        OrganisationUnitProgramLinkStore.create(d2.databaseAdapter()).insert(organisationUnitProgramLink);


        when(trackedEntityAttributeReservedValueQueryCallFactory.create(
                any(TrackedEntityAttributeReservedValueQuery.class)))
                .thenReturn(trackedEntityAttributeReservedValueCall);


        handler.handleMany(trackedEntityAttributeReservedValues);
    }

//    @Test
    public void get_one_reserved_value() throws D2Error {

        assertThat(selectAll().size()).isEqualTo(3);

        String value1 = d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);

        assertThat(value1).isEqualTo("value1");
    }

//    @Test
    public void get_more_than_one_reserved_value() throws D2Error {
        String value1 = d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);
        String value2 = d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);
        String value3 = d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);

        assertThat(value1).isEqualTo("value1");
        assertThat(value2).isEqualTo("value2");
        assertThat(value3).isEqualTo("value3");
    }

//    @Test
    public void sync_reserved_values_for_one_tracked_entity_attribute() {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, 100);
        assertThat(selectAll().size()).isEqualTo(100);
    }

//    @Test
    public void sync_20_reserved_values_for_one_tracked_entity_attribute() {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, 20);
        assertThat(selectAll().size()).isEqualTo(20);
    }

//    @Test
    public void sync_100_reserved_values_when_not_number_of_values_to_reserve_is_specified() {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, null);
        assertThat(selectAll().size()).isEqualTo(100);
    }

//    @Test
    public void sync_pop_sync_again_and_have_99_reserved_values_when_not_number_of_values_to_reserve_is_specified()
            throws D2Error {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, null);
        assertThat(selectAll().size()).isEqualTo(100);
        d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size()).isEqualTo(99);
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, null);
        assertThat(selectAll().size()).isEqualTo(99);
    }

//    @Test
    public void fill_up_to_100_values_if_db_does_not_have_at_least_50_values_when_not_number_of_values_to_reserve_is_specified()
            throws D2Error {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, 50);
        assertThat(selectAll().size()).isEqualTo(50);
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, null);
        assertThat(selectAll().size()).isEqualTo(50);
        d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size()).isEqualTo(49);
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, null);
        assertThat(selectAll().size()).isEqualTo(100);
    }

//    @Test
    public void sync_pop_sync_again_and_have_99_reserved_values_if_less_than_existing_values_are_requested()
            throws D2Error {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, 100);
        assertThat(selectAll().size()).isEqualTo(100);
        d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size()).isEqualTo(99);
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, 20);
        assertThat(selectAll().size()).isEqualTo(99);
    }

//    @Test
    public void reserve_100_new_values_and_take_one() throws D2Error {
        d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size()).isEqualTo(99);
    }

//    @Test
    public void have_98_values_after_sync_and_take_two() throws D2Error {
        d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);
        d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size()).isEqualTo(98);
    }

//    @Test
    public void sync_all_tracked_entity_instances() throws Exception {
        assertThat(selectAll().size()).isEqualTo(3);
        d2.metadataModule().blockingDownload();
        d2.trackedEntityModule().reservedValueManager().blockingDownloadAllReservedValues(null);

        /* 100 Reserved values by default * 2 TEA with generated property true on server = 200 */
        assertThat(selectAll().size()).isEqualTo(200);
    }

//    @Test
    public void create_the_right_query_when_nothing_is_passed() {
        manager.blockingDownloadAllReservedValues(null);
        assertQueryIsCreatedRight(97);
    }

//    @Test
    public void create_the_right_query_when_only_an_attribute_is_passed() {
        manager.blockingDownloadReservedValues(ownerUid, null);
        assertQueryIsCreatedRight(97);
    }

//    @Test
    public void create_the_right_query_when_only_a_organisation_unit_is_passed() {
        manager.blockingDownloadAllReservedValues(null);
        assertQueryIsCreatedRight(97);
    }

//    @Test
    public void create_the_right_query_when_an_attribute_and_a_organisation_unit_is_passed() {
        manager.blockingDownloadReservedValues(ownerUid, null);
        assertQueryIsCreatedRight(97);
    }

//    @Test
    public void create_the_right_query_when_a_number_of_values_to_fill_up_is_passed() {
        manager.blockingDownloadReservedValues(null, 20);
        assertQueryIsCreatedRight(17);
    }


//    @Test
    public void create_the_right_query_when_a_number_of_values_to_fill_up_and_an_attribute_is_passed() {
        manager.blockingDownloadReservedValues(ownerUid, 20);
        assertQueryIsCreatedRight(17);
    }

//    @Test
    public void create_the_right_query_when_a_number_of_values_to_fill_up_and_a_organisation_unit_is_passed() {
        manager.blockingDownloadAllReservedValues(20);
        assertQueryIsCreatedRight(17);
    }

//    @Test
    public void create_the_right_query_when_all_arguments_are_passed() {
        manager.blockingDownloadReservedValues(ownerUid, 20);
        assertQueryIsCreatedRight(17);
    }

//    @Test (expected = D2Error.class)
    public void return_d2_call_exception_if_no_valid_org_unit() throws D2Error {
        d2.trackedEntityModule().reservedValueManager().blockingGetValue(ownerUid, "not_stored_organisation_unit_uid");
    }

    private List<TrackedEntityAttributeReservedValue> selectAll() {
        return store.selectAll();
    }

    private void login() {
        try {
            if (!d2.userModule().isLogged().blockingGet()) {
                d2.userModule().logIn(username, password, url).blockingGet();
            }
        } catch (Exception ignored) {
        }
    }

    /*
     * This method stopped working because QueryCallFactory mock instance differs from Dagger's injected one,
     * so the code is calling .create() on Dagger's instance and .verify() is trying to catch the call from Mockito's instace.
     */
    private void assertQueryIsCreatedRight(Integer numberOfValuesExpected) {
        verify(trackedEntityAttributeReservedValueQueryCallFactory).create(trackedEntityAttributeReservedValueQueryCaptor.capture());

        TrackedEntityAttributeReservedValueQuery query = trackedEntityAttributeReservedValueQueryCaptor.getValue();
        assertThat(query.organisationUnit().uid()).isEqualTo(organisationUnit.uid());
        assertThat(query.numberToReserve()).isEqualTo(numberOfValuesExpected); // values expected - 3 that it had before.
        assertThat(query.trackedEntityAttributePattern()).isEqualTo(pattern);
        assertThat(query.trackedEntityAttributeUid()).isEqualTo(ownerUid);
    }

}
