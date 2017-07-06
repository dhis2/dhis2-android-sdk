/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.dialogs;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.ui.views.FontTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class QueryTrackedEntityInstancesResultDialogAdapter extends BaseAdapter implements
        Filterable {
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
    private Map<String, ProgramTrackedEntityAttribute> programTrackedEntityAttributeMap;

    private final LayoutInflater mInflater;
    private Context mContext;

    public QueryTrackedEntityInstancesResultDialogAdapter(LayoutInflater inflater,
            List<TrackedEntityInstance> selectedTrackedEntityInstances,
            Map<String, ProgramTrackedEntityAttribute> programTrackedEntityAttributeMap,
            Context context) {
        mInflater = inflater;
        mObjects = new ArrayList<>();
        this.selectedTrackedEntityInstances = selectedTrackedEntityInstances;
        this.programTrackedEntityAttributeMap = programTrackedEntityAttributeMap;
        mContext = context;
    }

    public List<TrackedEntityInstance> getData() {
        return mObjects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        if (mObjects == null) {
            return 0;
        }
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

        List<TrackedEntityAttributeValue> trackedEntityAttributesByProgram =
                getTrackedEntityAttributesByProgram(
                trackedEntityInstance.getAttributes(),
                programTrackedEntityAttributeMap);

        if (convertView == null) {
            view = mInflater.inflate(
                    R.layout.dialog_fragment_listview_item_teiqueryresult, parent, false);

            holder = new ViewHolder(view, mInflater, mContext);
            view.setTag(holder);
        } else {
            view = convertView;
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxTeiQuery);
            checkBox.setChecked(selectedTrackedEntityInstances.contains(trackedEntityInstance));
            holder = (ViewHolder) view.getTag();
        }

        if (trackedEntityInstance != null) {
            holder.setData(trackedEntityAttributesByProgram, programTrackedEntityAttributeMap);
        }
        view.setId(position);
        return view;
    }

    private List<TrackedEntityAttributeValue> getTrackedEntityAttributesByProgram(
            List<TrackedEntityAttributeValue> attributes,
            Map<String, ProgramTrackedEntityAttribute> programTrackedEntityAttributeMap) {

        List<TrackedEntityAttributeValue> attributesByProgram = new ArrayList<>();


        for (int i = 0; i < attributes.size(); i++) {
            TrackedEntityAttributeValue attributeValue = attributes.get(i);

            if (programTrackedEntityAttributeMap.containsKey(
                    attributeValue.getTrackedEntityAttributeId())) {
                attributesByProgram.add(attributeValue);
            }
        }

        return attributesByProgram;
    }

    private static class ViewHolder {
        LinearLayout attributeContainer;
        LayoutInflater mInflater;
        Context mContext;

        private ViewHolder(View view, LayoutInflater inflater, Context context) {
            mInflater = inflater;
            attributeContainer = (LinearLayout) view.findViewById(R.id.textviewcontainer);
            mContext = context;
        }

        /**
         * Sets the values of a given TrackedEntityAttributeValue list into the set textviews
         */
        public void setData(List<TrackedEntityAttributeValue> values,
                Map<String, ProgramTrackedEntityAttribute> programTrackedEntityAttributeMap) {

            attributeContainer.removeAllViews();

            if (values != null) {

                Comparator<TrackedEntityAttributeValue> trackedEntityAttributeValueComparator =
                        new TrackedEntityAttributeValueByIndexInProgramSorter(
                                programTrackedEntityAttributeMap);
                Collections.sort(values, trackedEntityAttributeValueComparator);

                for (int i = 0; i < values.size(); i++) {
                    TrackedEntityAttributeValue trackedEntityAttributeValue = values.get(i);
                    if (programTrackedEntityAttributeMap.containsKey(
                            trackedEntityAttributeValue.getTrackedEntityAttributeId())) {
                        if (trackedEntityAttributeValue != null ) {
                            ProgramTrackedEntityAttribute programTrackedEntityAttribute =
                                    programTrackedEntityAttributeMap.get(
                                            trackedEntityAttributeValue.getTrackedEntityAttributeId());

                            StringBuilder builder = new StringBuilder();
                            builder.append(programTrackedEntityAttribute.getTrackedEntityAttribute().getDisplayName());
                            builder.append(attributeContainer.getContext().getString(
                                    R.string.delimiter));

                            LinearLayout attributeLayout = (LinearLayout) mInflater.inflate(
                                    R.layout.two_horizontal_textviews, attributeContainer, false);

                            FontTextView labelTextView = (FontTextView) attributeLayout.findViewById(
                                    R.id.left_textview);
                            labelTextView.setText(builder.toString());


                            Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/" + mContext.getString(R.string.light_font_name));
                            Typeface font2 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/" + mContext.getString(R.string.medium_font_name));
                            SpannableStringBuilder SS = new SpannableStringBuilder(
                                    builder.toString() + trackedEntityAttributeValue.getValue());
                            SS.setSpan(new CustomTypefaceSpan(font), 0, builder.toString().length(),
                                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            SS.setSpan(new CustomTypefaceSpan(font2), builder.toString().length(),
                                    builder.toString().length()
                                            + trackedEntityAttributeValue.getValue().length(),
                                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            labelTextView.setText(SS);


                            attributeContainer.addView(attributeLayout);

                        }
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
                    for (TrackedEntityAttributeValue attrValue : trackedEntityInstanceValue
                            .getAttributes()) {
                        final String value = attrValue.getValue();
                        if (value == null) {
                            break;
                        }
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


    private static class CustomTypefaceSpan extends MetricAffectingSpan {
        private final Typeface typeface;

        public CustomTypefaceSpan(final Typeface typeface) {
            this.typeface = typeface;
        }

        @Override
        public void updateDrawState(final TextPaint drawState) {
            apply(drawState);
        }

        @Override
        public void updateMeasureState(final TextPaint paint) {
            apply(paint);
        }

        private void apply(final Paint paint) {
            final Typeface oldTypeface = paint.getTypeface();
            final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
            final int fakeStyle = oldStyle & ~typeface.getStyle();

            if ((fakeStyle & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fakeStyle & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(typeface);
        }
    }
}