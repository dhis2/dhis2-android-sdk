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
package org.hisp.dhis.android.core.user.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.data.database.LinkStoreAbstractIntegrationShould
import org.hisp.dhis.android.core.data.user.UserOrganisationUnitLinkSamples
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStoreImpl.Companion.create
import org.hisp.dhis.android.core.utils.integration.mock.TestDatabaseAdapterFactory
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class UserOrganisationUnitLinkStoreIntegrationShould : LinkStoreAbstractIntegrationShould<UserOrganisationUnitLink>(
    create(TestDatabaseAdapterFactory.get()),
    UserOrganisationUnitLinkTableInfo.TABLE_INFO, TestDatabaseAdapterFactory.get()
) {
    private var linkStore: UserOrganisationUnitLinkStore = store as UserOrganisationUnitLinkStore

    override fun addMasterUid(): String {
        return UserOrganisationUnitLinkSamples.getUserOrganisationUnitLink().organisationUnitScope()
    }

    override fun buildObject(): UserOrganisationUnitLink {
        return UserOrganisationUnitLinkSamples.getUserOrganisationUnitLink()
    }

    override fun buildObjectWithOtherMasterUid(): UserOrganisationUnitLink {
        return buildObject().toBuilder()
            .organisationUnitScope("other-scope")
            .build()
    }

    @Test
    fun assignedOrgUnitForDataCapture() {
        linkStore.insert(UserOrganisationUnitLinkSamples.getUserOrganisationUnitLink())
        linkStore.insert(
            UserOrganisationUnitLinkSamples
                .getAssignedUserOrganisationUnitLink(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        )
        linkStore.insert(
            UserOrganisationUnitLinkSamples
                .getUnassignedUserOrganisationUnitLink(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        )
        val orgUnitUids = linkStore
            .queryAssignedOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        assertThat(orgUnitUids.size).isEqualTo(2)
    }

    @Test
    fun assignedOrgUnitForTEISearch() {
        linkStore.insert(UserOrganisationUnitLinkSamples.getUserOrganisationUnitLink())
        linkStore.insert(
            UserOrganisationUnitLinkSamples
                .getAssignedUserOrganisationUnitLink(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
        )
        linkStore.insert(
            UserOrganisationUnitLinkSamples
                .getUnassignedUserOrganisationUnitLink(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
        )

        val orgUnitUids = linkStore.queryAssignedOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)

        assertThat(orgUnitUids.size).isEqualTo(1)
    }
}
