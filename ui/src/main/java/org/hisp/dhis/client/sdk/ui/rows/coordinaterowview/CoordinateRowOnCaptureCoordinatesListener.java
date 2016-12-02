package org.hisp.dhis.client.sdk.ui.rows.coordinaterowview;

import android.view.View;
import android.widget.EditText;

final class CoordinateRowOnCaptureCoordinatesListener implements View.OnClickListener {
    private final EditText latitudeEditText;
    private final EditText longitudeEditText;
    private final OnCoordinatesCaptured onCoordinatesCaptured;

    CoordinateRowOnCaptureCoordinatesListener(OnCoordinatesCaptured onCoordinatesCaptured, EditText
            latitudeEditText, EditText longitudeEditText) {
        this.onCoordinatesCaptured = onCoordinatesCaptured;
        this.latitudeEditText = latitudeEditText;
        this.longitudeEditText = longitudeEditText;
    }

    @Override
    public void onClick(View v) {
        if (onCoordinatesCaptured != null) {
            // TODO coordinates should be fed to row through
            // TODO FormEntityCoordinate model (not callback)

            // latitudeEditText.setText(String.valueOf(onCoordinatesCaptured.onLatitudeCaptured()));
            // longitudeEditText.setText(String.valueOf(onCoordinatesCaptured.onLongitudeCaptured()));
        } else {
            latitudeEditText.setText(String.valueOf(0.0));
            longitudeEditText.setText(String.valueOf(0.0));
        }
    }
}
