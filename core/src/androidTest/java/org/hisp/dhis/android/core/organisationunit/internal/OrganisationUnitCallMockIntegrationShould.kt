/*
 *  Copyright (c) 2004-2023, University of Oslo
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.internal.CategoryComboStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.internal.DataSetStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserInternalAccessor
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.hisp.dhis.android.core.user.internal.UserStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.hisp.dhis.android.persistence.category.CategoryComboTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetTableInfo
import org.hisp.dhis.android.persistence.program.ProgramTableInfo
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class OrganisationUnitCallMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {
    // The return of the organisationUnitCall to be tested:
    private lateinit var organisationUnitCall: suspend () -> Unit
    private val expectedAfroArabicClinic = OrganisationUnitSamples.getAfroArabClinic()
    private val expectedAdonkiaCHP = OrganisationUnitSamples.getAdonkiaCHP()

    @Before
    fun setUp() {
        runBlocking {
            dhis2MockServer.enqueueMockResponse("organisationunit/admin_organisation_units.json")
            val orgUnit = OrganisationUnit.builder().uid("O6uvpzGd5pu").path("/ImspTQPwCqd/O6uvpzGd5pu").build()
            val organisationUnits = listOf(orgUnit)

            // Create a user with the root as assigned organisation unit (for the test):
            val userStore = koin.get<UserStore>()
            val user = UserInternalAccessor.insertOrganisationUnits(User.builder(), organisationUnits)
                .uid(userId).build()
            userStore.insert(user)

            val categoryComboUid = "category_combo_uid"
            val categoryComboStore = koin.get<CategoryComboStore>()
            categoryComboStore.insert(CategoryCombo.builder().uid(categoryComboUid).build())

            // inserting programs for creating OrgUnitProgramLinks
            val programUid = "lxAQ7Zs9VYR"
            val programStore = koin.get<ProgramStore>()
            programStore.insert(
                Program.builder().uid(programUid).categoryCombo(ObjectWithUid.create(categoryComboUid)).build(),
            )

            // inserting dataSets for creating OrgUnitDataSetLinks
            val dataSetUid = "lyLU2wR22tC"
            val dataSetStore = koin.get<DataSetStore>()
            dataSetStore.insert(
                DataSet.builder().uid(dataSetUid).categoryCombo(ObjectWithUid.create(categoryComboUid)).build(),
            )

            organisationUnitCall = {
                OrganisationUnitCall(
                    koin.get(),
                    koin.get(),
                    koin.get(),
                    koin.get(),
                    koin.get(),
                ).download(user)
            }
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            koin.get<DataSetStore>().delete()
            koin.get<CategoryComboStore>().delete()
            koin.get<ProgramStore>().delete()
            koin.get<UserStore>().delete()
        }
    }

    @Test
    fun persist_organisation_unit_tree() = runTest {
        organisationUnitCall.invoke()
        val organisationUnitStore: OrganisationUnitStore = koin.get()
        val dbAfroArabicClinic = organisationUnitStore.selectByUid(expectedAfroArabicClinic.uid())
        val dbAdonkiaCHP = organisationUnitStore.selectByUid(expectedAdonkiaCHP.uid())

        assertThat(expectedAfroArabicClinic).isEqualTo(dbAfroArabicClinic)
        assertThat(expectedAdonkiaCHP).isEqualTo(dbAdonkiaCHP)
    }

    @Test
    fun persist_organisation_unit_user_links() = runTest {
        organisationUnitCall.invoke()
        val userOrganisationUnitStore: UserOrganisationUnitLinkStore = koin.get()
        val userOrganisationUnitLinks = userOrganisationUnitStore.selectAll()
        val linkOrganisationUnits: MutableSet<String> = HashSet(2)

        for (userOrganisationUnitLink in userOrganisationUnitLinks) {
            assertThat(userOrganisationUnitLink.user()).isEqualTo("user_uid")
            linkOrganisationUnits.add(userOrganisationUnitLink.organisationUnit())
        }
        assertThat(linkOrganisationUnits.contains(expectedAfroArabicClinic.uid())).isTrue()
        assertThat(linkOrganisationUnits.contains(expectedAdonkiaCHP.uid())).isTrue()
    }

    companion object {
        private const val userId = "user_uid"

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            runBlocking {
                d2.databaseAdapter().delete(ProgramTableInfo.TABLE_INFO.name())
                d2.databaseAdapter().delete(DataSetTableInfo.TABLE_INFO.name())
                d2.databaseAdapter().delete(CategoryComboTableInfo.TABLE_INFO.name())
                koin.get<UserStore>().delete(userId)
            }
        }
    }
}
