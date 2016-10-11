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

import android.support.v4.app.Fragment;

import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface used by {@link AbsProgramRuleFragment} to be implemented in different scenarios
 * where {@link ProgramRule}s should be used and applied.
 */
public interface IProgramRuleFragmentHelper {

    /**
     * Returns a list of the names of {@link org.hisp.dhis.android.sdk.persistence.models.DataElement}s
     * that have {@link ProgramRule} errors affecting them.
     * @return
     */
    ArrayList<String> getProgramRuleValidationErrors();

    /**
     * Nullifies all necessary references
     */
    void recycle();

    void initiateEvaluateProgramRules();

    /**
     * Maps {@link org.hisp.dhis.android.sdk.persistence.models.DataElement}s and {@link org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute}s
     * to {@link ProgramRule}s and {@link org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator}s
     * so that they can later be looked up to see what Rules or Indicators are affected by value changes.
     */
    void mapFieldsToRulesAndIndicators();

    /**
     * Returns the current Fragment for displaying UI changes.
     * @return
     */
    Fragment getFragment();

    /**
     * Displays a Dialog showing a warning if Data Entry Rows are hidden.
     * @param fragment
     * @param affectedValues
     */
    void showWarningHiddenValuesDialog(Fragment fragment, ArrayList<String> affectedValues);

    /**
     * Triggers an update of the UI, usually because the contents of the UI has changed based on Program Rules
     */
    void updateUi();

    /**
     * Returns a list of all {@link ProgramRule} for the current context
     * @return
     */
    List<ProgramRule> getProgramRules();

    /**
     * Returns the {@link Enrollment} for the current context
     * @return
     */
    Enrollment getEnrollment();

    /**
     * Returns the {@link Event} for the current context.
     * @return
     */
    Event getEvent();

    /**
     * Applies a {@link ProgramRuleAction} of type {@link org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType#SHOWWARNING}
     * @param programRuleAction
     */
    void applyShowWarningRuleAction(ProgramRuleAction programRuleAction);

    /**
     * Applies a {@link ProgramRuleAction} of type {@link org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType#SHOWERROR}
     * @param programRuleAction
     */
    void applyShowErrorRuleAction(ProgramRuleAction programRuleAction);

    /**
     * Applies a {@link ProgramRuleAction} of type {@link org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType#HIDEFIELD}
     * @param programRuleAction
     * @param affectedFieldsWithValue a list of Strings for the name of the {@link org.hisp.dhis.android.sdk.persistence.models.DataElement}
     *                                or {@link org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute} that are affected by
     *                                the {@link ProgramRuleAction}
     */
    void applyHideFieldRuleAction(ProgramRuleAction programRuleAction, List<String> affectedFieldsWithValue);

    /**
     * Applies a {@link ProgramRuleAction} of type {@link org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType#HIDESECTION}
     * @param programRuleAction
     */
    void applyHideSectionRuleAction(ProgramRuleAction programRuleAction);

    /**
     * Applies a {@link ProgramRuleAction} of type {@link org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType#CREATEEVENT}
     * @param programRuleAction
     */
    void applyCreateEventRuleAction(ProgramRuleAction programRuleAction);

    /**
     * Applies a {@link ProgramRuleAction} of type {@link org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType#DISPLAYKEYVALUEPAIR}
     * @param programRuleAction
     */
    void applyDisplayKeyValuePairRuleAction(ProgramRuleAction programRuleAction);

    /**
     * Applies a {@link ProgramRuleAction} of type {@link org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType#DISPLAYTEXT}
     * @param programRuleAction
     */
    void applyDisplayTextRuleAction(ProgramRuleAction programRuleAction);

    /**
     * Returns the {@link DataValue} for a given {@link org.hisp.dhis.android.sdk.persistence.models.DataElement}
     * @param uid
     * @return
     */
    DataValue getDataElementValue(String uid);

    /**
     * Returns the {@link TrackedEntityAttributeValue} for a given {@link org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute}
     * @param uid
     * @return
     */
    TrackedEntityAttributeValue getTrackedEntityAttributeValue(String uid);

    /**
     * Marks that data has changed and that the UI needs updating
     * @param dataChanged
     */
    void flagDataChanged(boolean dataChanged);

    /**
     * Triggers saving of a {@link DataValue} for a given {@link org.hisp.dhis.android.sdk.persistence.models.DataElement}
     * @param uid
     */
    void saveDataElement(String uid);

    /**
     * Triggers saving of a {@link TrackedEntityAttributeValue} for a given {@link org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute}
     * @param uid
     */
    void saveTrackedEntityAttribute(String uid);

    boolean blockingSpinnerNeeded();
}
