package org.hisp.dhis.client.sdk.ui.rows;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerRowView implements IRowView, DatePickerDialogFragment.DatePickerDialogHandler {

    private String dateFormat = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat;
    private FragmentManager fragmentManager;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager, LayoutInflater inflater, ViewGroup parent, DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(DatePickerRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        this.fragmentManager = fragmentManager;

        return new DatePickerRowViewHolder(inflater.inflate(
                R.layout.row_date_picker, parent, false), type);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final DataEntity dataEntity) {
        ((DatePickerRowViewHolder) holder).textInputLayout.setHint(dataEntity.getLabel());
        ((DatePickerRowViewHolder) holder).displayValueTextView.setHint(dataEntity.getValue());
        ((DatePickerRowViewHolder) holder).datePickerButtonNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat(dateFormat);
                String currentDate = simpleDateFormat.format(calendar.getTime());

                ((DatePickerRowViewHolder) holder).displayValueTextView.setText(currentDate);
            }
        });
        ((DatePickerRowViewHolder) holder).datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerBuilder datePickerBuilder = new DatePickerBuilder().setFragmentManager(fragmentManager)
                        .setStyleResId(R.style.BetterPickersDialogFragment)
                        .setYearOptional(true);

                datePickerBuilder.show();
            }
        });
    }

    @Override
    public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
        
    }

    private static class DatePickerRowViewHolder extends RecyclerView.ViewHolder {
        TextInputLayout textInputLayout;
        TextView displayValueTextView;
        Button datePickerButtonNow;
        Button datePickerButton;

        public DatePickerRowViewHolder(View itemView, DataEntity.Type type) {
            super(itemView);
            textInputLayout = (TextInputLayout) itemView.findViewById(R.id.date_picker_row_text_input_layout);
            displayValueTextView = (TextView) itemView.findViewById(R.id.date_picker_row_date_picker_text);
            datePickerButtonNow = (Button) itemView.findViewById(R.id.date_picker_row_date_picker_button_now);
            datePickerButton = (Button) itemView.findViewById(R.id.date_picker_row_date_picker_button);


            if (!configureViews(type)) {
                throw new IllegalArgumentException("unsupported view type");
            }
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
            textInputLayout.setHint(displayValueTextView.getContext().getString(R.string.enter_date));
            displayValueTextView.setInputType(InputType.TYPE_CLASS_DATETIME);

            return true;
        }

    }

}
