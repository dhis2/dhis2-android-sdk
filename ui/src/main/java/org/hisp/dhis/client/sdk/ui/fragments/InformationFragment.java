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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.preferences.PreferencesModuleImpl;
import org.hisp.dhis.client.sdk.android.common.LibInfo;
import org.hisp.dhis.client.sdk.core.common.preferences.PreferencesModule;
import org.hisp.dhis.client.sdk.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InformationFragment extends Fragment {

    public static final String LIBS_LIST = "libraires_list";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        super.onCreateView(inflater, container, state);
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView sessionText = (TextView) getActivity().findViewById(R.id.app_session);

        //TODO: refactor this, this is just for testing.
        //TextView documentation = (TextView) getActivity().findViewById(R.id.textview_documentation);

        PreferencesModule preferencesModule = new PreferencesModuleImpl(getContext());

        // inside app_session:
        sessionText.setText(String.format(Locale.getDefault(), "%s %s\n",
                getString(R.string.logged_in_as),
                D2.me().userCredentials().toBlocking().first().getUsername())
        );

        sessionText.append(getString(R.string.logged_in_at) + " ");
        addUrl(sessionText, preferencesModule.getConfigurationPreferences().get().getServerUrl());
        sessionText.setMovementMethod(LinkMovementMethod.getInstance());

        // setup fields :
        setAppNameAndVersion(getContext().getApplicationInfo().packageName);

        //setDocumentationUrl("https://dhis2.github.io/#android");

        //setSdkLicence("Dhis2-android-sdk", "https://dhis2.github.io/#license");

        //setup libraries list:
        /*Bundle args = this.getArguments();

        if (args != null) {
            ArrayList<LibInfo> libs = args.getParcelableArrayList(LIBS_LIST);
            for (LibInfo lib : libs) {
                Log.d("ParcelableLibs", "onViewCreated: " + lib.getName() + " - " + lib.getLicence());
            }
            setAppLibraries(libs);
        }*/
    }

    /**
     * App-info from app package name.
     * Fills in the App name, version, build and adds the app icon.
     *
     * @param packageName the name of the app package.
     */
    private void setAppNameAndVersion(String packageName) {
        TextView appNameTextView = (TextView) getActivity().findViewById(R.id.app_name);
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
                //e.printStackTrace();
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
    }

    public void setDocumentationUrl(String docUrl) {
        TextView documentationTextView = (TextView) getActivity().findViewById(R.id.textview_documentation);
        documentationTextView.setText("");
        documentationTextView.setText(getString(R.string.documentation_header) + "\n");
        addUrl(documentationTextView, docUrl);
        documentationTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setAppLibraries(ArrayList<LibInfo> libs) {
        TextView sdkLicenceTextView = (TextView) getActivity().findViewById(R.id.textview_libraries);

        for (LibInfo library : libs) {
            sdkLicenceTextView.append("\n");
            sdkLicenceTextView.append(library.getName() + " (" + library.getLicence() + ")");
        }
        sdkLicenceTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static Bundle newBundle(List<LibInfo> libraries) {
        Bundle libs = new Bundle();

        // This is where we would put the sdk libraries
        ArrayList<LibInfo> libsList = new ArrayList<>();/*Arrays.asList(
                new LibInfo("name1", "licence1"),
                new LibInfo("name2", "licence1"),
                new LibInfo("name3", "licence1")
        ));*/

        if (libraries != null) {
            for (LibInfo library : libraries) {
                libsList.add(library);
            }
        }

        libs.putParcelableArrayList(LIBS_LIST, libsList);
        return libs;
    }

    private void setSdkLicence(String name, String url) {
        TextView sdkLicenceTextView = (TextView) getActivity().findViewById(R.id.textview_libraries);

        if (name.isEmpty()) {
            name = url;
        }

        sdkLicenceTextView.setText("");
        sdkLicenceTextView.append(getString(R.string.libraries_header) + "\n");
        sdkLicenceTextView.append(
                Html.fromHtml(
                        String.format(Locale.getDefault(), "<a href=\"%s\">%s</a>",
                                url,
                                name)));
        //addUrl(sdkLicenceTextView, url);
        sdkLicenceTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * A wrapper method to append url to a textView.
     *
     * @param textView
     * @param url
     */
    private void addUrl(TextView textView, String url) {
        textView.append(
                Html.fromHtml(
                        String.format(Locale.getDefault(), "<a href=\"%s\">%s</a>",
                                url,
                                url)));
    }

    public class LibsAdapter extends RecyclerView.Adapter<LibsAdapter.ViewHolder> {
        private ArrayList<LibInfo> libs;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public LibsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(LibsAdapter.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
