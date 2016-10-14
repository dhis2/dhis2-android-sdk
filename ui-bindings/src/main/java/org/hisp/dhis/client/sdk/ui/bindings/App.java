package org.hisp.dhis.client.sdk.ui.bindings;

import android.app.Application;

import org.hisp.dhis.client.sdk.ui.bindings.commons.AppComponent;
import org.hisp.dhis.client.sdk.ui.bindings.commons.DefaultAppComponent;
import org.hisp.dhis.client.sdk.ui.bindings.commons.DefaultUserComponent;
import org.hisp.dhis.client.sdk.ui.bindings.commons.UserComponent;

public abstract class App extends Application {
    private AppComponent appComponent;
    private UserComponent userComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // create default application component on start
        appComponent = new DefaultAppComponent(this);
        userComponent = new DefaultUserComponent(appComponent);
    }

    public static App from(Application application) {
        if (application instanceof App) {
            return (App) application;
        }

        throw new IllegalArgumentException("application must be instance of App");
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }

    // replace instance of existing user component with a new one
    public UserComponent createUserComponent(String serverUrl) {
        return (userComponent = new DefaultUserComponent(appComponent, serverUrl));
    }
}
