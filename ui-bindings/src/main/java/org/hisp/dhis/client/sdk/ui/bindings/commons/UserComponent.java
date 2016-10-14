package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;

public interface UserComponent {
    D2 sdkInstance();

    HomePresenter homePresenter();

    LoginPresenter loginPresenter();

    ProfilePresenter profilePresenter();

    SettingsPresenter settingsPresenter();

    LauncherPresenter launcherPresenter();
}
