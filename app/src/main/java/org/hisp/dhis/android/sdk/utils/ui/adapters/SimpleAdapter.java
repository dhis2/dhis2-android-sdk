/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.utils.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;

public class SimpleAdapter<T> extends AbsAdapter<T> {
    private ExtractStringCallback<T> mCallback;

    public SimpleAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    public void setStringExtractor(ExtractStringCallback<T> callback) {
        mCallback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        View view;

        if (convertView == null) {
            View root = getInflater().inflate(R.layout.dialog_fragment_listview_item, parent, false);
            TextView textView = (TextView) root.findViewById(R.id.textview_item);

            holder = new TextViewHolder(textView);
            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (TextViewHolder) view.getTag();
        }

        String label = mCallback.getString(getData().get(position));
        holder.textView.setText(label);
        return view;
    }

    public T getItemSafely(int pos) {
        if (getData() != null && getData().size() > 0) {
            return getData().get(pos);
        } else {
            return null;
        }
    }

    public static interface ExtractStringCallback<T> {
        public String getString(T object);
    }

    private class TextViewHolder {
        final TextView textView;

        public TextViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}