package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class UserOrganisationUnitLinkStoreIntegrationsTests extends AbsStoreTestCase {
    private static final String[] USER_ORGANISATION_UNITS_PROJECTION = {
            UserOrganisationUnitLinkModel.Columns.USER,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE
    };

    private UserOrganisationUnitLinkStore organisationUnitLinkStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        organisationUnitLinkStore = new UserOrganisationUnitLinkStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        // insert a parent user and organisation unit
        ContentValues user = UserStoreIntegrationTests
                .create(1L, "test_user_uid");
        ContentValues organisationUnit = CreateOrganisationUnitUtils
                .createOrgUnit(1L, "test_organisation_unit_uid");
        database().insert(DbOpenHelper.Tables.USER, null, user);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        long rowId = organisationUnitLinkStore.insert(
                "test_user_uid",
                "test_organisation_unit_uid",
                "test_organisation_unit_scope"
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                USER_ORGANISATION_UNITS_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow("test_user_uid",
                        "test_organisation_unit_uid",
                        "test_organisation_unit_scope"
                ).isExhausted();
    }

    @Test
    public void delete_shouldDeleteAllRows() {
        ContentValues user = UserStoreIntegrationTests
                .create(1L, "test_user_uid");
        ContentValues organisationUnit = CreateOrganisationUnitUtils
                .createOrgUnit(1L, "test_organisation_unit_uid");
        ContentValues userOrganisationUnitLink = new ContentValues();
        userOrganisationUnitLink.put(UserOrganisationUnitLinkModel.Columns.USER, "test_user_uid");
        userOrganisationUnitLink.put(UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT, "test_organisation_unit_uid");
        userOrganisationUnitLink.put(UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE, "test_organisation_unit_scope");

        database().insert(DbOpenHelper.Tables.USER, null, user);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);
        database().insert(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrganisationUnitLink);

        int deleted = organisationUnitLinkStore.delete();

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                null, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1L);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        organisationUnitLinkStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
