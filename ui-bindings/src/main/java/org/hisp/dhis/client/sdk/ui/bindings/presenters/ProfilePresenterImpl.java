package org.hisp.dhis.client.sdk.ui.bindings.presenters;

import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.ui.SyncDateWrapper;
import org.hisp.dhis.client.sdk.ui.bindings.commons.DefaultAppAccountManager;
import org.hisp.dhis.client.sdk.ui.bindings.commons.DefaultNotificationHandler;
import org.hisp.dhis.client.sdk.ui.bindings.commons.RxOnValueChangedListener;
import org.hisp.dhis.client.sdk.ui.bindings.views.ProfileView;
import org.hisp.dhis.client.sdk.ui.bindings.views.View;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityCharSequence;
import org.hisp.dhis.client.sdk.ui.models.FormEntityDate;
import org.hisp.dhis.client.sdk.ui.models.FormEntityEditText;
import org.hisp.dhis.client.sdk.ui.models.FormEntityEditText.InputType;
import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.models.Picker;
import org.hisp.dhis.client.sdk.ui.rows.DatePickerRowView;
import org.hisp.dhis.client.sdk.utils.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;
import static org.hisp.dhis.client.sdk.utils.StringUtils.isEmpty;

public class ProfilePresenterImpl implements ProfilePresenter {
    static final String TAG = ProfilePresenter.class.getSimpleName();

    // callback which will be called when values change in view
    private final RxOnValueChangedListener onFormEntityChangeListener;
    private final CurrentUserInteractor currentUserAccountInteractor;
    private final Logger logger;
    private final DefaultNotificationHandler defaultNotificationHandler;

    private DefaultAppAccountManager appAccountManager;
    private SyncDateWrapper syncDateWrapper;

    private ProfileView profileView;
    private CompositeSubscription subscription;
    private UserAccount userAccount;

    public ProfilePresenterImpl(CurrentUserInteractor currentUserAccountInteractor,
                                SyncDateWrapper syncDateWrapper,
                                DefaultAppAccountManager appAccountManager,
                                DefaultNotificationHandler defaultNotificationHandler,
                                Logger logger) {
        this.onFormEntityChangeListener = new RxOnValueChangedListener();
        this.currentUserAccountInteractor = currentUserAccountInteractor;
        this.appAccountManager = appAccountManager;
        this.syncDateWrapper = syncDateWrapper;
        this.defaultNotificationHandler = defaultNotificationHandler;
        this.logger = logger;
    }

