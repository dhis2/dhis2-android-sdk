/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.testapp.organisationunit;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitCollectionRepositoryMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    @Test
    public void find_all() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits.get();
        assertThat(organisationUnits.size(), is(1));
    }

    @Test
    public void filter_by_parent_uid() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits
                .byParentUid().eq("YuQRtpLP10I").get();
        assertThat(organisationUnits.size(), is(1));
    }

    @Test
    public void filter_by_path() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits
                .byPath().eq("/ImspTQPwCqd/O6uvpzGd5pu/YuQRtpLP10I/DiszpKrYNg8").get();
        assertThat(organisationUnits.size(), is(1));
    }

    @Test
    public void filter_by_opening_date() throws ParseException {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits
                .byOpeningDate().eq(BaseIdentifiableObject.parseDate("1970-01-01T00:00:00.000")).get();
        assertThat(organisationUnits.size(), is(1));
    }

    @Test
    public void filter_by_closed_date() throws ParseException {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits
                .byClosedDate().eq(BaseIdentifiableObject.parseDate("2018-05-22T15:21:48.516")).get();
        assertThat(organisationUnits.size(), is(1));
    }

    @Test
    public void filter_by_level() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits
                .byLevel().eq(4).get();
        assertThat(organisationUnits.size(), is(1));
    }

    @Test
    public void filter_by_display_name_path() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits
                .byDisplayNamePath().eq("/Ngelehun CHC").get();
        assertThat(organisationUnits.size(), is(1));
    }

    @Test
    public void include_programs_as_children() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .withPrograms().one().get();
        assertThat(organisationUnit.programs().get(0).name(), is("Antenatal care visit"));
    }

    @Test
    public void include_data_sets_as_children() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .withDataSets().one().get();
        assertThat(organisationUnit.dataSets().get(0).name(), is("ART monthly summary"));
    }

    @Test
    public void include_organisation_unit_groups_as_children() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .withOrganisationUnitGroups().one().get();
        assertThat(organisationUnit.organisationUnitGroups().get(0).name(), is("CHC"));
    }

    @Test
    public void include_programs_as_children_in_collection_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .withAllChildren().get().get(0);
        assertThat(organisationUnit.programs().get(0).name(), is("Antenatal care visit"));
    }

    @Test
    public void include_data_sets_as_children_in_collection_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .withAllChildren().get().get(0);
        assertThat(organisationUnit.dataSets().get(0).name(), is("ART monthly summary"));
    }

    @Test
    public void include_organisation_unit_groups_as_children_in_collection_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .withAllChildren().get().get(0);
        assertThat(organisationUnit.organisationUnitGroups().get(0).name(), is("CHC"));
    }

    @Test
    public void include_programs_as_children_in_object_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .one().withAllChildren().get();
        assertThat(organisationUnit.programs().get(0).name(), is("Antenatal care visit"));
    }

    @Test
    public void include_data_sets_as_children_in_object_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .one().withAllChildren().get();
        assertThat(organisationUnit.dataSets().get(0).name(), is("ART monthly summary"));
    }

    @Test
    public void include_organisation_unit_groups_as_children_in_object_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits
                .one().withAllChildren().get();
        assertThat(organisationUnit.organisationUnitGroups().get(0).name(), is("CHC"));
    }
}