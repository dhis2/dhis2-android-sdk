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

import android.content.ContentValues
import com.google.common.truth.Truth.assertThat
import io.reactivex.Completable
import java.io.IOException
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleanerImpl
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandlerImpl
import org.hisp.dhis.android.core.category.CategoryComboTableInfo
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples
import org.hisp.dhis.android.core.dataset.DataSetTableInfo
import org.hisp.dhis.android.core.dataset.internal.DataSetOrganisationUnitLinkStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.core.program.ProgramTableInfo
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserInternalAccessor
import org.hisp.dhis.android.core.user.UserTableInfo
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class OrganisationUnitCallMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {
    // The return of the organisationUnitCall to be tested:
    private lateinit var organisationUnitCall: Completable
    private val expectedAfroArabicClinic = OrganisationUnitSamples.getAfroArabClinic()
    private val expectedAdonkiaCHP = OrganisationUnitSamples.getAdonkiaCHP()

    @Before
    @Throws(IOException::class)
    fun setUp() {
        dhis2MockServer.enqueueMockResponse("organisationunit/admin_organisation_units.json")
        val orgUnit = OrganisationUnit.builder().uid("O6uvpzGd5pu").path("/ImspTQPwCqd/O6uvpzGd5pu").build()
        val organisationUnits = listOf(orgUnit)

        // dependencies for the OrganisationUnitCall:
        val organisationUnitService = d2.retrofit().create(OrganisationUnitService::class.java)

        // Create a user with the root as assigned organisation unit (for the test):
        val user = UserInternalAccessor.insertOrganisationUnits(User.builder(), organisationUnits)
            .uid("user_uid").build()
        databaseAdapter.insert(UserTableInfo.TABLE_INFO.name(), null, user.toContentValues())
        val userContentValues = ContentValues()
        userContentValues.put(IdentifiableColumns.UID, "user_uid")
        databaseAdapter.insert(UserTableInfo.TABLE_INFO.name(), null, userContentValues)

        // inserting programs for creating OrgUnitProgramLinks
        val programUid = "lxAQ7Zs9VYR"
        insertObjectWithUid(programUid, ProgramTableInfo.TABLE_INFO)

        // inserting dataSets for creating OrgUnitDataSetLinks
        insertDataSet()
        val organisationUnitHandler = OrganisationUnitHandlerImpl(
            OrganisationUnitStore.create(databaseAdapter),
            LinkHandlerImpl(UserOrganisationUnitLinkStoreImpl.create(databaseAdapter)),
            LinkHandlerImpl(OrganisationUnitProgramLinkStore.create(databaseAdapter)),
            LinkHandlerImpl(DataSetOrganisationUnitLinkStore.create(databaseAdapter)),
            IdentifiableHandlerImpl(OrganisationUnitGroupStore.create(databaseAdapter)),
            LinkHandlerImpl(OrganisationUnitOrganisationUnitGroupLinkStore.create(databaseAdapter))
        )
        val organisationUnitCollectionCleaner: CollectionCleaner<OrganisationUnit> =
            CollectionCleanerImpl(OrganisationUnitTableInfo.TABLE_INFO.name(), databaseAdapter)
        val pathTransformer = OrganisationUnitDisplayPathTransformer()
        organisationUnitCall = OrganisationUnitCall(
            organisationUnitService,
            organisationUnitHandler,
            pathTransformer,
            UserOrganisationUnitLinkStoreImpl.create(databaseAdapter),
            OrganisationUnitStore.create(databaseAdapter),
            organisationUnitCollectionCleaner
        )
            .download(user)
    }

    private fun insertObjectWithUid(uid: String, tableInfo: TableInfo) {
        val contentValues = ContentValues()
        contentValues.put(IdentifiableColumns.UID, uid)
        databaseAdapter.insert(tableInfo.name(), null, contentValues)
    }

    private fun insertDataSet() {
        val dataSetUid = "lyLU2wR22tC"
        val categoryComboUid = "category_combo_uid"
        insertObjectWithUid(categoryComboUid, CategoryComboTableInfo.TABLE_INFO)
        val contentValues = ContentValues()
        contentValues.put(IdentifiableColumns.UID, dataSetUid)
        contentValues.put(DataSetTableInfo.Columns.CATEGORY_COMBO, categoryComboUid)
        databaseAdapter.insert(DataSetTableInfo.TABLE_INFO.name(), null, contentValues)
    }

    @Test
    fun persist_organisation_unit_tree() {
        organisationUnitCall.blockingAwait()
        val organisationUnitStore = OrganisationUnitStore.create(databaseAdapter)
        val dbAfroArabicClinic = organisationUnitStore.selectByUid(expectedAfroArabicClinic.uid())
        val dbAdonkiaCHP = organisationUnitStore.selectByUid(expectedAdonkiaCHP.uid())

        assertThat(expectedAfroArabicClinic).isEqualTo(dbAfroArabicClinic!!.toBuilder().id(null).build())
        assertThat(expectedAdonkiaCHP).isEqualTo(dbAdonkiaCHP!!.toBuilder().id(null).build())
    }

    @Test
    fun persist_organisation_unit_user_links() {
        organisationUnitCall.blockingAwait()
        val userOrganisationUnitStore = UserOrganisationUnitLinkStoreImpl.create(databaseAdapter)
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
        @AfterClass
        @JvmStatic
        fun tearDown() {
            d2.databaseAdapter().delete(ProgramTableInfo.TABLE_INFO.name())
            d2.databaseAdapter().delete(DataSetTableInfo.TABLE_INFO.name())
            d2.databaseAdapter().delete(CategoryComboTableInfo.TABLE_INFO.name())
        }
    }
}
