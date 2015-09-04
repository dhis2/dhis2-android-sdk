package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

/**
 * This class enables thread safe scheduling and re-scheduling of saving of data in a data entry
 * fragment. The class has been implemented as a mechanism to simply handle problematic cases
 * where the user wants to save data while data is already being saved on the same element.
 */
public class SaveThread extends AsyncHelperThread {
    private DataEntryFragment dataEntryFragment;

    public void init(DataEntryFragment dataEntryFragment) {
        setDataEntryFragment(dataEntryFragment);
    }

    public void setDataEntryFragment(DataEntryFragment dataEntryFragment) {
        this.dataEntryFragment = dataEntryFragment;
    }

    protected void work() {
        if(this.dataEntryFragment==null) {
            return;
        }
        this.dataEntryFragment.save();
    }

    @Override
    public void kill() {
        super.kill();
        dataEntryFragment = null;
    }
}
