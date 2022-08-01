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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.junit.Test

class AnalyticsOrganisationUnitHelperShould {

    private val userOrganisationUnitStore: UserOrganisationUnitLinkStore = mock()
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit> = mock()
    private val organisationUnitLevelStore: IdentifiableObjectStore<OrganisationUnitLevel> = mock()
    private val organisationUnitOrganisationUnitGroupLinkStore: LinkStore<OrganisationUnitOrganisationUnitGroupLink> =
        mock()

    private val helper = AnalyticsOrganisationUnitHelper(
        userOrganisationUnitStore,
        organisationUnitStore,
        organisationUnitLevelStore,
        organisationUnitOrganisationUnitGroupLinkStore
    )

    @Test
    fun `Should get relative organisation unit uids`() {
        val unorderedList = listOf("3", "2", "1")
        val orderedList = listOf("1", "2", "3")

        whenever(
            userOrganisationUnitStore
                .queryAssignedOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        ) doReturn unorderedList

        whenever(
            organisationUnitStore.selectUidsWhere(any(), any())
        ) doReturn orderedList

        val relativeUids = helper.getRelativeOrganisationUnitUids(RelativeOrganisationUnit.USER_ORGUNIT)

        assertThat(relativeUids).isEqualTo(orderedList)
    }
}
