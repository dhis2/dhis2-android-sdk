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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueLoader;
import org.hisp.dhis.android.sdk.controllers.datavalues.TrackedEntityInstancesResultHolder;
import org.hisp.dhis.android.sdk.events.BaseEvent;
import org.hisp.dhis.android.sdk.events.LoadingMessageEvent;
import org.hisp.dhis.android.sdk.fragments.ProgressDialogFragment;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.utils.ui.adapters.rows.AbsTextWatcher;

import java.util.ArrayList;
import java.util.List;

public class QueryTrackedEntityInstancesResultDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = QueryTrackedEntityInstancesResultDialogFragment.class.getSimpleName();

    private EditText mFilter;
    private TextView mDialogLabel;
    private QueryTrackedEntityInstancesResultDialogAdapter mAdapter;
    private int mDialogId;
    private ProgressDialogFragment progressDialogFragment;

    private static final String EXTRA_TRACKEDENTITYINSTANCESLIST = "extra:trackedEntityInstances";
    private static final String EXTRA_TRACKEDENTITYINSTANCESSELECTED = "extra:trackedEntityInstancesSelected";
    private static final String EXTRA_ORGUNIT = "extra:orgUnit";

    public static QueryTrackedEntityInstancesResultDialogFragment newInstance(List<TrackedEntityInstance> trackedEntityInstances, String orgUnit) {
        QueryTrackedEntityInstancesResultDialogFragment dialogFragment = new QueryTrackedEntityInstancesResultDialogFragment();
        Bundle args = new Bundle();
        Parcel parcel1 = Parcel.obtain();
        ParameterParcelable parcelable1 = new ParameterParcelable(trackedEntityInstances);
        parcelable1.writeToParcel(parcel1, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        Parcel parcel2 = Parcel.obtain();
        ParameterParcelable parcelable2 = new ParameterParcelable(new ArrayList<TrackedEntityInstance>());
        parcelable2.writeToParcel(parcel2, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        args.putParcelable(EXTRA_TRACKEDENTITYINSTANCESSELECTED, parcelable2);
        args.putParcelable(EXTRA_TRACKEDENTITYINSTANCESLIST, parcelable1);
        args.putString(EXTRA_ORGUNIT, orgUnit);
        dialogFragment.setArguments(args);
        Dhis2Application.getEventBus().register(dialogFragment);
        return dialogFragment;
    }

    private List<TrackedEntityInstance> getTrackedEntityInstances() {
        ParameterParcelable parameterParcelable = getArguments().getParcelable(EXTRA_TRACKEDENTITYINSTANCESLIST);
        List<TrackedEntityInstance> trackedEntityInstances = parameterParcelable.getTrackedEntityInstances();
        return trackedEntityInstances;
    }

    private List<TrackedEntityInstance> getSelectedTrackedEntityInstances() {
        ParameterParcelable parameterParcelable = getArguments().getParcelable(EXTRA_TRACKEDENTITYINSTANCESSELECTED);
        List<TrackedEntityInstance> trackedEntityInstances = parameterParcelable.getTrackedEntityInstances();
        return trackedEntityInstances;
    }

    private String getOrgUnit() {
        return getArguments().getString(EXTRA_ORGUNIT);
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

        return inflater.inflate(R.layout.dialog_fragment_teiqueryresult, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = (ListView) view
                .findViewById(R.id.simple_listview);
        ImageView loadDialogButton = (ImageView) view
                .findViewById(R.id.load_dialog_button);
        loadDialogButton.setImageResource(R.drawable.ic_download);
        ImageView closeDialogButton = (ImageView) view
                .findViewById(R.id.close_dialog_button);
        mFilter = (EditText) view
                .findViewById(R.id.filter_options);
        mDialogLabel = (TextView) view
                .findViewById(R.id.dialog_label);
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFilter.getWindowToken(), 0);

        mAdapter = new QueryTrackedEntityInstancesResultDialogAdapter(LayoutInflater.from(getActivity()), getSelectedTrackedEntityInstances());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        mFilter.addTextChangedListener(new AbsTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                mAdapter.getFilter().filter(s.toString());
            }
        });

        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dismiss();
            }
        });

        loadDialogButton.setOnClickListener(this);

        setDialogLabel(R.string.select_to_load);
        getAdapter().swapData(getTrackedEntityInstances());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TrackedEntityInstance value = mAdapter.getItem(position);
        List<TrackedEntityInstance> selected = getSelectedTrackedEntityInstances();
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxTeiQuery);
        if(checkBox.isChecked()) {
            selected.remove(value);
            checkBox.setChecked(false);
        } else {
            selected.add(value);
            checkBox.setChecked(true);
        }
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

    public QueryTrackedEntityInstancesResultDialogAdapter getAdapter() {
        return mAdapter;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.load_dialog_button) {
            initiateLoading(getOrgUnit());
            dismiss();
        }
    }

    public void initiateLoading(final String currentOrganisationUnit) {
        progressDialogFragment = ProgressDialogFragment.newInstance();
        ApiRequestCallback<TrackedEntityInstancesResultHolder> callback = new ApiRequestCallback<TrackedEntityInstancesResultHolder>() {
            @Override
            public void onSuccess(ResponseHolder<TrackedEntityInstancesResultHolder> holder) {
                progressDialogFragment.dismiss();
                TrackedEntityInstancesResultHolder results = holder.getItem();
                DataValueLoader.saveTrackedEntityInstances(results.getTrackedEntityInstances(), results.getEnrollments(), results.getEvents(), false, currentOrganisationUnit);
            }

            @Override
            public void onFailure(ResponseHolder<TrackedEntityInstancesResultHolder> holder) {
                progressDialogFragment.dismiss();
            }
        };
        progressDialogFragment.show(getFragmentManager(), TAG);
        Log.d(TAG, "loading: " + getSelectedTrackedEntityInstances().size());
        DataValueLoader.loadTrackedEntityInstances(callback, getSelectedTrackedEntityInstances());
        dismiss();
    }

    public interface OnTrackedEntityInstanceSelectedListener {
        void onTrackedEntityInstanceSelected(int dialogId, int position, String id, String name);
    }

    @Subscribe
    public void onLoadingMessageEvent(final LoadingMessageEvent event) {
        Log.d(TAG, "Message received" + event.message);
        if(progressDialogFragment!=null && progressDialogFragment.getDialog() != null &&
                progressDialogFragment.getDialog().isShowing()) {
            ((ProgressDialog) progressDialogFragment.getDialog()).setMessage(event.message);
        }
    }

    static class ParameterParcelable implements Parcelable {
        public static final String TAG = ParameterParcelable.class.getSimpleName();
        private final List<TrackedEntityInstance> trackedEntityInstances;
        public ParameterParcelable(List<TrackedEntityInstance> trackedEntityInstances) {
            Log.d(TAG, "parcelputting " + trackedEntityInstances.size());
            this.trackedEntityInstances = trackedEntityInstances;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(trackedEntityInstances);
        }

        public List<TrackedEntityInstance> getTrackedEntityInstances() {
            return trackedEntityInstances;
        }
    }
}
