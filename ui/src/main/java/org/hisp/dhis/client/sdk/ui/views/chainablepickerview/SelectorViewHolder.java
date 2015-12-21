package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import org.hisp.dhis.client.sdk.ui.R;

/**
 * ViewHolder to be used with {@link SelectorAdapter}
 */
public class SelectorViewHolder extends RecyclerView.ViewHolder {

    AutoCompleteTextView autoCompleteTextView;
    ImageView clearButton;
    SelectorListAdapter adapter;
    TextWatcher textWatcher;
    Picker picker;

    public SelectorViewHolder(View itemView) {
        super(itemView);
        autoCompleteTextView = (AutoCompleteTextView) itemView.findViewById(R.id.autoCompleteTextView);
        clearButton = (ImageView) itemView.findViewById(R.id.clear_text_view);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
                if(picker != null) {
                    picker.setPickedItem(null);
                }
            }
        });
        adapter = new SelectorListAdapter();
        autoCompleteTextView.setAdapter(adapter);
        textWatcher = null;
        picker = null;
    }
}
