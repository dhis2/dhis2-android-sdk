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

package org.hisp.dhis.android.sdk.ui.fragments.common;

import android.app.ProgressDialog;
import android.util.Log;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.utils.comparators.ProgramRulePriorityComparator;
import org.hisp.dhis.android.sdk.utils.services.ProgramRuleService;
import org.hisp.dhis.android.sdk.utils.services.VariableService;
import org.hisp.dhis.client.sdk.ui.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract Fragment that can be extended by Fragments that want to make use of Program Rules.
 *
 * @param <D>
 */
public abstract class AbsProgramRuleFragment<D> extends BaseFragment {

    private static final String TAG = AbsProgramRuleFragment.class.getSimpleName();
    protected IProgramRuleFragmentHelper programRuleFragmentHelper;
    private ProgressDialog progressDialog;

    public IProgramRuleFragmentHelper getProgramRuleFragmentHelper() {
        return programRuleFragmentHelper;
    }

    public void setProgramRuleFragmentHelper(IProgramRuleFragmentHelper programRuleFragmentHelper) {
        this.programRuleFragmentHelper = programRuleFragmentHelper;
    }

    /**
     * Evaluates the ProgramRules for the current program and the current data values and applies
     * the results. This is for example used for hiding views if a rule contains skip logic
     * If no rules exist for Enrollment, this won't be run
     */
    public void evaluateAndApplyProgramRules() {

        if (programRuleFragmentHelper == null ||
                programRuleFragmentHelper.getEnrollment() == null ||
                programRuleFragmentHelper.getEnrollment().getProgram() == null ||
                programRuleFragmentHelper.getEnrollment().getProgram().getProgramRules() == null ||
                programRuleFragmentHelper.getEnrollment().getProgram().getProgramRules().isEmpty()) {
            return;
        }
        if (programRuleFragmentHelper.blockingSpinnerNeeded()) {
            //showBlockingProgressBar();
        }
        VariableService.initialize(programRuleFragmentHelper.getEnrollment(), programRuleFragmentHelper.getEvent());
        programRuleFragmentHelper.mapFieldsToRulesAndIndicators();
        ArrayList<String> affectedFieldsWithValue = new ArrayList<>();

        List<ProgramRule> programRules = programRuleFragmentHelper.getProgramRules();

        Collections.sort(programRules, new ProgramRulePriorityComparator());
        for (ProgramRule programRule : programRules) {
            try {
                boolean evaluatedTrue = ProgramRuleService.evaluate(programRule.getCondition());
                for (ProgramRuleAction action : programRule.getProgramRuleActions()) {
                    if (evaluatedTrue) {
                        applyProgramRuleAction(action, affectedFieldsWithValue);
                    }
                }
            } catch (Exception e) {
                Log.e("PROGRAM RULE", "Error evaluating program rule", e);
            }
        }

//        Collections.sort(programRules, new ProgramRulePriorityComparator());
//        for (ProgramRule programRule : programRules) {
//            try {
//                boolean evaluatedTrue = ProgramRuleService.evaluate(programRule.getCondition());
//                for (ProgramRuleAction action : programRule.getProgramRuleActions()) {
//                    if (evaluatedTrue) {
//                        applyProgramRuleAction(action, affectedFieldsWithValue);
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("PROGRAM RULE", "Error evaluating program rule", e);
//            }
//        }

        if (!affectedFieldsWithValue.isEmpty()) {
            programRuleFragmentHelper.showWarningHiddenValuesDialog(programRuleFragmentHelper.getFragment(), affectedFieldsWithValue);
        }
        //hideBlockingProgressBar();
        programRuleFragmentHelper.updateUi();
    }

    protected void applyProgramRuleAction(ProgramRuleAction programRuleAction, List<String> affectedFieldsWithValue) {

        switch (programRuleAction.getProgramRuleActionType()) {
            case HIDEFIELD: {
                programRuleFragmentHelper.applyHideFieldRuleAction(programRuleAction, affectedFieldsWithValue);
                break;
            }
            case HIDESECTION: {
                programRuleFragmentHelper.applyHideSectionRuleAction(programRuleAction);
                break;
            }
            case SHOWWARNING: {
                programRuleFragmentHelper.applyShowWarningRuleAction(programRuleAction);
                break;
            }
            case SHOWERROR: {
                programRuleFragmentHelper.applyShowErrorRuleAction(programRuleAction);
                break;
            }
            case ASSIGN: {
                applyAssignRuleAction(programRuleAction);
                break;
            }
            case CREATEEVENT: {
                programRuleFragmentHelper.applyCreateEventRuleAction(programRuleAction);
                break;
            }
            case DISPLAYKEYVALUEPAIR: {
                programRuleFragmentHelper.applyDisplayKeyValuePairRuleAction(programRuleAction);
                break;
            }
            case DISPLAYTEXT: {
                programRuleFragmentHelper.applyDisplayTextRuleAction(programRuleAction);
                break;
            }
        }
    }


            protected void applyAssignRuleAction(ProgramRuleAction programRuleAction) {
        String stringResult = ProgramRuleService.getCalculatedConditionValue(programRuleAction.getData());
        String programRuleVariableName = programRuleAction.getContent();
        ProgramRuleVariable programRuleVariable;
        if (programRuleVariableName != null) {
            programRuleVariableName = programRuleVariableName.substring(2, programRuleVariableName.length() - 1);
            programRuleVariable = VariableService.getInstance().getProgramRuleVariableMap().get(programRuleVariableName);
            programRuleVariable.setVariableValue(stringResult);
            programRuleVariable.setHasValue(true);
        }
        String dataElementId = programRuleAction.getDataElement();
        if (dataElementId != null) {
            DataValue dataValue = programRuleFragmentHelper.getDataElementValue(dataElementId);
            if (dataValue != null) {
                dataValue.setValue(stringResult);
                programRuleFragmentHelper.flagDataChanged(true);
                programRuleFragmentHelper.saveDataElement(dataElementId);
            }
        }
        String trackedEntityAttributeId = programRuleAction.getTrackedEntityAttribute();
        if (trackedEntityAttributeId != null) {
            TrackedEntityAttributeValue trackedEntityAttributeValue = programRuleFragmentHelper.getTrackedEntityAttributeValue(trackedEntityAttributeId);
            if (trackedEntityAttributeValue != null) {
                trackedEntityAttributeValue.setValue(stringResult);
                programRuleFragmentHelper.flagDataChanged(true);
                programRuleFragmentHelper.saveTrackedEntityAttribute(trackedEntityAttributeId);
            }
        }
    }

    public void showBlockingProgressBar() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && isAdded()) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        progressDialog = ProgressDialog.show(
                                getContext(), "", getString(R.string.please_wait), true, false);
                    }
                }
            });
        }
    }

    public void hideBlockingProgressBar() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && isAdded()) {
                        if (progressDialog != null) {
                            progressDialog.cancel();
                            progressDialog.dismiss();
                        } else {
                            Log.w("HIDE PROGRESS",
                                    "Unable to hide progress dialog: AbsProgramRuleFragment.progressDialog is null");
                        }
                    }
                }
            });
        }
    }
}