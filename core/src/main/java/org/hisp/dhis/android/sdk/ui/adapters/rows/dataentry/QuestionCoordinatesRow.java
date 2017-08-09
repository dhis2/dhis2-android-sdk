package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.graphics.Color;
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
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.GpsController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow.TextRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

public final class QuestionCoordinatesRow extends TextRow {
    private static final String EMPTY_FIELD = "";
    public static final String UNDEFINED = "undefined";
    private final int MAX_INPUT_LENGTH = 9;
    // max input length = 9 for accepting 6 decimals in coordinates

    public static BaseValue saveCoordinates(EditText latitude, EditText longitude,
            BaseValue value) {
        value.setValue(getCoordinateValue(latitude, longitude));
        Dhis2Application.getEventBus().post(
                new RowValueChangedEvent(value, DataEntryRowTypes.TEXT.toString()));
        return value;
    }

    public static String getLatitudeFromValue(BaseValue baseValue) {
        if(baseValue == null || baseValue.getValue() == null)
            return "0";
        String value = baseValue.getValue();
        if (value.contains(",")) {
            String latitude = value.substring(value.indexOf(",") + 1, value.length()).replace("]", EMPTY_FIELD);
            if(!latitude.equals(UNDEFINED)){
                return latitude;
            }
        }
        return "";
    }

    public static String getLongitudeFromValue(BaseValue baseValue) {
        if(baseValue == null || baseValue.getValue() == null)
            return "0";
        String value = baseValue.getValue();
        if (value.contains(",")) {
            String longitude = value.substring(0, value.indexOf(",")).replace("[", EMPTY_FIELD);
            if(!longitude.equals(UNDEFINED)){
                return longitude;
            }
        }
        return "";
    }

    //the latitude and logitude is saved in the server with the format: "[longitude,latitude]"
    public static String getCoordinateValue(EditText latitude, EditText longitude) {
        String latitudeValue = latitude.getText().toString();
        if(latitudeValue!=null && !latitudeValue.equals("") && !latitudeValue.equals(UNDEFINED)){
            if(isInvalidLatitude(latitudeValue)){
                latitudeValue=UNDEFINED;
            }
        }
        String longitudeValue = longitude.getText().toString();

        if(longitudeValue!=null && !longitudeValue.equals("") && !longitudeValue.equals(UNDEFINED)){
            if(isInvalidLongitude(longitudeValue))
            {
                longitudeValue=UNDEFINED;
            }
        }

        return "[" + longitudeValue + "," + latitudeValue + "]";
    }

