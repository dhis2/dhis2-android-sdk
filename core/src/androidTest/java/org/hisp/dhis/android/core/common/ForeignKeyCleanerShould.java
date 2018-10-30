package org.hisp.dhis.android.core.common;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleActionModel;
import org.hisp.dhis.android.core.program.ProgramRuleActionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleActionType;
import org.hisp.dhis.android.core.program.ProgramRuleModel;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsModel;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ForeignKeyCleanerShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    private final String[] USER_CREDENTIALS_PROJECTION = {
            UserCredentialsModel.Columns.UID,
            UserCredentialsModel.Columns.USER
    };
    private final String[] PROGRAM_RULE_PROJECTION = {
            ProgramRuleModel.Columns.UID
    };
    private final String[] PROGRAM_RULE_ACTION_PROJECTION = {
            ProgramRuleActionModel.Columns.UID
    };

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

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
        final D2CallExecutor executor = new D2CallExecutor();

        executor.executeD2CallTransactionally(d2.databaseAdapter(), new Callable<Void>() {
            @Override
            public Void call() {
                givenAMetadataInDatabase();
                User user = User.builder().uid("no_user_uid").build();
                UserCredentials userCredentials = UserCredentials.builder()
                        .uid("user_credential_uid1")
                        .user(user)
                        .build();
                UserCredentialsStore.create(d2.databaseAdapter()).insert(userCredentials);

                Cursor userCredentialsCursor = getUserCredentialsCursor();
                assertThatCursorHasRowCount(userCredentialsCursor, 2);
                userCredentialsCursor.close();
                new ForeignKeyCleaner(d2.databaseAdapter()).cleanForeignKeyErrors();
                return null;
            }
        });

        Cursor cursor = getUserCredentialsCursor();
        assertThatCursorHasRowCount(cursor, 1);
        cursor.moveToFirst();

        int uidColumnIndex = cursor.getColumnIndex(UserCredentialsModel.Columns.UID);
        Truth.assertThat(cursor.getString(uidColumnIndex)).isEqualTo("M0fCOxtkURr");
        int userColumnIndex = cursor.getColumnIndex(UserCredentialsModel.Columns.USER);
        Truth.assertThat(cursor.getString(userColumnIndex)).isEqualTo("DXyJmlo9rge");

        assertThatCursor(cursor).isExhausted();
        cursor.close();
    }

    @Test
    public void cascade_deletion_on_foreign_key_error() throws Exception {
        final D2CallExecutor executor = new D2CallExecutor();

        final String PROGRAM_RULE_UID = "program_rule_uid";

        givenAMetadataInDatabase();

        final Cursor programRuleCursor = getProgramRuleCursor();
        Cursor programRuleActionCursor = getProgramRuleActionCursor();

        final Integer programRuleCount = programRuleCursor.getCount();
        final Integer programRuleActionCount = programRuleActionCursor.getCount();

        programRuleCursor.close();
        programRuleActionCursor.close();

        final Program program = Program.builder().uid("nonexisent-program").build();

        executor.executeD2CallTransactionally(d2.databaseAdapter(), new Callable<Void>() {
            @Override
            public Void call() throws D2CallException {
                ProgramRuleStore.create(d2.databaseAdapter()).insert(ProgramRule.builder().uid(PROGRAM_RULE_UID).name("Rule").program(program).build());

                new ProgramRuleActionStoreImpl(d2.databaseAdapter()).insert("action_uid", null, "name", null, new
                        Date(), new Date(), null, null, null, null, null, null, ProgramRuleActionType.ASSIGN,
                        null, null, PROGRAM_RULE_UID);

                Cursor programRuleCursor1 = getProgramRuleCursor();
                Cursor programRuleActionCursor1 = getProgramRuleActionCursor();
                assertThatCursorHasRowCount(programRuleCursor1, programRuleCount + 1);
                assertThatCursorHasRowCount(programRuleActionCursor1, programRuleActionCount + 1);
                programRuleCursor1.close();
                programRuleActionCursor1.close();

                ForeignKeyCleaner foreignKeyCleaner = new ForeignKeyCleaner(d2.databaseAdapter());
                Integer rowsAffected = foreignKeyCleaner.cleanForeignKeyErrors();

                Truth.assertThat(rowsAffected).isEqualTo(1);

                Cursor programRuleCursor2 = getProgramRuleCursor();
                Cursor programRuleActionCursor2 = getProgramRuleActionCursor();
                assertThatCursorHasRowCount(programRuleCursor2, programRuleCount);
                assertThatCursorHasRowCount(programRuleActionCursor2, programRuleActionCount);
                programRuleCursor2.close();
                programRuleActionCursor2.close();
                
                return null;
            }
        });

    }

    private void givenAMetadataInDatabase() {
        try {
            dhis2MockServer.enqueueMetadataResponses();
            d2.syncMetaData().call();
        } catch (Exception ignore) {
        }
    }

    private Cursor getUserCredentialsCursor() {
        return database().query(UserCredentialsModel.TABLE, USER_CREDENTIALS_PROJECTION, null, null,
                null, null, null);
    }

    private Cursor getProgramRuleCursor() {
        return database().query(ProgramRuleModel.TABLE, PROGRAM_RULE_PROJECTION, null, null,
                null, null, null);
    }

    private Cursor getProgramRuleActionCursor() {
        return database().query(ProgramRuleActionModel.TABLE, PROGRAM_RULE_ACTION_PROJECTION, null, null,
                null, null, null);
    }

    private void assertThatCursorHasRowCount(Cursor cursor, int rowCount) {
        Truth.assertThat(cursor.getCount()).isEqualTo(rowCount);
    }
}