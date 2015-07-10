/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.fragments.dataentry;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.raizlabs.android.dbflow.structure.Model;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.activities.INavigationHandler;
import org.hisp.dhis.android.sdk.activities.OnBackPressedListener;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.fragments.ProgressDialogFragment;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.utils.APIException;
import org.hisp.dhis.android.sdk.utils.OnEventClick;
import org.hisp.dhis.android.sdk.utils.services.ProgramIndicatorService;
import org.hisp.dhis.android.sdk.utils.services.ProgramRuleService;
import org.hisp.dhis.android.sdk.utils.ui.adapters.DataValueAdapter;
import org.hisp.dhis.android.sdk.utils.ui.adapters.SectionAdapter;
import org.hisp.dhis.android.sdk.utils.ui.adapters.rows.dataentry.IndicatorRow;
import org.hisp.dhis.android.sdk.utils.ui.adapters.rows.events.OnCompleteEventClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class DataEntryFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<DataEntryFragmentForm>,
        OnBackPressedListener, AdapterView.OnItemSelectedListener {
    public static final String TAG = DataEntryFragment.class.getSimpleName();

    private static final int LOADER_ID = 1;
    private static final int INITIAL_POSITION = 0;

    private static final String EXTRA_ARGUMENTS = "extra:Arguments";
    private static final String EXTRA_SAVED_INSTANCE_STATE = "extra:savedInstanceState";

    private static final String ORG_UNIT_ID = "extra:orgUnitId";
    private static final String PROGRAM_ID = "extra:ProgramId";
    private static final String PROGRAM_STAGE_ID = "extra:ProgramStageId";
    private static final String EVENT_ID = "extra:EventId";
    private static final String ENROLLMENT_ID = "extra:EnrollmentId";

    private ListView mListView;
    private ProgressBar mProgressBar;

    private ImageView mPreviousSectionButton;
    private ImageView mNextSectionButton;

    private View mSpinnerContainer;
    private Spinner mSpinner;
    private SectionAdapter mSpinnerAdapter;
    private DataValueAdapter mListViewAdapter;

    private DataEntryFragmentForm mForm;
    private ProgramRuleHelper mProgramRuleHelper;

    private boolean refreshing = false;
    private boolean hasDataChanged = false;
    private boolean saving = false;

    private ValidationErrorDialog validationErrorDialog;
    private ProgressDialogFragment progressDialogFragment;

    public static DataEntryFragment newInstance(String unitId, String programId, String programStageId) {
        DataEntryFragment fragment = new DataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, unitId);
        args.putString(PROGRAM_ID, programId);
        args.putString(PROGRAM_STAGE_ID, programStageId);
        fragment.setArguments(args);
        return fragment;
    }

    public static DataEntryFragment newInstance(String unitId, String programId, String programStageId,
                                                long eventId) {
        DataEntryFragment fragment = new DataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, unitId);
        args.putString(PROGRAM_ID, programId);
        args.putString(PROGRAM_STAGE_ID, programStageId);
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public static DataEntryFragment newInstanceWithEnrollment(String unitId, String programId, String programStageId,
                                                              long enrollmentId) {
        DataEntryFragment fragment = new DataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, unitId);
        args.putString(PROGRAM_ID, programId);
        args.putString(PROGRAM_STAGE_ID, programStageId);
        args.putLong(ENROLLMENT_ID, enrollmentId);
        fragment.setArguments(args);
        return fragment;
    }

    public static DataEntryFragment newInstanceWithEnrollment(String unitId, String programId, String programStageId,
                                                              long enrollmentId, long eventId) {
        DataEntryFragment fragment = new DataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, unitId);
        args.putString(PROGRAM_ID, programId);
        args.putString(PROGRAM_STAGE_ID, programStageId);
        args.putLong(EVENT_ID, eventId);
        args.putLong(ENROLLMENT_ID, enrollmentId);
        fragment.setArguments(args);
        return fragment;
    }

    private static Map<String, ProgramStageDataElement> toMap(List<ProgramStageDataElement> dataElements) {
        Map<String, ProgramStageDataElement> dataElementMap = new HashMap<>();
        if (dataElements != null && !dataElements.isEmpty()) {
            for (ProgramStageDataElement dataElement : dataElements) {
                dataElementMap.put(dataElement.getDataelement(), dataElement);
            }
        }
        return dataElementMap;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity) {
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        if (activity instanceof INavigationHandler) {
            ((INavigationHandler) activity).setBackPressedListener(this);
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

        Dhis2.disableGps();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_data_entry, menu);
        Log.d(TAG, "onCreateOptionsMenu");
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
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        mListView = (ListView) view.findViewById(R.id.datavalues_listview);
        View upButton = getLayoutInflater(savedInstanceState)
                .inflate(R.layout.up_button_layout, mListView, false);
        mListViewAdapter = new DataValueAdapter(getChildFragmentManager(),
                getLayoutInflater(savedInstanceState));

        mListView.addFooterView(upButton);
        mListView.setVisibility(View.VISIBLE);
        mListView.setAdapter(mListViewAdapter);

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.smoothScrollToPosition(INITIAL_POSITION);
            }
        });
    }

    @Override
    public void onDestroyView() {
        detachSpinner();
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            doBack();
            return true;
        } else if (menuItem.getItemId() == R.id.action_new_event) {
            if (validate()) {
                submitEvent();
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

        mProgressBar.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<DataEntryFragmentForm> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id && isAdded()) {
            // Adding Tables for tracking here is dangerous (since MetaData updates in background
            // can trigger reload of values from db which will reset all fields).
            // Hence, it would be more safe not to track any changes in any tables
            List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            Bundle fragmentArguments = args.getBundle(EXTRA_ARGUMENTS);
            return new DbLoader<>(
                    getActivity(), modelsToTrack, new DataEntryFragmentQuery(
                    fragmentArguments.getString(ORG_UNIT_ID),
                    fragmentArguments.getString(PROGRAM_ID),
                    fragmentArguments.getString(PROGRAM_STAGE_ID),
                    fragmentArguments.getLong(EVENT_ID, -1),
                    fragmentArguments.getLong(ENROLLMENT_ID, -1)
            )
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<DataEntryFragmentForm> loader, DataEntryFragmentForm data) {
        if (loader.getId() == LOADER_ID && isAdded()) {
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);

            mForm = data;
            mProgramRuleHelper = new ProgramRuleHelper(mForm.getStage().getProgram());
            if (mForm.getStatusRow() != null) {
                mForm.getStatusRow().setFragmentActivity(getActivity());
            }

            if (data.getStage() != null &&
                    data.getStage().getCaptureCoordinates()) {
                Dhis2.activateGps(getActivity().getBaseContext());
            }

            if (!data.getSections().isEmpty()) {
                if (data.getSections().size() > 1) {
                    attachSpinner();
                    mSpinnerAdapter.swapData(data.getSections());
                } else {
                    DataEntryFragmentSection section = data.getSections().get(0);
                    mListViewAdapter.swapData(section.getRows());
                    evaluateRules();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<DataEntryFragmentForm> loader) {
        if (loader.getId() == LOADER_ID) {
            if (mSpinnerAdapter != null) {
                mSpinnerAdapter.swapData(null);
            }
            if (mListViewAdapter != null) {
                mListViewAdapter.swapData(null);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectSection(position);
    }

    private void selectSection(int position) {
        if (hasDataChanged) {
            submitEvent();
        }
        DataEntryFragmentSection section = (DataEntryFragmentSection)
                mSpinnerAdapter.getItem(position);
        mForm.setCurrentSection(section);

        if (section != null) {
            mListView.smoothScrollToPosition(INITIAL_POSITION);
            mListViewAdapter.swapData(section.getRows());
            evaluateRules();
        }

        if (mNextSectionButton != null && mPreviousSectionButton != null) {
            if (position - 1 < 0) {
                mPreviousSectionButton.setVisibility(View.INVISIBLE);
            } else {
                mPreviousSectionButton.setVisibility(View.VISIBLE);
            }

            if (position + 1 >= mSpinnerAdapter.getCount()) {
                mNextSectionButton.setVisibility(View.INVISIBLE);
            } else {
                mNextSectionButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void doBack() {
        if (haveValuesChanged()) {
            Dhis2.getInstance().showConfirmDialog(getActivity(),
                    getString(R.string.discard), getString(R.string.discard_confirm_changes),
                    getString(R.string.discard),
                    getString(R.string.save_and_close),
                    getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (validate()) {
                                submitEvent();
                                getFragmentManager().popBackStack();
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Subscribe
    public void onItemClick(final OnCompleteEventClick eventClick)
    {
        if(validate() && !eventClick.getEvent().getStatus().equals(Event.STATUS_COMPLETED))
        {
            Dhis2.showConfirmDialog(getActivity(), eventClick.getLabel(), eventClick.getAction(),
                    eventClick.getLabel(), getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            flagDataChanged(true);
                            eventClick.getComplete().setText(R.string.incomplete);
                            eventClick.getEvent().setStatus(Event.STATUS_COMPLETED);
                        }
                    });

        }
        else
        {
            eventClick.getComplete().setText(R.string.complete);
            eventClick.getEvent().setStatus(Event.STATUS_ACTIVE);
        }

    }

    private boolean haveValuesChanged() {
        return hasDataChanged;
    }

    public void showWarningHiddenValuesDialog(ArrayList<String> affectedValues) {
        ArrayList<String> dataElementNames = new ArrayList<>();
        for (String s : affectedValues) {
            DataElement de = MetaDataController.getDataElement(s);
            if (de != null) {
                dataElementNames.add(de.getDisplayName());
            }
        }
        if (validationErrorDialog == null || !validationErrorDialog.isVisible()) {
            validationErrorDialog = ValidationErrorDialog
                    .newInstance(getString(R.string.warning_hidefieldwithvalue), dataElementNames
                    );
            validationErrorDialog.show(getChildFragmentManager());
        }
    }

    /**
     * Evaluates the ProgramRules for the current program and the current data values and applies
     * the results. This is for example used for hiding views if a rule contains skip logic
     */
    public void evaluateRules() {
//        showLoadingDialog(); // loadingDialog contains bugs. Won't dismiss 1/10 times
        new Thread() {
            public void run() {
                List<ProgramRule> rules = mForm.getStage().getProgram().getProgramRules();
                mListViewAdapter.resetHiding();
                if (mSpinnerAdapter != null) {
                    mSpinnerAdapter.resetHiding();
                }
                ArrayList<String> affectedFieldsWithValue = new ArrayList<>();
                boolean currentSelectedSectionRemoved = false;
                for (ProgramRule programRule : rules) {
                    boolean actionTrue = ProgramRuleService.evaluate(programRule.getCondition(), mForm.getEvent());
                    if (actionTrue) {
                        for (ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
                            boolean applyActionResult = applyProgramRuleAction(programRuleAction, actionTrue);
                            if (applyActionResult && programRuleAction.getProgramRuleActionType().equals(ProgramRuleAction.TYPE_HIDEFIELD)) {
                                affectedFieldsWithValue.add(programRuleAction.getDataElement());
                            } else if (applyActionResult && programRuleAction.getProgramRuleActionType().equals(ProgramRuleAction.TYPE_HIDESECTION)) {
                                currentSelectedSectionRemoved = true;
                            }
                        }
                    }
                }
                if (!affectedFieldsWithValue.isEmpty()) {
                    showWarningHiddenValuesDialog(affectedFieldsWithValue);
                }
                refreshListView();
                Activity activity = getActivity();
                if (mSpinnerAdapter != null) {
                    if (activity != null) {
                        activity.runOnUiThread(new UpdateSectionThread(currentSelectedSectionRemoved));
                    }
                } else {
//                    hideLoadingDialog(); // dialog.dismiss() won't work all times
                }
            }
        }.start();
    }

    private void showLoadingDialog() {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Thread() {
            public void run() {
                if (progressDialogFragment == null) {
                    progressDialogFragment = ProgressDialogFragment.newInstance(R.string.please_wait);
                }
                if (!progressDialogFragment.isAdded())
                    progressDialogFragment.show(getChildFragmentManager(), ProgressDialogFragment.TAG);
            }
        });
    }

    private void hideLoadingDialog() {
        if (progressDialogFragment != null) {
            if(progressDialogFragment.isAdded())
            {
                progressDialogFragment.dismiss();
                getChildFragmentManager().beginTransaction().remove(progressDialogFragment).commit();
//                getFragmentManager().beginTransaction().remove(progressDialogFragment).commit();
            }
            progressDialogFragment.dismiss(); // yolo
        }
    }

    private class UpdateSectionThread extends Thread {
        private final boolean refreshSelection;

        public UpdateSectionThread(boolean refreshSelection) {
            this.refreshSelection = refreshSelection;
        }

        @Override
        public void run() {
            mSpinnerAdapter.notifyDataSetChanged();
            if (refreshSelection) {
                selectSection(0);
            }
            hideLoadingDialog();
        }
    }

    public boolean applyProgramRuleAction(ProgramRuleAction programRuleAction, boolean actionTrue) {
        switch (programRuleAction.getProgramRuleActionType()) {
            case ProgramRuleAction.TYPE_HIDEFIELD: {
                if (actionTrue) {
                    return hideField(programRuleAction.getDataElement());
                }
                break;
            }

            case ProgramRuleAction.TYPE_HIDESECTION: {
                if (actionTrue) {
                    return hideSection(programRuleAction.getProgramStageSection());
                }
            }
        }
        return false;
    }

    /**
     * Hides a programstagesection from being displayed in the list of sections
     *
     * @param programStageSection
     * @return true if the section that's hidden is the one that's currently selected.
     */
    public boolean hideSection(String programStageSection) {
        if (mSpinnerAdapter == null) return false;
        DataEntryFragmentSection currentSection = mForm.getCurrentSection();
        mSpinnerAdapter.hideSection(programStageSection);
        if (currentSection.getId().equals(programStageSection)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Hides a field in the listView of dataEntryRows. Returns true if the hidden field contained
     * a value
     *
     * @param dataElement
     * @return
     */
    public boolean hideField(String dataElement) {
        mListViewAdapter.hideIndex(dataElement);
        DataValue dv = mForm.getDataValues().get(dataElement);
        if (dv != null && dv.getValue() != null && !dv.getValue().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private void refreshListView() {
        Activity activity = getActivity();
        if (activity == null) {
            refreshing = false;
            return;
        }
        activity.runOnUiThread(new Thread() {
            public void run() {
                int start = mListView.getFirstVisiblePosition();
                int end = mListView.getLastVisiblePosition();
                for (int pos = 0; pos <= end - start; pos++) {
                    View view = mListView.getChildAt(pos);
                    if (view != null) {
                        int adapterPosition = view.getId();
                        if (adapterPosition < 0 || adapterPosition >= mListViewAdapter.getCount())
                            continue;
                        if (!view.hasFocus()) {
                            mListViewAdapter.getView(adapterPosition, view, mListView);
                        }
                    }
                }
                refreshing = false;
            }
        });
    }

    public void flagDataChanged(boolean changed) {
        if (hasDataChanged != changed) {
            hasDataChanged = changed;
            getActivity().invalidateOptionsMenu();
        }
    }


    @Subscribe
    public void onRowValueChanged(final RowValueChangedEvent event) {
        Log.d(TAG, "onRowValueChanged");
        flagDataChanged(true);
        if (mForm == null || mForm.getIndicatorRows() == null) {
            return;
        }
        if (refreshing)
            return; //we don't want to stack this up since it runs every time a character is entered for example
        refreshing = true;

        new Thread() {
            public void run() {
                /**
                 * Updating views based on ProgramRules
                 */
                if (event.isDataValue() && mProgramRuleHelper.dataElementInRule(event.getId())) {
                    evaluateRules();
                }

                /*
                * updating indicator values in rows
                * */
                for (IndicatorRow indicatorRow : mForm.getIndicatorRows()) {
                    String newValue = ProgramIndicatorService.
                            getProgramIndicatorValue(mForm.getEvent(), indicatorRow.getIndicator());
                    if (newValue == null) {
                        newValue = "";
                    }
                    if (!newValue.equals(indicatorRow.getValue())) {
                        indicatorRow.updateValue(newValue);
                    }
                }

                refreshListView();
            }
        }.start();
    }

    private ActionBar getActionBar() {
        if (getActivity() != null &&
                getActivity() instanceof AppCompatActivity) {
            return ((AppCompatActivity) getActivity()).getSupportActionBar();
        } else {
            throw new IllegalArgumentException("Fragment should be attached to ActionBarActivity");
        }
    }

    private Toolbar getActionBarToolbar() {
        if (isAdded() && getActivity() != null) {
            return (Toolbar) getActivity().findViewById(R.id.toolbar);
        } else {
            throw new IllegalArgumentException("Fragment should be attached to MainActivity");
        }
    }

    private void attachSpinner() {
        if (!isSpinnerAttached()) {
            final Toolbar toolbar = getActionBarToolbar();

            final LayoutInflater inflater = LayoutInflater.from(getActivity());
            mSpinnerContainer = inflater.inflate(
                    R.layout.toolbar_spinner, toolbar, false);
            mPreviousSectionButton = (ImageView) mSpinnerContainer
                    .findViewById(R.id.previous_section);
            mNextSectionButton = (ImageView) mSpinnerContainer
                    .findViewById(R.id.next_section);
            final ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            toolbar.addView(mSpinnerContainer, lp);

            mSpinnerAdapter = new SectionAdapter(inflater);

            mSpinner = (Spinner) mSpinnerContainer.findViewById(R.id.toolbar_spinner);
            mSpinner.setAdapter(mSpinnerAdapter);
            mSpinner.setOnItemSelectedListener(this);

            mPreviousSectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = mSpinner.getSelectedItemPosition();
                    if (!(currentPosition - 1 < 0)) {
                        currentPosition = currentPosition - 1;
                        mSpinner.setSelection(currentPosition);
                    }
                }
            });

            mNextSectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = mSpinner.getSelectedItemPosition();
                    if (!(currentPosition + 1 >= mSpinnerAdapter.getCount())) {
                        currentPosition = currentPosition + 1;
                        mSpinner.setSelection(currentPosition);
                    }
                }
            });
        }
    }

    private void detachSpinner() {
        if (isSpinnerAttached()) {
            if (mSpinnerContainer != null) {
                ((ViewGroup) mSpinnerContainer.getParent()).removeView(mSpinnerContainer);
                mSpinnerContainer = null;
                mSpinner = null;
                if (mSpinnerAdapter != null) {
                    mSpinnerAdapter.swapData(null);
                    mSpinnerAdapter = null;
                }
            }
        }
    }

    private boolean isSpinnerAttached() {
        return mSpinnerContainer != null;
    }

    public boolean validate() {
        ArrayList<String> errors = isEventValid(mForm.getEvent(), mForm.getStage(), getActivity());
        if (!errors.isEmpty()) {
            ValidationErrorDialog dialog = ValidationErrorDialog
                    .newInstance(errors);
            dialog.show(getChildFragmentManager());
            return false;
        } else {
            return true;
        }
    }

    /**
     * returns true if the event was successfully saved
     *
     * @return
     */
    public void submitEvent() {
        if (saving) return;
        flagDataChanged(false);
        new Thread() {
            public void run() {
                saving = true;
                if (mForm != null && isAdded()) {
                    final Context context = getActivity().getBaseContext();

                    mForm.getEvent().setFromServer(false);
                    //mForm.getEvent().setLastUpdated(Utils.getCurrentTime());
                    mForm.getEvent().save();

                    final ApiRequestCallback callback = new ApiRequestCallback() {
                        @Override
                        public void onSuccess(Response response) {
                            //do nothing
                        }

                        @Override
                        public void onFailure(APIException exception) {
                            //do nothing
                        }
                    };

                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Dhis2.sendLocalData(context, callback);
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(timerTask, 5000);
                }
                saving = false;
            }

        }.start();
    }

    public static ArrayList<String> isEventValid(Event event, ProgramStage programStage,
                                                 Context context) {
        ArrayList<String> errors = new ArrayList<>();

        if (event == null || programStage == null) {
            return errors;
        }

        if (isEmpty(event.getEventDate())) {
            String reportDateDescription = programStage.getReportDateDescription() == null ?
                    context.getString(R.string.report_date) : programStage.getReportDateDescription();
            errors.add(reportDateDescription);
        }

        Map<String, ProgramStageDataElement> dataElements = toMap(
                programStage.getProgramStageDataElements()
        );

        for (DataValue dataValue : event.getDataValues()) {
            ProgramStageDataElement dataElement = dataElements.get(dataValue.getDataElement());
            if (dataElement.getCompulsory() && isEmpty(dataValue.getValue())) {
                errors.add(MetaDataController.getDataElement(dataElement.getDataelement()).getDisplayName());
            }
        }

        return errors;
    }
}