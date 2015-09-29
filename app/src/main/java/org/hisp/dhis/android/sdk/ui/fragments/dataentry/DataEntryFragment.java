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

package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.ui.activities.INavigationHandler;
import org.hisp.dhis.android.sdk.ui.activities.OnBackPressedListener;
import org.hisp.dhis.android.sdk.ui.adapters.DataValueAdapter;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.CoordinatesRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.IndicatorRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.StatusRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.utils.UiUtils;

import java.util.ArrayList;

public abstract class DataEntryFragment<D> extends Fragment
        implements LoaderManager.LoaderCallbacks<D>, AdapterView.OnItemSelectedListener {
    public static final String TAG = DataEntryFragment.class.getSimpleName();

    protected static final int LOADER_ID = 17;
    protected static final int INITIAL_POSITION = 0;
    protected static final String EXTRA_ARGUMENTS = "extra:Arguments";
    protected static final String EXTRA_SAVED_INSTANCE_STATE = "extra:savedInstanceState";
    protected ListView listView;
    protected ProgressBar progressBar;
    protected DataValueAdapter listViewAdapter;
    protected boolean refreshing = false;
    protected ValidationErrorDialog validationErrorDialog;
    private boolean hasDataChanged = false;
    private INavigationHandler navigationHandler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof AppCompatActivity) {
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }
        if (activity instanceof INavigationHandler) {
            navigationHandler = (INavigationHandler) activity;
//            navigationHandler.setBackPressedListener(this); //dunno if this is necessary for some devices
        } else {
            throw new IllegalArgumentException("Activity must implement INavigationHandler interface");
        }
    }

    @Override
    public void onDetach() {
        if (getActivity() != null &&
                getActivity() instanceof AppCompatActivity) {
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setHomeButtonEnabled(false);
        }

        // we need to nullify reference
        // to parent activity in order not to leak it
        if (getActivity() != null &&
                getActivity() instanceof INavigationHandler) {
            ((INavigationHandler) getActivity()).setBackPressedListener(null);
        }

        navigationHandler = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Dhis2Application.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Dhis2Application.getEventBus().unregister(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_new_event);
        menuItem.setEnabled(false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_data_entry, menu);
        MenuItem menuItem = menu.findItem(R.id.action_new_event);
        if (!hasDataChanged) {
            menuItem.setEnabled(false);
            menuItem.getIcon().setAlpha(0x30);
        } else {
            menuItem.setEnabled(true);
            menuItem.getIcon().setAlpha(0xFF);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_entry, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        listView = (ListView) view.findViewById(R.id.datavalues_listview);
        View upButton = getLayoutInflater(savedInstanceState)
                .inflate(R.layout.up_button_layout, listView, false);
        listViewAdapter = new DataValueAdapter(getChildFragmentManager(),
                getLayoutInflater(savedInstanceState));

        listView.addFooterView(upButton);
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(listViewAdapter);

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.smoothScrollToPosition(INITIAL_POSITION);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            //go back
            return true;
        } else if (menuItem.getItemId() == R.id.action_new_event) {
            if (isValid()) {
                save();
            } else {
                showValidationErrorDialog(getValidationErrors());
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle argumentsBundle = new Bundle();
        argumentsBundle.putBundle(EXTRA_ARGUMENTS, getArguments());
        argumentsBundle.putBundle(EXTRA_SAVED_INSTANCE_STATE, savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, argumentsBundle, this);

        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public DataValueAdapter getListViewAdapter() {
        return listViewAdapter;
    }

    protected void showLoadingDialog() {
        UiUtils.showLoadingDialog(getChildFragmentManager(), R.string.please_wait);
    }

    public void hideLoadingDialog() {
        UiUtils.hideLoadingDialog(getChildFragmentManager());
    }

    public static void refreshListView() {
        RefreshListViewAsyncTask task = new RefreshListViewAsyncTask();
        task.execute();
    }

    protected void flagDataChanged(boolean changed) {
        if (hasDataChanged != changed) {
            hasDataChanged = changed;
            if(isAdded()) {
                getActivity().invalidateOptionsMenu();
            }
        }
    }

    protected void showValidationErrorDialog(ArrayList<String> errors) {
        validationErrorDialog = ValidationErrorDialog
                .newInstance(errors);
        validationErrorDialog.show(getChildFragmentManager());
    }

    protected boolean haveValuesChanged() {
        return hasDataChanged;
    }

    protected Toolbar getActionBarToolbar() {
        if (isAdded() && getActivity() != null) {
            return (Toolbar) getActivity().findViewById(R.id.toolbar);
        } else {
            throw new IllegalArgumentException("Fragment should be attached to MainActivity");
        }
    }

    private ActionBar getActionBar() {
        if (getActivity() != null &&
                getActivity() instanceof AppCompatActivity) {
            return ((AppCompatActivity) getActivity()).getSupportActionBar();
        } else {
            throw new IllegalArgumentException("Fragment should be attached to ActionBarActivity");
        }
    }

    @Subscribe
    public void onRowValueChanged(final RowValueChangedEvent event) {
        flagDataChanged(true);
    }

    @Subscribe
    public void onRefreshListView(RefreshListViewEvent event) {
        int start = listView.getFirstVisiblePosition();
        int end = listView.getLastVisiblePosition();
        for (int pos = 0; pos <= end - start; pos++) {
            View view = listView.getChildAt(pos);
            if (view != null) {
                int adapterPosition = view.getId();
                if (adapterPosition < 0 || adapterPosition >= listViewAdapter.getCount())
                    continue;
                if (!view.hasFocus()) {
                    listViewAdapter.getView(adapterPosition, view, listView);
                }
            }
        }
        refreshing = false;
    }

    @Subscribe
    public void onShowDetailedInfo(OnDetailedInfoButtonClick eventClick) // may inherit code from DataEntryFragment
    {
        String message = "";

        if(eventClick.getRow() instanceof CoordinatesRow)
            message = getResources().getString(R.string.detailed_info_coordinate_row);
        else if(eventClick.getRow() instanceof StatusRow)
            message = getResources().getString(R.string.detailed_info_status_row);
        else if(eventClick.getRow() instanceof IndicatorRow)
            message = ""; // need to change ProgramIndicator to extend BaseValue for this to work
        else         // rest of the rows can either be of data element or tracked entity instance attribute
            message = eventClick.getRow().getDescription();

        UiUtils.showConfirmDialog(getActivity(),
                getResources().getString(R.string.detailed_info_dataelement),
                message, getResources().getString(R.string.ok_option),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
    }

    @Subscribe
    public void onHideLoadingDialog(HideLoadingDialogEvent event) {
        hideLoadingDialog();
    }

    private static class RefreshListViewAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            Dhis2Application.getEventBus().post(new RefreshListViewEvent());
        }
    }

    protected abstract ArrayList<String> getValidationErrors();

    protected abstract boolean isValid();

    protected abstract void save();

    @Override
    public abstract void onLoadFinished(Loader<D> loader, D data);
}