    @Override
    public void createUserAccountForm() {
        logger.d(TAG, "createUserAccountForm()");

        // kill previous subscription
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        // create a new one
        subscription = new CompositeSubscription();
        subscription.add(currentUserAccountInteractor.account().get()
                .map(new Func1<UserAccount, List<FormEntity>>() {
                    @Override
                    public List<FormEntity> call(UserAccount userAccount) {
                        return transformUserAccount(userAccount);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<FormEntity>>() {
                    @Override
                    public void call(List<FormEntity> entities) {
                        if (profileView != null) {
                            profileView.showUserAccountForm(entities);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        logger.d(TAG, throwable.getMessage(), throwable);
                    }
                }));

        // listening to events which UI emits, save them into database
        subscription.add(Observable.create(onFormEntityChangeListener)
                .debounce(512, TimeUnit.MILLISECONDS)
                .switchMap(new Func1<FormEntity, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(FormEntity formEntity) {
                        return onFormEntityChanged(formEntity);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isSaved) {
                        logger.d(TAG, String.format("UserAccount is saved: %s", isSaved));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        logger.e(TAG, throwable.getMessage(), throwable);
                    }
                }));
    }

    @Override
    public void attachView(View view) {
        isNull(view, "View must not be null");
        profileView = (ProfileView) view;

        // list account fields as soon as
        // presenter is attached to fragment
        createUserAccountForm();
    }

    @Override
    public void detachView() {
        profileView = null;

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    @Override
    public void sync() {
        if (profileView != null) {
            profileView.showProgressBar();
        }

        if (subscription == null) {
            subscription = new CompositeSubscription();
        }

        subscription.add(currentUserAccountInteractor.account().sync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserAccount>() {
                    @Override
                    public void call(UserAccount userAccount) {
                        logger.d(TAG, "UserAccount is successfully synced");

                        if (profileView != null) {
                            profileView.hideProgressBar();
                            createUserAccountForm();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        logger.e(TAG, "UserAccount syncMetaData() failed", throwable);

                        if (profileView != null) {
                            profileView.hideProgressBar();
                        }
                    }
                }));
    }

    @Override
    public void logout() {

        defaultNotificationHandler.removeAllNotifications();

        appAccountManager.removePeriodicSync();
        appAccountManager.removeAccount();

        // remove last synced (assume next user will be different)
        syncDateWrapper.clearLastSynced();

        currentUserAccountInteractor.signOut().toBlocking().first();
    }

    private List<FormEntity> transformUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;

        isNull(this.userAccount, "userAccount must not be null");
        isNull(this.profileView, "profileView must not be null");

        List<FormEntity> formEntities = new ArrayList<>();

        ///////////////////////////////////////////////////////////////////////////////
        // First name and surname user account fields
        ///////////////////////////////////////////////////////////////////////////////
        FormEntityEditText firstName = new FormEntityEditText(ProfileView.ID_FIRST_NAME,
                profileView.getUserAccountFieldLabel(ProfileView.ID_FIRST_NAME), InputType.TEXT);
        firstName.setValue(userAccount.getFirstName(), false);
        firstName.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(firstName);

        FormEntityEditText surname = new FormEntityEditText(ProfileView.ID_SURNAME,
                profileView.getUserAccountFieldLabel(ProfileView.ID_SURNAME), InputType.TEXT);
        surname.setValue(userAccount.getSurname(), false);
        surname.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(surname);

        ///////////////////////////////////////////////////////////////////////////////
        // Building gender picker
        ///////////////////////////////////////////////////////////////////////////////
        Picker genderPicker = new Picker.Builder()
                .hint(profileView.getUserAccountFieldLabel(ProfileView.ID_GENDER))
                .build();
        Picker pickerItemMale = new Picker.Builder()
                .id(UserAccount.GENDER_MALE)
                .name(profileView.getUserAccountFieldLabel(ProfileView.ID_GENDER_MALE))
                .parent(genderPicker)
                .build();
        Picker pickerItemFemale = new Picker.Builder()
                .id(UserAccount.GENDER_FEMALE)
                .name(profileView.getUserAccountFieldLabel(ProfileView.ID_GENDER_FEMALE))
                .parent(genderPicker)
                .build();
        Picker pickerItemOther = new Picker.Builder()
                .id(UserAccount.GENDER_OTHER)
                .name(profileView.getUserAccountFieldLabel(ProfileView.ID_GENDER_OTHER))
                .parent(genderPicker)
                .build();

        genderPicker.addChild(pickerItemMale);
        genderPicker.addChild(pickerItemFemale);
        genderPicker.addChild(pickerItemOther);

        if (UserAccount.GENDER_MALE.equals(userAccount.getGender())) {
            genderPicker.setSelectedChild(pickerItemMale);
        } else if (UserAccount.GENDER_FEMALE.equals(userAccount.getGender())) {
            genderPicker.setSelectedChild(pickerItemFemale);
        } else if (UserAccount.GENDER_OTHER.equals(userAccount.getGender())) {
            genderPicker.setSelectedChild(pickerItemOther);
        }

        FormEntityFilter gender = new FormEntityFilter(ProfileView.ID_GENDER,
                profileView.getUserAccountFieldLabel(ProfileView.ID_GENDER));
        gender.setPicker(genderPicker);
        gender.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(gender);

        ///////////////////////////////////////////////////////////////////////////////
        // Other user account fields
        ///////////////////////////////////////////////////////////////////////////////

        // formatting the string
        String birthdayString = "";
        if (!isEmpty(userAccount.getBirthday())) {
            DateTime birthdayDate = DateTime.parse(userAccount.getBirthday());
            birthdayString = birthdayDate.toString(DatePickerRowView.DATE_FORMAT);
        }

        FormEntityDate birthday = new FormEntityDate(ProfileView.ID_BIRTHDAY,
                profileView.getUserAccountFieldLabel(ProfileView.ID_BIRTHDAY));
        birthday.setValue(birthdayString, false);
        birthday.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(birthday);

        FormEntityEditText introduction = new FormEntityEditText(ProfileView.ID_INTRODUCTION,
                profileView.getUserAccountFieldLabel(ProfileView.ID_INTRODUCTION), InputType.TEXT);
        introduction.setValue(userAccount.getIntroduction(), false);
        introduction.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(introduction);

        FormEntityEditText education = new FormEntityEditText(ProfileView.ID_EDUCATION,
                profileView.getUserAccountFieldLabel(ProfileView.ID_EDUCATION), InputType.TEXT);
        education.setValue(userAccount.getEducation(), false);
        education.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(education);

        FormEntityEditText employer = new FormEntityEditText(ProfileView.ID_EMPLOYER,
                profileView.getUserAccountFieldLabel(ProfileView.ID_EMPLOYER), InputType.TEXT);
        employer.setValue(userAccount.getEmployer(), false);
        employer.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(employer);

        FormEntityEditText interests = new FormEntityEditText(ProfileView.ID_INTERESTS,
                profileView.getUserAccountFieldLabel(ProfileView.ID_INTERESTS), InputType.TEXT);
        interests.setValue(userAccount.getInterests(), false);
        interests.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(interests);

        FormEntityEditText jobTitle = new FormEntityEditText(ProfileView.ID_JOB_TITLE,
                profileView.getUserAccountFieldLabel(ProfileView.ID_JOB_TITLE), InputType.TEXT);
        jobTitle.setValue(userAccount.getJobTitle(), false);
        jobTitle.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(jobTitle);

        FormEntityEditText languages = new FormEntityEditText(ProfileView.ID_LANGUAGES,
                profileView.getUserAccountFieldLabel(ProfileView.ID_LANGUAGES), InputType.TEXT);
        languages.setValue(userAccount.getLanguages(), false);
        languages.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(languages);

        FormEntityEditText email = new FormEntityEditText(ProfileView.ID_EMAIL,
                profileView.getUserAccountFieldLabel(ProfileView.ID_EMAIL), InputType.TEXT);
        email.setValue(userAccount.getEmail(), false);
        email.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(email);

        FormEntityEditText phoneNumber = new FormEntityEditText(ProfileView.ID_PHONE_NUMBER,
                profileView.getUserAccountFieldLabel(ProfileView.ID_PHONE_NUMBER), InputType.TEXT);
        phoneNumber.setValue(userAccount.getPhoneNumber(), false);
        phoneNumber.setOnFormEntityChangeListener(onFormEntityChangeListener);
        formEntities.add(phoneNumber);

        return formEntities;
    }

    private Observable<Boolean> onFormEntityChanged(FormEntity formEntity) {
        if (userAccount == null) {
            logger.e(TAG, "onFormEntityChanged() is called without UserAccount");
            throw new IllegalArgumentException("No UserAccount instance is found");
        }

        String label = formEntity.getLabel();
        String value = "";

        if (formEntity instanceof FormEntityCharSequence) {
            FormEntityCharSequence formEntityCharSequence = (FormEntityCharSequence) formEntity;
            value = formEntityCharSequence.getValue().toString();
        }

        if (formEntity instanceof FormEntityFilter) {
            FormEntityFilter formEntityFilter = (FormEntityFilter) formEntity;

            if (formEntityFilter.getPicker() != null &&
                    formEntityFilter.getPicker().getSelectedChild() != null) {
                value = formEntityFilter.getPicker().getSelectedChild().getId();
            }
        }

        logger.d(TAG, String.format("New value '%s' is emitted for field: '%s'",
                value, label));

        switch (formEntity.getId()) {
            case ProfileView.ID_FIRST_NAME: {
                userAccount.setFirstName(value);
                break;
            }
            case ProfileView.ID_SURNAME: {
                userAccount.setSurname(value);
                break;
            }
            case ProfileView.ID_GENDER: {
                userAccount.setGender(value);
                break;
            }
            case ProfileView.ID_BIRTHDAY: {
                userAccount.setBirthday(value);
                break;
            }
            case ProfileView.ID_INTRODUCTION: {
                userAccount.setIntroduction(value);
                break;
            }
            case ProfileView.ID_EDUCATION: {
                userAccount.setEducation(value);
                break;
            }
            case ProfileView.ID_EMPLOYER: {
                userAccount.setEmployer(value);
                break;
            }
            case ProfileView.ID_INTERESTS: {
                userAccount.setInterests(value);
                break;
            }
            case ProfileView.ID_JOB_TITLE: {
                userAccount.setJobTitle(value);
                break;
            }
            case ProfileView.ID_LANGUAGES: {
                userAccount.setLanguages(value);
                break;
            }
            case ProfileView.ID_EMAIL: {
                userAccount.setEmail(value);
                break;
            }
            case ProfileView.ID_PHONE_NUMBER: {
                userAccount.setPhoneNumber(value);
                break;
            }
        }

        return currentUserAccountInteractor.account().save(userAccount);
    }
}
