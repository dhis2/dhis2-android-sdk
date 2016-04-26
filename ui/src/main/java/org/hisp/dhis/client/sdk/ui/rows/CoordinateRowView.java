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

package org.hisp.dhis.client.sdk.ui.rows;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntityText;
import org.hisp.dhis.client.sdk.ui.models.DataEntityCoordinate;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;

import static android.text.TextUtils.isEmpty;

public class CoordinateRowView implements RowView {
    private static final String TAG = CoordinateRowView.class.getSimpleName();
    private OnCoordinatesCaptured onCoordinatesCaptured;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager,
                                                      LayoutInflater inflater,
                                                      ViewGroup parent, DataEntityText.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(CoordinateRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }

        return new CoordinateRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_coordinate, parent, false), type, onCoordinatesCaptured);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, DataEntity dataEntity) {
        CoordinateRowViewHolder coordinateRowViewHolder = (CoordinateRowViewHolder) holder;
        DataEntityCoordinate coordinateDataEntity = (DataEntityCoordinate) dataEntity;
        coordinateRowViewHolder.update(coordinateDataEntity);

    }

    public void setOnCoordinatesCaptured(OnCoordinatesCaptured onCoordinatesCaptured) {
        this.onCoordinatesCaptured = onCoordinatesCaptured;
    }


    public interface OnCoordinatesCaptured {
        double onLatitudeCaptured();

        double onLongitudeCaptured();
    }

    private static class CoordinateRowViewHolder extends RecyclerView.ViewHolder {
        public final TextInputLayout latitudeTextInputLayout;
        public final TextInputLayout longitudeTextInputLayout;
        public final EditText latitudeEditText;
        public final EditText longitudeEditText;
        public final ImageButton captureCoordinateButton;
        public final OnCaptureCoordinatesListener onCaptureCoordinatesListener;
        public final OnFocusChangeListener onFocusChangeListener;
        public final OnValueChangedListener onValueChangedListener;
        public final TextView textViewLabel;

        public CoordinateRowViewHolder(View itemView, DataEntityText.Type type,
                                       OnCoordinatesCaptured onCoordinatesCaptured) {
            super(itemView);

            textViewLabel = (TextView) itemView.findViewById(R.id.textview_row_label);

            latitudeTextInputLayout = (TextInputLayout) itemView
                    .findViewById(R.id.coordinate_row_latitude_textinputlayout);
            longitudeTextInputLayout = (TextInputLayout) itemView
                    .findViewById(R.id.coordinate_row_longitude_textinputlayout);
            latitudeEditText = (EditText) itemView
                    .findViewById(R.id.coordinate_row_latitude_edittext);
            longitudeEditText = (EditText) itemView
                    .findViewById(R.id.coordinate_row_longitude_edittext);
            captureCoordinateButton = (ImageButton) itemView
                    .findViewById(R.id.capture_coordinate_button_picker_view);

            latitudeTextInputLayout.setHint(itemView.getContext()
                    .getString(R.string.enter_latitude));
            longitudeTextInputLayout.setHint(itemView.getContext()
                    .getString(R.string.enter_longitude));

            onCaptureCoordinatesListener = new OnCaptureCoordinatesListener(
                    onCoordinatesCaptured, latitudeEditText, longitudeEditText);
            onFocusChangeListener = new OnFocusChangeListener(
                    latitudeTextInputLayout, latitudeEditText);
            onValueChangedListener = new OnValueChangedListener();

            latitudeEditText.setOnFocusChangeListener(onFocusChangeListener);
            latitudeEditText.addTextChangedListener(onValueChangedListener);
            longitudeEditText.setOnFocusChangeListener(onFocusChangeListener);
            longitudeEditText.addTextChangedListener(onValueChangedListener);

            captureCoordinateButton.setOnClickListener(onCaptureCoordinatesListener);

        }

        public void update(DataEntityCoordinate entity) {
            textViewLabel.setText(R.string.enter_coordinates);
            onValueChangedListener.setDataEntity(entity);
            latitudeEditText.setText(entity.getValue().get("latitude").toString());
            longitudeEditText.setText(entity.getValue().get("longitude").toString());

            CharSequence hint = entity.getValue() == null ? null : onFocusChangeListener.getHint();
            latitudeTextInputLayout.setHint(hint);
            longitudeTextInputLayout.setHint(hint);
        }
    }

    private static class OnValueChangedListener extends AbsTextWatcher {
        private DataEntityCoordinate dataEntity;

        public void setDataEntity(DataEntityCoordinate dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (dataEntity != null) {
//                dataEntity.updateValue(editable.toString());
            }
        }
    }

    private static class OnFocusChangeListener implements View.OnFocusChangeListener {
        private final TextInputLayout textInputLayout;
        private final EditText editText;
        private final CharSequence hint;

        public OnFocusChangeListener(TextInputLayout inputLayout, EditText editText) {
            this.textInputLayout = inputLayout;
            this.editText = editText;
            this.hint = textInputLayout.getHint();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                textInputLayout.setHint(hint);
            } else {
                if (!isEmpty(editText.getText().toString())) {
                    textInputLayout.setHint(null);
                }
            }
        }

        public CharSequence getHint() {
            return hint;
        }
    }

    private static class OnCaptureCoordinatesListener implements View.OnClickListener {
        private EditText latitudeEditText;
        private EditText longitudeEditText;
        private OnCoordinatesCaptured onCoordinatesCaptured;

        public OnCaptureCoordinatesListener(OnCoordinatesCaptured onCoordinatesCaptured, EditText
                latitudeEditText, EditText longitudeEditText) {
            this.onCoordinatesCaptured = onCoordinatesCaptured;
            this.latitudeEditText = latitudeEditText;
            this.longitudeEditText = longitudeEditText;
        }

        @Override
        public void onClick(View v) {
            if (onCoordinatesCaptured != null) {
                latitudeEditText.setText(String.valueOf(onCoordinatesCaptured.onLatitudeCaptured
                        ()));
                longitudeEditText.setText(String.valueOf(onCoordinatesCaptured
                        .onLongitudeCaptured()));
            } else {
                latitudeEditText.setText(String.valueOf(0.0));
                longitudeEditText.setText(String.valueOf(0.0));
            }
        }
    }
}
