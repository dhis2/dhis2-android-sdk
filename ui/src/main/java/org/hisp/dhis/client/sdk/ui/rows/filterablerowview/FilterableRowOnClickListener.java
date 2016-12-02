package org.hisp.dhis.client.sdk.ui.rows.filterablerowview;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.fragments.FilterableDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.views.QuickSelectionContainer;

import static org.hisp.dhis.client.sdk.ui.rows.filterablerowview.FilterableRowViewHolder.EMPTY_STRING;

final class FilterableRowOnClickListener implements View.OnClickListener {
    private FormEntityFilter formEntityFilter;
    private final EditText filterEditText;
    private final QuickSelectionContainer quickSelectionContainer;
    private final FragmentManager fragmentManager;
    private final FilterableRowOnItemClickListener onItemClickListener;

    FilterableRowOnClickListener(EditText filterEditText, QuickSelectionContainer quickSelectionContainer, FragmentManager fragmentManager, FilterableRowOnItemClickListener onItemClickListener) {
        this.filterEditText = filterEditText;
        this.quickSelectionContainer = quickSelectionContainer;
        this.fragmentManager = fragmentManager;
        this.onItemClickListener = onItemClickListener;
    }

    public void setFormEntityFilter(FormEntityFilter formEntityFilter) {
        this.formEntityFilter = formEntityFilter;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.recyclerview_row_filter_edittext) {
            showFilterableDialogFragment();
        } else if (view.getId() == R.id.button_dropdown) {
            showFilterableDialogFragment();
        } else if (view.getId() == R.id.button_clear) {
            filterEditText.setText(EMPTY_STRING);

            // we also have to clear out selected child in picker
            if (formEntityFilter != null && formEntityFilter.getPicker() != null) {
                formEntityFilter.getPicker().setSelectedChild(null);

                // using this hack in order to trigger listener in formEntityFilter
                formEntityFilter.setPicker(formEntityFilter.getPicker());
            }

            quickSelectionContainer.refresh();
        } else {
            showFilterableDialogFragment();
        }
    }

    private void showFilterableDialogFragment() {
        if (formEntityFilter != null && formEntityFilter.getPicker() != null) {
            FilterableDialogFragment dialogFragment = FilterableDialogFragment
                    .newInstance(formEntityFilter.getPicker());
            dialogFragment.setOnPickerItemClickListener(onItemClickListener);
            dialogFragment.show(fragmentManager, FilterableDialogFragment.TAG);
        }
    }
}
