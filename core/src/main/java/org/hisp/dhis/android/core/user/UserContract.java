package org.hisp.dhis.android.core.user;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.commons.BaseIdentifiableObjectContract;
import org.hisp.dhis.android.core.database.DbContract;
import org.hisp.dhis.android.core.database.DbUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;

public final class UserContract {
    // ContentProvider related properties
    public static final String USERS = "users";
    public static final String USERS_ID = USERS + "/#";
    public static final String USERS_ID_ORGANISATION_UNITS = USERS_ID + "/" +
            OrganisationUnitContract.ORGANISATION_UNITS;

    public static final String CONTENT_TYPE_DIR = DbUtils.directoryType(USERS);
    public static final String CONTENT_TYPE_ITEM = DbUtils.itemType(USERS);

    @NonNull
    public static Uri users() {
        return Uri.withAppendedPath(DbContract.AUTHORITY_URI, USERS);
    }

    @NonNull
    public static Uri users(long id) {
        return ContentUris.withAppendedId(users(), id);
    }

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
