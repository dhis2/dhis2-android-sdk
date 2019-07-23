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

package org.hisp.dhis.android.core.maintenance.internal;

import android.database.Cursor;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkTableInfo;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.d2manager.D2Factory;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionTableInfo;
import org.hisp.dhis.android.core.program.ProgramRuleActionType;
import org.hisp.dhis.android.core.program.ProgramRuleTableInfo;
import org.hisp.dhis.android.core.program.internal.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.internal.ProgramRuleStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsTableInfo;
import org.hisp.dhis.android.core.user.UserTableInfo;
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ForeignKeyCleanerShould extends BaseRealIntegrationTest {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    private final String[] USER_CREDENTIALS_PROJECTION = {
            UserCredentialsTableInfo.Columns.UID,
            UserCredentialsTableInfo.Columns.USER
    };
    private final String[] PROGRAM_RULE_PROJECTION = {
            BaseIdentifiableObjectModel.Columns.UID
    };
    private final String[] PROGRAM_RULE_ACTION_PROJECTION = {
            BaseIdentifiableObjectModel.Columns.UID
    };

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer();

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void remove_rows_that_produce_foreign_key_errors() throws Exception {
        syncMetadataAndAddFKViolation();

        Cursor cursor = getUserCredentialsCursor();
        assertThatCursorHasRowCount(cursor, 1);
        cursor.moveToFirst();

        int uidColumnIndex = cursor.getColumnIndex(UserCredentialsTableInfo.Columns.UID);
        Truth.assertThat(cursor.getString(uidColumnIndex)).isEqualTo("M0fCOxtkURr");
        int userColumnIndex = cursor.getColumnIndex(UserCredentialsTableInfo.Columns.USER);
        Truth.assertThat(cursor.getString(userColumnIndex)).isEqualTo("DXyJmlo9rge");

        assertThatCursor(cursor).isExhausted();
        cursor.close();
    }

    @Test
    public void not_cause_null_records_on_fk_table() throws Exception {
        final D2CallExecutor executor = D2CallExecutor.create(d2.databaseAdapter());

        executor.executeD2CallTransactionally(() -> {
            givenAMetadataInDatabase();

            CategoryCategoryComboLink categoryCategoryComboLink = CategoryCategoryComboLink.builder()
                    .category("no_category")
                    .categoryCombo("no_category_combo")
                    .sortOrder(2)
                    .build();

            d2.databaseAdapter().database().insert(CategoryCategoryComboLinkTableInfo.TABLE_INFO.name(),
                    null, categoryCategoryComboLink.toContentValues());

            ForeignKeyCleanerImpl.create(d2.databaseAdapter()).cleanForeignKeyErrors();

            return null;
        });

        List<ForeignKeyViolation> foreignKeyViolationList =
                ForeignKeyViolationStore.create(d2.databaseAdapter()).selectAll();

        Truth.assertThat(foreignKeyViolationList.size()).isEqualTo(3);
    }

    @Test
    public void save_foreign_key_violations_when_some_errors_are_find() throws Exception {
        syncMetadataAndAddFKViolation();

        List<ForeignKeyViolation> foreignKeyViolationList =
                ForeignKeyViolationStore.create(d2.databaseAdapter()).selectAll();

        ForeignKeyViolation categoryOptionComboViolation = ForeignKeyViolation.builder()
                .toTable(UserTableInfo.TABLE_INFO.name())
                .toColumn(BaseIdentifiableObjectModel.Columns.UID)
                .fromTable(UserCredentialsTableInfo.TABLE_INFO.name())
                .fromColumn(UserCredentialsTableInfo.Columns.USER)
                .notFoundValue("no_user_uid")
                .fromObjectUid("user_credential_uid1")
                .build();

        List<ForeignKeyViolation> violationsToCompare = new ArrayList<>();
        for (ForeignKeyViolation violation : foreignKeyViolationList) {
            violationsToCompare.add(violation.toBuilder().id(null).created(null).fromObjectRow(null).build());
        }

        assertThat(violationsToCompare.contains(categoryOptionComboViolation), is(true));
        assertThat(violationsToCompare.contains(categoryOptionComboViolation), is(true));
    }

    @Test
    public void cascade_deletion_on_foreign_key_error() throws Exception {
        final D2CallExecutor executor = D2CallExecutor.create(d2.databaseAdapter());

        final String PROGRAM_RULE_UID = "program_rule_uid";

        givenAMetadataInDatabase();

        final Cursor programRuleCursor = getProgramRuleCursor();
        Cursor programRuleActionCursor = getProgramRuleActionCursor();

        final Integer programRuleCount = programRuleCursor.getCount();
        final Integer programRuleActionCount = programRuleActionCursor.getCount();

        programRuleCursor.close();
        programRuleActionCursor.close();

        final Program program = Program.builder().uid("nonexisent-program").build();

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

            Cursor programRuleCursor1 = getProgramRuleCursor();
            Cursor programRuleActionCursor1 = getProgramRuleActionCursor();
            assertThatCursorHasRowCount(programRuleCursor1, programRuleCount + 1);
            assertThatCursorHasRowCount(programRuleActionCursor1, programRuleActionCount + 1);
            programRuleCursor1.close();
            programRuleActionCursor1.close();

            ForeignKeyCleaner foreignKeyCleaner = ForeignKeyCleanerImpl.create(d2.databaseAdapter());
            Integer rowsAffected = foreignKeyCleaner.cleanForeignKeyErrors();

            Truth.assertThat(rowsAffected).isEqualTo(1);

            Cursor programRuleCursor2 = getProgramRuleCursor();
            Cursor programRuleActionCursor2 = getProgramRuleActionCursor();
            assertThatCursorHasRowCount(programRuleCursor2, programRuleCount);
            assertThatCursorHasRowCount(programRuleActionCursor2, programRuleActionCount);
            programRuleCursor2.close();
            programRuleActionCursor2.close();

            return null;
        });

    }

    private void syncMetadataAndAddFKViolation() throws D2Error {

        final D2CallExecutor executor = D2CallExecutor.create(d2.databaseAdapter());

        executor.executeD2CallTransactionally(() -> {
            givenAMetadataInDatabase();
            User user = User.builder().uid("no_user_uid").build();
            UserCredentials userCredentials = UserCredentials.builder()
                    .id(2L)
                    .uid("user_credential_uid1")
                    .user(user)
                    .build();

            IdentifiableObjectStore<UserCredentials> userCredentialsStore =
                    UserCredentialsStoreImpl.create(d2.databaseAdapter());
            userCredentialsStore.insert(userCredentials);

            List<UserCredentials> ds = userCredentialsStore.selectAll();
            assertThat(ds.contains(userCredentials), is(true));

            ForeignKeyCleanerImpl.create(d2.databaseAdapter()).cleanForeignKeyErrors();

            assertThat(userCredentialsStore.selectAll().contains(userCredentials), is(false));
            return null;
        });
    }

    private void givenAMetadataInDatabase() {
        try {
            dhis2MockServer.setRequestDispatcher();
            d2.metadataModule().blockingDownload();
        } catch (Exception ignore) {
        }
    }

    private Cursor getUserCredentialsCursor() {
        return database().query(UserCredentialsTableInfo.TABLE_INFO.name(), USER_CREDENTIALS_PROJECTION,
                null, null, null, null, null);
    }

    private Cursor getProgramRuleCursor() {
        return database().query(ProgramRuleTableInfo.TABLE_INFO.name(), PROGRAM_RULE_PROJECTION,
                null, null, null, null, null);
    }

    private Cursor getProgramRuleActionCursor() {
        return database().query(ProgramRuleActionTableInfo.TABLE_INFO.name(), PROGRAM_RULE_ACTION_PROJECTION,
                null, null, null, null, null);
    }

    private void assertThatCursorHasRowCount(Cursor cursor, int rowCount) {
        Truth.assertThat(cursor.getCount()).isEqualTo(rowCount);
    }
}