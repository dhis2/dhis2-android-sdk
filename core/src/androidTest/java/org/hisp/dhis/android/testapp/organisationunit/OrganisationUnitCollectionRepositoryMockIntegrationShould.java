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

package org.hisp.dhis.android.testapp.organisationunit;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class OrganisationUnitCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits().blockingGet();
        assertThat(organisationUnits.size()).isEqualTo(3);
    }

    @Test
    public void filter_by_parent_uid() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits()
                .byParentUid().eq("YuQRtpLP10I").blockingGet();
        assertThat(organisationUnits.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_path() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits()
                .byPath().eq("/ImspTQPwCqd/O6uvpzGd5pu/YuQRtpLP10I/DiszpKrYNg8").blockingGet();
        assertThat(organisationUnits.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_opening_date() throws ParseException {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits()
                .byOpeningDate().eq(BaseIdentifiableObject.parseDate("1970-01-01T00:00:00.000")).blockingGet();
        assertThat(organisationUnits.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_closed_date() throws ParseException {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits()
                .byClosedDate().eq(BaseIdentifiableObject.parseDate("2018-05-22T15:21:48.516")).blockingGet();
        assertThat(organisationUnits.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_level() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits()
                .byLevel().eq(4).blockingGet();
        assertThat(organisationUnits.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_organisation_unit_scope() {
        List<OrganisationUnit> captureOrganisationUnits = d2.organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .blockingGet();
        assertThat(captureOrganisationUnits.size()).isEqualTo(3);

        List<OrganisationUnit> searchOrganisationUnits = d2.organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
                .blockingGet();
        assertThat(searchOrganisationUnits.size()).isEqualTo(0);
    }

    @Test
    public void filter_by_root_organisation_unit() {
        List<OrganisationUnit> rootOrganisationUnits = d2.organisationUnitModule().organisationUnits()
                .byRootOrganisationUnit(Boolean.TRUE)
                .blockingGet();
        assertThat(rootOrganisationUnits.size()).isEqualTo(1);

        List<OrganisationUnit> notRootOrganisationUnits = d2.organisationUnitModule().organisationUnits()
                .byRootOrganisationUnit(Boolean.FALSE)
                .blockingGet();
        assertThat(notRootOrganisationUnits.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_program() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits()
                .byProgramUids(Collections.singletonList("lxAQ7Zs9VYR"))
                .blockingGet();
        assertThat(organisationUnits.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_data_Set() {
        List<OrganisationUnit> organisationUnits = d2.organisationUnitModule().organisationUnits()
                .byDataSetUids(Collections.singletonList("lyLU2wR22tC"))
                .blockingGet();
        assertThat(organisationUnits.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_root_capture_organisation_unit() {
        List<OrganisationUnit> rootOrganisationUnits = d2.organisationUnitModule().organisationUnits()
                .byRootOrganisationUnit(Boolean.TRUE)
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .blockingGet();
        assertThat(rootOrganisationUnits.size()).isEqualTo(1);
    }

    @Test
    public void include_programs_as_children() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq("DiszpKrYNg8")
                .withProgramUids().one().blockingGet();
        assertThat(organisationUnit.programs().size()).isEqualTo(2);
        for (ObjectWithUid program : organisationUnit.programs()) {
            assertThat(program.uid()).isNotNull();
        }
    }

    @Test
    public void include_data_sets_as_children() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq("DiszpKrYNg8")
                .withDataSetUids().one().blockingGet();
        assertThat(organisationUnit.dataSets().size()).isEqualTo(2);
        for (ObjectWithUid dataSet : organisationUnit.dataSets()) {
            assertThat(dataSet.uid()).isNotNull();
        }
    }

    @Test
    public void include_organisation_unit_groups_as_children() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq("DiszpKrYNg8")
                .withOrganisationUnitGroups().one().blockingGet();
        assertThat(organisationUnit.organisationUnitGroups().get(0).name()).isEqualTo("CHC");
    }

    @Test
    public void include_programs_as_children_in_collection_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .withProgramUids().blockingGet().get(1);
        assertThat(organisationUnit.programs().size()).isEqualTo(2);
        for (ObjectWithUid program : organisationUnit.programs()) {
            assertThat(program.uid()).isNotNull();
        }
    }

    @Test
    public void include_data_sets_as_children_in_collection_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .withDataSetUids().blockingGet().get(1);
        assertThat(organisationUnit.dataSets().size()).isEqualTo(2);
        for (ObjectWithUid dataSet : organisationUnit.dataSets()) {
            assertThat(dataSet.uid()).isNotNull();
        }
    }

    @Test
    public void include_organisation_unit_groups_as_children_in_collection_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .withOrganisationUnitGroups().blockingGet().get(1);
        assertThat(organisationUnit.organisationUnitGroups().get(0).name()).isEqualTo("CHC");
    }

    @Test
    public void include_programs_as_children_in_object_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq("DiszpKrYNg8")
                .withProgramUids().one().blockingGet();
        assertThat(organisationUnit.programs().size()).isEqualTo(2);
        for (ObjectWithUid program : organisationUnit.programs()) {
            assertThat(program.uid()).isNotNull();
        }
    }

    @Test
    public void include_data_sets_as_children_in_object_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq("DiszpKrYNg8")
                .withDataSetUids().one().blockingGet();
        assertThat(organisationUnit.dataSets().size()).isEqualTo(2);
        for (ObjectWithUid dataSet : organisationUnit.dataSets()) {
            assertThat(dataSet.uid()).isNotNull();
        }
    }

    @Test
    public void include_organisation_unit_groups_as_children_in_object_repository_when_all_selected() {
        OrganisationUnit organisationUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq("DiszpKrYNg8")
                .withOrganisationUnitGroups().one().blockingGet();
        assertThat(organisationUnit.organisationUnitGroups().get(0).name()).isEqualTo("CHC");
    }
}