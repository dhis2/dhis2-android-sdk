package org.hisp.dhis.client.sdk.ui.rows;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.adapters.OnPickerItemClickListener;
import org.hisp.dhis.client.sdk.ui.fragments.FilterableDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.models.Picker;

import static android.view.View.GONE;
import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class FilterableRowView implements RowView {
    private final FragmentManager fragmentManager;

    public FilterableRowView(FragmentManager fragmentManager) {
        this.fragmentManager = isNull(fragmentManager, "fragmentManager must not be null");
    }

    public RecyclerView.ViewHolder onCreateViewHolder(View view) {
        return new FilterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new FilterViewHolder(inflater.inflate(
                R.layout.recyclerview_row_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, FormEntity formEntity) {
        FormEntityFilter formEntityFilter = (FormEntityFilter) formEntity;
        ((FilterViewHolder) viewHolder).update(formEntityFilter);
    }

    private class FilterViewHolder extends RecyclerView.ViewHolder {
        private static final String EMPTY_STRING = "";

        private final TextView textViewLabel;
        private final EditText filterEditText;
        private final ImageButton buttonDropDown;
        private final ImageButton clearButton;

        private final OnClickListener onClickListener;
        private final OnItemClickListener onItemClickListener;
        private final FlexboxLayout flexboxLayout;

        public FilterViewHolder(View itemView) {
            super(itemView);

            textViewLabel = (TextView) itemView
                    .findViewById(R.id.textview_row_label);
            filterEditText = (EditText) itemView
                    .findViewById(R.id.recyclerview_row_filter_edittext);
            buttonDropDown = (ImageButton) itemView
                    .findViewById(R.id.button_dropdown);
            clearButton = (ImageButton) itemView
                    .findViewById(R.id.button_clear);
            flexboxLayout = (FlexboxLayout) itemView
                    .findViewById(R.id.recyclerview_row_filter_flexbox);

            onClickListener = new OnClickListener();
            onItemClickListener = new OnItemClickListener(filterEditText);

            filterEditText.setOnClickListener(onClickListener);
            clearButton.setOnClickListener(onClickListener);
            buttonDropDown.setOnClickListener(onClickListener);
        }

        public void update(FormEntityFilter formEntityFilter) {
            onClickListener.setFormEntityFilter(formEntityFilter);
            onItemClickListener.setFormEntityFilter(formEntityFilter);

            textViewLabel.setText(formEntityFilter.getLabel());

            // set the value to edittext
            String filterEditTextValue = EMPTY_STRING;

            Picker picker = formEntityFilter.getPicker();
            if (picker != null && picker.getSelectedChild() != null) {
                filterEditTextValue = picker.getSelectedChild().getName();
            }

            if (picker != null && picker.getChildren().size() < 6) {
                filterEditText.setVisibility(GONE);
                flexboxLayout.setVisibility(View.VISIBLE);
                drawFlexBoxLayout(flexboxLayout, picker);
            } else {
                filterEditText.setVisibility(View.VISIBLE);
                flexboxLayout.setVisibility(GONE);
                flexboxLayout.removeAllViews();
            }

            filterEditText.setText(filterEditTextValue);


            // after configuration change, callback
            // in dialog fragment can be lost
            attachListenerToExistingFragment(picker);
        }

        private void attachListenerToExistingFragment(Picker picker) {
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

        private class OnClickListener implements View.OnClickListener {
            private FormEntityFilter formEntityFilter;

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

                    drawFlexBoxLayout(flexboxLayout, formEntityFilter.getPicker());
                }
            }

            private void showFilterableDialogFragment() {
                if (formEntityFilter.getPicker() != null) {
                    FilterableDialogFragment dialogFragment = FilterableDialogFragment
                            .newInstance(formEntityFilter.getPicker());
                    dialogFragment.setOnPickerItemClickListener(onItemClickListener);
                    dialogFragment.show(fragmentManager, FilterableDialogFragment.TAG);
                }
            }
        }

        private class OnItemClickListener implements OnPickerItemClickListener {
            private final EditText formEditText;
            private FormEntityFilter formEntityFilter;

            public OnItemClickListener(EditText formEditText) {
                this.formEditText = formEditText;
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
                    drawFlexBoxLayout(flexboxLayout, selectedPicker.getParent());
                }
            }
        }
    }

    private void drawFlexBoxLayout(final FlexboxLayout flexboxLayout, final Picker picker) {
        flexboxLayout.removeAllViews();
        final Context context = flexboxLayout.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        for (final Picker picker1 : picker.getChildren()) {
            final View filterQuickSelection = layoutInflater.inflate(R.layout.filter_quick_selection, flexboxLayout, false);
            ((TextView) filterQuickSelection.findViewById(R.id.name)).setText(picker1.getName());
            final ImageView checkbox = (ImageView) filterQuickSelection.findViewById(R.id.checkbox);

            final GradientDrawable background = (GradientDrawable) filterQuickSelection.getBackground();

            if (picker.getSelectedChild() != null && picker.getSelectedChild().equals(picker1)) {
                filterQuickSelection.setTag(R.id.is_selected, true);
                checkbox.setImageResource(R.drawable.ic_quick_selection_selected);
                background.setColor(ContextCompat.getColor(context, R.color.color_accent_default));
            } else {
                filterQuickSelection.setTag(R.id.is_selected, false);
                checkbox.setImageResource(R.drawable.ic_quick_selection_unselected);
                background.setColor(ContextCompat.getColor(context, R.color.color_gray_icon));
            }

            filterQuickSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((Boolean) filterQuickSelection.getTag(R.id.is_selected)) {
                        filterQuickSelection.setTag(R.id.is_selected, false);
                        checkbox.setImageResource(R.drawable.ic_quick_selection_unselected);
                        background.setColor(ContextCompat.getColor(context, R.color.color_gray_icon));
                    } else {
                        for (int i = 0; i < flexboxLayout.getChildCount(); i++) {
                            View unselectedView = flexboxLayout.getChildAt(i);
                            unselectedView.setTag(R.id.is_selected, false);
                            ((ImageView) unselectedView.findViewById(R.id.checkbox)).setImageResource(R.drawable.ic_quick_selection_unselected);
                            ((GradientDrawable) unselectedView.getBackground()).setColor(ContextCompat.getColor(context, R.color.color_gray_icon));
                        }
                        picker.setSelectedChild(picker1);
                        filterQuickSelection.setTag(R.id.is_selected, true);
                        checkbox.setImageResource(R.drawable.ic_quick_selection_selected);
                        background.setColor(ContextCompat.getColor(context, R.color.color_accent_default));
                    }
                }
            });
            flexboxLayout.addView(filterQuickSelection);
        }
    }
}
