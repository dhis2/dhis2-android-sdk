package org.hisp.dhis.android.core.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.dataelement.DataElementContract;
import org.hisp.dhis.android.core.option.OptionContract;
import org.hisp.dhis.android.core.option.OptionSetContract;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;
import org.hisp.dhis.android.core.program.ProgramContract;
import org.hisp.dhis.android.core.program.ProgramStageContract;
import org.hisp.dhis.android.core.program.ProgramStageDataElementContract;
import org.hisp.dhis.android.core.program.ProgramStageSectionContract;
import org.hisp.dhis.android.core.relationship.RelationshipContract;
import org.hisp.dhis.android.core.relationship.RelationshipTypeContract;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityContract;
import org.hisp.dhis.android.core.user.AuthenticatedUserContract;
import org.hisp.dhis.android.core.user.UserContract;
import org.hisp.dhis.android.core.user.UserCredentialsContract;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkContract;

public final class DbOpenHelper extends SQLiteOpenHelper {

    // @VisibleForTesting
    // static final String NAME = "dhis.db";

    @VisibleForTesting
    static final int VERSION = 1;

    public interface Tables {
        String USER = "User";
        String USER_CREDENTIALS = "UserCredentials";
        String ORGANISATION_UNIT = "OrganisationUnit";
        String USER_ORGANISATION_UNIT = "UserOrganisationUnit";
        String AUTHENTICATED_USER = "AuthenticatedUser";
        String OPTION_SET = "OptionSet";
        String OPTION = "Option";
        String PROGRAM = "Program";
        String TRACKED_ENTITY = "TrackedEntity";
        String DATA_ELEMENT = "DataElement";
        String PROGRAM_STAGE_DATA_ELEMENT = "ProgramStageDataElement";
        String PROGRAM_STAGE_SECTION = "ProgramStageSection";
        String PROGRAM_STAGE = "ProgramStage";
        String RELATIONSHIP_TABLE = "Relationship";
        String RELATIONSHIP_TYPE = "RelationshipType";
    }

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + Tables.USER + " (" +
            UserContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            UserContract.Columns.CODE + " TEXT," +
            UserContract.Columns.NAME + " TEXT," +
            UserContract.Columns.DISPLAY_NAME + " TEXT," +
            UserContract.Columns.CREATED + " TEXT," +
            UserContract.Columns.LAST_UPDATED + " TEXT," +
            UserContract.Columns.BIRTHDAY + " TEXT," +
            UserContract.Columns.EDUCATION + " TEXT," +
            UserContract.Columns.GENDER + " TEXT," +
            UserContract.Columns.JOB_TITLE + " TEXT," +
            UserContract.Columns.SURNAME + " TEXT," +
            UserContract.Columns.FIRST_NAME + " TEXT," +
            UserContract.Columns.INTRODUCTION + " TEXT," +
            UserContract.Columns.EMPLOYER + " TEXT," +
            UserContract.Columns.INTERESTS + " TEXT," +
            UserContract.Columns.LANGUAGES + " TEXT," +
            UserContract.Columns.EMAIL + " TEXT," +
            UserContract.Columns.PHONE_NUMBER + " TEXT," +
            UserContract.Columns.NATIONALITY + " TEXT" +
            ");";

    private static final String CREATE_USER_CREDENTIALS_TABLE = "CREATE TABLE " + Tables.USER_CREDENTIALS + " (" +
            UserCredentialsContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserCredentialsContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            UserCredentialsContract.Columns.CODE + " TEXT," +
            UserCredentialsContract.Columns.NAME + " TEXT," +
            UserCredentialsContract.Columns.DISPLAY_NAME + " TEXT," +
            UserCredentialsContract.Columns.CREATED + " TEXT," +
            UserCredentialsContract.Columns.LAST_UPDATED + " TEXT," +
            UserCredentialsContract.Columns.USERNAME + " TEXT," +
            UserCredentialsContract.Columns.USER + " TEXT NOT NULL UNIQUE," +
            "FOREIGN KEY (" + UserCredentialsContract.Columns.USER + ") REFERENCES " + Tables.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE" +
            ");";

