package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.IPickable;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;

public class DataEntityAutoComplete implements IDataEntity<CharSequence>{
    private final DataEntity.Type type;
    private final CharSequence label;
    private CharSequence value;
    private List<IPickable> options;

    private OnValueChangeListener<Pair<CharSequence, CharSequence>> onValueChangeListener;
    private List<IValueValidator<CharSequence>> dataEntityValueValidators;

//    for simplicity, let's ignore these properties from beginning

//    private final String description;
//    private final String error;
//    private final String warning;
//    private final boolean isMandatory;
//    private final boolean isEditable;

    private DataEntityAutoComplete(String label, String value, DataEntity.Type type,
                                   List<IPickable> options) {
        isNull(label, "label must not be null");
        isNull(type, "type must not be null");

        this.label = label;
        this.value = value;
        this.type = type;
        this.options = options;

        this.dataEntityValueValidators = new ArrayList<>();
    }

    private DataEntityAutoComplete(String label, String value, DataEntity.Type type,
                                   List<IPickable> options,
                                   OnValueChangeListener<Pair<CharSequence, CharSequence>> onValueChangeListener) {
        isNull(label, "label must not be null");
        isNull(type, "type must not be null");

        this.label = label;
        this.value = value;
        this.type = type;
        this.options = options;
        this.onValueChangeListener = onValueChangeListener;
        this.dataEntityValueValidators = new ArrayList<>();
    }

    public static DataEntityAutoComplete create(@NonNull String label, @NonNull DataEntity.Type type,
                                                List<IPickable> options) {
        return new DataEntityAutoComplete(label, "", type, options);
    }

    public static DataEntityAutoComplete create(@NonNull String label, @NonNull String value, @NonNull DataEntity.Type type,
                                                List<IPickable> options) {
        return new DataEntityAutoComplete(label, value, type, options);
    }

    public static DataEntityAutoComplete create(@NonNull String label, @NonNull String value, @NonNull DataEntity.Type type,
                                                List<IPickable> options,
                                                OnValueChangeListener<Pair<CharSequence, CharSequence>> onValueChangeListener) {
        return new DataEntityAutoComplete(label, value, type, options, onValueChangeListener);
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
    public DataEntity.Type getType() {
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

    public List<IPickable> getOptions() {
        return options;
    }
}
