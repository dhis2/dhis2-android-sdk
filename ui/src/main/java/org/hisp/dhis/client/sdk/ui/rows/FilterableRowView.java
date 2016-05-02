package org.hisp.dhis.client.sdk.ui.rows;


import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.fragments.FilterableDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.models.Picker;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class FilterableRowView implements RowView {
    private final FragmentManager fragmentManager;

    public FilterableRowView(FragmentManager fragmentManager) {
        this.fragmentManager = isNull(fragmentManager, "fragmentManager must not be null");
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

            onClickListener = new OnClickListener();

            filterEditText.setOnClickListener(onClickListener);
            clearButton.setOnClickListener(onClickListener);
            buttonDropDown.setOnClickListener(onClickListener);
        }

        public void update(FormEntityFilter formEntityFilter) {
            onClickListener.setFormEntityFilter(formEntityFilter);

            textViewLabel.setText(formEntityFilter.getLabel());

            // set the value to edittext
            String filterEditTextValue = EMPTY_STRING;

            Picker picker = formEntityFilter.getPicker();
            if (picker != null && picker.getSelectedChild() != null) {
                filterEditTextValue = picker.getName();
            }

            filterEditText.setText(filterEditTextValue);
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
                }
            }

            private void showFilterableDialogFragment() {
                if (formEntityFilter.getPicker() != null) {
                    FilterableDialogFragment dialogFragment = FilterableDialogFragment
                            .newInstance(formEntityFilter.getPicker());
                    dialogFragment.show(fragmentManager, FilterableDialogFragment.TAG);
                }
            }
        }
    }
}
