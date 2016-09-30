package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow;

import android.support.v4.app.FragmentManager;
import android.view.View;

import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogFragment;

import java.util.ArrayList;

class AutoCompleteDropDownButtonListener implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private String optionSetId;
    private AutoCompleteOnOptionSelectedListener listener;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setOptionSetId(String optionSetId) {
        this.optionSetId = optionSetId;
    }

    public void setListener(AutoCompleteOnOptionSelectedListener listener) {
        this.listener = listener;
    }

    public AutoCompleteOnOptionSelectedListener getListener() {
        return listener;
    }

    @Override
    public void onClick(View v) {
        OptionDialogFragment fragment =
                OptionDialogFragment.newInstance(optionSetId, listener);
        fragment.show(fragmentManager);
    }
}