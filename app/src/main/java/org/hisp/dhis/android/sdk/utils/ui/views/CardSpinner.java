package org.hisp.dhis.android.sdk.utils.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.hisp.dhis.android.sdk.R;


public class CardSpinner extends CardView {
    private FontSpinner spinner;
    private CharSequence hint;

    public CardSpinner(Context context) {
        super(context);
        init(context);
    }

    public CardSpinner(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context);

        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.ButtonHint);
            hint = attrs.getString(R.styleable.ButtonHint_hint);
            setText(hint);
            attrs.recycle();
        }
    }

    private void init(Context context) {
        spinner = new FontSpinner(context);
        spinner.setClickable(true);
        //spinner.setId(getId()); causes the app to crash because of duplicate ids
        spinner.setBackgroundResource(R.drawable.spinner_background_holo_light);
        spinner.setFont(getContext().getString(R.string.regular_font_name));

        addView(spinner);
    }

    public void setText(CharSequence sequence) {
        if (spinner != null && sequence != null) {
            //spinner.setText(sequence);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        spinner.setOnClickListener(listener);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        spinner.setEnabled(isEnabled);
        setText(hint);
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }

    public void setAdapter(ArrayAdapter adapter) {
        spinner.setAdapter(adapter);
    }

    public int getSelectedItemPosition() {
        return spinner.getSelectedItemPosition();
    }

    public void setSelection(int position) {
        spinner.setSelection(position);
    }
}
