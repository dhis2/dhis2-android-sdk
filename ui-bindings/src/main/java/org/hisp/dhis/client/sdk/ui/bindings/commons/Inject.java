package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class Inject {
    // singleton scope
    private static Inject inject;

    private DefaultAppModule defaultAppModule;
    private DefaultUserModule defaultUserModule;
    private UserComponent userComponent;

    private ModuleProvider moduleProvider;

    private Inject(ModuleProvider provider) {
        this.moduleProvider = isNull(provider,
                "ModuleProvider must not be null");
        this.defaultAppModule = isNull(provider.provideAppModule(),
                "DefaultAppModule must not be null");
        this.defaultUserModule = isNull(provider.provideUserModule(null),
                "DefaultUserModule must not be null");
        this.userComponent = new UserComponent(defaultAppModule, defaultUserModule);
    }

    private Inject(DefaultAppModule appModule, DefaultUserModule userModule) {
        this.defaultAppModule = isNull(appModule, "DefaultAppModule must not be null");
        this.defaultUserModule = isNull(userModule, "DefaultUserModule must not be null");
    }

    public static void init(Context context, String authority, String accountType) {
        DefaultAppModule defaultAppModule = new DefaultAppModuleImpl(context);
        DefaultUserModule defaultUserModule = new DefaultUserModuleImpl(authority, accountType);

        inject = new Inject(defaultAppModule, defaultUserModule);
    }

    public static void init(ModuleProvider provider) {
        inject = new Inject(provider);
    }

    public static UserComponent createUserComponent(String serverUrl, String authority, String accountType) {
        isNull(inject, "you must call init first");

        if (inject.moduleProvider != null) {
            inject.defaultUserModule = inject.moduleProvider.provideUserModule(serverUrl);
        } else {
            inject.defaultUserModule = new DefaultUserModuleImpl(serverUrl, authority, accountType);
        }

        inject.userComponent = new UserComponent(
                inject.defaultAppModule, inject.defaultUserModule);

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

    public interface ModuleProvider {
        DefaultAppModule provideAppModule();

        DefaultUserModule provideUserModule(String serverUrl);
    }
}
