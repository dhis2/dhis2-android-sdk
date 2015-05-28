package org.hisp.dhis.android.sdk.activities;

import android.support.v4.app.Fragment;

/**
 * Created by araz on 31.03.2015.
 */
public interface INavigationHandler {
    void switchFragment(Fragment fragment, String tag, boolean addToBackStack);
    public void setBackPressedListener(OnBackPressedListener backPressedListener);
    void onBackPressed();
}
