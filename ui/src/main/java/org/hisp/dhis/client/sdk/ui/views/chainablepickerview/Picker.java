/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import java.util.List;

/**
 * Picker class for representing a chainable {@link AutoCompleteTextView} in {@link RecyclerView}.
 * Use this class with an {@link SelectorAdapter}
 */
public class Picker implements Parcelable {

    public static final Creator<Picker> CREATOR = new Creator<Picker>() {
        @Override
        public Picker createFromParcel(Parcel in) {
            return new Picker(in);
        }

        @Override
        public Picker[] newArray(int size) {
            return new Picker[size];
        }
    };
    private Picker nextLinkedSibling;
    private List<IPickable> pickableItems;
    private AdapterView.OnItemClickListener listener;
    private View.OnClickListener onClickListener;
    private View.OnFocusChangeListener onFocusChangeListener;
    private List<Picker> parentList;
    private RecyclerView parentView;
    private IPickable pickedItem;
    private String hint;
    private String mimeType;
    private boolean added;
    private IPickableItemClearListener pickedItemClearListener;

    public Picker(final List<IPickable> pickableItems, String hint, String mimeType) {
        this.pickableItems = pickableItems;
        this.hint = hint;
        this.mimeType = mimeType;
        this.onFocusChangeListener = new AutoCompleteOnFocusChangeListener(this);
        this.listener = new AutoCompleteOnItemClickListener();
        this.onClickListener = new OnPickerClickedListener();
        this.added = false;
    }

    protected Picker(Parcel in) {
        pickedItem = in.readParcelable(IPickable.class.getClassLoader());
        pickableItems = in.readArrayList(IPickable.class.getClassLoader());
        String[] data = new String[2];
        in.readStringArray(data);
        hint = data[0];
        mimeType = data[1];
        this.onFocusChangeListener = new AutoCompleteOnFocusChangeListener(this);
        this.onClickListener = new OnPickerClickedListener();
        this.listener = new AutoCompleteOnItemClickListener();
        boolean[] booleanValues = new boolean[1];
        in.readBooleanArray(booleanValues);
        this.added = booleanValues[0];
    }

    public void registerPickedItemClearListener(IPickableItemClearListener listener) {
        this.pickedItemClearListener = listener;
    }

    public View.OnFocusChangeListener getOnFocusChangeListener() {
        return onFocusChangeListener;
    }

    public void setOnFocusChangeListener(final View.OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    public void setParentList(List<Picker> parentList) {
        this.parentList = parentList;
    }

    public AdapterView.OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(final AdapterView.OnItemClickListener listener) {
        AutoCompleteOnItemClickListener mergedListener = new AutoCompleteOnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                listener.onItemClick(parent, view, position, id);
            }
        };
        this.listener = mergedListener;
    }

    public void setParentView(RecyclerView recyclerView) {
        this.parentView = recyclerView;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public Picker getNextLinkedSibling() {
        return nextLinkedSibling;
    }

    public void setNextLinkedSibling(Picker picker) {
        this.nextLinkedSibling = picker;
    }

    public IPickable getPickedItem() {
        return pickedItem;
    }

    public void setPickedItem(IPickable pickable) {
        this.pickedItem = pickable;

        if (pickedItem == null && pickedItemClearListener != null) {
            pickedItemClearListener.clearedCallback();
        }
        parentView.getAdapter().notifyDataSetChanged();
    }

    public List<IPickable> getPickableItems() {
        return pickableItems;
    }

    public void setPickableItems(List<IPickable> pickableItems) {
        this.pickableItems = pickableItems;
        if (parentView != null) {
            parentView.getAdapter().notifyDataSetChanged();
        }
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void showNext() {
        if (nextLinkedSibling != null && !parentList.contains(nextLinkedSibling)) {
            nextLinkedSibling.setParentList(parentList);
            nextLinkedSibling.setParentView(parentView);
            int position = parentList.indexOf(this);
            parentList.add(position + 1, nextLinkedSibling);
            nextLinkedSibling.setAdded(true);
            parentView.getAdapter().notifyDataSetChanged();
        }
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void hideNextSibling() {
        if (nextLinkedSibling != null && nextLinkedSibling.isAdded()) {
            nextLinkedSibling.hide();
            parentView.getAdapter().notifyDataSetChanged();
        }
    }

    public void hide() {
        hideNextSibling();
        setPickedItem(null);
        parentList.remove(this);
        parentList = null;
        parentView = null;
        added = false;
    }

    public void recycle() {
        parentView = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hint);
        dest.writeString(mimeType);
        dest.writeParcelable(pickedItem, flags);
        IPickable[] pickers = new IPickable[pickableItems.size()];
        dest.writeParcelableArray(pickableItems.toArray(pickers), flags);
        boolean[] booleanValues = new boolean[1];
        booleanValues[0] = added;
        dest.writeBooleanArray(booleanValues);
    }


    public class OnPickerClickedListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }

    /**
     * Triggers the next chained {@link Picker} to be shown if it has been set.
     */
    public class AutoCompleteOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            pickedItem = pickableItems.get(position);
            showNext();
        }
    }

    /**
     * Triggers to show drop down if the current view has focus.
     * If the current view does not have focus, then the content of the {@link AutoCompleteTextView}
     * will be matched with the available {@link IPickable}s and either cleared, or set to the
     * previous selection.
     */
    public class AutoCompleteOnFocusChangeListener implements View.OnFocusChangeListener {
        private final Picker picker;

        public AutoCompleteOnFocusChangeListener(Picker picker) {
            this.picker = picker;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!(v instanceof AutoCompleteTextView)) {
                return;
            }
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) v;

            if (hasFocus) {
                autoCompleteTextView.showDropDown();
                return;
            } else {
                if (autoCompleteTextView.getText().length() <= 0) {
                    return;
                }
            }
            for (IPickable pickable : picker.getPickableItems()) {
                if (pickable.toString().equals(autoCompleteTextView.getText().toString())) {
                    return;
                }
            }
            String previousText = "";
            if (pickedItem != null) {
                previousText = pickedItem.toString();
            }
            autoCompleteTextView.setText(previousText);
            if (previousText.length() <= 0) {
                picker.hideNextSibling();
            }
        }

        public Picker getPicker() {
            return picker;
        }
    }
}
