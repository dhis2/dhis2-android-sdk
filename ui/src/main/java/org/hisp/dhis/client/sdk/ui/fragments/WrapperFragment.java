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
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.R;

import java.io.Serializable;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class WrapperFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_TITLE = "arg:title";
    private static final String ARG_NESTED_FRAGMENT = "arg:nestedFragment";
    public static final String ARG_NESTED_BUNDLE = "arg:nestedBundle";

    private Toolbar toolbar;

    @NonNull
    public static WrapperFragment newInstance(@NonNull Class<? extends Fragment> fragmentClass,
                                              String title) {
        isNull(fragmentClass, "Fragment class must bot be null");
        isNull(title, "title must bot be null");

        Bundle arguments = new Bundle();
        arguments.putString(ARG_TITLE, title);
        arguments.putSerializable(ARG_NESTED_FRAGMENT, fragmentClass);

        WrapperFragment wrapperFragment = new WrapperFragment();
        wrapperFragment.setArguments(arguments);

        return wrapperFragment;
    }

    @NonNull
    public static WrapperFragment newInstance(@NonNull Class<? extends Fragment> fragmentClass,
                                              String title, Bundle bundle) {
        isNull(fragmentClass, "Fragment class must bot be null");
        isNull(title, "title must bot be null");

        Bundle arguments = new Bundle();
        arguments.putString(ARG_TITLE, title);
        arguments.putSerializable(ARG_NESTED_FRAGMENT, fragmentClass);
        arguments.putBundle(ARG_NESTED_BUNDLE, bundle);

        WrapperFragment wrapperFragment = new WrapperFragment();
        wrapperFragment.setArguments(arguments);

        return wrapperFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wrapper, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        Drawable buttonDrawable = DrawableCompat.wrap(VectorDrawableCompat.create(getContext().getResources(),
                R.drawable.ic_menu, getContext().getTheme()));
        DrawableCompat.setTint(buttonDrawable, ContextCompat
                .getColor(getContext(), android.R.color.white));

        toolbar.setNavigationIcon(buttonDrawable);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setTitle(getTitle());

        // don't force fragment attachment if it is already in fragment manager
        if (getFragmentClass() != null) {
            Fragment fragment = getFragment();
            // TODO: Vlad :check for null etc..
            fragment.setArguments(getArguments().getBundle(ARG_NESTED_BUNDLE));
            attachFragment(fragment, getFragmentClass().getSimpleName());
        }
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
    private Fragment getFragment() {
        if (isAdded() && getArguments() != null) {
            Class<? extends Fragment> fragmentClass = getFragmentClass();

            // Using reflection API to create an instance of fragment
            if (fragmentClass != null) {
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

    @SuppressWarnings("unchecked")
    private Class<? extends Fragment> getFragmentClass() {
        Serializable fragmentClassSerialized = getArguments()
                .getSerializable(ARG_NESTED_FRAGMENT);
        if (fragmentClassSerialized != null) {
            return (Class<? extends Fragment>) fragmentClassSerialized;
        }

        return null;
    }

    private void attachFragment(@NonNull Fragment fragment, String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager.findFragmentByTag(tag) == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container_fragment_frame, fragment, tag)
                    .commit();
        }
    }
}
