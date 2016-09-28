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

import android.support.v4.app.Fragment;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.ui.fragments.common.IProgramRuleFragmentHelper;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.ValidationErrorDialog;
import org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType;
import org.hisp.dhis.android.sdk.utils.services.ProgramIndicatorService;
import org.hisp.dhis.android.sdk.utils.services.ProgramRuleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventDataEntryRuleHelper implements IProgramRuleFragmentHelper {

    private EventDataEntryFragment eventDataEntryFragment;
    private ArrayList<String> programRuleValidationErrors;

    public EventDataEntryRuleHelper(EventDataEntryFragment eventDataEntryFragment) {
        this.eventDataEntryFragment = eventDataEntryFragment;
        this.programRuleValidationErrors = new ArrayList<>();
    }

    @Override
    public ArrayList<String> getProgramRuleValidationErrors() {
        return programRuleValidationErrors;
    }

    @Override
    public void recycle() {
        eventDataEntryFragment = null;
    }

    @Override
    public void initiateEvaluateProgramRules() {
        eventDataEntryFragment.initiateEvaluateProgramRules();
    }

    @Override
    public void mapFieldsToRulesAndIndicators() {
        eventDataEntryFragment.setProgramRulesForDataElements(new HashMap<String, List<ProgramRule>>());
        eventDataEntryFragment.setProgramIndicatorsForDataElements(new HashMap<String, List<ProgramIndicator>>());
        for (ProgramRule programRule : eventDataEntryFragment.getForm().getStage().getProgram().getProgramRules()) {
            for (String dataElement : ProgramRuleService.getDataElementsInRule(programRule)) {
                List<ProgramRule> rulesForDataElement = eventDataEntryFragment.getProgramRulesForDataElements().get(dataElement);
                if (rulesForDataElement == null) {
                    rulesForDataElement = new ArrayList<>();
                    rulesForDataElement.add(programRule);
                    eventDataEntryFragment.getProgramRulesForDataElements().put(dataElement, rulesForDataElement);
                } else {
                    rulesForDataElement.add(programRule);
                }
            }
        }
        for (ProgramIndicator programIndicator : eventDataEntryFragment.getForm().getStage().getProgramIndicators()) {
            for (String dataElement : ProgramIndicatorService.getDataElementsInExpression(programIndicator)) {
                List<ProgramIndicator> programIndicatorsForDataElement = eventDataEntryFragment.getProgramIndicatorsForDataElements().get(dataElement);
                if (programIndicatorsForDataElement == null) {
                    programIndicatorsForDataElement = new ArrayList<>();
                    programIndicatorsForDataElement.add(programIndicator);
                    eventDataEntryFragment.getProgramIndicatorsForDataElements().put(dataElement, programIndicatorsForDataElement);
                } else {
                    programIndicatorsForDataElement.add(programIndicator);
                }
            }
        }
    }

    @Override
    public Fragment getFragment() {
        return eventDataEntryFragment;
    }

    @Override
    public List<ProgramRule> getProgramRules() {

        ArrayList<ProgramRule> programRules = new ArrayList<>();

        if (eventDataEntryFragment.getForm() == null) {
            return new ArrayList<>();
        }

        List<ProgramRule> allRules = eventDataEntryFragment.getForm().getStage().getProgram().getProgramRules();
        for (ProgramRule programRule : allRules) {
            if (programRule.getProgramStage() == null || programRule.getProgramStage().isEmpty()) {
                programRules.add(programRule);
            } else if (getEvent() != null &&
                    programRule.getProgramStage().equals(getEvent().getProgramStageId())) {
                programRules.add(programRule);
            }
        }
        return programRules;

    }

    @Override
    public Event getEvent() {
        return eventDataEntryFragment.getForm().getEvent();
    }

    @Override
    public void applyCreateEventRuleAction(ProgramRuleAction programRuleAction) {
        //do nothing
    }

    @Override
    public void applyDisplayKeyValuePairRuleAction(ProgramRuleAction programRuleAction) {
        //do nothing
    }

    @Override
    public void applyDisplayTextRuleAction(ProgramRuleAction programRuleAction) {
        //do nothing
    }

    @Override
    public Enrollment getEnrollment() {
        return eventDataEntryFragment.getForm().getEnrollment();
    }

    @Override
    public DataValue getDataElementValue(String dataElementId) {
        return eventDataEntryFragment.getForm().getDataValues().get(dataElementId);
    }

    @Override
    public TrackedEntityAttributeValue getTrackedEntityAttributeValue(String uid) {
        return eventDataEntryFragment.getForm().getTrackedEntityAttributeValues().get(uid);
    }

    @Override
    public void saveDataElement(String id) {
        if (eventDataEntryFragment != null &&
                eventDataEntryFragment.getSaveThread() != null) {
            eventDataEntryFragment.getSaveThread().scheduleSaveDataValue(id);
        }
    }

    @Override
    public void saveTrackedEntityAttribute(String uid) {
        TrackedEntityAttributeValue trackedEntityAttributeValue = getTrackedEntityAttributeValue(uid);
        if (trackedEntityAttributeValue != null) {
            trackedEntityAttributeValue.save();
        }
    }

    @Override
    public void applyHideSectionRuleAction(ProgramRuleAction programRuleAction) {
        eventDataEntryFragment.hideSection(programRuleAction.getProgramStageSection());
    }

    @Override
    public void updateUi() {
        if (eventDataEntryFragment.getForm().getEvent() != null
                && eventDataEntryFragment.getForm().getEvent().getEventDate() == null) {
            eventDataEntryFragment.getListViewAdapter().hideAll();
            if (eventDataEntryFragment.getSpinnerAdapter() != null) {
                eventDataEntryFragment.getSpinnerAdapter().hideAll();
            }
        }
        eventDataEntryFragment.updateSections();
        eventDataEntryFragment.refreshListView();
    }

    @Override
    public void applyShowWarningRuleAction(ProgramRuleAction programRuleAction) {
        String uid = programRuleAction.getDataElement();
        if (uid == null) {
            uid = programRuleAction.getTrackedEntityAttribute();
        }
        eventDataEntryFragment.getListViewAdapter().showWarningOnIndex(uid, programRuleAction.getContent());
    }

    @Override
    public void applyShowErrorRuleAction(ProgramRuleAction programRuleAction) {
        String uid = programRuleAction.getDataElement();
        if (uid == null) {
            uid = programRuleAction.getTrackedEntityAttribute();
        }
        eventDataEntryFragment.getListViewAdapter().showErrorOnIndex(uid, programRuleAction.getContent());
        if (!programRuleValidationErrors.contains(programRuleAction.getContent())) {
            programRuleValidationErrors.add(programRuleAction.getContent() + " " + programRuleAction.getData());
        }
    }

    @Override
    public void applyHideFieldRuleAction(ProgramRuleAction programRuleAction, List<String> affectedFieldsWithValue) {
        eventDataEntryFragment.getListViewAdapter().hideIndex(programRuleAction.getDataElement());
        DataValue dataValue = getDataElementValue(programRuleAction.getDataElement());
        if (dataValue != null && eventDataEntryFragment.containsValue(dataValue)) {// form.getDataValues().get(programRuleAction.getDataElement()))) {
            affectedFieldsWithValue.add(programRuleAction.getDataElement());
            dataValue.setValue(""); // After it is hidden, remove value
            // Post changes. Using an empty string as rowtype ensures effective persistence
            Dhis2Application.getEventBus().post(new RowValueChangedEvent(dataValue, ""));
        }
    }

    /**
     * Displays a warning dialog to the user, indicating the data entry rows with values in them
     * are being hidden due to program rules.
     *
     * @param fragment
     * @param affectedValues
     */
    public void showWarningHiddenValuesDialog(Fragment fragment, ArrayList<String> affectedValues) {
        ArrayList<String> dataElementNames = new ArrayList<>();
        for (String s : affectedValues) {
            DataElement de = MetaDataController.getDataElement(s);
            if (de != null) {
                dataElementNames.add(de.getDisplayName());
            }
        }
        if (dataElementNames.isEmpty()) {
            return;
        }
        if (eventDataEntryFragment.getValidationErrorDialog() == null || !eventDataEntryFragment.getValidationErrorDialog().isVisible()) {
            ValidationErrorDialog validationErrorDialog = ValidationErrorDialog
                    .newInstance(fragment.getString(R.string.warning_hidefieldwithvalue), dataElementNames
                    );
            eventDataEntryFragment.setValidationErrorDialog(validationErrorDialog);
            if (fragment.isAdded()) {
                eventDataEntryFragment.getValidationErrorDialog().show(fragment.getChildFragmentManager());
            }
        }
    }

    public void flagDataChanged(boolean hasChanged) {
        eventDataEntryFragment.flagDataChanged(hasChanged);
    }

    @Override
    public boolean blockingSpinnerNeeded() {
        List<ProgramRule> programRules = getProgramRules();
        for (ProgramRule programRule : programRules) {
            for (ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
                if (programRuleAction.getProgramRuleActionType().equals(ProgramRuleActionType.HIDEFIELD)) {
                    return true;
                }
            }
        }
        // we have no hidefield rules in this screen so no need to show a progress spinner
        return false;
    }
}
