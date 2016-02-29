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
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;

import static android.text.TextUtils.isEmpty;

public class CoordinateRowView implements IRowView {
    private static final String TAG = CoordinateRowView.class.getSimpleName();
    private OnCoordinatesCaptured onCoordinatesCaptured;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager,
                                                      LayoutInflater inflater,
                                                      ViewGroup parent, DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(CoordinateRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }

        return new CoordinateRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_coordinate, parent, false), type, onCoordinatesCaptured);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, DataEntity dataEntity) {
        CoordinateRowViewHolder coordinateRowViewHolder = (CoordinateRowViewHolder) holder;
        coordinateRowViewHolder.update(dataEntity);

    }

    public void setOnCoordinatesCaptured(OnCoordinatesCaptured onCoordinatesCaptured) {
        this.onCoordinatesCaptured = onCoordinatesCaptured;
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

        public CoordinateRowViewHolder(View itemView, DataEntity.Type type,
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

        public void update(DataEntity entity) {
            textViewLabel.setText(R.string.enter_coordinates);
            onValueChangedListener.setDataEntity(entity);
            latitudeEditText.setText(entity.getValue());
            longitudeEditText.setText(entity.getValue());

            CharSequence hint = !isEmpty(entity.getValue()) ? null : onFocusChangeListener.getHint();
            latitudeTextInputLayout.setHint(hint);
            longitudeTextInputLayout.setHint(hint);
        }
    }
    private static class OnValueChangedListener extends AbsTextWatcher {
        private DataEntity dataEntity;

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (dataEntity != null) {
                dataEntity.updateValue(editable.toString());
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

        public OnCaptureCoordinatesListener(OnCoordinatesCaptured onCoordinatesCaptured, EditText latitudeEditText, EditText longitudeEditText) {
            this.onCoordinatesCaptured = onCoordinatesCaptured;
            this.latitudeEditText = latitudeEditText;
            this.longitudeEditText = longitudeEditText;
        }

        @Override
        public void onClick(View v) {
            if(onCoordinatesCaptured != null) {
                latitudeEditText.setText(String.valueOf(onCoordinatesCaptured.onLatitudeCaptured()));
                longitudeEditText.setText(String.valueOf(onCoordinatesCaptured.onLongitudeCaptured()));
            }
            else {
                latitudeEditText.setText(String.valueOf(0.0));
                longitudeEditText.setText(String.valueOf(0.0));
            }
        }
    }

    public interface OnCoordinatesCaptured {
        double onLatitudeCaptured();
        double onLongitudeCaptured();
    }
}
