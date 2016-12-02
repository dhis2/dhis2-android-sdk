package org.hisp.dhis.client.sdk.ui.rows.filterablerowview;

import android.widget.EditText;

import org.hisp.dhis.client.sdk.ui.adapters.OnPickerItemClickListener;
import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.models.Picker;
import org.hisp.dhis.client.sdk.ui.views.QuickSelectionContainer;

import static android.view.View.GONE;

final class FilterableRowOnItemClickListener implements OnPickerItemClickListener {
    private final EditText formEditText;
    private final QuickSelectionContainer quickSelectionContainer;
    private FormEntityFilter formEntityFilter;

    public FilterableRowOnItemClickListener(EditText formEditText, QuickSelectionContainer quickSelectionContainer) {
        this.formEditText = formEditText;
        this.quickSelectionContainer = quickSelectionContainer;
    }

    public void setFormEntityFilter(FormEntityFilter formEntityFilter) {
        this.formEntityFilter = formEntityFilter;
    }

    @Override
    public void onPickerItemClickListener(Picker selectedPicker) {
        if (selectedPicker.getParent() != null) {
            Picker parentPicker = selectedPicker.getParent();
            parentPicker.setSelectedChild(selectedPicker);

            if (formEntityFilter != null) {
                formEntityFilter.setPicker(parentPicker);
            }
        }

        // setting value to edittext
        if (formEditText.getVisibility() != GONE) {
            formEditText.setText(selectedPicker.getName());
        } else {
            quickSelectionContainer.refresh();
        }
    }
}
