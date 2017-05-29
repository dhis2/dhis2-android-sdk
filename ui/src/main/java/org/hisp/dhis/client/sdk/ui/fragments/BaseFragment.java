/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.fragments;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import org.hisp.dhis.client.sdk.ui.activities.BaseActivity;
import org.hisp.dhis.client.sdk.ui.activities.NavigationCallback;
import org.hisp.dhis.client.sdk.ui.activities.OnBackPressedCallback;
import org.hisp.dhis.client.sdk.ui.activities.OnBackPressedFromFragmentCallback;

public class BaseFragment extends Fragment implements OnBackPressedCallback {
    private NavigationCallback navigationCallback;
    private OnBackPressedFromFragmentCallback onBackPressedFromFragmentCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NavigationCallback) {
            navigationCallback = (NavigationCallback) context;
        }

        if (context instanceof BaseActivity) {
            ((BaseActivity) context).setOnBackPressedCallback(this);
        }

        if (context instanceof OnBackPressedFromFragmentCallback) {
            onBackPressedFromFragmentCallback = (OnBackPressedFromFragmentCallback) context;
        }
    }

    @Override
    public void onDetach() {
        navigationCallback = null;
        onBackPressedFromFragmentCallback = null;

        // nullifying callback references
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).setOnBackPressedCallback(null);
        }

        super.onDetach();
    }

    protected void toggleNavigationDrawer() {
        if (navigationCallback != null) {
            navigationCallback.toggleNavigationDrawer();
        }
    }

    /**
     * This method will return Toolbar instance only in case
     * when Fragment is attached to WrapperFragment
     */
    @Nullable
    protected Toolbar getParentToolbar() {
        if (getParentFragment() != null && getParentFragment() instanceof WrapperFragment) {
            return ((WrapperFragment) getParentFragment()).getToolbar();
        }
        return null;
    }

    @Override
    public boolean onBackPressed() {
        if (onBackPressedFromFragmentCallback != null) {
            onBackPressedFromFragmentCallback.onBackPressedFromFragment();
            return false;
        }
        return true;
    }


}
