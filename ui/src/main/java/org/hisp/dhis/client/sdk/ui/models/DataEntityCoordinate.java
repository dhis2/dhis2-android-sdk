package org.hisp.dhis.client.sdk.ui.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;

public class DataEntityCoordinate implements IDataEntity<Map<String, Object>> {
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final DataEntity.Type type;
    private final CharSequence label;
    private Map<String, Object> coordinateMap;

    private OnValueChangeListener<Map<String, Object>> onValueChangeListener;
    private List<IValueValidator<CharSequence>> dataEntityValueValidators;

    public DataEntityCoordinate(String label, Map<String,Object> coordinateMap, DataEntity.Type type) {
        isNull(label, "label must not be null");
        isNull(type, "type must not be null");

        this.label = label;
        this.type = type;
        this.coordinateMap = coordinateMap;

    }

    private DataEntityCoordinate(String label, Map<String,Object> coordinateMap, DataEntity.Type type,
                       OnValueChangeListener<Map<String, Object>> onValueChangeListener) {
        isNull(label, "label must not be null");
        isNull(type, "type must not be null");

        this.label = label;
        this.coordinateMap = coordinateMap;
        this.type = type;
        this.onValueChangeListener = onValueChangeListener;
        this.dataEntityValueValidators = new ArrayList<>();
    }

    public static DataEntityCoordinate create(@NonNull String label, @NonNull DataEntity.Type type) {

        return new DataEntityCoordinate(label, null, type);
    }

    public static DataEntityCoordinate create(@NonNull String label,
                                              @NonNull Map<String,Object> value,
                                              @NonNull DataEntity.Type type) {
        return new DataEntityCoordinate(label, value, type);
    }

    public static DataEntityCoordinate create(
                                    @NonNull String label,
                                    @NonNull Map<String,Object> value,
                                    @NonNull DataEntity.Type type,
                                    OnValueChangeListener<Map<String, Object>> onValueChangeListener) {
        return new DataEntityCoordinate(label, value, type, onValueChangeListener);
    }

    @Override
    public DataEntity.Type getType() {
        return type;
    }

    @Override
    public Map<String,Object> getValue() {
        return coordinateMap;
    }


    @Override
    public CharSequence getLabel() {
        return label;
    }

    @Override
    public OnValueChangeListener<Map<String,Object>> getOnValueChangedListener() {
        return onValueChangeListener;
    }

    @Override
    public List<IValueValidator<CharSequence>> getDataEntityValueValidators() {
        return dataEntityValueValidators;
    }

    @Override
    public boolean updateValue(Map<String,Object> value) {
        if(validateValue(value)) {
            coordinateMap.put(LATITUDE, value.get(LATITUDE));
            coordinateMap.put(LONGITUDE, value.get(LONGITUDE));

            return true;
        }

        return false;
    }

    @Override
    public boolean clearValue() {
        if(this.coordinateMap != null) {
            this.coordinateMap.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean validateValue(Map<String, Object> value) {
        double latitude = (double) coordinateMap.get(LATITUDE);
        double longitude = (double) coordinateMap.get(LONGITUDE);


        if(latitude > -90 && latitude < 90
                && longitude > -90 && longitude < 90) {
            return true;
        }
        return false;
    }

}
