package org.hisp.dhis.client.sdk.ui.bindings;

import android.app.Application;

public class App extends Application {
    private AppComponent2 appComponent;
    private UserComponent2 userComponent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static App from(Application application) {
        if (application instanceof App) {
            return (App) application;
        }

        throw new IllegalArgumentException("application must be instance of App");
    }

    public AppComponent2 getAppComponent() {
        return appComponent;
    }

    public UserComponent2 getUserComponent() {
        return userComponent;
    }

    public UserComponent2 createUserComponent(String serverUrl) {
        return null;
    }
}
