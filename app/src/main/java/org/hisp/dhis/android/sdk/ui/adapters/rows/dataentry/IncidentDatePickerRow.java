package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import static android.text.TextUtils.isEmpty;


public class IncidentDatePickerRow extends AbsEnrollmentDatePickerRow {

    private Enrollment mEnrollment;
    private String mLabel;
    private String mIncidentDate;

    public IncidentDatePickerRow(String label, Enrollment enrollment, String incidentDate) {
        super(label, enrollment, incidentDate);
        this.mEnrollment = enrollment;
        this.mIncidentDate = incidentDate;
        this.mLabel = label;
    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        DatePickerRowHolder holder;

        if (convertView != null && convertView.getTag() instanceof DatePickerRowHolder) {
            view = convertView;
            holder = (DatePickerRowHolder) view.getTag();
        } else {
            View root = inflater.inflate(
                    R.layout.listview_row_event_datepicker, container, false);
            detailedInfoButton = root.findViewById(R.id.detailed_info_button_layout); // need to keep reference
            holder = new DatePickerRowHolder(root, inflater.getContext(), detailedInfoButton);

            root.setTag(holder);
            view = root;
        }

        if (!isEditable()) {
            holder.clearButton.setEnabled(false);
            holder.textLabel.setEnabled(false); //change color
            holder.pickerInvoker.setEnabled(false);
        } else {
            holder.clearButton.setEnabled(true);
            holder.textLabel.setEnabled(true);
            holder.pickerInvoker.setEnabled(true);
        }
        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));
        holder.updateViews(mLabel, mEnrollment, mIncidentDate);

        if (isDetailedInfoButtonHidden())
            holder.detailedInfoButton.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public int getViewType() {
        return super.getViewType();
    }

    private class DatePickerRowHolder {
        final TextView textLabel;
        final TextView pickerInvoker;
        final ImageButton clearButton;
        final View detailedInfoButton;
        final DateSetListener dateSetListener;
        final OnEditTextClickListener invokerListener;
        final ClearButtonListener clearButtonListener;

        public DatePickerRowHolder(View root, Context context, View detailedInfoButton) {
            textLabel = (TextView) root.findViewById(R.id.text_label);
            pickerInvoker = (TextView) root.findViewById(R.id.date_picker_text_view);
            clearButton = (ImageButton) root.findViewById(R.id.clear_text_view);
            this.detailedInfoButton = detailedInfoButton;

            dateSetListener = new DateSetListener(pickerInvoker);
            invokerListener = new OnEditTextClickListener(context, dateSetListener);
            clearButtonListener = new ClearButtonListener(pickerInvoker);

            clearButton.setOnClickListener(clearButtonListener);
            pickerInvoker.setOnClickListener(invokerListener);

        }

        public void updateViews(String label, Enrollment enrollment, String incidentDate ) {
            dateSetListener.setEnrollment(enrollment);
            clearButtonListener.setEnrollment(enrollment);

            String eventDate = null;


            if(enrollment != null && incidentDate != null && !isEmpty(incidentDate))
            {
                dateSetListener.setIncidentDate(enrollment.getDateOfIncident());
                clearButtonListener.setIncidentDate(enrollment.getDateOfIncident());
                DateTime incidentDateTime = DateTime.parse(enrollment.getDateOfIncident());
                eventDate = incidentDateTime.toString(DATE_FORMAT);
            }

            textLabel.setText(label);
            pickerInvoker.setText(eventDate);
        }


    }

    private static class OnEditTextClickListener implements View.OnClickListener {
        private final Context context;
        private final DateSetListener listener;

        public OnEditTextClickListener(Context context,
                                       DateSetListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            LocalDate currentDate = new LocalDate();
            DatePickerDialog picker = new DatePickerDialog(context, listener,
                    currentDate.getYear(), currentDate.getMonthOfYear() - 1, currentDate.getDayOfMonth());
            picker.getDatePicker().setMaxDate(DateTime.now().getMillis());
            picker.show();
        }
    }
    private static class ClearButtonListener implements View.OnClickListener {
        private final TextView textView;
        private Enrollment enrollment;
        private String enrollmentDate;
        private String incidentDate;

        public ClearButtonListener(TextView textView) {
            this.textView = textView;
        }

        public void setEnrollment(Enrollment enrollment) {
            this.enrollment = enrollment;
        }

        public void setEnrollmentDate(String enrollmentDate) {
            this.enrollmentDate = enrollmentDate;
        }

        public void setIncidentDate(String incidentDate) {
            this.incidentDate = incidentDate;
        }

        @Override
        public void onClick(View view) {
            textView.setText(EMPTY_FIELD);
            if (enrollmentDate != null)
                enrollment.setDateOfEnrollment(EMPTY_FIELD);
            else if (incidentDate != null)
                enrollment.setDateOfIncident(EMPTY_FIELD);
        }
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {
        private static final String DATE_FORMAT = "YYYY-MM-dd";
        private final TextView textView;
        private Enrollment enrollment;
        private DataValue value;
        private String enrollmentDate;
        private String incidentDate;

        public DateSetListener(TextView textView) {
            this.textView = textView;
        }

        public void setEnrollment(Enrollment enrollment) {
            this.enrollment = enrollment;
        }

        public void setIncidentDate(String incidentDate) {
            this.incidentDate = incidentDate;
        }

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            LocalDate date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            if (value == null) value = new DataValue();


            if (incidentDate != null)
                value.setValue(incidentDate);

            String newValue = date.toString(DATE_FORMAT);
            textView.setText(newValue);

            if (!newValue.equals(value.getValue())) {
                value.setValue(newValue);

                if (incidentDate != null)
                    enrollment.setDateOfIncident(value.getValue());

                Dhis2Application.getEventBus().post(new RowValueChangedEvent(value, DataEntryRowTypes.ENROLLMENT_DATE.toString()));
            }

        }
    }
}
