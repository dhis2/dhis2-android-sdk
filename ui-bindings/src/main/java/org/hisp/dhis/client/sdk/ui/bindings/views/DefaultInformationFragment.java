package org.hisp.dhis.client.sdk.ui.bindings.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.core.PreferencesModule;
import org.hisp.dhis.client.sdk.core.PreferencesModuleImpl;
import org.hisp.dhis.client.sdk.ui.bindings.R;
import org.hisp.dhis.client.sdk.ui.fragments.AbsInformationFragment;

import java.util.Locale;

public class DefaultInformationFragment extends AbsInformationFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferencesModule preferencesModule = new PreferencesModuleImpl(getContext());

        TextView sessionText = (TextView) getActivity().findViewById(R.id.app_session);
        // inside app_session:
        sessionText.setText(String.format(Locale.getDefault(), "%s %s\n",
                getString(R.string.logged_in_as),
                D2.me().username())
        );
        sessionText.append(getString(R.string.logged_in_at) + " ");
        addUrl(sessionText, preferencesModule.getConfigurationPreferences().get().getServerUrl());
        sessionText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
