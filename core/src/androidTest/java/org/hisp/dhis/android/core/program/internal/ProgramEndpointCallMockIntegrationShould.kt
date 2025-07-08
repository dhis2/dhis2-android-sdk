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
package org.hisp.dhis.android.core.program.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.persistence.category.CategoryComboTableInfo
import org.hisp.dhis.android.core.category.internal.CreateCategoryComboUtils
import org.hisp.dhis.android.core.data.program.ProgramRuleVariableSamples
import org.hisp.dhis.android.core.data.program.ProgramSamples
import org.hisp.dhis.android.core.data.program.ProgramTrackedEntityAttributeSamples
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils
import org.hisp.dhis.android.persistence.dataelement.DataElementTableInfo
import org.hisp.dhis.android.core.program.CreateProgramStageUtils
import org.hisp.dhis.android.persistence.program.ProgramStageTableInfo
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeStore
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityAttributeUtils
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeTableInfo
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.util.collections.Sets

@RunWith(D2JunitRunner::class)
class ProgramEndpointCallMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {

    @Test
    fun persist_program_when_call() = runTest {
        val store: ProgramStore = koin.get()

        assertThat(store.count()).isEqualTo(3)
        assertThat(store.selectByUid(PROGRAM_UID)!!.toBuilder().id(null).build())
            .isEqualTo(ProgramSamples.getAntenatalProgram())
    }

    @Test
    fun persist_program_rule_variables_on_call() = runTest {
        val store: ProgramRuleVariableStore = koin.get()

        assertThat(store.count()).isEqualTo(2)
        assertThat(store.selectByUid("omrL0gtPpDL")).isEqualTo(ProgramRuleVariableSamples.getHemoglobin())
    }

    @Test
    fun persist_program_tracker_entity_attributes_when_call() = runTest {
        val store: ProgramTrackedEntityAttributeStore = koin.get()

        assertThat(store.count()).isEqualTo(2)
        assertThat(store.selectByUid("YhqgQ6Iy4c4"))
            .isEqualTo(ProgramTrackedEntityAttributeSamples.getChildProgrammeGender())
    }

    @Test
    fun not_persist_relationship_type_when_call() = runTest {
        val store: RelationshipTypeStore = koin.get()
        assertThat(store.count()).isEqualTo(0)
    }

    companion object {
        private const val PROGRAM_UID = "lxAQ7Zs9VYR"

        @BeforeClass
        @JvmStatic
        fun setUpTestClass() = runTest {
            setUpClass()

            val executor = objects.d2DIComponent.coroutineApiCallExecutor
            executor.wrapTransactionally {
                val categoryComboUid = "m2jTvAj5kkm"
                val categoryCombo = CreateCategoryComboUtils.create(categoryComboUid)
                databaseAdapter.insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo)

                // inserting tracked entity
                val trackedEntityType = CreateTrackedEntityUtils.create("nEenWmSyUEp")
                databaseAdapter.insert(TrackedEntityTypeTableInfo.TABLE_INFO.name(), null, trackedEntityType)

                // inserting tracked entity attributes
                val trackedEntityAttribute1 = CreateTrackedEntityAttributeUtils.create("aejWyOfXge6", null)
                databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute1)
                val trackedEntityAttribute2 = CreateTrackedEntityAttributeUtils.create("cejWyOfXge6", null)
                databaseAdapter.insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute2)
                val dataElement1 = CreateDataElementUtils.create("vANAXwtLwcT", categoryComboUid, null)
                databaseAdapter.insert(DataElementTableInfo.TABLE_INFO.name(), null, dataElement1)
                val dataElement2 = CreateDataElementUtils.create("sWoqcoByYmD", categoryComboUid, null)
                databaseAdapter.insert(DataElementTableInfo.TABLE_INFO.name(), null, dataElement2)
                val programStage = CreateProgramStageUtils.create("dBwrot7S420", PROGRAM_UID)
                databaseAdapter.insert(ProgramStageTableInfo.TABLE_INFO.name(), null, programStage)
                dhis2MockServer.enqueueMockResponse("program/programs.json")

                objects.d2DIComponent.programCall.download(Sets.newSet(PROGRAM_UID))
            }
        }
    }
}
