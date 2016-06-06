package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class Inject {
    private static Inject inject;

    private final InjectionComponent injectionComponent;

    private Inject(InjectionComponent injectionComponent) {
        this.injectionComponent = injectionComponent;
    }

    public static void init(Context context) {
        inject = new Inject(new InjectionComponent(context));
    }

    public static void init(InjectionComponent injectionComponent) {
        inject = new Inject(injectionComponent);
    }

    public static InjectionComponent getComponent() {
        isNull(inject, "you must call init first");

        return inject.injectionComponent;
    }
}
