package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public final class UserContract {
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
