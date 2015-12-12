package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;


public abstract class AbsEnrollmentDatePickerRow extends Row {

    public static final String EMPTY_FIELD = "";
    public static final String DATE_FORMAT = "YYYY-MM-dd";
    private final Enrollment mEnrollment;
    private final String date;

    public AbsEnrollmentDatePickerRow(String label, Enrollment enrollment, String date) {
        if (date == null)
            throw new IllegalArgumentException("Date must not be null");


        mLabel = label;
        mEnrollment = enrollment;
        this.date = date;

        checkNeedsForDescriptionButton();
    }


    @Override
    public abstract View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container);

    @Override
    public int getViewType() {
        return DataEntryRowTypes.ENROLLMENT_DATE.ordinal();
    }
}
