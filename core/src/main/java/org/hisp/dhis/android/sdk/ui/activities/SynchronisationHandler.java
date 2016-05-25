package org.hisp.dhis.android.sdk.ui.activities;


public class SynchronisationHandler {

    public interface OnCustomStateListener {
        void stateChanged();
    }

    private static SynchronisationHandler mInstance;
    private OnCustomStateListener mListener;
    private boolean mState;

    private SynchronisationHandler() {}

    public static SynchronisationHandler getInstance() {
        if(mInstance == null) {
            mInstance = new SynchronisationHandler();
        }
        return mInstance;
    }

    public void setListener(OnCustomStateListener listener) {
        mListener = listener;
    }

    public void changeState(boolean state) {
        if(mListener != null) {
            mState = state;
            notifyStateChange();
        }
    }

    public boolean getState() {
        return mState;
    }

    private void notifyStateChange() {
        mListener.stateChanged();
    }
}
