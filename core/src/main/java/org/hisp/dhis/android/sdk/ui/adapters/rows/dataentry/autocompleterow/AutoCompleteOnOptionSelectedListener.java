package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow;

import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogFragment;

public interface AutoCompleteOnOptionSelectedListener extends AutoCompleteDialogFragment.OnOptionSelectedListener {
    void setValue(BaseValue value);
}
