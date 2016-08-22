/*
 *  Copyright (c) 2016, University of Oslo
 *
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.client.sdk.android.dataelement.DataElementFilter;

import java.util.ArrayList;
import java.util.Collections;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class SessionPreferencesImpl implements SessionPreferences {
    public final static String PREFS_NAME = "preferences:selectorSession";

    public static final String SELECTED_PICKER_UID = "key:selectedPickerUid";

    private final SharedPreferences sharedPreferences;

    public SessionPreferencesImpl(Context context) {
        isNull(context, "context must not be null!");
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean clearSelectedPickers() {

        //TODO: fix this
        //return sharedPreferences.edit().clear().commit();
        return true;
    }

    @Override
    public String getSelectedPickerUid(int index) {
        return sharedPreferences.getString(SELECTED_PICKER_UID + index, null);
    }

    @Override
    public boolean setSelectedPickerUid(int index, String pickerUid) {
        return sharedPreferences.edit().putString(SELECTED_PICKER_UID + index, pickerUid).commit();
    }


    @Override
    public void setReportEntityDataModelFilters(String programUid,
                                                ArrayList<DataElementFilter> filters) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (DataElementFilter filter : filters) {
            String filterKey = String.format("%s:%s", programUid, filter.getDataElementId());
            editor.putBoolean(filterKey, filter.show());
        }
        editor.apply();
    }

    @Override
    public ArrayList<DataElementFilter> getReportEntityDataModelFilters(
            String programUid, ArrayList<DataElementFilter> filters) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (DataElementFilter dataElementFilter : filters) {
            String filterKey = String.format("%s:%s", programUid, dataElementFilter.getDataElementId());

            String dataElementLabel = dataElementFilter.getDataElementLabel();

            boolean defaultValue = dataElementFilter.show();

            if (!sharedPreferences.contains(filterKey)) {
                editor.putBoolean(filterKey, defaultValue).apply();
            } else {
                boolean storedValue = sharedPreferences.getBoolean(filterKey, defaultValue);
                if (storedValue != defaultValue) {
                    dataElementFilter.setShow(storedValue);
                }
            }
        }
        editor.apply();

        Collections.sort(filters);

        return filters;
    }

}
