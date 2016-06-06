package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class Inject {
    // singleton scope
    private static Inject inject;
    private final AppModule appModule;

    // user component which performs injection
    private UserModule userModule;
    private UserComponent userComponent;

    private Inject(AppModule appModule) {
        this.appModule = appModule;
    }

    public static void init(Context context) {
        inject = new Inject(new AppModuleImpl(context));
    }

    public static void init(AppModule appModule) {
        inject = new Inject(appModule);
    }

    public static UserComponent createUserComponent(String serverUrl) {
        isNull(inject, "you must call init first");

        inject.userModule = new UserModuleImpl(inject.appModule, serverUrl);
        inject.userComponent = new UserComponent(inject.appModule, inject.userModule);

        return inject.userComponent;
    }

    public static UserComponent getUserComponent() {
        isNull(inject, "you must call init first");

        return inject.userComponent;
    }

    public static void releaseUserComponent() {
        isNull(inject, "you must call init first");

        inject.userModule = null;
        inject.userComponent = null;
    }
}
