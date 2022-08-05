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
package org.hisp.dhis.android.core.organisationunit.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Single
import java.io.IOException
import java.util.*
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Filter
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserInternalAccessor
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OrganisationUnitCallUnitShould {
    private val organisationUnitPayload: Payload<OrganisationUnit> = mock()
    private val organisationUnitService: OrganisationUnitService = mock()

    // Captors for the organisationUnitService arguments:
    private val fieldsCaptor = argumentCaptor<Fields<OrganisationUnit>>()
    private val filtersCaptor = argumentCaptor<Filter<OrganisationUnit, String>>()
    private val pagingCaptor = argumentCaptor<Boolean>()
    private val pageCaptor = argumentCaptor<Int>()
    private val pageSizeCaptor = argumentCaptor<Int>()
    private val orderCaptor = argumentCaptor<String>()

    private val organisationUnit: OrganisationUnit = mock()
    private val user: User = mock()
    private val created: Date = mock()
    private val collectionCleaner: CollectionCleaner<OrganisationUnit> = mock()
    private val organisationUnitHandler: OrganisationUnitHandler = mock()
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore = mock()
    private val organisationUnitIdentifiableObjectStore: IdentifiableObjectStore<OrganisationUnit> = mock()
    private val organisationUnitDisplayPathTransformer: OrganisationUnitDisplayPathTransformer = mock()

    // the call we are testing:
    private lateinit var lastUpdated: Date
    private lateinit var organisationUnitCall: Completable

    @Before
    @Throws(IOException::class)
    fun setUp() {
        lastUpdated = Date()

        val orgUnitUid = "orgUnitUid1"
        whenever(organisationUnit.uid()).doReturn(orgUnitUid)
        whenever(organisationUnit.code()).doReturn("organisation_unit_code")
        whenever(organisationUnit.name()).doReturn("organisation_unit_name")
        whenever(organisationUnit.displayName()).doReturn("organisation_unit_display_name")
        whenever(organisationUnit.deleted()).doReturn(false)
        whenever(organisationUnit.created()).doReturn(created)
        whenever(organisationUnit.lastUpdated()).doReturn(lastUpdated)
        whenever(organisationUnit.shortName()).doReturn("organisation_unit_short_name")
        whenever(organisationUnit.displayShortName()).doReturn("organisation_unit_display_short_name")
        whenever(organisationUnit.description()).doReturn("organisation_unit_description")
        whenever(organisationUnit.displayDescription()).doReturn("organisation_unit_display_description")
        whenever(organisationUnit.path()).doReturn("/root/orgUnitUid1")
        whenever(organisationUnit.openingDate()).doReturn(created)
        whenever(organisationUnit.closedDate()).doReturn(lastUpdated)
        whenever(organisationUnit.level()).doReturn(4)
        whenever(organisationUnit.parent()).doReturn(null)
        whenever(user.uid()).doReturn("user_uid")
        whenever(user.code()).doReturn("user_code")
        whenever(user.name()).doReturn("user_name")
        whenever(user.displayName()).doReturn("user_display_name")
        whenever(user.created()).doReturn(created)
        whenever(user.lastUpdated()).doReturn(lastUpdated)
        whenever(user.birthday()).doReturn("user_birthday")
        whenever(user.education()).doReturn("user_education")
        whenever(user.gender()).doReturn("user_gender")
        whenever(user.jobTitle()).doReturn("user_job_title")
        whenever(user.surname()).doReturn("user_surname")
        whenever(user.firstName()).doReturn("user_first_name")
        whenever(user.introduction()).doReturn("user_introduction")
        whenever(user.employer()).doReturn("user_employer")
        whenever(user.interests()).doReturn("user_interests")
        whenever(user.languages()).doReturn("user_languages")
        whenever(user.email()).doReturn("user_email")
        whenever(user.phoneNumber()).doReturn("user_phone_number")
        whenever(user.nationality()).doReturn("user_nationality")

        organisationUnitCall = OrganisationUnitCall(
            organisationUnitService,
            organisationUnitHandler,
            organisationUnitDisplayPathTransformer,
            userOrganisationUnitLinkStore,
            organisationUnitIdentifiableObjectStore,
            collectionCleaner
        )
            .download(user)

        // Return only one organisationUnit.
        val organisationUnits = listOf(organisationUnit)
        whenever(UserInternalAccessor.accessOrganisationUnits(user)).doReturn(organisationUnits)
        whenever(
            organisationUnitService.getOrganisationUnits(
                fieldsCaptor.capture(), filtersCaptor.capture(), orderCaptor.capture(), pagingCaptor.capture(),
                pageSizeCaptor.capture(), pageCaptor.capture()
            )
        ).doReturn(Single.just(organisationUnitPayload))
        whenever(organisationUnitPayload.items()).doReturn(organisationUnits)
    }

    @Test
    fun invoke_server_with_correct_parameters() {
        organisationUnitCall.blockingGet()

        assertThat(fieldsCaptor.firstValue).isEqualTo(OrganisationUnitFields.allFields)
        assertThat(filtersCaptor.firstValue.operator()).isEqualTo("like")
        assertThat(filtersCaptor.firstValue.field()).isEqualTo(OrganisationUnitFields.path)
        assertThat(orderCaptor.firstValue).isEqualTo(OrganisationUnitFields.ASC_ORDER)
        assertThat(pagingCaptor.firstValue).isTrue()
        assertThat(pageCaptor.firstValue).isEqualTo(1)
    }

    @Test
    fun invoke_handler_if_request_succeeds() {
        organisationUnitCall.blockingGet()

        verify(organisationUnitHandler, times(1)).handleMany(any(), any())
    }

    @Test
    fun perform_call_twice_on_consecutive_calls() {
        organisationUnitCall.blockingGet()
        organisationUnitCall.blockingGet()

        verify(organisationUnitHandler, times(2)).handleMany(any(), any())
    }
}
