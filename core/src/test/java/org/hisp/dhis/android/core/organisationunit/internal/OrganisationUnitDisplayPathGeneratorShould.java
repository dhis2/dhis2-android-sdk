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

package org.hisp.dhis.android.core.organisationunit.internal;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitInternalAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganisationUnitDisplayPathGeneratorShould {

    @Mock
    private OrganisationUnit grandparent;

    @Mock
    private OrganisationUnit parent;

    @Mock
    private OrganisationUnit organisationUnit;

    private static final String GRANDPARENT_DISPLAY_NAME = "grandparentDisplayName";
    private static final String PARENT_DISPLAY_NAME = "parentDisplayName";
    private static final String ORG_UNIT_DISPLAY_NAME = "orgUnitDisplayName";

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(OrganisationUnitInternalAccessor.accessAncestors(organisationUnit))
                .thenReturn(Lists.newArrayList(grandparent, parent));
        when(parent.uid()).thenReturn("parentUid");

        when(grandparent.displayName()).thenReturn(GRANDPARENT_DISPLAY_NAME);
        when(parent.displayName()).thenReturn(PARENT_DISPLAY_NAME);
        when(organisationUnit.displayName()).thenReturn(ORG_UNIT_DISPLAY_NAME);
    }

    @Test
    public void build_display_name_path_from_ancestors() {
        assertThat(OrganisationUnitDisplayPathGenerator.generateDisplayPath(organisationUnit))
                .isEqualTo(Lists.newArrayList(GRANDPARENT_DISPLAY_NAME, PARENT_DISPLAY_NAME, ORG_UNIT_DISPLAY_NAME));
    }
}
