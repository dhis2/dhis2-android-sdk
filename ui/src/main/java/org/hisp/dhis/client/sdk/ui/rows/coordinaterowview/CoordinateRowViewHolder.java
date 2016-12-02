package org.hisp.dhis.client.sdk.ui.rows.coordinaterowview;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntityCoordinate;


final class CoordinateRowViewHolder extends RecyclerView.ViewHolder {
    final TextView textViewLabel;
    final TextInputLayout latitudeTextInputLayout;
    final TextInputLayout longitudeTextInputLayout;
    final EditText latitudeEditText;
    final EditText longitudeEditText;
    final ImageButton captureCoordinateButton;

    final CoordinateRowOnCaptureCoordinatesListener onCaptureCoordinatesListener;
    final CoordinateRowOnFocusChangeListener onFocusChangeListener;
    final CoordinateRowOnValueChangedListener onValueChangedListener;

    CoordinateRowViewHolder(View itemView, OnCoordinatesCaptured onCoordinatesCaptured) {
        super(itemView);

        textViewLabel = (TextView) itemView
                .findViewById(R.id.textview_row_label);
        latitudeTextInputLayout = (TextInputLayout) itemView
                .findViewById(R.id.coordinate_row_latitude_textinputlayout);
        longitudeTextInputLayout = (TextInputLayout) itemView
                .findViewById(R.id.coordinate_row_longitude_textinputlayout);
        latitudeEditText = (EditText) itemView
                .findViewById(R.id.coordinate_row_latitude_edittext);
        longitudeEditText = (EditText) itemView
                .findViewById(R.id.coordinate_row_longitude_edittext);
        captureCoordinateButton = (ImageButton) itemView
                .findViewById(R.id.capture_coordinate_button_picker_view);

        latitudeTextInputLayout.setHint(itemView.getContext()
                .getString(R.string.enter_latitude));
        longitudeTextInputLayout.setHint(itemView.getContext()
                .getString(R.string.enter_longitude));

        onCaptureCoordinatesListener = new CoordinateRowOnCaptureCoordinatesListener(
                onCoordinatesCaptured, latitudeEditText, longitudeEditText);
        onFocusChangeListener = new CoordinateRowOnFocusChangeListener(
                latitudeTextInputLayout, latitudeEditText);
        onValueChangedListener = new CoordinateRowOnValueChangedListener();

        latitudeEditText.setOnFocusChangeListener(onFocusChangeListener);
        latitudeEditText.addTextChangedListener(onValueChangedListener);
        longitudeEditText.setOnFocusChangeListener(onFocusChangeListener);
        longitudeEditText.addTextChangedListener(onValueChangedListener);

        captureCoordinateButton.setOnClickListener(onCaptureCoordinatesListener);

    }

    public void update(FormEntityCoordinate entity) {
        textViewLabel.setText(R.string.enter_coordinates);
        onValueChangedListener.setDataEntity(entity);

        latitudeEditText.setText(String.valueOf(entity.getLatitude()));
        longitudeEditText.setText(String.valueOf(entity.getLongitude()));

        // CharSequence hint = entity.getValue() == null
        // ? null : onFocusChangeListener.getHint();

        latitudeTextInputLayout.setHint(onFocusChangeListener.getHint());
        longitudeTextInputLayout.setHint(onFocusChangeListener.getHint());

        if(entity.isLocked()) {
            latitudeEditText.setClickable(false);
            latitudeEditText.setEnabled(false);
            longitudeEditText.setClickable(false);
            longitudeEditText.setEnabled(false);
            captureCoordinateButton.setClickable(false);
            captureCoordinateButton.setEnabled(false);
        }
        else {
            latitudeEditText.setClickable(true);
            latitudeEditText.setEnabled(true);
            longitudeEditText.setClickable(true);
            longitudeEditText.setEnabled(true);
            captureCoordinateButton.setClickable(true);
            captureCoordinateButton.setEnabled(true);
        }
    }
}
