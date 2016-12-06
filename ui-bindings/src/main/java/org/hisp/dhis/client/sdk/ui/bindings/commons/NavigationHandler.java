package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.ui.activities.AbsHomeActivity;
import org.hisp.dhis.client.sdk.ui.activities.AbsLoginActivity;
import org.hisp.dhis.client.sdk.ui.activities.AbsSynchronizeActivity;

public class NavigationHandler {
    private static NavigationHandler navigationHandler = new NavigationHandler();

    private Class<? extends AbsLoginActivity> loginActivity;
    private Class<? extends AbsHomeActivity> homeActivity;
    private Class<? extends AbsSynchronizeActivity> synchronizeActivity;

    private NavigationHandler() {
        // empty constructor
    }

    public static void loginActivity(Class<? extends AbsLoginActivity> loginActivity) {
        navigationHandler.loginActivity = loginActivity;
    }

    public static void homeActivity(Class<? extends AbsHomeActivity> homeActivity) {
        navigationHandler.homeActivity = homeActivity;
    }

    public static void synchronizeActivity(Class<? extends AbsSynchronizeActivity> synchronizeActivity) {
        navigationHandler.synchronizeActivity = synchronizeActivity;
    }

    public static Class<? extends AbsLoginActivity> loginActivity() {
        return navigationHandler.loginActivity;
    }

    public static Class<? extends AbsHomeActivity> homeActivity() {
        return navigationHandler.homeActivity;
    }

    public static Class<? extends AbsSynchronizeActivity> synchronizeActivity() {
        return navigationHandler.synchronizeActivity;
    }

    public static void reset() {
        navigationHandler = new NavigationHandler();
    }
}
