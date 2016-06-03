package org.hisp.dhis.client.sdk.ui.bindings.views;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import org.hisp.dhis.client.sdk.ui.models.FormEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface ProfileView extends View {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            ID_FIRST_NAME,
            ID_SURNAME,
            ID_GENDER,
            ID_GENDER_MALE,
            ID_GENDER_FEMALE,
            ID_GENDER_OTHER,
            ID_BIRTHDAY,
            ID_INTRODUCTION,
            ID_EDUCATION,
            ID_EMPLOYER,
            ID_INTERESTS,
            ID_JOB_TITLE,
            ID_LANGUAGES,
            ID_EMAIL,
            ID_PHONE_NUMBER
    })
    @interface UserAccountFieldId {
    }

    String ID_FIRST_NAME = "id:firstName";
    String ID_SURNAME = "id:surname";
    String ID_GENDER = "id:gender";
    String ID_GENDER_MALE = "id:genderMale";
    String ID_GENDER_FEMALE = "id:genderFemale";
    String ID_GENDER_OTHER = "id:other";
    String ID_BIRTHDAY = "id:birthday";
    String ID_INTRODUCTION = "id:introduction";
    String ID_EDUCATION = "id:education";
    String ID_EMPLOYER = "id:employer";
    String ID_INTERESTS = "id:interests";
    String ID_JOB_TITLE = "id:jobTitle";
    String ID_LANGUAGES = "id:languages";
    String ID_EMAIL = "id:email";
    String ID_PHONE_NUMBER = "id:phoneNumber";

    void showProgressBar();

    void hideProgressBar();

    void showUserAccountForm(List<FormEntity> formEntities);

    String getUserAccountFieldLabel(@NonNull @UserAccountFieldId String fieldId);
}
