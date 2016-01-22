package org.hisp.dhis.client.sdk.ui.presenters;

public abstract class BasePresenter {

    /**
     * Called when the presenter is initialized, this method represents the start of the presenter
     * lifecycle.
     */
    public abstract void onCreate();

    /**
     * Called when the presenter is resumed. After the onCreate and when the presenter comes
     * from a pause state.
     */
    public abstract void onResume();

    /**
     * Called when the presenter is paused.
     */
    public abstract void onPause();
}
