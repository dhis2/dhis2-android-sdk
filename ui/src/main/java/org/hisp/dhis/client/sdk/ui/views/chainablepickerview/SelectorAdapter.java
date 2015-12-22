package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.R;

import java.util.List;

/**
 * Adapter for RecyclerView using {@link Picker} as child elements
 */
public class SelectorAdapter extends RecyclerView.Adapter<SelectorViewHolder> {

    private List<Picker> pickers;

    public void setPickers(List<Picker> pickers) {
        this.pickers = pickers;
    }

    @Override
    public SelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickerview,
                parent, false);
        return new SelectorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectorViewHolder holder, int position) {
        Picker picker = pickers.get(position);
        holder.picker = picker;
        holder.adapter.swapData(picker.getPickableItems());
        holder.adapter.notifyDataSetChanged();
        holder.autoCompleteTextView.setOnItemClickListener(picker.getListener());
        holder.autoCompleteTextView.setHint(picker.getHint());

        TextWatcher previousTextWatcher = holder.textWatcher;
        if(previousTextWatcher != null) {
            holder.autoCompleteTextView.removeTextChangedListener(previousTextWatcher);
        }

        IPickable pickedItem = picker.getPickedItem();
        if(pickedItem != null) {
            holder.autoCompleteTextView.setText(pickedItem.toString());
        } else {
            holder.autoCompleteTextView.setText("");
        }
        Picker.AutoCompleteDismissListener onDismissListener = picker.getOnDismissListener();
        onDismissListener.setAutoCompleteTextView(holder.autoCompleteTextView);
        holder.autoCompleteTextView.setOnDismissListener(onDismissListener);
        holder.autoCompleteTextView.addTextChangedListener(picker);
        holder.textWatcher = picker;
    }

    @Override
    public int getItemCount() {
        return pickers.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recycle();
    }

    public void recycle() {
        for(Picker picker : pickers) {
            picker.recycle();
        }
        pickers.clear();
    }
}