    public QuestionCoordinatesRow(String label, boolean mandatory, String warning, BaseValue baseValue,
            DataEntryRowTypes rowType) {
        mLabel = label;
        mMandatory = mandatory;
        mWarning = warning;
        mValue = baseValue;
        mRowType = rowType;

        //checkNeedsForDescriptionButton();

    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
            View convertView, ViewGroup container) {
        View view;
        CoordinateViewHolder holder;

        if (convertView != null && convertView.getTag() instanceof QuestionCoordinatesRow) {
            view = convertView;
            holder = (CoordinateViewHolder) view.getTag();
        } else {
            View root = inflater.inflate(R.layout.listview_row_event_coordinate_picker, container,
                    false);
            root.setBackgroundColor(Color.WHITE);
            View detailedInfoButton =  root.findViewById(R.id.detailed_info_button_layout);
            holder = new CoordinateViewHolder(root, detailedInfoButton);

            root.setTag(holder);
            view = root;
        }
        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));
        if(mValue!=null) {
            holder.updateViews(mLabel,mValue);
        }
        //input filters for coordinate row text fields
        InputFilter[] latitudeFilters = new InputFilter[2];
        InputFilter[] longitudeFilters = new InputFilter[2];
        InputFilter maxCharFilter = new InputFilter.LengthFilter(MAX_INPUT_LENGTH);
        InputFilter invalidLatitudeFilter = new InvalidLatitudeInputValueFilter(mValue);
        InputFilter invalidLongitudeFilter = new InvalidLongitudeInputValueFilter(mValue);
        latitudeFilters[0] = maxCharFilter;
        latitudeFilters[1] = invalidLatitudeFilter;
        longitudeFilters[0] = maxCharFilter;
        longitudeFilters[1] = invalidLongitudeFilter;

        holder.latitude.setFilters(latitudeFilters);
        holder.longitude.setFilters(longitudeFilters);
        holder.updateViews(mLabel, mValue);
        holder.latitude.setOnEditorActionListener(mOnEditorActionListener);
        holder.longitude.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.QUESTION_COORDINATES.ordinal();
    }

    private class CoordinateViewHolder {
        private final TextView labelTextView;
        private final EditText latitude;
        private final EditText longitude;
        private final ImageButton captureCoords;
        public final View detailedInfoButton;
        private final LatitudeWatcher latitudeWatcher;
        private final LongitudeWatcher longitudeWatcher;
        private final OnCaptureCoordsClickListener onButtonClickListener;

        public CoordinateViewHolder(View view, View detailedInfoButton) {
            final String latitudeMessage = view.getContext()
                    .getString(R.string.latitude_error_message);
            final String longitudeMessage = view.getContext()
                    .getString(R.string.longitude_error_message);

            /* views */
            labelTextView = (TextView) view.findViewById(R.id.text_label);
            latitude = (EditText) view.findViewById(R.id.latitude_edittext);
            longitude = (EditText) view.findViewById(R.id.longitude_edittext);
            captureCoords = (ImageButton) view.findViewById(R.id.capture_coordinates);
            this.detailedInfoButton = detailedInfoButton;
            /* text watchers and click listener */
            latitudeWatcher = new LatitudeWatcher(latitude, longitude, latitudeMessage,
                    longitudeMessage);
            longitudeWatcher = new LongitudeWatcher(latitude, longitude, latitudeMessage,
                    longitudeMessage);
            onButtonClickListener = new OnCaptureCoordsClickListener(latitude, longitude);

            latitude.addTextChangedListener(latitudeWatcher);
            longitude.addTextChangedListener(longitudeWatcher);
            captureCoords.setOnClickListener(onButtonClickListener);
        }

        public void updateViews(String labelText,BaseValue baseValue) {
            String lon = getLongitudeFromValue(baseValue);
            String lat = getLatitudeFromValue(baseValue);

            labelTextView.setText(labelText);
            latitudeWatcher.setBaseValue(baseValue);
            latitude.setText(lat);
            longitudeWatcher.setBaseValue(baseValue);
            longitude.setText(lon);
        }
    }
    private abstract static class CoordinateWatcher extends AbsTextWatcher {
        final EditText mEditTextLatitude;
        final  EditText mEditTextLongitude;
        final String mLatitudeMessage;
        final String mLongitudeMessage;
        BaseValue mBaseValue;

        public CoordinateWatcher(EditText mEditTextLatitude, EditText mEditTextLongitude,
                String mLatitudeMessage, String mLongitudeMessage) {
            this.mEditTextLatitude = mEditTextLatitude;
            this.mEditTextLongitude = mEditTextLongitude;
            this.mLongitudeMessage = mLongitudeMessage;
            this.mLatitudeMessage = mLatitudeMessage;
        }

        public void setBaseValue(BaseValue mDataValue) {
            this.mBaseValue = mDataValue;
        }

        @Override
        public abstract void afterTextChanged(Editable s);
    }

    private class LatitudeWatcher extends CoordinateWatcher {

        public LatitudeWatcher(EditText mLatitude, EditText mLongitude, String mLatitudeMessage,
                String mLongitudeMessage) {
            super(mLatitude, mLongitude, mLatitudeMessage, mLongitudeMessage);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 1) {
                if (s.toString().equals(getLatitudeFromValue(mBaseValue))) {
                    //ignore
                    return;
                }
                String newValue = s.toString();
                saveCoordinates(mEditTextLatitude, mEditTextLongitude, mBaseValue);
                setValidationError(newValue);
            }
        }

        private void setValidationError(String newValue) {
            if (isInvalidLatitude(newValue)) {
                mEditTextLatitude.setError(mLatitudeMessage);
            }
            mErrorStringId = null;
            if (mEditTextLatitude.getText().length() > 0) {
                if (isInvalidLatitude(mEditTextLatitude.getText().toString())) {
                    mErrorStringId = R.string.error_location_values;
                }
            }
            if (mEditTextLongitude.getText().length() > 0) {
                if (isInvalidLongitude(mEditTextLongitude.getText().toString())) {
                    mErrorStringId = R.string.error_location_values;
                }
            }
        }
    }


    private class LongitudeWatcher extends CoordinateWatcher {

        public LongitudeWatcher(EditText mLatitude, EditText mLongitude, String mLatitudeMessage,
                String mLongitudeMessage) {
            super(mLatitude, mLongitude, mLatitudeMessage, mLongitudeMessage);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 1) {
                if (s.toString().equals(getLatitudeFromValue(mBaseValue))) {
                    //ignore
                    return;
                }
                String newValue = s.toString();
                saveCoordinates(mEditTextLatitude, mEditTextLongitude, mBaseValue);
                setValidationError(newValue);
            }
        }

        private void setValidationError(String newValue) {
            if (isInvalidLongitude(newValue)) {
                mEditTextLongitude.setError(mLongitudeMessage);
            }
            mErrorStringId = null;
            if (mEditTextLatitude.getText().length() > 0) {
                if (isInvalidLatitude(mEditTextLatitude.getText().toString())) {
                    mErrorStringId = R.string.error_location_values;
                }
            }
            if (mEditTextLongitude.getText().length() > 0) {
                if (isInvalidLongitude(mEditTextLongitude.getText().toString())) {
                    mErrorStringId = R.string.error_location_values;
                }
            }
        }
    }

    private static boolean isInvalidLongitude(String newValue) {
        return Double.parseDouble(newValue) < -180 || Double.parseDouble(newValue) > 180;
    }

    private static boolean isInvalidLatitude(String newValue) {
        return Double.parseDouble(newValue) < -90 || Double.parseDouble(newValue) > 90;
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

    private abstract class InvalidInputValueFilter implements InputFilter {
        BaseValue baseValue;
        final String invalidValue = "0.0";
        // we don't want users to overwrite existing coordinates with 0.0 - aka no
        // network coords

        public InvalidInputValueFilter(BaseValue baseValue) {
            this.baseValue = baseValue;
        }

        @Override
        public abstract CharSequence filter(CharSequence charSequence, int i, int i2,
                Spanned spanned, int i3, int i4);
    }

    private class InvalidLatitudeInputValueFilter extends InvalidInputValueFilter {
        public InvalidLatitudeInputValueFilter(BaseValue latitude) {
            super(latitude);
        }

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned,
                int i3, int i4) {
            if (charSequence != null && charSequence.toString().trim().equals(invalidValue)) {
                if (baseValue == null || baseValue.getValue() == null || getLongitudeFromValue(baseValue)
                        == null) {
                    return invalidValue; //if getLat == null && location.getLat== 0.0, return 0.0
                } else {
                    return getLongitudeFromValue(baseValue);
                }
            }

            return null;
        }
    }

    private class InvalidLongitudeInputValueFilter extends InvalidInputValueFilter {
        public InvalidLongitudeInputValueFilter(BaseValue longitude) {
            super(longitude);
        }

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned,
                int i3, int i4) {
            if (charSequence != null && charSequence.toString().trim().equals(invalidValue)) {
                if (baseValue == null || baseValue.getValue() == null || getLatitudeFromValue(baseValue)
                        == null) {
                    return invalidValue;
                } else {
                    return getLatitudeFromValue(baseValue);
                }
            }
            return null;
        }
    }
}