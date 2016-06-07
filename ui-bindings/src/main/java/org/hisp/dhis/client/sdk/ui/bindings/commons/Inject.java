package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class Inject {
    // singleton scope
    private static Inject inject;
    private final DefaultAppModule defaultAppModule;

    // user component which performs injection
    private DefaultUserModule defaultUserModule;
    private UserComponent userComponent;

    private Inject(DefaultAppModule defaultAppModule, DefaultUserModule defaultUserModule) {
        this.defaultAppModule = defaultAppModule;
        this.defaultUserModule = defaultUserModule;
        this.userComponent = new UserComponent(defaultAppModule, defaultUserModule);
    }

    public static void init(Context context, String authority, String accountType) {
        DefaultAppModule defaultAppModule = new DefaultAppModuleImpl(context, authority, accountType);
        DefaultUserModule defaultUserModule = new DefaultUserModuleImpl();

        inject = new Inject(defaultAppModule, defaultUserModule);
    }

    public static void init(DefaultAppModule defaultAppModule, DefaultUserModule defaultUserModule) {
        inject = new Inject(defaultAppModule, defaultUserModule);
    }

    public static UserComponent createUserComponent(String serverUrl) {
        isNull(inject, "you must call init first");

        inject.defaultUserModule = new DefaultUserModuleImpl(serverUrl);
        inject.userComponent = new UserComponent(inject.defaultAppModule, inject.defaultUserModule);

        return inject.userComponent;
    }

    public static UserComponent getUserComponent() {
        isNull(inject, "you must call init first");

        return inject.userComponent;
    }

    public static void releaseUserComponent() {
        isNull(inject, "you must call init first");

        inject.defaultUserModule = null;
        inject.userComponent = null;
    }
}
