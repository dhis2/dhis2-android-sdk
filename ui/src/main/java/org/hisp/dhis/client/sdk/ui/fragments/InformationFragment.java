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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.BuildConfig;
import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.activities.BaseActivity;
import org.hisp.dhis.client.sdk.ui.activities.OnBackPressedCallback;
import org.hisp.dhis.client.sdk.ui.activities.OnBackPressedFromFragmentCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;


public class InformationFragment extends BaseFragment implements OnBackPressedCallback {

    public static final String LIBS_LIST = "libraires_list";
    public static final String USERNAME = "username";
    public static final String URL = "url";

    private String username;
    private String url;
    private OnBackPressedFromFragmentCallback onBackPressedFromFragmentCallback;

    public static InformationFragment newInstance(String username, String url) {
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(URL, url);
        InformationFragment fragment = new InformationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        super.onCreateView(inflater, container, state);
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    public static Spanned getCommitMessage(Context context) {
        String stringCommit = getCommitHash(context);

        if (stringCommit.contains(context.getString(R.string.unavailable))) {
            stringCommit = String.format(context.getString(R.string.last_commit), stringCommit);
            stringCommit = stringCommit + " " + context.getText(R.string.lastcommit_unavailable);
        } else {
            stringCommit = String.format(context.getString(R.string.last_commit), stringCommit);
        }

        return Html.fromHtml(stringCommit);
    }

    public static String getCommitHash(Context context) {
        String stringCommit;
        //Check if lastcommit.txt file exist, and if not exist show as unavailable.
        int layoutId = context.getResources().getIdentifier("lastcommit", "raw",
                BuildConfig.main_app);
        if (layoutId == 0) {
            stringCommit = context.getString(R.string.unavailable);
        } else {
            InputStream commit = context.getResources().openRawResource(layoutId);
            stringCommit = convertFromInputStreamToString(commit).toString();
        }
        return stringCommit;
    }

    private static StringBuilder convertFromInputStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = r.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            username = args.getString(USERNAME);
            url = args.getString(URL);
        }
        // setup fields :
        setAppNameAndVersion(getContext().getApplicationInfo().packageName);
        setCommitHash(getContext());
        setDescription(getContext());
    }

    /**
     * Commit hash from main app file.
     * Fills in the commit hash if exists, or how make it works.
     *
     * @param context the name of the app package.
     */
    private void setCommitHash(Context context) {
        TextView commitTextView = (TextView) getActivity().findViewById(R.id.commit_hash);
        commitTextView.setText(getCommitMessage(context));
    }

    /**
     * App-info from main app file.
     * Fills in the app description message.
     *
     * @param context the name of the app package.
     */
    private void setDescription(Context context) {
        TextView descriptionTextView = (TextView) getActivity().findViewById(R.id.description);
        descriptionTextView.setText(getDescriptionMessage(context));
    }

    private SpannableString getDescriptionMessage(Context context) {
        InputStream message = context.getResources().openRawResource(R.raw.description);
        String stringMessage = convertFromInputStreamToString(message).toString();
        final SpannableString linkedMessage = new SpannableString(Html.fromHtml(stringMessage));
        Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
        return linkedMessage;
    }
    /**
     * App-info from app package name.
     * Fills in the App name, version, build and adds the app icon.
     *
     * @param packageName the name of the app package.
     */
    private void setAppNameAndVersion(String packageName) {
        TextView appNameTextView = (TextView) getActivity().findViewById(R.id.app_name);
        TextView sessionText = (TextView) getActivity().findViewById(R.id.app_session);
        TextView appVersionTextView = (TextView) getActivity().findViewById(R.id.app_version);
        ImageView appIconImageView = (ImageView) getActivity().findViewById(R.id.app_icon);

        ApplicationInfo applicationInfo = null;
        PackageManager packageManager = getContext().getPackageManager();

        String appName = "";
        String appVersion = "";
        String appBuild = "";

        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (applicationInfo != null) {
            appName = packageManager.getApplicationLabel(applicationInfo).toString();
            try {
                appVersion = "" + packageManager.getPackageInfo(packageName, 0).versionName;
                appBuild = "" + packageManager.getPackageInfo(packageName, 0).versionCode;
                appIconImageView.setImageDrawable(packageManager.getApplicationIcon(packageName));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (appName.length() > 0 && appVersion.length() > 0) {
            appNameTextView.setText(appName);
            if (!appBuild.isEmpty()) {
                appVersionTextView.setText(appVersion + " (" + appBuild + ")");
            } else {
                appVersionTextView.setText(appVersion);
            }
        }

        if (url != null && username != null) {
            // inside app_session:
            sessionText.setText(String.format(Locale.getDefault(), "%s %s\n",
                    getString(R.string.logged_in_as),
                    username));

            sessionText.append(getString(R.string.logged_in_at) + " ");
            addUrl(sessionText, url);
            sessionText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * A wrapper method to append url to a textView.
     *
     * @param textView
     * @param url
     */
    protected void addUrl(TextView textView, String url) {
        textView.append(
                Html.fromHtml(
                        String.format(Locale.getDefault(), "<a href=\"%s\">%s</a>",
                                url
                                ,
                                url)));
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).setOnBackPressedCallback(this);
        }
        if (context instanceof OnBackPressedFromFragmentCallback) {
            onBackPressedFromFragmentCallback = (OnBackPressedFromFragmentCallback) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        // nullifying callback references
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).setOnBackPressedCallback(null);
        }
        onBackPressedFromFragmentCallback = null;
        super.onDetach();
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