    private static final String CREATE_ORGANISATION_UNITS_TABLE = "CREATE TABLE " + Tables.ORGANISATION_UNIT + " (" +
            OrganisationUnitContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OrganisationUnitContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            OrganisationUnitContract.Columns.CODE + " TEXT," +
            OrganisationUnitContract.Columns.NAME + " TEXT," +
            OrganisationUnitContract.Columns.DISPLAY_NAME + " TEXT," +
            OrganisationUnitContract.Columns.CREATED + " TEXT," +
            OrganisationUnitContract.Columns.LAST_UPDATED + " TEXT," +
            OrganisationUnitContract.Columns.SHORT_NAME + " TEXT," +
            OrganisationUnitContract.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            OrganisationUnitContract.Columns.DESCRIPTION + " TEXT," +
            OrganisationUnitContract.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            OrganisationUnitContract.Columns.PATH + " TEXT," +
            OrganisationUnitContract.Columns.OPENING_DATE + " TEXT," +
            OrganisationUnitContract.Columns.CLOSED_DATE + " TEXT," +
            OrganisationUnitContract.Columns.LEVEL + " INTEGER," +
            OrganisationUnitContract.Columns.PARENT + " TEXT" + ");";

    private static final String CREATE_USER_ORGANISATION_UNIT_TABLE = "CREATE TABLE " + Tables.USER_ORGANISATION_UNIT + " (" +
            UserOrganisationUnitLinkContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserOrganisationUnitLinkContract.Columns.USER + " TEXT NOT NULL," +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE + " TEXT NOT NULL," +
            "FOREIGN KEY (" + UserOrganisationUnitLinkContract.Columns.USER + ") REFERENCES " + Tables.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT + ") REFERENCES " + Tables.ORGANISATION_UNIT +
            " (" + OrganisationUnitContract.Columns.UID + ") ON DELETE CASCADE," +
            "UNIQUE (" + UserOrganisationUnitLinkContract.Columns.USER + ", " +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT + ", " +
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE + ")" +
            ");";

    private static final String CREATE_AUTHENTICATED_USER_TABLE = "CREATE TABLE " + Tables.AUTHENTICATED_USER + " (" +
            AuthenticatedUserContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AuthenticatedUserContract.Columns.USER + " TEXT NOT NULL UNIQUE," +
            AuthenticatedUserContract.Columns.CREDENTIALS + " TEXT NOT NULL," +
            "FOREIGN KEY (" + AuthenticatedUserContract.Columns.USER + ") REFERENCES " + Tables.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE" +
            ");";

    private static final String CREATE_OPTION_SET_TABLE = "CREATE TABLE " + Tables.OPTION_SET + " (" +
            OptionSetContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OptionSetContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            OptionSetContract.Columns.CODE + " TEXT," +
            OptionSetContract.Columns.NAME + " TEXT," +
            OptionSetContract.Columns.DISPLAY_NAME + " TEXT," +
            OptionSetContract.Columns.CREATED + " TEXT," +
            OptionSetContract.Columns.LAST_UPDATED + " TEXT," +
            OptionSetContract.Columns.VERSION + " INTEGER," +
            OptionSetContract.Columns.VALUE_TYPE + " TEXT" +
            ");";

    private static final String CREATE_OPTION_TABLE = "CREATE TABLE " + Tables.OPTION + " (" +
            OptionContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OptionContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            OptionContract.Columns.CODE + " TEXT," +
            OptionContract.Columns.NAME + " TEXT," +
            OptionContract.Columns.DISPLAY_NAME + " TEXT," +
            OptionContract.Columns.CREATED + " TEXT," +
            OptionContract.Columns.LAST_UPDATED + " TEXT," +
            OptionContract.Columns.OPTION_SET + " TEXT NOT NULL," +
            "FOREIGN KEY (" + OptionContract.Columns.OPTION_SET + ") REFERENCES " + Tables.OPTION_SET +
            " (" + OptionSetContract.Columns.UID + ") ON DELETE CASCADE" +
            ");";

