package org.hisp.dhis.android.sdk.ui.activities;


public class SynchronisationStateHandler {

    public interface OnSynchronisationStateListener {
        void stateChanged();
    }

    private static SynchronisationStateHandler mInstance;
    private OnSynchronisationStateListener mListener;
    private boolean mState;

    private SynchronisationStateHandler() {}

    public static SynchronisationStateHandler getInstance() {
        if(mInstance == null) {
            mInstance = new SynchronisationStateHandler();
        }
        return mInstance;
    }

    public void setListener(OnSynchronisationStateListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
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
