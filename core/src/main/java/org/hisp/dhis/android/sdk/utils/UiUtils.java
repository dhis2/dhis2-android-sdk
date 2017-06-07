/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.events.LoadingMessageEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseSerializableModel;
import org.hisp.dhis.android.sdk.ui.dialogs.CustomDialogFragment;
import org.hisp.dhis.android.sdk.ui.dialogs.ItemStatusDialogFragment;
import org.hisp.dhis.android.sdk.ui.fragments.progressdialog.ProgressDialogFragment;

/**
 * @author Simen Skogly Russnes on 25.08.15.
 */
public final class UiUtils {

    private static UiUtils uiUtils;
    private ProgressDialogFragment progressDialogFragment;

    static {
        uiUtils = new UiUtils();
    }

    private ProgressDialogFragment getProgressDialogFragment() {
        return progressDialogFragment;
    }

    private void setProgressDialogFragment(ProgressDialogFragment progressDialogFragment) {
        this.progressDialogFragment = progressDialogFragment;
    }

    private static UiUtils getInstance() {
        return uiUtils;
    }

    private UiUtils() {}

    public static void showErrorDialog(final Activity activity, final String title, final String message) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CustomDialogFragment(title, message,
                        "OK", null).show(activity.getFragmentManager(), title);
            }
        });
    }

    public static void showErrorDialog(final Activity activity, final String title,
                                       final String message, final int iconId) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CustomDialogFragment(title, message,
                        "OK", iconId, null).show(activity.getFragmentManager(), title);
            }
        });
    }

    public static void showErrorDialog(final Activity activity, final String title, final String message, final DialogInterface.OnClickListener onConfirmClickListener) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CustomDialogFragment(title, message,
                        "OK", onConfirmClickListener).show(activity.getFragmentManager(), title);
            }
        });
    }

    public static void showConfirmDialog(final Activity activity, final String title, final String message,
                                         final String confirmOption, final String cancelOption,
                                         DialogInterface.OnClickListener onClickListener) {
        new CustomDialogFragment(title, message, confirmOption, cancelOption, onClickListener).
                show(activity.getFragmentManager(), title);
    }
    public static void showConfirmDialog(final Activity activity, final String title, final String message,
                                         final String confirmOption,
                                         DialogInterface.OnClickListener onClickListener) {
        new CustomDialogFragment(title, message, confirmOption, onClickListener).
                show(activity.getFragmentManager(), title);
    }
    public static void showConfirmDialog(final Activity activity, final String title, final String message,
                                         final String confirmOption, final String cancelOption,
                                         final int iconId,
                                         DialogInterface.OnClickListener onClickListener) {
        new CustomDialogFragment(title, message, confirmOption, cancelOption,iconId ,  onClickListener).
                show(activity.getFragmentManager(), title);
    }

    public static void showConfirmDialog(final Activity activity, final String title, final String message,
                                         final String confirmOption, final String cancelOption,
                                         DialogInterface.OnClickListener onConfirmListener,
                                         DialogInterface.OnClickListener onCancelListener) {
        new CustomDialogFragment(title, message, confirmOption, cancelOption, onConfirmListener,
                onCancelListener).
                show(activity.getFragmentManager(), title);
    }

    public static void showConfirmDialog(final Activity activity, final String title, final String message,
                                         final String firstOption, final String secondOption, final String thirdOption,
                                         DialogInterface.OnClickListener firstOptionListener,
                                         DialogInterface.OnClickListener secondOptionListener,
                                         DialogInterface.OnClickListener thirdOptionListener) {
        new CustomDialogFragment(title, message, firstOption, secondOption, thirdOption,
                firstOptionListener, secondOptionListener, thirdOptionListener).
                show(activity.getFragmentManager(), title);
    }

    /**
     * Sends an event with feedback to user on loading. Picked up in LoadingFragment.
     *
     * @param message
     * @param eventType
     */
    public static void postProgressMessage(final String message, final LoadingMessageEvent.EventType eventType) {
        new Thread() {
            @Override
            public void run() {
                Dhis2Application.bus.post(new LoadingMessageEvent(message, eventType));
            }
        }.start();
    }

    @Deprecated
    /**
     * @deprecated due to abstraction of status dialog fragment
     */
    public static void showStatusDialog(FragmentManager fragmentManager, BaseSerializableModel item) {
//        ItemStatusDialogFragment fragment = new ItemStatusDialogFragment(item).newInstance(item);
//        fragment.show(fragmentManager);
    }



    public static void showLoadingDialog(final FragmentManager fragmentManager, int message) {
        if (getInstance().getProgressDialogFragment() != null) {
            getInstance().getProgressDialogFragment().dismissAllowingStateLoss();
            getInstance().setProgressDialogFragment(null);
        }
        getInstance().setProgressDialogFragment(ProgressDialogFragment.newInstance(message));
        getInstance().getProgressDialogFragment().show(fragmentManager, ProgressDialogFragment.TAG);
    }

    public static void hideLoadingDialog(final FragmentManager fragmentManager) {
        if (getInstance().getProgressDialogFragment() != null) {
            if(getInstance().getProgressDialogFragment().isAdded()) {
                getInstance().getProgressDialogFragment().dismissAllowingStateLoss();
            }
            getInstance().setProgressDialogFragment(null);
        }
    }

    public static void showSnackBar(View parentLayout, String text, int duration)
    {
        Snackbar.make(parentLayout, text, duration).show();
    }
    public static void showSnackBarWithAction(View parentLayout, String text, int duration, int resId, View.OnClickListener onClickListener )
    {
        Snackbar.make(parentLayout, text, duration).setAction(resId, onClickListener).show();
    }
    public static void showSnackBarWithAction(View parentLayout, String text, int duration, String actionText, View.OnClickListener onClickListener )
    {
        Snackbar.make(parentLayout, text, duration).setAction(actionText, onClickListener).show();
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        //hide keyboard
        if(view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
