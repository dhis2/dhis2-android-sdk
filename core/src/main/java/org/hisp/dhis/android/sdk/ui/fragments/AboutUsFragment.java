package org.hisp.dhis.android.sdk.ui.fragments;


import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.sdk.BuildConfig;
import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.ui.views.FontTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutUsFragment extends Fragment {
    public static final String TAG = AboutUsFragment.class.getSimpleName();

    public static String getAppVersion() {
        return String.valueOf(BuildConfig.VERSION_NAME);
    }

    public static String getCommitHash(Context context) {
        String stringCommit;
        //Check if lastcommit.txt file exist, and if not exist show as unavailable.
        int layoutId = context.getResources().getIdentifier("lastcommit", "raw",
                context.getPackageName());
        if (layoutId == 0) {
            stringCommit = context.getString(R.string.unavailable);
        } else {
            InputStream commit = context.getResources().openRawResource(layoutId);
            stringCommit = convertFromInputStreamToString(commit).toString();
        }
        return stringCommit;
    }

    protected static StringBuilder convertFromInputStreamToString(InputStream inputStream) {
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

    public static String getVersionMessage(Context context) {
        String version = getAppVersion();
        return String.format(context.getString(R.string.app_version), version);
    }

    private SpannableString getDescriptionMessage(Context context) {
        InputStream message = context.getResources().openRawResource(R.raw.description);
        String stringMessage = convertFromInputStreamToString(message).toString();
        final SpannableString linkedMessage = new SpannableString(Html.fromHtml(stringMessage));
        Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
        return linkedMessage;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ((FontTextView) view.findViewById(R.id.app_version)).setText(
                getVersionMessage(getContext()));
        ((FontTextView) view.findViewById(R.id.commit_hash)).setText(
                getCommitMessage(getContext()));
        ((FontTextView) view.findViewById(R.id.description)).setText(
                getDescriptionMessage(getContext()));
    }
}