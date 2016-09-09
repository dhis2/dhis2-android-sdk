package org.hisp.dhis.android.sdk.export;

/**
 * Created by thomaslindsjorn on 07/09/16.
 */
public class ExportResponse {

    private Throwable error;

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
