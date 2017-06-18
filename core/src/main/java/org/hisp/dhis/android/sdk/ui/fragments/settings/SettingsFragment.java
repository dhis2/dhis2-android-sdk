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

package org.hisp.dhis.android.sdk.ui.fragments.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.HttpUrl;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.controllers.PeriodicSynchronizerController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.controllers.SyncStrategy;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.events.LoadingMessageEvent;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.network.Session;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.AppPreferences;
import org.hisp.dhis.android.sdk.ui.activities.LoginActivity;
import org.hisp.dhis.android.sdk.utils.UiUtils;

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
    private Button synchronizeRemovedEventsButton;
    private ProgressBar mProgressBar;
    private TextView syncTextView;
    private LoadingMessageEvent progressMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // we need to disable options menu in this fragment
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        if(getActionBar() != null) {
            getActionBar().setTitle(getString(R.string.settings));
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        updateFrequencySpinner = (Spinner) view.findViewById(R.id.settings_update_frequency_spinner);
        updateFrequencySpinner.setSelection(PeriodicSynchronizerController.getUpdateFrequency(getActivity()));
        updateFrequencySpinner.setOnItemSelectedListener(this);

        synchronizeButton = (Button) view.findViewById(R.id.settings_sync_button);
        synchronizeRemovedEventsButton = (Button) view.findViewById(
                R.id.settings_sync_remotely_deleted_events_button);
        logoutButton = (Button) view.findViewById(R.id.settings_logout_button);
        mProgressBar = (ProgressBar) view.findViewById(R.id.settings_progessbar);
        syncTextView = (TextView) view.findViewById(R.id.settings_sync_textview);
        mProgressBar.setVisibility(View.GONE);
        logoutButton.setOnClickListener(this);
        synchronizeButton.setOnClickListener(this);
        synchronizeRemovedEventsButton.setOnClickListener(this);

        //if(DhisController.isLoading() && getProgressMessage() != null)
        {
            //syncTextView.setText(getProgressMessage());
            //Log.d(TAG, getProgressMessage());
        }
        //else if(!DhisController.isLoading())
        {
            //setSummaryFromLastSync in syncTextView
            //syncTextView.setText(DhisController.getLastSynchronizationSummary());
        }
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.settings_logout_button) {
            UiUtils.showConfirmDialog(getActivity(), getString(R.string.logout_title),
                    getString(R.string.logout_message),
                    getString(R.string.logout), getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (DhisController.hasUnSynchronizedDatavalues) {
                                //show error dialog
                                UiUtils.showErrorDialog(getActivity(),
                                        getString(R.string.error_message),
                                        getString(R.string.unsynchronized_data_values),
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                            } else {
                                Session session = DhisController.getInstance().getSession();
                                if (session != null) {
                                    HttpUrl httpUrl = session.getServerUrl();
                                    if (httpUrl != null) {
                                        String serverUrlString = httpUrl.toString();
                                        AppPreferences appPreferences = new AppPreferences(
                                                getActivity().getApplicationContext());
                                        appPreferences.putServerUrl(serverUrlString);
                                    }
                                }
                                DhisService.logOutUser(getActivity());

                                int apiVersion = Build.VERSION.SDK_INT;
                                if(apiVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finishAffinity();
                                }
                                else {
                                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                            }
                        }
                    });
        } else if (view.getId() == R.id.settings_sync_button) {
            if (isAdded()) {
                final Context context = getActivity().getBaseContext();
                Toast.makeText(context, getString(R.string.syncing), Toast.LENGTH_SHORT).show();
                setProgressMessage(new LoadingMessageEvent(getString(R.string.syncing),
                        LoadingMessageEvent.EventType.METADATA));
                new Thread() {
                    @Override
                    public void run() {
                        DhisService.synchronize(context, SyncStrategy.DOWNLOAD_ALL);
                    }
                }.start();
                startSync();
            }
        } else if (view.getId() == R.id.settings_sync_remotely_deleted_events_button) {
            if (isAdded()) {
                final Context context = getActivity().getBaseContext();
                Toast.makeText(context, getString(R.string.sync_deleted_events),
                        Toast.LENGTH_SHORT).show();
                setProgressMessage(new LoadingMessageEvent(getString(R.string.sync_deleted_events),
                        LoadingMessageEvent.EventType.REMOVE_DATA));

                new Thread() {
                    @Override
                    public void run() {
                        DhisService.synchronizeRemotelyDeletedData(context);
                    }
                }.start();
                startSync();
            }
        }
    }

    private void startSync() {
        changeUiVisibility(false);
        setText(getProgressMessage());
    }

    private void endSync() {
        changeUiVisibility(true);
        syncTextView.setText("");
        synchronizeButton.setText(R.string.synchronize_with_server);
        synchronizeRemovedEventsButton.setText(R.string.synchronize_deleted_data);
    }

    private void changeUiVisibility(boolean enabled) {
        if (!enabled) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        synchronizeButton.setEnabled(enabled);
        synchronizeRemovedEventsButton.setEnabled(enabled);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        PeriodicSynchronizerController.setUpdateFrequency(getActivity(), position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // stub implementation
    }

    private void setText(LoadingMessageEvent event)
    {
        if (event != null) {
            if (event.eventType.equals(LoadingMessageEvent.EventType.DATA) ||
                    event.eventType.equals(LoadingMessageEvent.EventType.METADATA) ||
                    event.eventType.equals(LoadingMessageEvent.EventType.STARTUP)) {
                changeUiVisibility(false);
                synchronizeButton.setText(getActivity().getApplicationContext().getString(
                        R.string.synchronizing));
            } else if (event.eventType.equals(LoadingMessageEvent.EventType.REMOVE_DATA)) {
                synchronizeRemovedEventsButton.setText(
                        getActivity().getApplicationContext().getString(
                                R.string.synchronizing));
                changeUiVisibility(false);
            } else if (event.eventType.equals(LoadingMessageEvent.EventType.FINISH)) {
                endSync();
            }

            if (event.message != null) {
                syncTextView.setText(event.message);
            } else
                Log.d(TAG, "Loading message is null");
        }
    }
    @Subscribe
    public void onLoadingMessageEvent(final LoadingMessageEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                setProgressMessage(event);
                setText(event);
            }
        });
    }

    private void enableUi(boolean enable)
    {
        if(!enable)
        {
            startSync();
        }
        else
        {
            endSync();
        }
    }

    @Subscribe
    public void onSynchronizationFinishedEvent(final UiEvent event)
    {
        if (event.getEventType().equals(UiEvent.UiEventType.SYNCING_START)) {
            enableUi(false);
        } else if (event.getEventType().equals(UiEvent.UiEventType.SYNCING_END))
        {
            enableUi(true);
        }
    }

    public LoadingMessageEvent getProgressMessage() {
        return progressMessage;
    }

    public void setProgressMessage(LoadingMessageEvent progressMessage) {
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

        //if(!DhisController.isLoading())
        {
            enableUi(true);
        }
        //else
        //    enableUi(false);
        if (!MetaDataController.isDataLoaded(getActivity().getApplicationContext())) {
            LoadingMessageEvent event = new LoadingMessageEvent("",
                    LoadingMessageEvent.EventType.STARTUP);
            setProgressMessage(event);
            setText(event);
        }
    }

    public ActionBar getActionBar() {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }
}
