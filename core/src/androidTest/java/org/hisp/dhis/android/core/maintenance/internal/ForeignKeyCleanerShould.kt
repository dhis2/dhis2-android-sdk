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
package org.hisp.dhis.android.core.maintenance.internal

import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.option.OptionSetTableInfo
import org.hisp.dhis.android.core.option.OptionTableInfo
import org.hisp.dhis.android.core.option.internal.OptionStoreImpl
import org.hisp.dhis.android.core.program.ProgramRule
import org.hisp.dhis.android.core.program.ProgramRuleAction
import org.hisp.dhis.android.core.program.ProgramRuleActionType
import org.hisp.dhis.android.core.program.internal.ProgramRuleActionStoreImpl
import org.hisp.dhis.android.core.program.internal.ProgramRuleStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForeignKeyCleanerShould : BaseMockIntegrationTestEmptyDispatcher() {

    @Before
    fun setUp() {
        d2.databaseAdapter().delete(ForeignKeyViolationTableInfo.TABLE_INFO.name())
    }

    @Test
    fun remove_rows_that_produce_foreign_key_errors() {
        addOptionForeignKeyViolation()
        val options = d2.optionModule().options().blockingGet()
        assertThat(options).isEmpty()
    }

    @Test
    fun add_foreign_key_violation_to_table() {
        addOptionForeignKeyViolation()
        assertThat(d2.maintenanceModule().foreignKeyViolations().blockingCount()).isEqualTo(1)
        val foreignKeyViolation = d2.maintenanceModule().foreignKeyViolations().one().blockingGet()!!
        val expectedViolation = ForeignKeyViolation.builder()
            .toTable(OptionSetTableInfo.TABLE_INFO.name())
            .toColumn(IdentifiableColumns.UID)
            .fromTable(OptionTableInfo.TABLE_INFO.name())
            .fromColumn(OptionTableInfo.Columns.OPTION_SET)
            .notFoundValue("no_option_set")
            .fromObjectUid("option_uid")
            .build()
        val violationWithoutId = foreignKeyViolation.toBuilder()
            .id(null)
            .created(null)
            .fromObjectRow(null)
            .build()
        assertThat(expectedViolation).isEqualTo(violationWithoutId)
    }

    @Test
    @Throws(Exception::class)
    fun delete_in_cascade_on_foreign_key_error() = runTest {
        val executor = D2CallExecutor.create(d2.databaseAdapter())
        val PROGRAM_RULE_UID = "program_rule_uid"
        val program = ObjectWithUid.create("nonexisent-program")
        executor.executeD2CallTransactionally {
            ProgramRuleStoreImpl(d2.databaseAdapter()).insert(
                ProgramRule.builder()
                    .uid(PROGRAM_RULE_UID).name("Rule").program(program).build(),
            )
            val programRuleAction = ProgramRuleAction.builder()
                .uid("action_uid")
                .name("name")
                .programRuleActionType(ProgramRuleActionType.ASSIGN)
                .programRule(ObjectWithUid.create(PROGRAM_RULE_UID))
                .build()
            ProgramRuleActionStoreImpl(d2.databaseAdapter()).insert(programRuleAction)
            assertThat(d2.programModule().programRules().blockingCount()).isEqualTo(1)
            assertThat(d2.programModule().programRuleActions().blockingCount()).isEqualTo(1)
            val foreignKeyCleaner = ForeignKeyCleanerImpl.create(d2.databaseAdapter())
            val rowsAffected = foreignKeyCleaner.cleanForeignKeyErrors()
            assertThat(rowsAffected).isEqualTo(1)
            assertThat(d2.programModule().programRules().blockingCount()).isEqualTo(0)
            assertThat(d2.programModule().programRuleActions().blockingCount()).isEqualTo(0)
        }
    }

    private fun addOptionForeignKeyViolation() = runTest {
        val executor = D2CallExecutor.create(d2.databaseAdapter())

        executor.executeD2CallTransactionally<Unit> {
            val optionSet = ObjectWithUid.create("no_option_set")
            val option = Option.builder()
                .uid("option_uid")
                .optionSet(optionSet)
                .build()
            val optionStore: IdentifiableObjectStore<Option> =
                OptionStoreImpl(d2.databaseAdapter())
            optionStore.insert(option)
            ForeignKeyCleanerImpl.create(d2.databaseAdapter()).cleanForeignKeyErrors()
        }
    }
}
