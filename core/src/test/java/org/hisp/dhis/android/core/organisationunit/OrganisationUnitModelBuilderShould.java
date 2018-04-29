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
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.NameableModelBuilderAbstractShould;
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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CODE;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DISPLAY_NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.UID;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganisationUnitModelBuilderShould extends NameableModelBuilderAbstractShould<OrganisationUnit,
        OrganisationUnitModel> {

    @Mock
    private OrganisationUnit parent;

    @Mock
    private OrganisationUnit grandparent;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        when(parent.uid()).thenReturn("parentUid");
        when(parent.displayName()).thenReturn("parentDisplayName");
        when(grandparent.displayName()).thenReturn("grandparentDisplayName");
    }

    @Override
    protected OrganisationUnit buildPojo() {
        return OrganisationUnit.create(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                CREATED,
                LAST_UPDATED,
                "shortName",
                "displayShortName",
                "description",
                "displayDescription",
                parent,
                "path",
                CREATED,
                CREATED,
                3,
                new ArrayList<Program>(),
                new ArrayList<DataSet>(),
                Lists.newArrayList(grandparent, parent),
                false
        );
    }

    @Override
    protected ModelBuilder<OrganisationUnit, OrganisationUnitModel> modelBuilder() {
        return new OrganisationUnitModelBuilder();
    }

    @Test
    public void copy_pojo_organisation_unit_properties() {
        assertThat(model.path()).isEqualTo(pojo.path());
        assertThat(model.openingDate()).isEqualTo(pojo.openingDate());
        assertThat(model.closedDate()).isEqualTo(pojo.closedDate());
        assertThat(model.level()).isEqualTo(pojo.level());
    }

    @Test
    public void copy_pojo_organisation_parent_uid() {
        assertThat(model.parent()).isEqualTo(parent.uid());
    }

    @Test
    public void build_display_name_path_from_ancestors() {
        String expectedDisplayNamePath = "/" + grandparent.displayName() + "/" + parent.displayName() + "/" +
                pojo.displayName();
        assertThat(model.displayNamePath()).isEqualTo(expectedDisplayNamePath);
    }
}