    private static final String CREATE_PROGRAM_TABLE = "CREATE TABLE " + Tables.PROGRAM + " (" +
            ProgramContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramContract.Columns.CODE + " TEXT," +
            ProgramContract.Columns.NAME + " TEXT," +
            ProgramContract.Columns.DISPLAY_NAME + " TEXT," +
            ProgramContract.Columns.CREATED + " TEXT," +
            ProgramContract.Columns.LAST_UPDATED + " TEXT," +
            ProgramContract.Columns.SHORT_NAME + " TEXT," +
            ProgramContract.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            ProgramContract.Columns.DESCRIPTION + " TEXT," +
            ProgramContract.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            ProgramContract.Columns.VERSION + " INTEGER," +
            ProgramContract.Columns.ONLY_ENROLL_ONCE + " INTEGER," +
            ProgramContract.Columns.ENROLLMENT_DATE_LABEL + " TEXT," +
            ProgramContract.Columns.DISPLAY_INCIDENT_DATE + " INTEGER," +
            ProgramContract.Columns.INCIDENT_DATE_LABEL + " TEXT," +
            ProgramContract.Columns.REGISTRATION + " INTEGER," +
            ProgramContract.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE + " INTEGER," +
            ProgramContract.Columns.DATA_ENTRY_METHOD + " INTEGER," +
            ProgramContract.Columns.IGNORE_OVERDUE_EVENTS + " INTEGER," +
            ProgramContract.Columns.RELATIONSHIP_FROM_A + " INTEGER," +
            ProgramContract.Columns.SELECT_INCIDENT_DATES_IN_FUTURE + " INTEGER," +
            ProgramContract.Columns.CAPTURE_COORDINATES + " INTEGER," +
            ProgramContract.Columns.USE_FIRST_STAGE_DURING_REGISTRATION + " INTEGER," +
            ProgramContract.Columns.DISPLAY_FRONT_PAGE_LIST + " INTEGER," +
            ProgramContract.Columns.PROGRAM_TYPE + " TEXT," +
            ProgramContract.Columns.RELATIONSHIP_TYPE + " TEXT," +
            ProgramContract.Columns.RELATIONSHIP_TEXT + " TEXT," +
            ProgramContract.Columns.RELATED_PROGRAM + " TEXT" +
            ");";

    private static final String CREATE_TRACKED_ENTITY_TABLE = "CREATE TABLE " + Tables.TRACKED_ENTITY + " (" +
            TrackedEntityContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            TrackedEntityContract.Columns.CODE + " TEXT," +
            TrackedEntityContract.Columns.NAME + " TEXT," +
            TrackedEntityContract.Columns.DISPLAY_NAME + " TEXT," +
            TrackedEntityContract.Columns.CREATED + " TEXT," +
            TrackedEntityContract.Columns.LAST_UPDATED + " TEXT," +
            TrackedEntityContract.Columns.SHORT_NAME + " TEXT," +
            TrackedEntityContract.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            TrackedEntityContract.Columns.DESCRIPTION + " TEXT," +
            TrackedEntityContract.Columns.DISPLAY_DESCRIPTION + " TEXT" +
            ");";

    private static final String CREATE_DATA_ELEMENT_TABLE = "CREATE TABLE " + Tables.DATA_ELEMENT + " (" +
            DataElementContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataElementContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            DataElementContract.Columns.CODE + " TEXT," +
            DataElementContract.Columns.NAME + " TEXT," +
            DataElementContract.Columns.DISPLAY_NAME + " TEXT," +
            DataElementContract.Columns.CREATED + " TEXT," +
            DataElementContract.Columns.LAST_UPDATED + " TEXT," +
            DataElementContract.Columns.SHORT_NAME + " TEXT," +
            DataElementContract.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            DataElementContract.Columns.DESCRIPTION + " TEXT," +
            DataElementContract.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            DataElementContract.Columns.VALUE_TYPE + " TEXT," +
            DataElementContract.Columns.ZERO_IS_SIGNIFICANT + " INTEGER," +
            DataElementContract.Columns.AGGREGATION_OPERATOR + " TEXT," +
            DataElementContract.Columns.FORM_NAME + " TEXT," +
            DataElementContract.Columns.NUMBER_TYPE + " TEXT," +
            DataElementContract.Columns.DOMAIN_TYPE + " TEXT," +
            DataElementContract.Columns.DIMENSION + " TEXT," +
            DataElementContract.Columns.DISPLAY_FORM_NAME + " TEXT," +
            DataElementContract.Columns.OPTION_SET + " TEXT," +
            " FOREIGN KEY ( " + DataElementContract.Columns.OPTION_SET + ")" +
            " REFERENCES " + Tables.OPTION_SET + " (" + OptionSetContract.Columns.UID + ")" +
            ");";

