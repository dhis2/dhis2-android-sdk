/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.sdk.utils.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueSender;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.persistence.loaders.ModelChangeObserver;
import org.hisp.dhis.android.sdk.persistence.models.BaseSerializableModel;
import org.hisp.dhis.android.sdk.persistence.models.Conflict;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.utils.ui.views.FontTextView;

import java.util.ArrayList;
import java.util.List;

public class ItemStatusDialogFragment extends DialogFragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<ItemStatusDialogFragmentForm> {
    private static final String TAG = ItemStatusDialogFragment.class.getSimpleName();

    private static final int LOADER_ID = 9564013;

    private ItemStatusDialogFragmentForm mForm;
    private TextView mDialogLabel;
    private ImageView mItemStatusImage;
    private FontTextView mDetails;
    private FontTextView mStatus;
    private int mDialogId;

    private static final String EXTRA_ID = "extra:id";
    private static final String EXTRA_TYPE = "extra:type";
    private static final String EXTRA_ARGUMENTS = "extra:Arguments";
    private static final String EXTRA_SAVED_INSTANCE_STATE = "extra:savedInstanceState";

    public static ItemStatusDialogFragment newInstance(BaseSerializableModel item) {
        ItemStatusDialogFragment dialogFragment = new ItemStatusDialogFragment();
        Bundle args = new Bundle();

        args.putLong(EXTRA_ID, item.getLocalId());
        if(item instanceof TrackedEntityInstance) {
            args.putString(EXTRA_TYPE, FailedItem.TRACKEDENTITYINSTANCE);
        } else if (item instanceof Enrollment) {
            args.putString(EXTRA_TYPE, FailedItem.ENROLLMENT);
        } else if (item instanceof Event) {
            args.putString(EXTRA_TYPE, FailedItem.EVENT);
        }

        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return inflater.inflate(R.layout.dialog_fragment_trackedentityinstancestatus, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mItemStatusImage = (ImageView) view.findViewById(R.id.itemstatus);

        mDetails = (FontTextView) view.findViewById(R.id.item_detailed_info);

        mStatus = (FontTextView) view.findViewById(R.id.statusinfo);

        ImageView syncDialogButton = (ImageView) view
                .findViewById(R.id.sync_dialog_button);
        ImageView closeDialogButton = (ImageView) view
                .findViewById(R.id.close_dialog_button);
        mDialogLabel = (TextView) view
                .findViewById(R.id.dialog_label);

        closeDialogButton.setOnClickListener(this);
        syncDialogButton.setOnClickListener(this);
        mDetails.setOnClickListener(this);

        setDialogLabel(R.string.status);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Bundle argumentsBundle = new Bundle();
        argumentsBundle.putBundle(EXTRA_ARGUMENTS, getArguments());
        argumentsBundle.putBundle(EXTRA_SAVED_INSTANCE_STATE, savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, argumentsBundle, this);
    }

    @Override
    public Loader<ItemStatusDialogFragmentForm> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id && isAdded()) {
            // Adding Tables for tracking here is dangerous (since MetaData updates in background
            // can trigger reload of values from db which will reset all fields).
            // Hence, it would be more safe not to track any changes in any tables
            List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            modelsToTrack.add(TrackedEntityInstance.class);
            modelsToTrack.add(Enrollment.class);
            modelsToTrack.add(Event.class);
            modelsToTrack.add(FailedItem.class);
            Bundle fragmentArguments = args.getBundle(EXTRA_ARGUMENTS);
            long idd = fragmentArguments.getLong(EXTRA_ID);
            String type = fragmentArguments.getString(EXTRA_TYPE);

            return new DbLoader<>(
                    getActivity().getBaseContext(), modelsToTrack, new ItemStatusDialogFragmentQuery(
                    idd, type)
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ItemStatusDialogFragmentForm> loader, ItemStatusDialogFragmentForm data) {

        Log.d(TAG, "load finished");
        if (loader.getId() == LOADER_ID && isAdded())
        {
            mForm = data;
            switch (mForm.getStatus()) {
                case SENT:
                    mItemStatusImage.setImageResource(R.drawable.ic_from_server);
                    mStatus.setText(getString(R.string.status_sent_description));
                    break;
                case ERROR: {
                    mItemStatusImage.setImageResource(R.drawable.ic_event_error);
                    mStatus.setText(getString(R.string.status_error_description));
                    FailedItem failedItem = DataValueController.getFailedItem(data.getType(), data.getItem().getLocalId());
                    if(failedItem!= null) {
                        String details = "";
                        if( failedItem.getErrorMessage() != null) {
                            details += failedItem.getErrorMessage() + '\n';
                        }
                        if ( failedItem.getImportSummary() != null && failedItem.getImportSummary().getDescription() != null ) {
                            details += failedItem.getImportSummary().getDescription() + '\n';
                        }
                        if ( failedItem.getImportSummary() != null && failedItem.getImportSummary().getConflicts() != null ) {
                            for(Conflict conflict: failedItem.getImportSummary().getConflicts() ) {
                                if( conflict != null ) {
                                    details += conflict.getObject() + ": " + conflict.getValue() + "\n";
                                }
                            }
                        }
                        mDetails.setText(details);
                    }
                }
                    break;
                case OFFLINE:
                    mStatus.setText(getString(R.string.status_offline_description));
                    mItemStatusImage.setImageResource(R.drawable.ic_offline);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ItemStatusDialogFragmentForm> loader) {

    }

    /* This method must be called only after onViewCreated() */
    public void setDialogLabel(int resourceId) {
        if (mDialogLabel != null) {
            mDialogLabel.setText(resourceId);
        }
    }

    /* This method must be called only after onViewCreated() */
    public void setDialogLabel(CharSequence sequence) {
        if (mDialogLabel != null) {
            mDialogLabel.setText(sequence);
        }
    }

    public void setDialogId(int dialogId) {
        mDialogId = dialogId;
    }

    public int getDialogId() {
        return mDialogId;
    }

    /* This method must be called only after onViewCreated() */
    public CharSequence getDialogLabel() {
        if (mDialogLabel != null) {
            return mDialogLabel.getText();
        } else {
            return null;
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sync_dialog_button) {
            Toast.makeText(getActivity(), getString(R.string.sending_data_server), Toast.LENGTH_LONG).show();
            sendToServer(mForm.getItem(), this);
            ItemStatusDialogFragment.this.dismiss();
        } else if(v.getId() == R.id.close_dialog_button) {
            dismiss();
        } else if(v.getId() == R.id.item_detailed_info) {
            if(mDetails!=null && mDetails.getText().length()>0) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", mDetails.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), getString(R.string.copied_text), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void sendToServer(final BaseSerializableModel item, ItemStatusDialogFragment fragment) {
        final ApiRequestCallback finalCallback = new DoneSendingCallback(fragment);
        if(item instanceof TrackedEntityInstance) {
            TrackedEntityInstance trackedEntityInstance = (TrackedEntityInstance) item;
            sendTrackedEntityInstance(finalCallback, trackedEntityInstance);
        } else if(item instanceof Enrollment) {
            Enrollment enrollment = (Enrollment) item;
            sendEnrollment(finalCallback, enrollment);
        } else if(item instanceof Event) {
            Event event = (Event) item;
            sendEvent(finalCallback, event);
        }

    }

    public static void sendTrackedEntityInstance(final ApiRequestCallback callback, final TrackedEntityInstance trackedEntityInstance) {
        List<Enrollment> enrollments = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        ApiRequestCallback sendEventsCallback = new InitiateSendingEventsCallback(enrollments, events, callback);
        final ApiRequestCallback sendEnrollmentsCallback = new InitiateSendingEnrollmentsCallback(trackedEntityInstance, enrollments, sendEventsCallback);
        new Thread() {
            @Override
            public void run() {
                DataValueSender.initSendTrackedEntityInstance(sendEnrollmentsCallback, trackedEntityInstance);
            }}.start();
    }

    private static class InitiateSendingEventsCallback implements ApiRequestCallback {

        private final List<Enrollment> enrollments;
        private final List<Event> events;
        private final ApiRequestCallback parentCallback;

        public InitiateSendingEventsCallback(List<Enrollment> enrollments, List<Event> events, ApiRequestCallback parentCallback) {
            this.enrollments = enrollments;
            this.events = events;
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            try {
                Thread.sleep(5000); //sleeping to wait for database to get updated with references
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(Enrollment enrollment: enrollments) {
                List<Event> enrollmentEvents = DataValueController.getEventsByEnrollment(enrollment.getLocalId());
                if(enrollmentEvents != null) {
                    events.addAll(enrollmentEvents);
                }
            }
            DataValueSender.initSendEvents(parentCallback, events);
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            parentCallback.onFailure(responseHolder);
        }
    }

    private static class InitiateSendingEnrollmentsCallback implements ApiRequestCallback {

        private final TrackedEntityInstance trackedEntityInstance;
        private final List<Enrollment> enrollments;
        private final ApiRequestCallback parentCallback;

        public InitiateSendingEnrollmentsCallback(TrackedEntityInstance trackedEntityInstance, List<Enrollment> enrollments, ApiRequestCallback parentCallback) {

            this.trackedEntityInstance = trackedEntityInstance;
            this.enrollments = enrollments;
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            try {
                Thread.sleep(5000); //sleeping to wait for database to get updated with references
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<Enrollment> tempEnrollments = DataValueController.getEnrollments(trackedEntityInstance);
            enrollments.addAll(tempEnrollments);
            DataValueSender.initSendEnrollments(parentCallback, enrollments);
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            parentCallback.onFailure(responseHolder);
        }
    }

    public static void sendEnrollment(ApiRequestCallback finalCallback, final Enrollment enrollment) {
        final List<Enrollment> enrollments = new ArrayList<>();
        final List<Event> events = new ArrayList<>();
        enrollments.add(enrollment);
        final ApiRequestCallback sendEventsCallback = new InitiateSendingEventsCallback(enrollments, events, finalCallback);
        new Thread() {
            @Override
            public void run() {
                DataValueSender.initSendEnrollment(sendEventsCallback, enrollment);
            }}.start();
    }

    public static void sendEvent(final ApiRequestCallback finalCallback, final Event event) {
        new Thread() {
            @Override
            public void run() {
                DataValueSender.initSendEvent(finalCallback, event);
            }}.start();
    }

    static class DoneSendingCallback implements ApiRequestCallback {
        final ItemStatusDialogFragment fragment;
        public DoneSendingCallback(ItemStatusDialogFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            Bundle argumentsBundle = new Bundle();
            argumentsBundle.putBundle(EXTRA_ARGUMENTS, fragment.getArguments());
            if(fragment.isAdded() && !fragment.getLoaderManager().getLoader(LOADER_ID).isAbandoned()
                    && !fragment.getLoaderManager().getLoader(LOADER_ID).isStarted() ) {
                fragment.getLoaderManager().restartLoader(LOADER_ID, argumentsBundle, fragment);
            }
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            Log.d(TAG, "onFailure");
            String error = "Something went wrong while sending data";
            if(responseHolder.getApiException().isNetworkError()) {
                error+= ": "+fragment.getString(R.string.network_error);
            }
            final String error1 = error;
            if(fragment!=null) {
                final Activity activity = fragment.getActivity();
                if(activity!=null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, error1, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    }
}
