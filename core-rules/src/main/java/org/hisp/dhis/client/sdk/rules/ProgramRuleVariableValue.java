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

import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class ProgramRuleVariableValue {
    private TrackedEntityDataValue value;
    private List<TrackedEntityDataValue> allValues;
    private List<String> allValuesString;
    private boolean hasValue;

    public ProgramRuleVariableValue(TrackedEntityDataValue value, List<TrackedEntityDataValue> allValues, boolean hasValue) {
        this.value = value;
        this.allValues = allValues;
        initializeAllValuesString();
        this.hasValue = hasValue;
    }

    private void initializeAllValuesString(){
        this.allValuesString = new ArrayList<>();
        if (this.allValues != null) {
            for (TrackedEntityDataValue otherValue : this.allValues) {
                this.allValuesString.add(otherValue.getValue());
            }
        }
    }

    public void setValueString(String value) {
        this.value = new TrackedEntityDataValue();
        this.value.setValue(value);

        this.allValues = new ArrayList<>();
        this.allValues.add(this.value);
        initializeAllValuesString();
    }

    public String getValueString() {
        return this.value.getValue();
    }

    public List<String> getAllValuesString() {
        return this.allValuesString;
    }

    public boolean hasValue() { return this.hasValue; }

    @Override
    public String toString() {
        return this.getValueString();
    }
}