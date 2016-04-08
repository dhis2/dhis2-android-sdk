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
import java.util.Map;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;

public class DataEntityCoordinate implements DataEntity<Map<String, Object>> {
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final DataEntityText.Type type;
    private final CharSequence label;
    private Map<String, Object> coordinateMap;

    private OnValueChangeListener<Map<String, Object>> onValueChangeListener;
    private List<ValueValidator<CharSequence>> dataEntityValueValidators;

    public DataEntityCoordinate(String label, Map<String, Object> coordinateMap, DataEntityText.Type
            type) {
        isNull(label, "label must not be null");
        isNull(type, "type must not be null");

        this.label = label;
        this.type = type;
        this.coordinateMap = coordinateMap;

    }

    private DataEntityCoordinate(String label, Map<String, Object> coordinateMap, DataEntityText.Type
            type,
                                 OnValueChangeListener<Map<String, Object>> onValueChangeListener) {
        isNull(label, "label must not be null");
        isNull(type, "type must not be null");

        this.label = label;
        this.coordinateMap = coordinateMap;
        this.type = type;
        this.onValueChangeListener = onValueChangeListener;
        this.dataEntityValueValidators = new ArrayList<>();
    }

    public static DataEntityCoordinate create(@NonNull String label, @NonNull DataEntityText.Type
            type) {

        return new DataEntityCoordinate(label, null, type);
    }

    public static DataEntityCoordinate create(@NonNull String label,
                                              @NonNull Map<String, Object> value,
                                              @NonNull DataEntityText.Type type) {
        return new DataEntityCoordinate(label, value, type);
    }

    public static DataEntityCoordinate create(
            @NonNull String label,
            @NonNull Map<String, Object> value,
            @NonNull DataEntityText.Type type,
            OnValueChangeListener<Map<String, Object>> onValueChangeListener) {
        return new DataEntityCoordinate(label, value, type, onValueChangeListener);
    }

    @Override
    public DataEntityText.Type getType() {
        return type;
    }

    @Override
    public Map<String, Object> getValue() {
        return coordinateMap;
    }


    @Override
    public CharSequence getLabel() {
        return label;
    }

    @Override
    public OnValueChangeListener<Map<String, Object>> getOnValueChangedListener() {
        return onValueChangeListener;
    }

    @Override
    public List<ValueValidator<CharSequence>> getDataEntityValueValidators() {
        return dataEntityValueValidators;
    }

    @Override
    public boolean updateValue(Map<String, Object> value) {
        if (validateValue(value)) {
            coordinateMap.put(LATITUDE, value.get(LATITUDE));
            coordinateMap.put(LONGITUDE, value.get(LONGITUDE));

            return true;
        }

        return false;
    }

    @Override
    public boolean clearValue() {
        if (this.coordinateMap != null) {
            this.coordinateMap.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean validateValue(Map<String, Object> value) {
        double latitude = (double) coordinateMap.get(LATITUDE);
        double longitude = (double) coordinateMap.get(LONGITUDE);


        if (latitude > -90 && latitude < 90
                && longitude > -90 && longitude < 90) {
            return true;
        }
        return false;
    }

}