    private static final String CREATE_PROGRAM_STAGE_DATA_ELEMENT_TABLE = "CREATE TABLE " +
            Tables.PROGRAM_STAGE_DATA_ELEMENT + " (" +
            ProgramStageDataElementContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramStageDataElementContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramStageDataElementContract.Columns.CODE + " TEXT," +
            ProgramStageDataElementContract.Columns.NAME + " TEXT," +
            ProgramStageDataElementContract.Columns.DISPLAY_NAME + " TEXT," +
            ProgramStageDataElementContract.Columns.CREATED + " TEXT," +
            ProgramStageDataElementContract.Columns.LAST_UPDATED + " TEXT," +
            ProgramStageDataElementContract.Columns.DISPLAY_IN_REPORTS + " INTEGER NOT NULL," +
            ProgramStageDataElementContract.Columns.COMPULSORY + " INTEGER NOT NULL," +
            ProgramStageDataElementContract.Columns.ALLOW_PROVIDED_ELSEWHERE + " INTEGER NOT NULL," +
            ProgramStageDataElementContract.Columns.SORT_ORDER + " INTEGER," +
            ProgramStageDataElementContract.Columns.ALLOW_FUTURE_DATE + " INTEGER NOT NULL," +
            ProgramStageDataElementContract.Columns.DATA_ELEMENT + " TEXT NOT NULL," +
            ProgramStageDataElementContract.Columns.PROGRAM_STAGE_SECTION + " TEXT," +
            " FOREIGN KEY (" + ProgramStageDataElementContract.Columns.DATA_ELEMENT + ")" +
            "REFERENCES " + Tables.DATA_ELEMENT + " (" + DataElementContract.Columns.UID + ")" +
            "ON DELETE CASCADE," +
            "FOREIGN KEY (" + ProgramStageDataElementContract.Columns.PROGRAM_STAGE_SECTION + ")" +
            "REFERENCES " + Tables.PROGRAM_STAGE_SECTION + " (" + ProgramStageSectionContract.Columns.UID + ")" +
            "ON DELETE CASCADE" +
            ");";

    private static final String CREATE_RELATIONSHIP_TABLE =
            "CREATE TABLE " + Tables.RELATIONSHIP_TABLE + "(" +
                    RelationshipContract.Columns.ID + " INTEGER PRIMARY KEY," +
                    RelationshipContract.Columns.TRACKED_ENTITY_INSTANCE_A + " TEXT," +
                    RelationshipContract.Columns.TRACKED_ENTITY_INSTANCE_B + " TEXT," +
                    RelationshipContract.Columns.RELATIONSHIP_TYPE + " TEXT NOT NULL," +
                    "FOREIGN KEY (" + RelationshipContract.Columns.RELATIONSHIP_TYPE + ") " +
                    "REFERENCES " + Tables.RELATIONSHIP_TYPE +
                    " (" + RelationshipTypeContract.Columns.UID + ")" +
                    ");";

    private static final String CREATE_RELATIONSHIP_TYPE_TABLE = "CREATE TABLE " +
            Tables.RELATIONSHIP_TYPE + "( " +
            RelationshipTypeContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            RelationshipTypeContract.Columns.UID + " TEXT NOT NULL UNIQUE, " +
            RelationshipTypeContract.Columns.CODE + " TEXT, " +
            RelationshipTypeContract.Columns.NAME + " TEXT, " +
            RelationshipTypeContract.Columns.DISPLAY_NAME + " TEXT, " +
            RelationshipTypeContract.Columns.CREATED + " TEXT, " +
            RelationshipTypeContract.Columns.LAST_UPDATED + " TEXT, " +
            RelationshipTypeContract.Columns.B_IS_TO_A + " TEXT, " +
            RelationshipTypeContract.Columns.A_IS_TO_B + " TEXT " +
            ");";

    private static final String CREATE_PROGRAM_STAGE_SECTION_TABLE = "CREATE TABLE " +
            Tables.PROGRAM_STAGE_SECTION + " (" +
            ProgramStageSectionContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramStageSectionContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramStageSectionContract.Columns.CODE + " TEXT," +
            ProgramStageSectionContract.Columns.NAME + " TEXT," +
            ProgramStageSectionContract.Columns.DISPLAY_NAME + " TEXT," +
            ProgramStageSectionContract.Columns.CREATED + " TEXT," +
            ProgramStageSectionContract.Columns.LAST_UPDATED + " TEXT," +
            ProgramStageSectionContract.Columns.SORT_ORDER + " INTEGER," +
            ProgramStageSectionContract.Columns.PROGRAM_STAGE + " TEXT NOT NULL," +
            " FOREIGN KEY ( " + ProgramStageSectionContract.Columns.PROGRAM_STAGE + ")" +
            " REFERENCES " + Tables.PROGRAM_STAGE + " (" + ProgramStageContract.Columns.UID + ")" +
            ");";

