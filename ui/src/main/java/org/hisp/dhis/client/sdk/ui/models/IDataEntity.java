package org.hisp.dhis.client.sdk.ui.models;

import java.util.List;

public interface IDataEntity<T> {
    DataEntity.Type getType();

    T getValue();

    CharSequence getLabel();

    OnValueChangeListener<T> getOnValueChangedListener();

    List<IValueValidator<CharSequence>> getDataEntityValueValidators();

    boolean updateValue(T value);

    boolean clearValue();

    boolean validateValue(T value);

}
