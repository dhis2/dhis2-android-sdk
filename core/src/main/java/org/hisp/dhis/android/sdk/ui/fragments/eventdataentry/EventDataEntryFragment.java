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

package org.hisp.dhis.android.sdk.ui.fragments.eventdataentry;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.raizlabs.android.dbflow.structure.Model;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.GpsController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.ui.adapters.SectionAdapter;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.IndicatorRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.Row;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.RunProgramRulesEvent;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnCompleteEventClick;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.DataEntryFragment;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.DataEntryFragmentSection;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.HideLoadingDialogEvent;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RefreshListViewEvent;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;
import org.hisp.dhis.android.sdk.utils.UiUtils;
import org.hisp.dhis.android.sdk.utils.comparators.EventDateComparator;
import org.hisp.dhis.android.sdk.utils.services.ProgramIndicatorService;
import org.hisp.dhis.android.sdk.utils.services.VariableService;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDataEntryFragment extends DataEntryFragment<EventDataEntryFragmentForm> {

    public static final String TAG = EventDataEntryFragment.class.getSimpleName();
    private Map<String, List<ProgramRule>> programRulesForDataElements;
    private Map<String, List<ProgramIndicator>> programIndicatorsForDataElements;

    private IndicatorEvaluatorThread indicatorEvaluatorThread;
    private EventSaveThread saveThread;

    public static final String ORG_UNIT_ID = "extra:orgUnitId";
    public static final String PROGRAM_ID = "extra:ProgramId";
    public static final String PROGRAM_STAGE_ID = "extra:ProgramStageId";
    public static final String EVENT_ID = "extra:EventId";
    public static final String ENROLLMENT_ID = "extra:EnrollmentId";
    private ImageView previousSectionButton;
    private ImageView nextSectionButton;
    private View spinnerContainer;
    private Spinner spinner;
    private SectionAdapter spinnerAdapter;
    private EventDataEntryFragmentForm form;
    private DateTime scheduledDueDate;

    public EventDataEntryFragment() {
        setProgramRuleFragmentHelper(new EventDataEntryRuleHelper(this));
    }

    public static EventDataEntryFragment newInstance(String unitId, String programId, String programStageId) {
        EventDataEntryFragment fragment = new EventDataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, unitId);
        args.putString(PROGRAM_ID, programId);
        args.putString(PROGRAM_STAGE_ID, programStageId);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventDataEntryFragment newInstance(String unitId, String programId, String programStageId,
                                                     long eventId) {
        EventDataEntryFragment fragment = new EventDataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, unitId);
        args.putString(PROGRAM_ID, programId);
        args.putString(PROGRAM_STAGE_ID, programStageId);
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventDataEntryFragment newInstanceWithEnrollment(String unitId, String programId, String programStageId,
                                                                   long enrollmentId) {
        EventDataEntryFragment fragment = new EventDataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, unitId);
        args.putString(PROGRAM_ID, programId);
        args.putString(PROGRAM_STAGE_ID, programStageId);
        args.putLong(ENROLLMENT_ID, enrollmentId);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventDataEntryFragment newInstanceWithEnrollment(String unitId, String programId, String programStageId,
                                                                   long enrollmentId, long eventId) {
        EventDataEntryFragment fragment = new EventDataEntryFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, unitId);
        args.putString(PROGRAM_ID, programId);
        args.putString(PROGRAM_STAGE_ID, programStageId);
        args.putLong(ENROLLMENT_ID, enrollmentId);
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        detachSpinner();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        GpsController.disableGps();
        super.onDetach();
    }

    private void detachSpinner() {
        if (isSpinnerAttached()) {
            if (spinnerContainer != null) {
                ((ViewGroup) spinnerContainer.getParent()).removeView(spinnerContainer);
                spinnerContainer = null;
                spinner = null;
                if (spinnerAdapter != null) {
                    spinnerAdapter.swapData(null);
                    spinnerAdapter = null;
                }
            }
        }
    }

    private boolean isSpinnerAttached() {
        return spinnerContainer != null;
    }

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        VariableService.reset();
        if (saveThread == null || saveThread.isKilled()) {
            saveThread = new EventSaveThread();
            saveThread.start();
        }
        saveThread.init(this);
        if (indicatorEvaluatorThread == null || indicatorEvaluatorThread.isKilled()) {
            indicatorEvaluatorThread = new IndicatorEvaluatorThread();
            indicatorEvaluatorThread.start();
        }

        indicatorEvaluatorThread.init(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        new Thread() {
            public void run() {
                saveThread.kill();
                indicatorEvaluatorThread.kill();
                indicatorEvaluatorThread = null;
                saveThread = null;
            }
        }.start();
        super.onDestroy();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_new_event);
        menuItem.setVisible(false);
    }

    void hideSection(String programStageSectionId) {
        if (spinnerAdapter != null) {
            spinnerAdapter.hideSection(programStageSectionId);
        }
    }

    @Override
    public Loader<EventDataEntryFragmentForm> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id && isAdded()) {
            // Adding Tables for tracking here is dangerous (since MetaData updates in background
            // can trigger reload of values from db which will reset all fields).
            // Hence, it would be more safe not to track any changes in any tables
            List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            Bundle fragmentArguments = args.getBundle(EXTRA_ARGUMENTS);
            return new DbLoader<>(
                    getActivity(), modelsToTrack, new EventDataEntryFragmentQuery(
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
    public void onLoadFinished(Loader<EventDataEntryFragmentForm> loader, EventDataEntryFragmentForm data) {
        if (loader.getId() == LOADER_ID && isAdded()) {
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            form = data;

            saveThread.setEvent(form.getEvent());

            if (form.getStatusRow() != null) {
                form.getStatusRow().setFragmentActivity(getActivity());
            }
            if (data.getStage() != null &&
                    data.getStage().getCaptureCoordinates()) {
                GpsController.activateGps(getActivity().getBaseContext());
            }else{
                if(hasCoordinateQuestion()){
                    GpsController.activateGps(getActivity().getBaseContext());
                }
            }
            if (data.getStage() != null &&
                    data.getStage().getCaptureCoordinates()) {
                GpsController.activateGps(getActivity().getBaseContext());
            }
            if (data.getSections() != null && !data.getSections().isEmpty()) {
                if (data.getSections().size() > 1) {
                    attachSpinner();
                    spinnerAdapter.swapData(data.getSections());
                } else {
                    if (form.getStage() != null) {
                        getActionBarToolbar().setTitle(form.getStage().getName());
                    }
                    DataEntryFragmentSection section = data.getSections().get(0);
                    listViewAdapter.swapData(section.getRows());
                }
            }

            if (form.getEvent() == null) {
                // form is null - show error message and disable editing
                showErrorAndDisableEditing("No event present");
            } else {
                OrganisationUnit eventOrganisationUnit = MetaDataController.getOrganisationUnit(form.getEvent().getOrganisationUnitId());
                if (eventOrganisationUnit == null) {
                    showErrorAndDisableEditing("Missing Organisation Unit");
                } else if (!OrganisationUnit.TYPE.ASSIGNED.equals(eventOrganisationUnit.getType())) { // if user is not assigned to the event's OrgUnit. Disable data entry screen
                    setEditableDataEntryRows(form, false, false);
                }
                if (Event.STATUS_COMPLETED.equals(form.getEvent().getStatus()) && form.getStage().isBlockEntryForm()) { // if event is completed and should be blocked. Disable data entry screen
                    setEditableDataEntryRows(form, false, true);
                }
            }

            initiateEvaluateProgramRules();
        }
    }

    private boolean hasCoordinateQuestion() {
        List<Row> rows = new ArrayList<>();
        if(form.getSections()!=null) {
            List<DataEntryFragmentSection> sections = form.getSections();
            for(DataEntryFragmentSection section : sections){
                rows.addAll(section.getRows());
            }
        }
        if(form.getCurrentSection()!=null){
            rows.addAll(form.getCurrentSection().getRows());
        }
        for(Row row:rows){
            if(row.getViewType()==(DataEntryRowTypes.QUESTION_COORDINATES.ordinal())){
                return  true;
            }
        }
        return false;
    }

    private void showErrorAndDisableEditing(String extraInfo) {
        Toast.makeText(getContext(), "Error with form: " + extraInfo +". Please retry.", Toast.LENGTH_LONG).show();
        setEditableDataEntryRows(form, false, false);
    }

    public void setEditableDataEntryRows(EventDataEntryFragmentForm form, boolean editableDataEntryRows, boolean editableStatusRow) {
        List<Row> rows = new ArrayList<>();
        if (form.getSections() != null && !form.getSections().isEmpty()) {
            if (form.getSections().size() > 1) {
                for (DataEntryFragmentSection section : form.getSections()) {
                    rows.addAll(section.getRows());
                }
            } else {
                rows = form.getSections().get(0).getRows();
            }
        }
        listViewAdapter.swapData(null);
        if (editableDataEntryRows) {
            for (Row row : rows) {
                row.setEditable(true);
            }
        } else {
            for (Row row : rows) {
                row.setEditable(false);
            }
        }
        if (editableStatusRow) {
            form.getStatusRow().setEditable(true);
        }

        listView.setAdapter(null);
//        listViewAdapter.swapData(rows);
        if (form.getSections() != null) {
            listViewAdapter.swapData(form.getSections().get(0).getRows()); //TODO find a better solution for this hack
        } else {
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
        }
        listView.setAdapter(listViewAdapter);
    }

    private void attachSpinner() {
        if (!isSpinnerAttached()) {
            final Toolbar toolbar = getActionBarToolbar();
            final LayoutInflater inflater = LayoutInflater.from(getActivity());
            spinnerContainer = inflater.inflate(
                    R.layout.toolbar_spinner, toolbar, false);
            previousSectionButton = (ImageView) spinnerContainer
                    .findViewById(R.id.previous_section);
            nextSectionButton = (ImageView) spinnerContainer
                    .findViewById(R.id.next_section);
            final ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            toolbar.addView(spinnerContainer, lp);
            spinnerAdapter = new SectionAdapter(inflater);
            spinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(this);
            previousSectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = spinner.getSelectedItemPosition();
                    if (!(currentPosition - 1 < 0)) {
                        currentPosition = currentPosition - 1;
                        spinner.setSelection(currentPosition);
                    }
                }
            });
            nextSectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = spinner.getSelectedItemPosition();
                    if (!(currentPosition + 1 >= spinnerAdapter.getCount())) {
                        currentPosition = currentPosition + 1;
                        spinner.setSelection(currentPosition);
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<EventDataEntryFragmentForm> loader) {
        if (loader.getId() == LOADER_ID) {
            if (spinnerAdapter != null) {
                spinnerAdapter.swapData(null);
            }
            if (listViewAdapter != null) {
                listViewAdapter.swapData(null);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectSection(position);
    }

    private void selectSection(int position) {
        DataEntryFragmentSection section = (DataEntryFragmentSection)
                spinnerAdapter.getItem(position);
        form.setCurrentSection(section);
        if (section != null) {
            listView.smoothScrollToPosition(INITIAL_POSITION);
            listViewAdapter.swapData(section.getRows());
        }
        updateSectionNavigationButtons();
    }

    @Override
    protected boolean isValid() {
        if (form.getEvent() == null || form.getStage() == null) {
            return false;
        }
        if (isEmpty(form.getEvent().getEventDate())) {
            return false;
        }
        Map<String, ProgramStageDataElement> dataElements = toMap(
                form.getStage().getProgramStageDataElements()
        );

        for (DataEntryFragmentSection dataEntryFragmentSection:form.getSections()) {
            for (Row row : dataEntryFragmentSection.getRows()) {
                if (row.getValidationError() != null) {
                    return false;
                }
            }
        }
        for (DataValue dataValue : form.getEvent().getDataValues()) {
            ProgramStageDataElement dataElement = dataElements.get(dataValue.getDataElement());
            if (dataElement == null) {
                return false;
            }
            if (dataElement.getCompulsory() && isEmpty(dataValue.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void save() {
        if (form != null && form.getEvent() != null) {
            flagDataChanged(false);
        }
    }

    @Override
    protected void proceed() {

    }

    @Override
    public ArrayList<String> getValidationErrors() {
        ArrayList<String> errors = new ArrayList<>();
        if (form.getEvent() == null || form.getStage() == null) {
            return errors;
        }
        if (isEmpty(form.getEvent().getEventDate())) {
            String reportDateDescription = form.getStage().getReportDateDescription() == null ?
                    getString(R.string.report_date) : form.getStage().getReportDateDescription();
            errors.add(reportDateDescription);
        }
        Map<String, ProgramStageDataElement> dataElements = toMap(
                form.getStage().getProgramStageDataElements()
        );
        for (DataValue dataValue : form.getEvent().getDataValues()) {
            ProgramStageDataElement dataElement = dataElements.get(dataValue.getDataElement());
            if (dataElement == null) {
                // don't do anything
            } else if (dataElement.getCompulsory() && isEmpty(dataValue.getValue())) {
                errors.add(MetaDataController.getDataElement(dataElement.getDataelement()).getDisplayName());
            }
        }
        return errors;
    }

    private void evaluateRulesAndIndicators(String dataElement) {
        if (dataElement == null || form == null || form.getIndicatorRows() == null) {
            return;
        }

        if (hasRules(dataElement)) {
            getProgramRuleFragmentHelper().getProgramRuleValidationErrors().clear();
            initiateEvaluateProgramRules();
        }
        if (hasIndicators(dataElement)) {
            initiateEvaluateProgramIndicators(dataElement);
        }
    }

    private boolean hasRules(String dataElement) {
        if (programRulesForDataElements == null) {
            return false;
        }
        return programRulesForDataElements.containsKey(dataElement);
    }

    private boolean hasIndicators(String dataElement) {
        if (programIndicatorsForDataElements == null) {
            return false;
        }
        return programIndicatorsForDataElements.containsKey(dataElement);
    }

    /**
     * Schedules evaluation and updating of views based on ProgramRules in a thread.
     * This is used to avoid stacking up calls to evaluateAndApplyProgramRules
     */
    public void initiateEvaluateProgramRules() {
        if (rulesEvaluatorThread != null) {
            rulesEvaluatorThread.schedule();
        }
    }

    /**
     * Schedules evaluation and updating of views based on ProgramIndicators in a thread.
     * This is used to avoid stacking up calls to evaluateAndApplyProgramIndicators
     *
     * @param dataElement
     */
    private synchronized void initiateEvaluateProgramIndicators(String dataElement) {
        if (programIndicatorsForDataElements == null) {
            return;
        }
        List<ProgramIndicator> programIndicators = programIndicatorsForDataElements.get(dataElement);
        indicatorEvaluatorThread.schedule(programIndicators);
    }

    void evaluateAndApplyProgramIndicator(ProgramIndicator programIndicator) {
        if (VariableService.getInstance().getProgramRuleVariableMap() == null) {
            VariableService.initialize(form.getEnrollment(), form.getEvent());
        }
        IndicatorRow indicatorRow = form.getIndicatorToIndicatorRowMap().get(programIndicator.getUid());
        updateIndicatorRow(indicatorRow, form.getEvent());
        refreshListView();
    }

    /**
     * Calculates and updates the value in a IndicatorRow view for the corresponding Indicator
     *
     * @param indicatorRow
     * @param event
     */
    private static void updateIndicatorRow(IndicatorRow indicatorRow, Event event) {
        String newValue = ProgramIndicatorService.
                getProgramIndicatorValue(event, indicatorRow.getIndicator());
        if (newValue == null) {
            newValue = "";
        }
        if (!newValue.equals(indicatorRow.getValue())) {
            indicatorRow.updateValue(newValue);
        }
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

    private void updateSectionNavigationButtons() {
        if (nextSectionButton != null && previousSectionButton != null) {
            if (spinner.getSelectedItemPosition() - 1 < 0) {
                previousSectionButton.setVisibility(View.INVISIBLE);
            } else {
                previousSectionButton.setVisibility(View.VISIBLE);
            }
            if (spinner.getSelectedItemPosition() + 1 >= spinnerAdapter.getCount()) {
                nextSectionButton.setVisibility(View.INVISIBLE);
            } else {
                nextSectionButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public SectionAdapter getSpinnerAdapter() {
        return spinnerAdapter;
    }

    public static ArrayList<String> getValidationErrors(Event event, ProgramStage programStage, Context context) {
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

    private static ArrayList<String> getRowsErrors(Context context, EventDataEntryFragmentForm form) {
        ArrayList<String> errors = new ArrayList<>();
        for (DataEntryFragmentSection dataEntryFragmentSection:form.getSections()){
            for(Row row: dataEntryFragmentSection.getRows()) {
                if (row.getValidationError() != null) {
                    Integer stringId = row.getValidationError();
                    if(stringId!=null) {
                        errors.add(context.getString(stringId));
                    }
                }
            }
        }
        return errors;
    }

    @Subscribe
    public void onHideLoadingDialog(HideLoadingDialogEvent event) {
        super.onHideLoadingDialog(event);
    }

    @Subscribe
    public void onUpdateSectionsSpinner(UpdateSectionsEvent event) {
        if (spinnerAdapter != null) {
            spinnerAdapter.notifyDataSetChanged();
            if (form != null && form.getCurrentSection() != null && form.getCurrentSection()
                    .isHidden()) {
                selectSection(0);
            }
        }
    }

    @Subscribe
    public void onRefreshListView(RefreshListViewEvent event) {
        super.onRefreshListView(event);
    }

    @Subscribe
    public void onDetailedInfoClick(OnDetailedInfoButtonClick eventClick) {
        super.onShowDetailedInfo(eventClick);
    }

    @Subscribe
    public void onItemClick(final OnCompleteEventClick eventClick) {
        if (isValid()) {
            if (!eventClick.getEvent().getStatus().equals(Event.STATUS_COMPLETED)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        UiUtils.showConfirmDialog(getActivity(), eventClick.getLabel(), eventClick.getAction(),
                                eventClick.getLabel(), getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String labelForCompleteButton = "";
                                        if (form.getStage().isBlockEntryForm()) {
                                            labelForCompleteButton = getString(R.string.edit);
                                        } else {
                                            labelForCompleteButton = getString(R.string.incomplete);
                                        }

                                        eventClick.getComplete().setText(labelForCompleteButton);
                                        eventClick.getEvent().setStatus(Event.STATUS_COMPLETED);
                                        form.getEvent().setFromServer(false);
                                        ProgramStage currentProgramStage = MetaDataController
                                                .getProgramStage(form.getEvent().getProgramStageId());

                                        // checking if should schedule new event
                                        boolean isShowingSchedulingOfNewEvent = false;
                                        if (currentProgramStage.getAllowGenerateNextVisit()) {
                                            if (currentProgramStage.getRepeatable()) {
                                                DateTime scheduleTime = calculateScheduledDate(currentProgramStage, form.getEnrollment());
                                                isShowingSchedulingOfNewEvent = true;
                                                showDatePicker(currentProgramStage, scheduleTime); // datePicker will close this fragment when date is picked and new event is scheduled
                                            } else {
                                                int sortOrder = currentProgramStage.getSortOrder();
                                                Program currentProgram = currentProgramStage.getProgram();
                                                ProgramStage programStageToSchedule = null;
                                                for (ProgramStage programStage : currentProgram.getProgramStages()) {
                                                    if (programStage.getSortOrder() == (sortOrder + 1)) {
                                                        programStageToSchedule = programStage;
                                                    }
                                                }

                                                if (programStageToSchedule != null) {

                                                    List<Event> events = form.getEnrollment().getEvents();
                                                    List<Event> eventForStage = new ArrayList<>();
                                                    for (Event event : events) {
                                                        if (programStageToSchedule.getUid().equals(event.getProgramStageId())) {
                                                            eventForStage.add(event);
                                                        }
                                                    }
                                                    if (eventForStage.size() < 1) {
                                                        DateTime dateTime = calculateScheduledDate(programStageToSchedule, form.getEnrollment());
                                                        isShowingSchedulingOfNewEvent = true;
                                                        showDatePicker(programStageToSchedule, dateTime); // datePicker will close this fragment when date is picked and new event is scheduled
                                                    }
                                                }
                                            }
                                        }
                                        // Checking if dataEntryForm should be blocked after completed
                                        if (currentProgramStage.isBlockEntryForm()) {
                                            setEditableDataEntryRows(form, false, true);
                                        }

                                        eventClick.getEvent().setCompletedDate(new DateTime().toString());

                                        Dhis2Application.getEventBus().post(new RowValueChangedEvent(null, null));
                                        //Exit the activity if it has just been completed.
                                        if (currentProgramStage.isBlockEntryForm() && !isShowingSchedulingOfNewEvent) {
                                            goBackToPreviousActivity();
                                        }
                                    }
                                });

                    }
                });
            } else {
                eventClick.getComplete().setText(R.string.complete);
                form.getEvent().setStatus(Event.STATUS_ACTIVE);
                form.getEvent().setFromServer(false);

                // Checking if dataEntryForm should be enabled after un-completed
                ProgramStage currentProgramStage = MetaDataController.getProgramStage(form.getEvent().getProgramStageId());

                if (currentProgramStage.isBlockEntryForm()) {
                    setEditableDataEntryRows(form, true, true);
                }

                Dhis2Application.getEventBus().post(new RowValueChangedEvent(null, null));
            }
        } else {
            showValidationErrorDialog(getValidationErrors(), getProgramRuleFragmentHelper().getProgramRuleValidationErrors(), getRowsErrors(getContext(), form));
        }
    }

    @Subscribe
    public void onRowValueChanged(final RowValueChangedEvent event) {
        super.onRowValueChanged(event);

        // do not run program rules for EditTextRows - DelayedDispatcher takes care of this
        if (event.getRow() == null || !(event.getRow().isEditTextRow())) {
            evaluateRulesAndIndicators(event.getId());
        }

        //if rowType is coordinate or event date, save the event
       if(event.getRowType() == null
                || DataEntryRowTypes.EVENT_COORDINATES.toString().equals(event.getRowType())
                || DataEntryRowTypes.EVENT_DATE.toString().equals(event.getRowType())) {
            //save event
            saveThread.scheduleSaveEvent();
            List<Event> eventsForEnrollment = new ArrayList<>();

            for (Event eventd : form.getEnrollment().getEvents()) {
                if (eventd.getUid().equals(form.getEvent().getUid())) {
                    eventsForEnrollment.add(form.getEvent());
                } else {
                    eventsForEnrollment.add(eventd);
                }
            }
            form.getEnrollment().setEvents(eventsForEnrollment);
        } else {// save data element
            saveThread.scheduleSaveDataValue(event.getId());
        }

        //rules evaluation are triggered depending on the data element uid and if it has rules
        //for event date, we have to trigger it manually
        if (DataEntryRowTypes.EVENT_DATE.toString().equals(event.getRowType())) {
            initiateEvaluateProgramRules();
        }
    }

    @Subscribe
    public void onRunProgramRules(final RunProgramRulesEvent event) {
        evaluateRulesAndIndicators(event.getId());
    }

    public EventSaveThread getSaveThread() {
        return saveThread;
    }

    public void setSaveThread(EventSaveThread saveThread) {
        this.saveThread = saveThread;
    }

    public EventDataEntryFragmentForm getForm() {
        return form;
    }

    public void setForm(EventDataEntryFragmentForm form) {
        this.form = form;
    }

    public Map<String, List<ProgramRule>> getProgramRulesForDataElements() {
        return programRulesForDataElements;
    }

    public void setProgramRulesForDataElements(Map<String, List<ProgramRule>> programRulesForDataElements) {
        this.programRulesForDataElements = programRulesForDataElements;
    }

    public Map<String, List<ProgramIndicator>> getProgramIndicatorsForDataElements() {
        return programIndicatorsForDataElements;
    }

    public void setProgramIndicatorsForDataElements(Map<String, List<ProgramIndicator>> programIndicatorsForDataElements) {
        this.programIndicatorsForDataElements = programIndicatorsForDataElements;
    }

    private void showDatePicker(final ProgramStage programStage, DateTime scheduledDueDate) {

//        final DateTime dueDate = new DateTime(1, 1, 1, 1, 0);
        int standardInterval = 0;

        if (programStage.getStandardInterval() > 0) {
            standardInterval = programStage.getStandardInterval();
        }

//        LocalDate currentDate = new LocalDate();

        final DatePickerDialog enrollmentDatePickerDialog =
                new DatePickerDialog(getActivity(),
                        null, scheduledDueDate.getYear(),
                        scheduledDueDate.getMonthOfYear() - 1, scheduledDueDate.getDayOfMonth() + standardInterval);
        enrollmentDatePickerDialog.setTitle(getActivity().getString(R.string.please_enter) + " Due date for " + programStage.getDisplayName());
        enrollmentDatePickerDialog.setCanceledOnTouchOutside(true);

        enrollmentDatePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatePicker dp = enrollmentDatePickerDialog.getDatePicker();
                        DateTime pickedDueDate = new DateTime(dp.getYear(), dp.getMonth() + 1, dp.getDayOfMonth(), 0, 0);
                        scheduleNewEvent(programStage, pickedDueDate);
                        goBackToPreviousActivity();
                    }
                });
        enrollmentDatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goBackToPreviousActivity();
                    }
                });

        enrollmentDatePickerDialog.show();
    }

    private DateTime calculateScheduledDate(ProgramStage programStage, Enrollment enrollment) {
        DateTime scheduledDate = new DateTime();

        if (programStage.getPeriodType() == null ||
                programStage.getPeriodType().equals("")) {
            List<Event> eventsForEnrollment = new ArrayList<>();
            eventsForEnrollment.addAll(enrollment.getEvents());
            Collections.sort(eventsForEnrollment, new EventDateComparator());
            Event lastKnownEvent = eventsForEnrollment.get(eventsForEnrollment.size() - 1);

            if (lastKnownEvent != null) {
                return new DateTime(lastKnownEvent.getEventDate());
            }

            if (programStage.getProgram().getDisplayIncidentDate()) {
                return new DateTime(enrollment.getIncidentDate());
            } else if (programStage.getGeneratedByEnrollmentDate()) {
                return new DateTime(enrollment.getEnrollmentDate());
            }
        } else {
            //// TODO: 18.04.16  implement periods
        }

        return scheduledDate;
    }

    private void goBackToPreviousActivity() {
        getActivity().finish();
    }

    public void scheduleNewEvent(ProgramStage programStage, DateTime scheduledDueDate) {
        Event event = new Event(form.getEnrollment().getOrgUnit(), Event.STATUS_FUTURE_VISIT,
                form.getEnrollment().getProgram().getUid(), programStage,
                form.getEnrollment().getTrackedEntityInstance(),
                form.getEnrollment().getEnrollment(), scheduledDueDate.toString());
        event.save();
        List<Event> eventsForEnrollment = form.getEnrollment().getEvents();
        eventsForEnrollment.add(event);
        form.getEnrollment().setEvents(eventsForEnrollment);
        form.getEnrollment().save();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            doBack();
            return true;
        }else
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean doBack() {
        List<String> errors = getRowsErrors(getContext(), form);
        if (errors.size() > 0) {
            showErrorAndGoBack();
            return false;
        } else {
            return super.doBack();
        }
    }

    private void showErrorAndGoBack() {

        String title = getContext().getString(R.string.validation_field_title);
        String message = getContext().getString(R.string.validation_field_exit);
        UiUtils.showConfirmDialog(getActivity(),
                title, message,
                getString(R.string.ok_option),
                getString(org.hisp.dhis.android.sdk.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //discard
                        EventDataEntryFragment.this.removeInvalidFields();
                        EventDataEntryFragment.super.doBack();
                    }
                });
    }

    private void removeInvalidFields() {
        for (DataEntryFragmentSection dataEntryFragmentSection : form.getSections()) {
            for (Row row : dataEntryFragmentSection.getRows()) {
                if (row.getValidationError() != null && row.getValue() != null) {
                    row.getValue().delete();
                }
            }
        }
    }
}
