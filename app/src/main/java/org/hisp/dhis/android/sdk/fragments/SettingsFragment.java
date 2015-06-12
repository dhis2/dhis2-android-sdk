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

package org.hisp.dhis.android.sdk.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.events.LoadingEvent;
import org.hisp.dhis.android.sdk.events.LoadingMessageEvent;
import org.hisp.dhis.android.sdk.events.SynchronizationFinishedEvent;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.utils.APIException;

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
    private ProgressBar mProgessBar;
    private TextView syncTextView;
    private String progressMessage;

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
        mProgessBar = (ProgressBar) view.findViewById(R.id.settings_progessbar);
        syncTextView = (TextView) view.findViewById(R.id.settings_sync_textview);
        mProgessBar.setVisibility(View.GONE);
        logoutButton.setOnClickListener(this);
        synchronizeButton.setOnClickListener(this);

        if(Dhis2.isLoading() && getProgressMessage() != null)
        {
            syncTextView.setText(getProgressMessage());
            Log.d(TAG, getProgressMessage());
        }
        else if(!Dhis2.isLoading())
        {
            //setSummaryFromLastSync in syncTextView
            syncTextView.setText(Dhis2.getLastSynchronizationSummary());
        }
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

                ApiRequestCallback callback = new ApiRequestCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        //do nothing
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        //do nothing
                    }
                };

                Dhis2.synchronize(context, callback);
                synchronizeButton.setEnabled(false);
                mProgessBar.setVisibility(View.VISIBLE);
                synchronizeButton.setText("Synchronizing...");
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
    private void setText(CharSequence text)
    {
        if(isAdded())
        {
            if(text != null)
            {
                syncTextView.setText(text);
            }
            else
                Log.d(TAG, "Loading message is null");
        }
    }
    @Subscribe
    public void onLoadingMessageEvent(final LoadingMessageEvent event) {
        Log.d(TAG, "Message received" + event.message);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                setProgressMessage(event.message);
                setText(event.message);
            }
        });
    }

    private void enableUi(boolean enable)
    {
        if(!enable)
        {
            synchronizeButton.setEnabled(false);
            mProgessBar.setVisibility(View.VISIBLE);
            synchronizeButton.setText("Synchronizing...");
            syncTextView.setText(getProgressMessage());
        }
        else
        {
            synchronizeButton.setEnabled(true);
            mProgessBar.setVisibility(View.GONE);
            syncTextView.setText(Dhis2.getLastSynchronizationSummary());
            synchronizeButton.setText(R.string.synchronize_with_server);
        }

    }

    @Subscribe
    public void onLoadingEvent(final LoadingEvent event)
    {
        if(event.success && Dhis2.isLoading())
        {
            enableUi(false);
        }
    }

    @Subscribe
    public void onSynchronizationFinishedEvent(final SynchronizationFinishedEvent event)
    {
        if(event.success)
        {
            enableUi(true);
        }
    }

    public String getProgressMessage() {
        return progressMessage;
    }

    public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    @Override
    public void onPause() {
        super.onPause();
        Dhis2Application.getEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Dhis2Application.getEventBus().register(this);

        if(!Dhis2.isLoading())
        {
            enableUi(true);
        }
        else
            enableUi(false);
    }
}
