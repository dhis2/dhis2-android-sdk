package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.commons.database.BaseIdentifiableObjectContract;
import org.hisp.dhis.android.core.commons.database.DbUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;

public final class UserContract {
    // ContentProvider related properties
    public static final String USERS = "Users";
    public static final String USERS_ID = USERS + "/#";
    public static final String USERS_ID_ORGANISATION_UNITS = USERS_ID + "/" +
            OrganisationUnitContract.ORGANISATION_UNIT;

    public static final String CONTENT_TYPE_DIR = DbUtils.directoryType(USERS);
    public static final String CONTENT_TYPE_ITEM = DbUtils.itemType(USERS);


    // sql schema
    public static final String USER = "User";
    public static final String CREATE_TABLE = "CREATE TABLE " + USER + " (" +
            Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Columns.UID + " TEXT NOT NULL UNIQUE," +
            Columns.CODE + " TEXT," +
            Columns.NAME + " TEXT," +
            Columns.DISPLAY_NAME + " TEXT," +
            Columns.CREATED + " TEXT," +
            Columns.LAST_UPDATED + " TEXT," +
            Columns.BIRTHDAY + " TEXT," +
            Columns.EDUCATION + " TEXT," +
            Columns.GENDER + " TEXT," +
            Columns.JOB_TITLE + " TEXT," +
            Columns.SURNAME + " TEXT," +
            Columns.FIRST_NAME + " TEXT," +
            Columns.INTRODUCTION + " TEXT," +
            Columns.EMPLOYER + " TEXT," +
            Columns.INTERESTS + " TEXT," +
            Columns.LANGUAGES + " TEXT," +
            Columns.EMAIL + " TEXT," +
            Columns.PHONE_NUMBER + " TEXT," +
            Columns.NATIONALITY + " TEXT" +
            ");";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + USER;

    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String BIRTHDAY = "birthday";
        String EDUCATION = "education";
        String GENDER = "gender";
        String JOB_TITLE = "jobTitle";
        String SURNAME = "surname";
        String FIRST_NAME = "firstName";
        String INTRODUCTION = "introduction";
        String EMPLOYER = "employer";
        String INTERESTS = "interests";
        String LANGUAGES = "languages";
        String EMAIL = "email";
        String PHONE_NUMBER = "phoneNumber";
        String NATIONALITY = "nationality";
    }
}
