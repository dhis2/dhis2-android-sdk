/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hisp.dhis.android.sdk.ui.dialogs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.ui.views.FontTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class QueryTrackedEntityInstancesResultDialogAdapter extends BaseAdapter implements Filterable {
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();
    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */
    private List<TrackedEntityInstance> mObjects;

    private List<TrackedEntityInstance> selectedTrackedEntityInstances;

    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
    private ArrayList<TrackedEntityInstance> mOriginalValues;
    private ArrayFilter mFilter;

    private final LayoutInflater mInflater;

    public QueryTrackedEntityInstancesResultDialogAdapter(LayoutInflater inflater, List<TrackedEntityInstance> selectedTrackedEntityInstances) {
        mInflater = inflater;
        mObjects = new ArrayList<>();
        this.selectedTrackedEntityInstances = selectedTrackedEntityInstances;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return mObjects.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TrackedEntityInstance getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        TrackedEntityInstance trackedEntityInstance = mObjects.get(position);
        if (convertView == null) {
            view = mInflater.inflate(
                    R.layout.dialog_fragment_listview_item_teiqueryresult, parent, false);

            List<FontTextView> views = new ArrayList<>();
            if(trackedEntityInstance!=null && trackedEntityInstance.getAttributes()!=null) {
                for (int i = 0; i < trackedEntityInstance.getAttributes().size(); i++) {
                    FontTextView textView = (FontTextView) mInflater.inflate(R.layout.listview_row_text_view_label, parent, false);
                    LinearLayout ll = (LinearLayout) view.findViewById(R.id.textviewcontainer);
                    ll.addView(textView);
                    views.add(textView);
                }
            }


            holder = new ViewHolder(views);
            view.setTag(holder);
        } else {
            view = convertView;
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxTeiQuery);
            checkBox.setChecked(selectedTrackedEntityInstances.contains(trackedEntityInstance));
            holder = (ViewHolder) view.getTag();
        }

        if(trackedEntityInstance!=null) {
            holder.setData(trackedEntityInstance.getAttributes());
        }
        return view;
    }

    private static class ViewHolder {
        public final List<FontTextView> textViews;

        private ViewHolder(List<FontTextView> textViews) {
            this.textViews = textViews;
        }

        /**
         * Sets the values of a given TrackedEntityAttributeValue list into the set textviews
         * @param values
         */
        public void setData(List<TrackedEntityAttributeValue> values) {
            for(FontTextView fontTextView: textViews) {
                fontTextView.setText("");
            }
            if(values!=null) {
                for (int i = 0; i < values.size() && i < textViews.size(); i++) {
                    if(values.get(i)!=null) {
                        textViews.get(i).setText(values.get(i).getValue());
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<TrackedEntityInstance> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<TrackedEntityInstance> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<TrackedEntityInstance> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final TrackedEntityInstance trackedEntityInstanceValue = values.get(i);
                    for(TrackedEntityAttributeValue attrValue: trackedEntityInstanceValue.getAttributes()) {
                        final String value = attrValue.getValue();
                        final String valueText = value.toLowerCase();

                        // First match against the whole, non-splitted value
                        if (valueText.startsWith(prefixString)) {
                            newValues.add(trackedEntityInstanceValue);
                        } else {
                            final String[] words = valueText.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].startsWith(prefixString)) {
                                    newValues.add(trackedEntityInstanceValue);
                                    break;
                                }
                            }
                        }
                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<TrackedEntityInstance>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    public void swapData(List<TrackedEntityInstance> values) {
        if (values == null) {
            values = new ArrayList<>();
        }

        clear();
        addAll(values);
        notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    private void addAll(Collection<TrackedEntityInstance> collection) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(collection);
            } else {
                mObjects.addAll(collection);
            }
        }
    }

    /**
     * Remove all elements from the list.
     */
    private void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } else {
                mObjects.clear();
            }
        }
    }

    public static class OptionAdapterValue {
        public final String id;
        public final String label;

        public OptionAdapterValue(String id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof OptionAdapterValue)) {
                return false;
            }
            OptionAdapterValue p = (OptionAdapterValue) o;
            return objectsEqual(p.id, label) && objectsEqual(p.id, label);
        }

        private static boolean objectsEqual(Object a, Object b) {
            return a == b || (a != null && a.equals(b));
        }

        @Override
        public int hashCode() {
            return (id == null ? 0 : id.hashCode()) ^ (label == null ? 0 : label.hashCode());
        }
    }
}