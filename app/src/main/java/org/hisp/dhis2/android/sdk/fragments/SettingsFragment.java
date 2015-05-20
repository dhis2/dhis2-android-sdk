/*
 *  Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis2.android.sdk.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.Dhis2;

/**
 * Basic settings Fragment giving users options to change update frequency to the server,
 * and logging out.
 *
 * @author Simen Skogly Russnes on 02.03.15.
 */
public class SettingsFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private Spinner updateFrequencySpinner;
    private Button logoutButton;
    private Button synchronizeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // we need to disable options menu in this fragment
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        updateFrequencySpinner = (Spinner) view.findViewById(R.id.settings_update_frequency_spinner);
        updateFrequencySpinner.setSelection(Dhis2.getUpdateFrequency(getActivity()));
        updateFrequencySpinner.setOnItemSelectedListener(this);

        synchronizeButton = (Button) view.findViewById(R.id.settings_sync_button);
        logoutButton = (Button) view.findViewById(R.id.settings_logout_button);

        logoutButton.setOnClickListener(this);
        synchronizeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.settings_logout_button) {
            Dhis2.showConfirmDialog(getActivity(), getString(R.string.logout_title), getString(R.string.logout_message),
                    getString(R.string.logout_option), getString(R.string.cancel_option), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Dhis2.logout(getActivity());
                            getActivity().finish();
                        }
                    });
        } else if (view.getId() == R.id.settings_sync_button) {
            if (isAdded()) {
                Context context = getActivity().getBaseContext();
                Toast.makeText(context, getString(R.string.syncing), Toast.LENGTH_SHORT).show();
                Dhis2.synchronize(context);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Dhis2.setUpdateFrequency(getActivity(), position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // stub implementation
    }
}
