package org.hisp.dhis.client.sdk.ui.rows.filterablerowview;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.fragments.FilterableDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.models.Picker;
import org.hisp.dhis.client.sdk.ui.utils.FormUtils;
import org.hisp.dhis.client.sdk.ui.views.QuickSelectionContainer;

import static android.view.View.GONE;

final class FilterableRowViewHolder extends RecyclerView.ViewHolder {
    static final String EMPTY_STRING = "";

    private final TextView textViewLabel;
    private final EditText filterEditText;
    private final ImageButton buttonDropDown;
    private final ImageButton clearButton;
    private final LinearLayout rootLinearLayout;

    private final FilterableRowOnClickListener onClickListener;
    private final FilterableRowOnItemClickListener onItemClickListener;
    private final QuickSelectionContainer quickSelectionContainer;

    FilterableRowViewHolder(View itemView, FragmentManager fragmentManager) {
        super(itemView);

        textViewLabel = (TextView) itemView
                .findViewById(R.id.textview_row_label);
        filterEditText = (EditText) itemView
                .findViewById(R.id.recyclerview_row_filter_edittext);
        buttonDropDown = (ImageButton) itemView
                .findViewById(R.id.button_dropdown);
        clearButton = (ImageButton) itemView
                .findViewById(R.id.button_clear);
        quickSelectionContainer = (QuickSelectionContainer) itemView
                .findViewById(R.id.recyclerview_row_filter_quick_selection_container);

        onItemClickListener = new FilterableRowOnItemClickListener(filterEditText, quickSelectionContainer);
        onClickListener = new FilterableRowOnClickListener(filterEditText, quickSelectionContainer, fragmentManager, onItemClickListener);

        rootLinearLayout = (LinearLayout) itemView.findViewById(R.id.root_linear_layout);
        rootLinearLayout.setOnClickListener(onClickListener);

        filterEditText.setOnClickListener(onClickListener);
        clearButton.setOnClickListener(onClickListener);
        buttonDropDown.setOnClickListener(onClickListener);
    }

    public void update(FormEntityFilter formEntityFilter, FragmentManager fragmentManager) {
        onClickListener.setFormEntityFilter(formEntityFilter);
        onItemClickListener.setFormEntityFilter(formEntityFilter);

        textViewLabel.setText(FormUtils.getFormEntityLabel(formEntityFilter));

        // set the value to edittext
        String filterEditTextValue = EMPTY_STRING;

        Picker picker = formEntityFilter.getPicker();
        if (picker != null && picker.getSelectedChild() != null) {
            filterEditTextValue = picker.getSelectedChild().getName();
        }

        if (picker != null && picker.getChildren().size() < 6) {
            filterEditText.setVisibility(GONE);
            quickSelectionContainer.setFormEntityFilter(formEntityFilter);
        } else {
            filterEditText.setVisibility(View.VISIBLE);
            quickSelectionContainer.hide();
        }

        filterEditText.setText(filterEditTextValue);

        if (formEntityFilter.isLocked()) {
            filterEditText.setEnabled(false);
            clearButton.setEnabled(false);
            clearButton.setClickable(false);
            buttonDropDown.setEnabled(false);
            buttonDropDown.setClickable(false);
            rootLinearLayout.setEnabled(false);
        } else {
            filterEditText.setEnabled(true);
            clearButton.setEnabled(true);
            clearButton.setClickable(true);
            buttonDropDown.setEnabled(true);
            buttonDropDown.setClickable(true);
            rootLinearLayout.setEnabled(true);
        }

        // after configuration change, callback
        // in dialog fragment can be lost
        attachListenerToExistingFragment(picker, fragmentManager);
    }

    private void attachListenerToExistingFragment(Picker picker, FragmentManager fragmentManager) {
        FilterableDialogFragment fragment = (FilterableDialogFragment)
                fragmentManager.findFragmentByTag(FilterableDialogFragment.TAG);

        // if we don't have fragment attached to activity,
        // we don't want to do anything else
        if (fragment == null) {
            return;
        }

        // get the arguments bundle out from fragment
        Bundle arguments = fragment.getArguments();

        // if we don't have picker set to fragment, we can't distinguish
        // the fragment which we need to update
        if (arguments == null || !arguments
                .containsKey(FilterableDialogFragment.ARGS_PICKER)) {
            return;
        }

        Picker existingPicker = (Picker) arguments
                .getSerializable(FilterableDialogFragment.ARGS_PICKER);
        if (picker.equals(existingPicker)) {
            fragment.setOnPickerItemClickListener(onItemClickListener);
        }
    }
}
