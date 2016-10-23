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

package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.GpsController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

public final class DataValueCoordinatesRow extends Row {
    private static final String EMPTY_FIELD = "";
    private final int MAX_INPUT_LENGTH = 9; // max input length = 9 for accepting 6 decimals in coordinates

    public DataValueCoordinatesRow(String label, boolean mandatory, String warning, BaseValue baseValue, DataEntryRowTypes rowType) {
        mLabel = label;
        mMandatory = mandatory;
        mWarning = warning;
        mValue = baseValue;
        mRowType = rowType;

        if (!DataEntryRowTypes.DATAVALUECOORDINATES.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        checkNeedsForDescriptionButton();
    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        View view;
        CoordinateViewHolder holder;

        if (convertView != null && convertView.getTag() instanceof CoordinateViewHolder) {
            view = convertView;
            holder = (CoordinateViewHolder) view.getTag();
        } else {
            View root = inflater.inflate(
                    R.layout.listview_row_coordinate_picker_with_one_edittext, container, false);
//            detailedInfoButton =  root.findViewById(R.id.detailed_info_button_layout);
            holder = new CoordinateViewHolder(root);

            root.setTag(holder);
            view = root;
        }
//        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));

        //input filters for coordinate row text fields
//        InputFilter[] latitudeFilters = new InputFilter[2];
//        InputFilter[] longitudeFilters = new InputFilter[2];
//        InputFilter maxCharFilter = new InputFilter.LengthFilter(MAX_INPUT_LENGTH);
//        InputFilter invalidLatitudeFilter = new InvalidLatitudeInputValueFilter(mEvent);
//        InputFilter invalidLongitudeFilter = new InvalidLongitudeInputValueFilter(mEvent);
//        latitudeFilters[0] = maxCharFilter;
//        latitudeFilters[1] = invalidLatitudeFilter;
//        longitudeFilters[0] = maxCharFilter;
//        longitudeFilters[1] = invalidLongitudeFilter;
//
//        holder.latitude.setFilters(latitudeFilters);
//        holder.longitude.setFilters(longitudeFilters);
        holder.updateViews(mValue);

        // Coordinates cannot be manually entered
//        holder.latitude.setEnabled(false);
//        holder.longitude.setEnabled(false);
        holder.coordinates.setEnabled(false);

        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.COORDINATES.ordinal();
    }

    private static class CoordinateViewHolder {
        private final EditText coordinates;
        private final ImageButton captureCoords;
        private final CoordinateWatcher coordinateWatcher;
        private final OnCaptureCoordsClickListener onButtonClickListener;

        public CoordinateViewHolder(View view) {
            final String latitudeMessage = view.getContext()
                    .getString(R.string.latitude_error_message);
            final String longitudeMessage = view.getContext()
                    .getString(R.string.longitude_error_message);

            /* views */
            coordinates = (EditText) view.findViewById(R.id.coordinates_edittext);
            captureCoords = (ImageButton) view.findViewById(R.id.capture_coordinates);
//            this.detailedInfoButton = detailedInfoButton;

            /* text watchers and click listener */
            String coordinateMessage = latitudeMessage + ". " + longitudeMessage;
            coordinateWatcher = new CoordinateWatcher(coordinates, coordinateMessage);
            onButtonClickListener = new OnCaptureCoordsClickListener(coordinates);

            coordinates.addTextChangedListener(coordinateWatcher);
            captureCoords.setOnClickListener(onButtonClickListener);
        }

        public void updateViews(BaseValue baseValue) {
            coordinateWatcher.setBaseValue(baseValue);
            String coordinatesString = baseValue.getValue() == null ? EMPTY_FIELD : baseValue.getValue();

            coordinates.setText(coordinatesString);
        }
    }
    private static class CoordinateWatcher extends AbsTextWatcher {
        final EditText mEditText;
        final String mCoordinateMessage;
        //Event mEvent;
        BaseValue mBaseValue;
        //double value;
        String value;

        public CoordinateWatcher(EditText mEditText, String mCoordinateMessage)
        {
            this.mEditText = mEditText;
            this.mCoordinateMessage = mCoordinateMessage;
        }

        public void setBaseValue(BaseValue baseValue) {
            this.mBaseValue = baseValue;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mBaseValue.getValue() != null) {
                value = mBaseValue.getValue();
            }
            if (s.length() > 1) {
                String newValue = s.toString();

                if(!newValue.equals(value)) {
                    mBaseValue.setValue(newValue);
                    Dhis2Application.getEventBus().post(new RowValueChangedEvent(mBaseValue, DataEntryRowTypes.DATAVALUECOORDINATES.toString()));
                }
            }
        }
    }

    private static class OnCaptureCoordsClickListener implements View.OnClickListener {
        private final EditText mCoordinates;

        public OnCaptureCoordsClickListener(EditText coordinates) {
            mCoordinates = coordinates;
        }

        @Override
        public void onClick(View v) {
            Location location = GpsController.getLocation();
            mCoordinates.setText(String.valueOf(location.getLatitude() + ", " + String.valueOf(location.getLongitude())));
        }
    }

    private abstract class InvalidInputValueFilter implements InputFilter{
        final BaseValue baseValue;
        final String invalidValue = "0.0"; // we don't want users to overwrite existing coordinates with 0.0 - aka no network coords

        protected InvalidInputValueFilter(BaseValue baseValue) {
            this.baseValue = baseValue;
        }

        @Override
        public abstract CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4);
    }

    private class InvalidLatitudeInputValueFilter extends InvalidInputValueFilter
    {
        public InvalidLatitudeInputValueFilter(BaseValue baseValue) {
            super(baseValue);
        }

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            if(charSequence != null && charSequence.toString().trim().equals(invalidValue))
            {
                if(baseValue.getValue() == null)
                    return invalidValue; //if getLat == null && location.getLat== 0.0, return 0.0
                else
                    return baseValue.getValue();
            }

            return null;
        }
    }
    private class InvalidLongitudeInputValueFilter extends InvalidInputValueFilter
    {
        public InvalidLongitudeInputValueFilter(BaseValue baseValue) {
            super(baseValue);
        }

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            if(charSequence != null && charSequence.toString().trim().equals(invalidValue))
            {
                if(baseValue.getValue() == null)
                    return invalidValue; //if getLong == null && location.getLong == 0.0, return 0.0
                else
                    return baseValue.getValue();
            }
            return null;
        }
    }
}