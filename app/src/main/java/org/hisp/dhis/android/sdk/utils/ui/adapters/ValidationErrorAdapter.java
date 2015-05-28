package org.hisp.dhis.android.sdk.utils.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;

public final class ValidationErrorAdapter extends AbsAdapter<String> {

    public ValidationErrorAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = getInflater().inflate(
                    R.layout.dialog_fragment_listview_item_validation_error, parent, false);
            holder = new ViewHolder(
                    (TextView) view.findViewById(R.id.text_label)
            );
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(getData().get(position));
        return view;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private static class ViewHolder {
        public final TextView textView;

        private ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
