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
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;

public class DataEntity implements IDataEntity<CharSequence> {
    private final Type type;
    private final CharSequence label;
    private CharSequence value;

    private OnValueChangeListener<Pair<CharSequence, CharSequence>> onValueChangeListener;
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

    private DataEntity(String label, String value, Type type,
                       OnValueChangeListener<Pair<CharSequence, CharSequence>> onValueChangeListener) {
        isNull(label, "label must not be null");
        isNull(type, "type must not be null");

        this.label = label;
        this.value = value;
        this.type = type;
        this.onValueChangeListener = onValueChangeListener;
        this.dataEntityValueValidators = new ArrayList<>();
    }

    public static DataEntity create(@NonNull String label, @NonNull Type type) {
        return new DataEntity(label, "", type);
    }

    public static DataEntity create(@NonNull String label, @NonNull String value, @NonNull Type type) {
        return new DataEntity(label, value, type);
    }

    public static DataEntity create(@NonNull String label, @NonNull String value, @NonNull Type type,
                                    OnValueChangeListener<Pair<CharSequence, CharSequence>> onValueChangeListener) {
        return new DataEntity(label, value, type, onValueChangeListener);
    }

    @NonNull
    public CharSequence getLabel() {
        return label;
    }

    @Override
    public OnValueChangeListener<CharSequence> getOnValueChangedListener() {
        return null;
    }

    @Override
    public List<IValueValidator<CharSequence>> getDataEntityValueValidators() {
        return dataEntityValueValidators;
    }

    @Override
    @NonNull
    public CharSequence getValue() {
        return value;
    }

    @Override
    @NonNull
    public Type getType() {
        return type;
    }

    public void setOnValueChangedListener(OnValueChangeListener<Pair<CharSequence, CharSequence>> onValueChangedListener) {
        this.onValueChangeListener = onValueChangedListener;
    }

    @Override
    public boolean updateValue(@NonNull CharSequence value) {
        if (validateValue(value) && (!value.equals(this.value))) {
            this.value = value;
            this.onValueChangeListener.onValueChanged(new Pair<>(this.label,value));

            return true;
        }
        return false;
    }

    @Override
    public boolean clearValue() {
        return false;
    }

    @Override
    public boolean validateValue(CharSequence newValue) {
        for (IValueValidator<CharSequence> valueValidator : dataEntityValueValidators) {
            if (!valueValidator.validate(newValue)) {
                return false;
            }
        }

        return true;
    }

    public enum Type {
        TEXT, DATE, TRUE_ONLY, AUTO_COMPLETE, COORDINATES, BOOLEAN,INTEGER, NUMBER,
        LONG_TEXT, INTEGER_NEGATIVE, INTEGER_ZERO_OR_POSITIVE,
        INTEGER_POSITIVE, GENDER, INDICATOR, EVENT_DATE,
        ENROLLMENT_DATE, FILE, INCIDENT_DATE
    }
}
