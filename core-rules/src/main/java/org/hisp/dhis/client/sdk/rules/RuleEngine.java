/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.constant.Constant;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public class RuleEngine {
    private final List<TrackedEntityAttribute> trackedEntityAttributes;
    private final List<ProgramRuleVariable> programRuleVariables;
    private final List<ProgramRule> programRules;
    private final List<DataElement> dataElements;
    private final List<OptionSet> optionSets;
    private final List<Constant> constants;

    private RuleEngine(List<TrackedEntityAttribute> trackedEntityAttributes,
                       List<ProgramRuleVariable> programRuleVariables,
                       List<ProgramRule> programRules, List<DataElement> dataElements,
                       List<OptionSet> optionSets, List<Constant> constants) {
        this.programRuleVariables = programRuleVariables;
        this.programRules = programRules;
        this.dataElements = dataElements;
        this.trackedEntityAttributes = trackedEntityAttributes;
        this.optionSets = optionSets;
        this.constants = constants;
    }

    private List<RuleEffect> execute(
            Event event, TrackedEntityInstance instance, List<Event> events) {

        // this is the place where magic happens
        return new ArrayList<>();
    }

    public List<RuleEffect> execute(Event currentEvent, Enrollment enrollment) {
        return execute(currentEvent, enrollment.getTrackedEntityInstance(), enrollment.getEvents());
    }

    public List<RuleEffect> execute(Event currentEvent, List<Event> events) {
        return execute(currentEvent, null, events);
    }

    public static class Builder {
        private List<TrackedEntityAttribute> trackedEntityAttributes;
        private List<ProgramRuleVariable> programRuleVariables;
        private List<ProgramRule> programRules;
        private List<DataElement> dataElements;
        private List<OptionSet> optionSets;
        private List<Constant> constants;

        public Builder() {
            // explicit empty constructor
        }

        public Builder trackedEntityAttributes(List<TrackedEntityAttribute> entityAttributes) {
            this.trackedEntityAttributes = entityAttributes;
            return this;
        }

        public Builder programRuleVariables(List<ProgramRuleVariable> programRuleVariables) {
            this.programRuleVariables = programRuleVariables;
            return this;
        }

        public Builder programRules(List<ProgramRule> programRules) {
            this.programRules = programRules;
            return this;
        }

        public Builder dataElements(List<DataElement> dataElements) {
            this.dataElements = dataElements;
            return this;
        }

        public Builder optionSets(List<OptionSet> optionSets) {
            this.optionSets = optionSets;
            return this;
        }

        public Builder constants(List<Constant> constants) {
            this.constants = constants;
            return this;
        }

        public RuleEngine build() {
            return new RuleEngine(
                    trackedEntityAttributes, programRuleVariables,
                    programRules, dataElements,
                    optionSets, constants);
        }
    }
}
