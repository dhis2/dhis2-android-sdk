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

package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.ui.activities.OnBackPressedListener;
import org.hisp.dhis.android.sdk.ui.adapters.DataValueAdapter;
import org.hisp.dhis.android.sdk.ui.adapters.SectionAdapter;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.EventCoordinatesRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.IndicatorRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.QuestionCoordinatesRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.StatusRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.common.AbsProgramRuleFragment;
import org.hisp.dhis.android.sdk.ui.fragments.eventdataentry.RulesEvaluatorThread;
import org.hisp.dhis.android.sdk.ui.fragments.eventdataentry.UpdateSectionsEvent;
import org.hisp.dhis.android.sdk.utils.UiUtils;

import java.util.ArrayList;

public abstract class DataEntryFragment<D> extends AbsProgramRuleFragment<D>
        implements LoaderManager.LoaderCallbacks<D>, AdapterView.OnItemSelectedListener,
        OnBackPressedListener {
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
    protected RulesEvaluatorThread rulesEvaluatorThread;
    private Parcelable listViewState;
    private Parcelable listViewAdapterState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (rulesEvaluatorThread == null || rulesEvaluatorThread.isKilled()) {
            rulesEvaluatorThread = new RulesEvaluatorThread();
            rulesEvaluatorThread.start();
        }
        rulesEvaluatorThread.init(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rulesEvaluatorThread.kill();
        rulesEvaluatorThread = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Dhis2Application.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        listViewState = listView.onSaveInstanceState();
        super.onPause();
        Dhis2Application.getEventBus().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_data_entry, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_entry, container, false);
    }

    public ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
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

        if (listViewState != null) {
            listView.onRestoreInstanceState(listViewState);
        }

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
            getActivity().finish();
            return true;
        } else if (menuItem.getItemId() == R.id.action_new_event) {
            proceed();
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
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public static void resetHidingAndWarnings(DataValueAdapter dataValueAdapter, SectionAdapter sectionAdapter) {
        if (dataValueAdapter != null) {
            dataValueAdapter.resetHiding();
            dataValueAdapter.resetWarnings();
            dataValueAdapter.resetErrors();
        }
        if (sectionAdapter != null) {
            sectionAdapter.resetHiding();
        }
    }

    public static boolean containsValue(BaseValue value) {
        if (value != null && value.getValue() != null && !value.getValue().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void updateSections() {
        Dhis2Application.getEventBus().post(new UpdateSectionsEvent());
    }

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
        Dhis2Application.getEventBus().post(new RefreshListViewEvent());
    }

    public void flagDataChanged(boolean changed) {
        if (hasDataChanged != changed) {
            hasDataChanged = changed;
            if (isAdded()) {
                getActivity().invalidateOptionsMenu();
            }
        }
    }

    private void showErrorsDialog(ArrayList<String> errors) {
        if (!errors.isEmpty()) {
            validationErrorDialog = ValidationErrorDialog
                    .newInstance(getActivity().getString(R.string.unable_to_complete_registration) + " " + getActivity().getString(R.string.review_errors), errors);
            validationErrorDialog.show(getChildFragmentManager());
        } else {
            Toast.makeText(getContext(), R.string.unable_to_complete_registration, Toast.LENGTH_LONG).show();

        }
    }

    protected void showValidationErrorDialog(ArrayList<String> mandatoryFieldsMissingErrors, ArrayList<String> programRulesErrors, ArrayList<String> fieldValidationError) {
        ArrayList<String> errors = new ArrayList<>();
        addMandatoryErrors(mandatoryFieldsMissingErrors, errors);
        addErrors(programRulesErrors, errors);
        addErrors(fieldValidationError, errors);
        showErrorsDialog(errors);
    }

    private void addErrors(ArrayList<String> programRulesErrors,
            ArrayList<String> errors) {
        if (programRulesErrors != null) {
            for (String programRulesError : programRulesErrors) {
                errors.add(getActivity().getString(R.string.error_message) + ": " + programRulesError);
            }
        }
    }

    private void addMandatoryErrors(ArrayList<String> mandatoryFieldsMissingErrors,
            ArrayList<String> errors) {
        if (mandatoryFieldsMissingErrors != null) {
            for (String mandatoryFieldsError : mandatoryFieldsMissingErrors) {
                errors.add(getActivity().getString(R.string.missing_mandatory_field) + ": " + mandatoryFieldsError);
            }
        }
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

    public ValidationErrorDialog getValidationErrorDialog() {
        return validationErrorDialog;
    }

    public void setValidationErrorDialog(ValidationErrorDialog validationErrorDialog) {
        this.validationErrorDialog = validationErrorDialog;
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
                if (adapterPosition < 0 || adapterPosition >= listViewAdapter.getCount()) {
                    continue;
                }
                listViewAdapter.getView(adapterPosition, view, listView);
            }
        }
        refreshing = false;
    }

    @Subscribe
    public void onShowDetailedInfo(OnDetailedInfoButtonClick eventClick) // may inherit code from DataEntryFragment
    {
        String message = "";

        if(eventClick.getRow() instanceof EventCoordinatesRow || eventClick.getRow() instanceof QuestionCoordinatesRow)
            message = getResources().getString(R.string.detailed_info_coordinate_row);
        else if (eventClick.getRow() instanceof StatusRow)
            message = getResources().getString(R.string.detailed_info_status_row);
        else if (eventClick.getRow() instanceof IndicatorRow)
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

    public abstract SectionAdapter getSpinnerAdapter();

    protected abstract ArrayList<String> getValidationErrors();

    protected abstract boolean isValid();

    protected abstract void save();

    protected abstract void proceed();

    @Override
    public abstract void onLoadFinished(Loader<D> loader, D data);

    @Override
    public boolean doBack() {
        if (getActivity() != null) {
            getActivity().finish();
        }
        return false;
    }
}
