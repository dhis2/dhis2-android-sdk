package org.hisp.dhis.client.sdk.ui.rows.coordinaterowview;

interface OnCoordinatesCaptured {
    void onLatitudeChangeListener(double latitude);

    void onLongitudeChangeListener(double longitude);
}
