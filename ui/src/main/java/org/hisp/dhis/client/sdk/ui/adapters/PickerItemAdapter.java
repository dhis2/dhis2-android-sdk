package org.hisp.dhis.client.sdk.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.Picker;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class PickerItemAdapter extends RecyclerView.Adapter {
    // view inflater
    private final LayoutInflater inflater;

    // Adapter data
    private Picker currentPicker;
    private final List<Picker> originalPickers;
    private final List<Picker> filteredPickers;
    private final Context context;

    private OnPickerItemClickListener onPickerItemClickListener;

    public PickerItemAdapter(Context context, Picker picker) {
        this.context = isNull(context, "context must not be null!");
        this.inflater = LayoutInflater.from(context);
        this.currentPicker = isNull(picker, "Picker must not be null");
        this.originalPickers = currentPicker.getChildren();
        this.filteredPickers = new ArrayList<>(currentPicker.getChildren());
    }

    public PickerItemAdapter(Context context) {
        this.context = isNull(context, "context must not be null!");
        this.inflater = LayoutInflater.from(context);
        this.originalPickers = new ArrayList<>();
        this.filteredPickers = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PickerItemViewHolder(inflater.inflate(
                R.layout.recyclerview_row_picker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PickerItemViewHolder pickerViewHolder = (PickerItemViewHolder) holder;
        Picker picker = filteredPickers.get(position);

        if (this.currentPicker != null && this.currentPicker.getSelectedChild() != null &&
                picker.equals(this.currentPicker.getSelectedChild())) {
            pickerViewHolder.updateViewHolder(picker, true);
        } else {
            pickerViewHolder.updateViewHolder(picker, false);
        }
    }

    @Override
    public int getItemCount() {
        return filteredPickers.size();
    }

    public void setOnPickerItemClickListener(OnPickerItemClickListener onPickerItemClickListener) {
        this.onPickerItemClickListener = onPickerItemClickListener;
    }

    public void swapData(Picker picker) {
        currentPicker = picker;
        originalPickers.clear();
        filteredPickers.clear();

        if (picker != null) {
            originalPickers.addAll(picker.getChildren());
            filteredPickers.addAll(picker.getChildren());
        }

        notifyDataSetChanged();
    }

    public Picker getData() {
        return currentPicker;
    }

    public void filter(@NonNull String query) {
        filteredPickers.clear();

        query = query.toLowerCase();
        for (Picker picker : originalPickers) {
            if (picker.getName() != null && picker.getName().toLowerCase().contains(query)) {
                filteredPickers.add(picker);
            }
        }

        notifyDataSetChanged();
    }

    private class PickerItemViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewLabel;
        final OnClickListener onTextViewLabelClickListener;

        public PickerItemViewHolder(View itemView) {
            super(itemView);

            this.textViewLabel = (TextView) itemView;
            this.onTextViewLabelClickListener = new OnClickListener();

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            // for selected state
                            new int[]{android.R.attr.state_selected},

                            // default color state
                            new int[]{}
                    },
                    new int[]{
                            ContextCompat.getColor(
                                    context, R.color.color_primary_default),
                            textViewLabel.getCurrentTextColor()
                    });

            this.textViewLabel.setTextColor(colorStateList);
            this.textViewLabel.setOnClickListener(onTextViewLabelClickListener);
        }

        public void updateViewHolder(Picker picker, boolean isSelected) {
            textViewLabel.setSelected(isSelected);
            textViewLabel.setText(picker.getName());
            onTextViewLabelClickListener.setPicker(picker);
        }

        private class OnClickListener implements View.OnClickListener {
            private Picker picker;

            public void setPicker(Picker picker) {
                this.picker = picker;
            }

            @Override
            public void onClick(View view) {
                if (onPickerItemClickListener != null) {
                    onPickerItemClickListener.onPickerItemClickListener(picker);
                }
            }
        }
    }
}