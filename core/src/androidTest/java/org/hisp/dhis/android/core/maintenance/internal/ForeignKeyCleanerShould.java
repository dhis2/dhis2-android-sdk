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

package org.hisp.dhis.android.core.maintenance.internal;

import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionType;
import org.hisp.dhis.android.core.program.internal.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.internal.ProgramRuleStore;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsTableInfo;
import org.hisp.dhis.android.core.user.UserTableInfo;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ForeignKeyCleanerShould extends BaseMockIntegrationTestEmptyDispatcher {

    @Before
    public void setUp() {
        d2.databaseAdapter().delete(ForeignKeyViolationTableInfo.TABLE_INFO.name());
    }

    @Test
    public void remove_rows_that_produce_foreign_key_errors() throws Exception {
        addUserCredentialsForeignKeyViolation();
        UserCredentials userCredentials = d2.userModule().userCredentials().blockingGet();
        assertThat(userCredentials.uid()).isEqualTo("M0fCOxtkURr");
        assertThat(userCredentials.user().uid()).isEqualTo("DXyJmlo9rge");
    }

    @Test
    public void add_foreign_key_violation_to_table() throws Exception {
        addUserCredentialsForeignKeyViolation();

        assertThat(d2.maintenanceModule().foreignKeyViolations().blockingCount()).isEqualTo(1);

        ForeignKeyViolation foreignKeyViolation = d2.maintenanceModule().foreignKeyViolations().one().blockingGet();

        ForeignKeyViolation expectedViolation = ForeignKeyViolation.builder()
                .toTable(UserTableInfo.TABLE_INFO.name())
                .toColumn(IdentifiableColumns.UID)
                .fromTable(UserCredentialsTableInfo.TABLE_INFO.name())
                .fromColumn(UserCredentialsTableInfo.Columns.USER)
                .notFoundValue("no_user_uid")
                .fromObjectUid("user_credential_uid1")
                .build();

        ForeignKeyViolation violationWithoutId = foreignKeyViolation.toBuilder()
                .id(null)
                .created(null)
                .fromObjectRow(null)
                .build();

        assertThat(expectedViolation).isEqualTo(violationWithoutId);
    }

    @Test
    public void delete_in_cascade_on_foreign_key_error() throws Exception {
        final D2CallExecutor executor = D2CallExecutor.create(d2.databaseAdapter());

        final String PROGRAM_RULE_UID = "program_rule_uid";

        final ObjectWithUid program = ObjectWithUid.create("nonexisent-program");

        executor.executeD2CallTransactionally(() -> {
            ProgramRuleStore.create(d2.databaseAdapter()).insert(ProgramRule.builder()
                    .uid(PROGRAM_RULE_UID).name("Rule").program(program).build());

            ProgramRuleAction programRuleAction = ProgramRuleAction.builder()
                    .uid("action_uid")
                    .name("name")
                    .programRuleActionType(ProgramRuleActionType.ASSIGN)
                    .programRule(ObjectWithUid.create(PROGRAM_RULE_UID))
                    .build();

            ProgramRuleActionStore.create(d2.databaseAdapter()).insert(programRuleAction);

            assertThat(d2.programModule().programRules().blockingCount()).isEqualTo(1);
            assertThat(d2.programModule().programRuleActions().blockingCount()).isEqualTo(1);

            ForeignKeyCleaner foreignKeyCleaner = ForeignKeyCleanerImpl.create(d2.databaseAdapter());
            Integer rowsAffected = foreignKeyCleaner.cleanForeignKeyErrors();

            assertThat(rowsAffected).isEqualTo(1);

            assertThat(d2.programModule().programRules().blockingCount()).isEqualTo(0);
            assertThat(d2.programModule().programRuleActions().blockingCount()).isEqualTo(0);

            return null;
        });

    }

    private void addUserCredentialsForeignKeyViolation() throws D2Error {
        final D2CallExecutor executor = D2CallExecutor.create(d2.databaseAdapter());

        executor.executeD2CallTransactionally(() -> {
            ObjectWithUid user = ObjectWithUid.create("no_user_uid");
            UserCredentials userCredentials = UserCredentials.builder()
                    .id(2L)
                    .uid("user_credential_uid1")
                    .user(user)
                    .build();

            IdentifiableObjectStore<UserCredentials> userCredentialsStore =
                    UserCredentialsStoreImpl.create(d2.databaseAdapter());
            userCredentialsStore.insert(userCredentials);

            ForeignKeyCleanerImpl.create(d2.databaseAdapter()).cleanForeignKeyErrors();

            return null;
        });
    }
}