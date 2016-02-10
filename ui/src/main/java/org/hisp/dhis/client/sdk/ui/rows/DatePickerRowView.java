package org.hisp.dhis.client.sdk.ui.rows;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.text.TextUtils.isEmpty;

public class DatePickerRowView implements IRowView {
    private static final String TAG = DatePickerRowView.class.getSimpleName();
    private static final String EMPTY_FIELD = "";
    private static FragmentManager fragmentManager;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager, LayoutInflater inflater, ViewGroup parent, DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(DatePickerRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        this.fragmentManager = fragmentManager;

        return new DatePickerRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_datepicker, parent, false), type);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final DataEntity dataEntity) {
        final DatePickerRowViewHolder datePickerRowViewHolder = (DatePickerRowViewHolder) holder;
        Context context = datePickerRowViewHolder.textInputLayout.getContext();

        CharSequence hint = !isEmpty(dataEntity.getValue()) ? null :
                datePickerRowViewHolder.onFocusChangeListener.getHint();
        datePickerRowViewHolder.textInputLayout.setHint(hint);
        datePickerRowViewHolder.onValueChangedListener.setDataEntity(dataEntity);
        datePickerRowViewHolder.onDateSetListener.setDataEntity(dataEntity);
        datePickerRowViewHolder.onClearListener.setDataEntity(dataEntity);
        datePickerRowViewHolder.textViewLabel.setText(dataEntity.getLabel());
        datePickerRowViewHolder.displayValueEditText.setOnClickListener(datePickerRowViewHolder.onEditTextClickListener);
        datePickerRowViewHolder.clearButton.setOnClickListener(datePickerRowViewHolder.onClearListener);
        datePickerRowViewHolder.datePickerButtonNow.setText(context.getString(R.string.todays_date));
        datePickerRowViewHolder.datePickerButtonNow.setOnClickListener(datePickerRowViewHolder.onTodaysDateClickListener);

        datePickerRowViewHolder.datePickerButton.setText(context.getString(R.string.pick_date));
        datePickerRowViewHolder.datePickerButton.setOnClickListener(datePickerRowViewHolder.onEditTextClickListener);
    }

    private static class DatePickerRowViewHolder extends RecyclerView.ViewHolder {
        public final TextInputLayout textInputLayout;
        public final EditText displayValueEditText;
        public final TextView textViewLabel;
        public final Button datePickerButtonNow;
        public final Button datePickerButton;
        public final ImageButton clearButton;

        public final OnValueChangedListener onValueChangedListener;
        public final OnFocusChangeListener onFocusChangeListener;
        public final DateSetListener onDateSetListener;
        public final OnEditTextClickListener onEditTextClickListener;
        public final OnTodaysDateClickListener onTodaysDateClickListener;
        public final OnClearListener onClearListener;

        public DatePickerRowViewHolder(View itemView, DataEntity.Type type) {
            super(itemView);
            textInputLayout = (TextInputLayout) itemView.findViewById(R.id.date_picker_row_text_input_layout);
            textViewLabel = (TextView) itemView.findViewById(R.id.date_picker_row_label);
            displayValueEditText = (EditText) itemView.findViewById(R.id.date_picker_row_date_picker_text);
            datePickerButtonNow = (Button) itemView.findViewById(R.id.date_picker_row_date_picker_button_now);
            datePickerButton = (Button) itemView.findViewById(R.id.date_picker_row_date_picker_button);
            clearButton = (ImageButton) itemView.findViewById(R.id.clear_date_picker_view);


            if (!configureViews(type)) {
                throw new IllegalArgumentException("unsupported DatePickerRow type");
            }

            onValueChangedListener = new OnValueChangedListener();
            onFocusChangeListener = new OnFocusChangeListener(textInputLayout, displayValueEditText);
            onDateSetListener = new DateSetListener(displayValueEditText);
            onEditTextClickListener = new OnEditTextClickListener(onDateSetListener);
            onTodaysDateClickListener = new OnTodaysDateClickListener(onDateSetListener);
            onClearListener = new OnClearListener(displayValueEditText);


            displayValueEditText.setOnFocusChangeListener(onFocusChangeListener);
            displayValueEditText.addTextChangedListener(onValueChangedListener);

        }

        private boolean configureViews(DataEntity.Type entityType) {
            switch (entityType) {
                case DATE:
                    return configure(entityType);
                case ENROLLMENT_DATE:
                    return configure(entityType);
                case INCIDENT_DATE:
                    return configure(entityType);
                case EVENT_DATE:
                    return configure(entityType);
                default:
                    return false;
            }
        }

        private boolean configure(DataEntity.Type type) {
            textInputLayout.setHint(displayValueEditText.getContext().getString(R.string.enter_date));
            displayValueEditText.setInputType(InputType.TYPE_CLASS_DATETIME);

            return true;
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

        private static class OnClearListener implements View.OnClickListener {
            private final EditText editText;
            private DataEntity dataEntity;

            public OnClearListener(EditText editText) {
                this.editText = editText;
            }

            public void setDataEntity(DataEntity dataEntity) {
                this.dataEntity = dataEntity;
            }

            @Override
            public void onClick(View view) {
                editText.setText(EMPTY_FIELD);
                dataEntity.updateValue(EMPTY_FIELD);
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

        private static class OnEditTextClickListener implements View.OnClickListener {
            private final DateSetListener listener;


            public OnEditTextClickListener(DateSetListener listener) {
                this.listener = listener;
            }

            @Override
            public void onClick(View view) {
                listener.show(fragmentManager, TAG);
            }
        }

        private static class OnTodaysDateClickListener implements View.OnClickListener {
            private final DateSetListener listener;
            private final Calendar calendar;

            public OnTodaysDateClickListener(DateSetListener listener) {
                this.listener = listener;
                calendar = Calendar.getInstance();
            }

            @Override
            public void onClick(View view) {
                listener.onDateSet(null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
        }


        private static class DateSetListener extends DialogFragment implements DatePickerDialog.OnDateSetListener {
            private static final String DATE_FORMAT = "yyyy-MM-dd";
            private SimpleDateFormat simpleDateFormat;
            private DataEntity dataEntity;
            private final EditText editText;
            private final Calendar calendar;
            private boolean allowDatesInFuture;


            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                if(!allowDatesInFuture) {
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                }
                return datePickerDialog;
            }

            public DateSetListener(EditText editText) {
                this.editText = editText;
                this.calendar = Calendar.getInstance();

            }

            public void setDataEntity(DataEntity dataEntity) {
                this.dataEntity = dataEntity;
            }



            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
                String newValue = simpleDateFormat.format(calendar.getTime());
                editText.setText(newValue);
                dataEntity.updateValue(newValue);

            }
            public void setAllowDatesInFuture(boolean allowDatesInFuture) {
                this.allowDatesInFuture = allowDatesInFuture;
            }

        }
    }

}
