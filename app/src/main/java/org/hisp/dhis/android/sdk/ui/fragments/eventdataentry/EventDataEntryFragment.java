/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.sdk.ui.fragments.eventdataentry;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.raizlabs.android.dbflow.structure.Model;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.GpsController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.ui.adapters.SectionAdapter;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.IndicatorRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnCompleteEventClick;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.DataEntryFragment;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.DataEntryFragmentSection;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.HideLoadingDialogEvent;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RefreshListViewEvent;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;
import org.hisp.dhis.android.sdk.utils.UiUtils;
import org.hisp.dhis.android.sdk.utils.services.ProgramIndicatorService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EventDataEntryFragment extends DataEntryFragment<EventDataEntryFragmentForm> {

    public static final String TAG = EventDataEntryFragment.class.getSimpleName();
    private Map<String, List<ProgramRule>> programRulesForDataElements;
    private Map<String, List<ProgramIndicator>> programIndicatorsForDataElements;

    private IndicatorEvaluatorThread indicatorEvaluatorThread;
    private EventSaveThread saveThread;
    private RulesEvaluatorBufferThread rulesEvaluatorBufferThread;

    private static final String ORG_UNIT_ID = "extra:orgUnitId";
    private static final String PROGRAM_ID = "extra:ProgramId";
    private static final String PROGRAM_STAGE_ID = "extra:ProgramStageId";
    private static final String EVENT_ID = "extra:EventId";
    private static final String ENROLLMENT_ID = "extra:EnrollmentId";
    private ImageView previousSectionButton;
    private ImageView nextSectionButton;
    private View spinnerContainer;
    private Spinner spinner;
    private SectionAdapter spinnerAdapter;
    private EventDataEntryFragmentForm form;

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
        args.putLong(EVENT_ID, eventId);
        args.putLong(ENROLLMENT_ID, enrollmentId);
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
        if(saveThread == null || saveThread.isKilled()) {
            saveThread = new EventSaveThread();
            saveThread.start();
        }
        saveThread.init(this);
        if(indicatorEvaluatorThread == null || indicatorEvaluatorThread.isKilled()) {
            indicatorEvaluatorThread = new IndicatorEvaluatorThread();
            indicatorEvaluatorThread.start();
        }
        indicatorEvaluatorThread.init(this);
        rulesEvaluatorBufferThread = new RulesEvaluatorBufferThread(this);
        rulesEvaluatorBufferThread.start();
    }

    @Override
    public void onDestroy() {
        new Thread() {
            public void run() {
                saveThread.kill();
                indicatorEvaluatorThread.kill();
                indicatorEvaluatorThread = null;
                saveThread = null;
                rulesEvaluatorBufferThread.kill();
            }
        }.start();
        super.onDestroy();
    }

    void hideSection(String programStageSectionId) {
        if(spinnerAdapter!=null) {
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
            }
            if (!data.getSections().isEmpty()) {
                if (data.getSections().size() > 1) {
                    attachSpinner();
                    spinnerAdapter.swapData(data.getSections());
                } else {
                    if(form.getStage() != null) {
                        getActionBarToolbar().setTitle(form.getStage().getName());
                    }
                    DataEntryFragmentSection section = data.getSections().get(0);
                    listViewAdapter.swapData(section.getRows());
                }
            }
            initiateEvaluateProgramRules();
        }
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
                form.getStage() .getProgramStageDataElements()
        );
        for (DataValue dataValue : form.getEvent().getDataValues()) {
            ProgramStageDataElement dataElement = dataElements.get(dataValue.getDataElement());
            if (dataElement.getCompulsory() && isEmpty(dataValue.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void save() {
        if (form != null && form.getEvent()!=null) {
            flagDataChanged(false);
        }
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
            if (dataElement.getCompulsory() && isEmpty(dataValue.getValue())) {
                errors.add(MetaDataController.getDataElement(dataElement.getDataelement()).getDisplayName());
            }
        }
        return errors;
    }

    private void evaluateRulesAndIndicators(String dataElement) {
        if (dataElement == null || form == null || form.getIndicatorRows() == null) {
            return;
        }
        if(hasRules(dataElement)) {
            rulesEvaluatorBufferThread.trigger();
        }
        if(hasIndicators(dataElement)) {
            initiateEvaluateProgramIndicators(dataElement);
        }
    }

    private boolean hasRules(String dataElement) {
        if(programRulesForDataElements==null) {
            return false;
        }
        return programRulesForDataElements.containsKey(dataElement);
    }

    private boolean hasIndicators(String dataElement) {
        if(programIndicatorsForDataElements==null) {
            return false;
        }
        return programIndicatorsForDataElements.containsKey(dataElement);
    }

    /**
     * Schedules evaluation and updating of views based on ProgramRules in a thread.
     * This is used to avoid stacking up calls to evaluateAndApplyProgramRules
     */
    public void initiateEvaluateProgramRules() {
        if(rulesEvaluatorThread!=null) {
            rulesEvaluatorThread.schedule();
        }
    }

    /**
     * Schedules evaluation and updating of views based on ProgramIndicators in a thread.
     * This is used to avoid stacking up calls to evaluateAndApplyProgramIndicators
     * @param dataElement
     */
    private synchronized void initiateEvaluateProgramIndicators(String dataElement) {
        if(programIndicatorsForDataElements == null) {
            return;
        }
        List<ProgramIndicator> programIndicators = programIndicatorsForDataElements.get(dataElement);
        indicatorEvaluatorThread.schedule(programIndicators);
    }

    void evaluateAndApplyProgramIndicator(ProgramIndicator programIndicator) {
        IndicatorRow indicatorRow = form.getIndicatorToIndicatorRowMap().get(programIndicator.getUid());
        updateIndicatorRow(indicatorRow, form.getEvent());
        refreshListView();
    }

    /**
     * Calculates and updates the value in a IndicatorRow view for the corresponding Indicator
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

    @Subscribe
    public void onHideLoadingDialog(HideLoadingDialogEvent event) {
        super.onHideLoadingDialog(event);
    }

    @Subscribe
    public void onUpdateSectionsSpinner(UpdateSectionsEvent event) {
        if(spinnerAdapter != null) {
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
    public void onDetailedInfoClick(OnDetailedInfoButtonClick eventClick)
    {
        super.onShowDetailedInfo(eventClick);
    }

    @Subscribe
    public void onItemClick(final OnCompleteEventClick eventClick) {
        if(isValid()) {
            if(!eventClick.getEvent().getStatus().equals(Event.STATUS_COMPLETED)) {
                UiUtils.showConfirmDialog(getActivity(), eventClick.getLabel(), eventClick.getAction(),
                        eventClick.getLabel(), getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eventClick.getComplete().setText(R.string.incomplete);
                                eventClick.getEvent().setStatus(Event.STATUS_COMPLETED);
                                eventClick.getEvent().setFromServer(false);
                                Dhis2Application.getEventBus().post(new RowValueChangedEvent(null, null));
                            }
                        });
            } else {
                eventClick.getComplete().setText(R.string.complete);
                eventClick.getEvent().setStatus(Event.STATUS_ACTIVE);
                eventClick.getEvent().setFromServer(false);
                Dhis2Application.getEventBus().post(new RowValueChangedEvent(null, null));
            }
        } else {
            showValidationErrorDialog(getValidationErrors());
        }
    }

    @Subscribe
    public void onRowValueChanged(final RowValueChangedEvent event) {
        super.onRowValueChanged(event);
        evaluateRulesAndIndicators(event.getId());

        //if rowType is coordinate or event date, save the event
        if(event.getRowType() == null
                || DataEntryRowTypes.COORDINATES.toString().equals(event.getRowType())
                || DataEntryRowTypes.EVENT_DATE.toString().equals(event.getRowType())) {
            //save event
            saveThread.scheduleSaveEvent();
        } else {// save data element
            saveThread.scheduleSaveDataValue(event.getId());
        }

        //rules evaluation are triggered depending on the data element uid and if it has rules
        //for event date, we have to trigger it manually
        if(DataEntryRowTypes.EVENT_DATE.toString().equals(event.getRowType())) {
            rulesEvaluatorBufferThread.trigger();
        }
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
}
