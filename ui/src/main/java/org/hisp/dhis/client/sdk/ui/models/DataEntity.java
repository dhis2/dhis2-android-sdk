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

package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;

public class DataEntity {
    private final CharSequence label;
    private final Type type;
    private CharSequence value;

    private OnValueChangeListener<CharSequence> onValueChangeListener;
    private List<IValueValidator<CharSequence>> dataEntityValueValidators;

//    for simplicity, let's ignore these properties from beginning

//    private final String description;
//    private final String error;
//    private final String warning;
//    private final boolean isMandatory;
//    private final boolean isEditable;

    private DataEntity(String label, String value, Type type) {
        isNull(label, "label must not be null");
        isNull(type, "type must not be null");

        this.label = label;
        this.value = value;
        this.type = type;

        this.dataEntityValueValidators = new ArrayList<>();
    }

    public static DataEntity create(@NonNull String label, @NonNull Type type) {
        return new DataEntity(label, "", type);
    }

    public static DataEntity create(@NonNull String label, @NonNull String value, @NonNull Type type) {
        return new DataEntity(label, value, type);
    }

    @NonNull
    public CharSequence getLabel() {
        return label;
    }

    @NonNull
    public CharSequence getValue() {
        return value;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    public void setOnValueChangedListener(OnValueChangeListener<CharSequence> onValueChangedListener) {
        this.onValueChangeListener = onValueChangedListener;
    }

    public boolean updateValue(@NonNull CharSequence value) {
        if (validateValue(value)) {
            this.value = value;
            return true;
        }
        return false;
    }

    public boolean clearValue() {
        return false;
    }

    public boolean validateValue(CharSequence newValue) {
        for (IValueValidator<CharSequence> valueValidator : dataEntityValueValidators) {
            if (!valueValidator.validate(newValue)) {
                return false;
            }
        }

        return true;
    }

    public enum Type {
        TEXT, DATE,
        LONG_TEXT, NUMBER, INTEGER, INTEGER_NEGATIVE, INTEGER_ZERO_OR_POSITIVE,
        INTEGER_POSITIVE, BOOLEAN, GENDER, TRUE_ONLY, AUTO_COMPLETE, INDICATOR, EVENT_DATE,
        ENROLLMENT_DATE, COORDINATES, FILE, INCIDENT_DATE
    }
}
