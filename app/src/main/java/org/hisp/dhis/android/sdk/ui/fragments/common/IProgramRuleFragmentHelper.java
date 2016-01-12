package org.hisp.dhis.android.sdk.ui.fragments.common;

import android.support.v4.app.Fragment;

import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;

import java.util.ArrayList;
import java.util.List;

public interface IProgramRuleFragmentHelper {

    void initiateEvaluateProgramRules();
    void mapFieldsToRulesAndIndicators();
    Fragment getFragment();
    void showWarningHiddenValuesDialog(Fragment fragment, ArrayList<String> affectedValues);
    void updateUi();
    List<ProgramRule> getProgramRules();
    Enrollment getEnrollment();
    Event getEvent();
    void applyShowWarningRuleAction(ProgramRuleAction programRuleAction);
    void applyShowErrorRuleAction(ProgramRuleAction programRuleAction);
    void applyHideFieldRuleAction(ProgramRuleAction programRuleAction, List<String> affectedFieldsWithValue);
    void applyHideSectionRuleAction(ProgramRuleAction programRuleAction);
    void applyCreateEventRuleAction(ProgramRuleAction programRuleAction);
    void applyDisplayKeyValuePairRuleAction(ProgramRuleAction programRuleAction);
    void applyDisplayTextRuleAction(ProgramRuleAction programRuleAction);
    DataValue getDataElementValue(String uid);
    TrackedEntityAttributeValue getTrackedEntityAttributeValue(String uid);
    void flagDataChanged(boolean dataChanged);
    void saveDataElement(String uid);
    void saveTrackedEntityAttribute(String uid);
}