    private static final String CREATE_PROGRAM_STAGE_TABLE = "CREATE TABLE " +
            Tables.PROGRAM_STAGE + " (" +
            ProgramStageContract.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramStageContract.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramStageContract.Columns.CODE + " TEXT," +
            ProgramStageContract.Columns.NAME + " TEXT," +
            ProgramStageContract.Columns.DISPLAY_NAME + " TEXT," +
            ProgramStageContract.Columns.CREATED + " TEXT," +
            ProgramStageContract.Columns.LAST_UPDATED + " TEXT," +
            ProgramStageContract.Columns.EXECUTION_DATE_LABEL + " TEXT," +
            ProgramStageContract.Columns.ALLOW_GENERATE_NEXT_VISIT + " INTEGER," +
            ProgramStageContract.Columns.VALID_COMPLETE_ONLY + " INTEGER," +
            ProgramStageContract.Columns.REPORT_DATE_TO_USE + " TEXT," +
            ProgramStageContract.Columns.OPEN_AFTER_ENROLLMENT + " INTEGER," +
            ProgramStageContract.Columns.REPEATABLE + " INTEGER," +
            ProgramStageContract.Columns.CAPTURE_COORDINATES + " INTEGER," +
            ProgramStageContract.Columns.FORM_TYPE + " TEXT," +
            ProgramStageContract.Columns.DISPLAY_GENERATE_EVENT_BOX + " INTEGER," +
            ProgramStageContract.Columns.GENERATED_BY_ENROLMENT_DATE + " INTEGER," +
            ProgramStageContract.Columns.AUTO_GENERATE_EVENT + " INTEGER," +
            ProgramStageContract.Columns.SORT_ORDER + " INTEGER," +
            ProgramStageContract.Columns.HIDE_DUE_DATE + " INTEGER," +
            ProgramStageContract.Columns.BLOCK_ENTRY_FORM + " INTEGER," +
            ProgramStageContract.Columns.MIN_DAYS_FROM_START + " INTEGER," +
            ProgramStageContract.Columns.STANDARD_INTERVAL + " INTEGER," +
            ProgramStageContract.Columns.PROGRAM + " TEXT NOT NULL," +
            " FOREIGN KEY ( " + ProgramStageContract.Columns.PROGRAM + ")" +
            " REFERENCES " + Tables.PROGRAM + " (" + ProgramContract.Columns.UID + ")" +
            ");";

    /**
     * This method should be used only for testing purposes
     */
    // ToDo: Revise usage of this method
    @VisibleForTesting
    static SQLiteDatabase create() {
        return create(SQLiteDatabase.create(null));
    }

    private static SQLiteDatabase create(SQLiteDatabase database) {
        database.execSQL(CREATE_USER_TABLE);
        database.execSQL(CREATE_USER_CREDENTIALS_TABLE);
        database.execSQL(CREATE_ORGANISATION_UNITS_TABLE);
        database.execSQL(CREATE_USER_ORGANISATION_UNIT_TABLE);
        database.execSQL(CREATE_AUTHENTICATED_USER_TABLE);
        database.execSQL(CREATE_OPTION_SET_TABLE);
        database.execSQL(CREATE_OPTION_TABLE);
        database.execSQL(CREATE_PROGRAM_TABLE);
        database.execSQL(CREATE_TRACKED_ENTITY_TABLE);
        database.execSQL(CREATE_DATA_ELEMENT_TABLE);
        database.execSQL(CREATE_PROGRAM_STAGE_DATA_ELEMENT_TABLE);
        database.execSQL(CREATE_RELATIONSHIP_TABLE);
        database.execSQL(CREATE_RELATIONSHIP_TYPE_TABLE);
        database.execSQL(CREATE_PROGRAM_STAGE_SECTION_TABLE);
        database.execSQL(CREATE_PROGRAM_STAGE_TABLE);

        return database;
    }

    public DbOpenHelper(@NonNull Context context, @NonNull String databaseName) {
        super(context, databaseName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ToDo: logic for proper schema migration
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // enable foreign key support in database
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}
