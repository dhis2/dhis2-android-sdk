package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface InjectionModule {
    Logger providesLogger();

    ProfilePresenter providesProfilePresenter();
}
