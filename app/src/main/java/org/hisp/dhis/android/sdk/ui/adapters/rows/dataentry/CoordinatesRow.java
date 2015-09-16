/*
 *  Copyright (c) 2015, University of Oslo
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.controllers.GpsController;

public final class CoordinatesRow extends Row {
    private static final String EMPTY_FIELD = "";
    private final Event mEvent;

    public CoordinatesRow(Event event) {
        mEvent = event;
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
                    R.layout.listview_row_coordinate_picker, container, false);
            detailedInfoButton =  root.findViewById(R.id.detailed_info_button_layout);
            holder = new CoordinateViewHolder(root, detailedInfoButton);

            root.setTag(holder);
            view = root;
        }
        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));

        holder.updateViews(mEvent);

        if(!isEditable())
        {
            holder.latitude.setEnabled(false);
            holder.longitude.setEnabled(false);
            holder.captureCoords.setEnabled(false);
        }
        else
        {
            holder.latitude.setEnabled(true);
            holder.longitude.setEnabled(true);
            holder.captureCoords.setEnabled(true);
        }

        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.COORDINATES.ordinal();
    }



    private static class CoordinateViewHolder {
        private final EditText latitude;
        private final EditText longitude;
        private final ImageButton captureCoords;
        private final View detailedInfoButton;
        private final LatitudeWatcher latitudeWatcher;
        private final LongitudeWatcher longitudeWatcher;
        private final OnCaptureCoordsClickListener onButtonClickListener;

        public CoordinateViewHolder(View view, View detailedInfoButton) {
            final String latitudeMessage = view.getContext()
                    .getString(R.string.latitude_error_message);
            final String longitudeMessage = view.getContext()
                    .getString(R.string.longitude_error_message);

            /* views */
            latitude = (EditText) view.findViewById(R.id.latitude_edittext);
            longitude = (EditText) view.findViewById(R.id.longitude_edittext);
            captureCoords = (ImageButton) view.findViewById(R.id.capture_coordinates);
            this.detailedInfoButton = detailedInfoButton;

            /* text watchers and click listener */
            latitudeWatcher = new LatitudeWatcher(latitude, latitudeMessage);
            longitudeWatcher = new LongitudeWatcher(longitude, longitudeMessage);
            onButtonClickListener = new OnCaptureCoordsClickListener(latitude, longitude);

            latitude.addTextChangedListener(latitudeWatcher);
            longitude.addTextChangedListener(longitudeWatcher);
            captureCoords.setOnClickListener(onButtonClickListener);
        }

        public void updateViews(Event event) {
            latitudeWatcher.setEvent(event);
            longitudeWatcher.setEvent(event);

            String lat = event.getLatitude() == null ? EMPTY_FIELD
                    : String.valueOf(event.getLatitude());
            String lon = event.getLongitude() == null ? EMPTY_FIELD
                    : String.valueOf(event.getLongitude());

            latitude.setText(lat);
            longitude.setText(lon);
        }
    }

    private static class LatitudeWatcher extends AbsTextWatcher {
        private final EditText mLatitude;
        private final String mLatitudeMessage;
        private Event mEvent;
        private double value;

        public LatitudeWatcher(EditText latitude, String latitudeMessage) {
            mLatitude = latitude;
            mLatitudeMessage = latitudeMessage;
        }

        public void setEvent(Event event) {
            mEvent = event;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mEvent.getLatitude() != null)
                value = mEvent.getLatitude();

            if (s.length() > 1) {
                double newValue = Double.parseDouble(s.toString());
                if (newValue < -90 || newValue > 90) {
                    mLatitude.setError(mLatitudeMessage);
                }
                if(newValue != value)
                {
                    mEvent.setLatitude(Double.valueOf(newValue));
                    DataValue dataValue = new DataValue();
                    dataValue.setValue(""+ newValue);
                    Dhis2Application.getEventBus().post(new RowValueChangedEvent(dataValue));
                }
            }
        }
    }

    private static class LongitudeWatcher extends AbsTextWatcher {
        private final EditText mLongitude;
        private final String mLongitudeMessage;
        private Event mEvent;
        private double value;

        public LongitudeWatcher(EditText latitude, String latitudeMessage) {
            mLongitude = latitude;
            mLongitudeMessage = latitudeMessage;
        }

        public void setEvent(Event event) {
            mEvent = event;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mEvent.getLongitude() != null)
                value = mEvent.getLongitude();

            if (s.length() > 1) {
                double newValue = Double.parseDouble(s.toString());
                if (newValue < -180 || newValue > 180) {
                    mLongitude.setError(mLongitudeMessage);
                }
                if(newValue != value)
                {
                    mEvent.setLongitude(Double.valueOf(newValue));
                    DataValue dataValue = new DataValue();
                    dataValue.setValue(""+ newValue);
                    Dhis2Application.getEventBus().post(new RowValueChangedEvent(dataValue));
                }
            }
        }
    }

    private static class OnCaptureCoordsClickListener implements View.OnClickListener {
        private final EditText mLatitude;
        private final EditText mLongitude;

        public OnCaptureCoordsClickListener(EditText latitude, EditText longitude) {
            mLatitude = latitude;
            mLongitude = longitude;
        }

        @Override
        public void onClick(View v) {
            Location location = GpsController.getLocation();
            mLatitude.setText(String.valueOf(location.getLatitude()));
            mLongitude.setText(String.valueOf(location.getLongitude()));
        }
    }
}