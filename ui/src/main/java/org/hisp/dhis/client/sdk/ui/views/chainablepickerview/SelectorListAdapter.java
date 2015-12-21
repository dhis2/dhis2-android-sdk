package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for filtered items in a AutoCompleteTextView, using {@link IPickable}
 */
public class SelectorListAdapter extends BaseAdapter implements Filterable {

    private List<IPickable> items;

    public void swapData(List<IPickable> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public IPickable getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectorListViewHolder holder;
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickerlistviewitem, null);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            holder = new SelectorListViewHolder(textView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (SelectorListViewHolder) convertView.getTag();
        }
        holder.textView.setText(items.get(position).toString());
        return view;
    }

    @Override
    public Filter getFilter() {
        return new PickableFilter();
    }

    private class PickableFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<String> values = new ArrayList<>();
            filterResults.values = values;
            filterResults.count = values.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private class SelectorListViewHolder {
        TextView textView;
        public SelectorListViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
