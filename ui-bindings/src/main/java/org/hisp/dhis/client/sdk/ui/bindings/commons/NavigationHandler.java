package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.ui.activities.AbsHomeActivity;
import org.hisp.dhis.client.sdk.ui.activities.AbsLoginActivity;

public class NavigationHandler {
    private static NavigationHandler navigationHandler = new NavigationHandler();

    private Class<? extends AbsLoginActivity> loginActivity;
    private Class<? extends AbsHomeActivity> homeActivity;

    private NavigationHandler() {
        // empty constructor
    }

    public static void loginActivity(Class<? extends AbsLoginActivity> loginActivity) {
        navigationHandler.loginActivity = loginActivity;
    }

    public static void homeActivity(Class<? extends AbsHomeActivity> homeActivity) {
        navigationHandler.homeActivity = homeActivity;
    }

    public static Class<? extends AbsLoginActivity> loginActivity() {
        return navigationHandler.loginActivity;
    }

    public static Class<? extends AbsHomeActivity> homeActivity() {
        return navigationHandler.homeActivity;
    }

    public static void reset() {
        navigationHandler = new NavigationHandler();
    }
}
