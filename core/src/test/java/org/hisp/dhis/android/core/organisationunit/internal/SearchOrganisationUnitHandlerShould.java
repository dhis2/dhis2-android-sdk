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
package org.hisp.dhis.android.core.organisationunit.internal;

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class SearchOrganisationUnitHandlerShould {

    @Mock
    private IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;

    @Mock
    private UserOrganisationUnitLinkStore userOrganisationUnitLinkHandler;

    private OrganisationUnit organisationUnit;

    private String organisationUnitUid = "orgUnitUid";

    private SearchOrganisationUnitHandler handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        organisationUnit = OrganisationUnit.builder().uid(organisationUnitUid).build();

        handler = new SearchOrganisationUnitHandlerImpl(
                organisationUnitStore,
                userOrganisationUnitLinkHandler);
    }

    @Test
    public void persist_organisation_unit_user_link() {
        String userUid = "userUid";
        UserOrganisationUnitLink userOrganisationUnitLink = UserOrganisationUnitLink.builder()
                .organisationUnit(organisationUnitUid)
                .user(userUid).root(false)
                .organisationUnitScope(OrganisationUnit.Scope.SCOPE_TEI_SEARCH.toString())
                .build();

        handler.setUser(User.builder().uid(userUid).build());
        handler.handleMany(Lists.newArrayList(organisationUnit), new OrganisationUnitDisplayPathTransformer());
        verify(userOrganisationUnitLinkHandler).insert(userOrganisationUnitLink);
    }
}