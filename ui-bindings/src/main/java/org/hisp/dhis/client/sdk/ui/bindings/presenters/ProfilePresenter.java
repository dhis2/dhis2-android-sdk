package org.hisp.dhis.client.sdk.ui.bindings.presenters;

public interface ProfilePresenter extends Presenter {
    void createUserAccountForm();

    void sync();

    void logout();
}
