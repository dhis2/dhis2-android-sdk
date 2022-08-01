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
package org.hisp.dhis.android.core.organisationunit

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class OrganisationUnitServiceShould {

    private val organisationUnitUid: String = "organisationUnitUid"

    private val organisationUnit: OrganisationUnit = mock()

    private val organisationUnitRepository: OrganisationUnitCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)

    private val organisationUnitService = OrganisationUnitService(organisationUnitRepository)

    // Dates
    private val firstJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
    private val secondJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-02T00:00:00.000")
    private val thirdJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-03T00:00:00.000")

    @Before
    fun setUp() {
        whenever(organisationUnitRepository.uid(organisationUnitUid).blockingGet()) doReturn organisationUnit

        whenever(organisationUnit.uid()) doReturn organisationUnitUid
    }

    @Test
    fun `Should return true if date within orgunit range`() {
        whenever(organisationUnit.openingDate()) doReturn null
        whenever(organisationUnit.closedDate()) doReturn null
        assertTrue(organisationUnitService.blockingIsDateInOrgunitRange(organisationUnitUid, secondJanuary))

        whenever(organisationUnit.openingDate()) doReturn firstJanuary
        whenever(organisationUnit.closedDate()) doReturn null
        assertTrue(organisationUnitService.blockingIsDateInOrgunitRange(organisationUnitUid, secondJanuary))

        whenever(organisationUnit.openingDate()) doReturn null
        whenever(organisationUnit.closedDate()) doReturn thirdJanuary
        assertTrue(organisationUnitService.blockingIsDateInOrgunitRange(organisationUnitUid, secondJanuary))

        whenever(organisationUnit.openingDate()) doReturn firstJanuary
        whenever(organisationUnit.closedDate()) doReturn thirdJanuary
        assertTrue(organisationUnitService.blockingIsDateInOrgunitRange(organisationUnitUid, secondJanuary))
    }

    @Test
    fun `Should return false if date out of orgunit range`() {
        whenever(organisationUnit.openingDate()) doReturn thirdJanuary
        whenever(organisationUnit.closedDate()) doReturn null
        assertFalse(organisationUnitService.blockingIsDateInOrgunitRange(organisationUnitUid, secondJanuary))

        whenever(organisationUnit.openingDate()) doReturn null
        whenever(organisationUnit.closedDate()) doReturn firstJanuary
        assertFalse(organisationUnitService.blockingIsDateInOrgunitRange(organisationUnitUid, secondJanuary))
    }
}
