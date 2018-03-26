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

package org.hisp.dhis.android.core.organisationunit;

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.program.Program;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganisationUnitModelBuilderShould {

    @Mock
    private OrganisationUnit parent;

    @Mock
    private OrganisationUnit grandparent;

    @Mock
    private OrganisationUnit pojo;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);


        pojo = OrganisationUnit.create(
                "uid",
                "code",
                "name",
                "displayName",
                new Date(),
                new Date(),
                "shortName",
                "displayShortName",
                "description",
                "displayDescription",
                parent,
                "path",
                new Date(),
                new Date(),
                3,
                new ArrayList<Program>(),
                new ArrayList<DataSet>(),
                Lists.newArrayList(grandparent, parent),
                false
        );

        when(parent.uid()).thenReturn("parentUid");
        when(parent.displayName()).thenReturn("parentDisplayName");
        when(grandparent.displayName()).thenReturn("grandparentDisplayName");
    }

    @Test
    public void copy_pojo_identifiable_properties() {
        OrganisationUnitModel model = new OrganisationUnitModelBuilder().buildModel(pojo);

        assertThat(model.uid()).isEqualTo(pojo.uid());
        assertThat(model.code()).isEqualTo(pojo.code());
        assertThat(model.name()).isEqualTo(pojo.name());
        assertThat(model.displayName()).isEqualTo(pojo.displayName());
        assertThat(model.created()).isEqualTo(pojo.created());
        assertThat(model.lastUpdated()).isEqualTo(pojo.lastUpdated());
    }

    @Test
    public void copy_pojo_nameable_properties() {
        OrganisationUnitModel model = new OrganisationUnitModelBuilder().buildModel(pojo);

        assertThat(model.shortName()).isEqualTo(pojo.shortName());
        assertThat(model.displayShortName()).isEqualTo(pojo.displayShortName());
        assertThat(model.description()).isEqualTo(pojo.description());
        assertThat(model.displayDescription()).isEqualTo(pojo.displayDescription());
    }

    @Test
    public void copy_pojo_organisation_unit_properties() {
        OrganisationUnitModel model = new OrganisationUnitModelBuilder().buildModel(pojo);

        assertThat(model.path()).isEqualTo(pojo.path());
        assertThat(model.openingDate()).isEqualTo(pojo.openingDate());
        assertThat(model.closedDate()).isEqualTo(pojo.closedDate());
        assertThat(model.level()).isEqualTo(pojo.level());
    }

    @Test
    public void copy_pojo_organisation_parent_uid() {
        OrganisationUnitModel model = new OrganisationUnitModelBuilder().buildModel(pojo);

        assertThat(model.parent()).isEqualTo(parent.uid());
    }

    @Test
    public void build_display_name_path_from_acestors() {
        OrganisationUnitModel model = new OrganisationUnitModelBuilder().buildModel(pojo);

        String expectedDisplayNamePath = "/" + grandparent.displayName() + "/" + parent.displayName() + "/" +
                pojo.displayName() + "/";
        assertThat(model.displayNamePath()).isEqualTo(expectedDisplayNamePath);
    }
}
