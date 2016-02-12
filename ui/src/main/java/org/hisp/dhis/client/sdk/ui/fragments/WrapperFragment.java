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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.R;

import java.io.Serializable;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;


public class WrapperFragment extends BaseFragment2 implements View.OnClickListener {
    private static final String ARG_TITLE = "arg:title";
    private static final String ARG_NESTED_FRAGMENT = "arg:nestedFragment";

    private Toolbar toolbar;

    @NonNull
    public static WrapperFragment newInstance(@NonNull Class<? extends Fragment> fragmentClass, String title) {
        isNull(fragmentClass, "Fragment class must bot be null");
        isNull(title, "title must bot be null");

        Bundle arguments = new Bundle();
        arguments.putString(ARG_TITLE, title);
        arguments.putSerializable(ARG_NESTED_FRAGMENT, fragmentClass);

        WrapperFragment wrapperFragment = new WrapperFragment();
        wrapperFragment.setArguments(arguments);

        return wrapperFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wrapper, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final Drawable buttonDrawable = DrawableCompat.wrap(ContextCompat
                .getDrawable(getActivity(), R.drawable.ic_menu));

        DrawableCompat.setTint(buttonDrawable, ContextCompat
                .getColor(getContext(), android.R.color.white));

        toolbar.setNavigationIcon(buttonDrawable);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setTitle(getTitle());

        attachFragment(getFragment());
    }

    @Override
    public void onClick(View v) {
        toggleNavigationDrawer();
    }

    @NonNull
    private String getTitle() {
        if (isAdded() && getArguments() != null) {
            return getArguments().getString(ARG_TITLE, "");
        }

        return "";
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private Fragment getFragment() {
        if (isAdded() && getArguments() != null) {
            Serializable fragmentClassSerialized = getArguments()
                    .getSerializable(ARG_NESTED_FRAGMENT);

            // Using reflection API to create an instance of fragment
            if (fragmentClassSerialized != null) {
                Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>)
                        fragmentClassSerialized;
                try {
                    return fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return new Fragment();
    }

    @NonNull
    protected Toolbar getToolbar() {
        return toolbar;
    }

    private void attachFragment(@NonNull Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container_fragment_frame, fragment)
                .commit();
    }
}
