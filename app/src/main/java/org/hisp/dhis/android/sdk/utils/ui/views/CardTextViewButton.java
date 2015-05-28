package org.hisp.dhis.android.sdk.utils.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.CardView;

import org.hisp.dhis.android.sdk.R;


public class CardTextViewButton extends CardView {
    private FontTextView mTextView;
    private CharSequence mHint;

    public CardTextViewButton(Context context) {
        super(context);
        init(context);
    }

    public CardTextViewButton(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context);

        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.ButtonHint);
            mHint = attrs.getString(R.styleable.ButtonHint_hint);
            setText(mHint);
            attrs.recycle();
        }
    }

    private void init(Context context) {
        int pxs = getResources().getDimensionPixelSize(R.dimen.card_text_view_margin);
        FrameLayout.LayoutParams textViewParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParams.setMargins(pxs, pxs, pxs, pxs);

        mTextView = new FontTextView(context);
        mTextView.setClickable(true);
        mTextView.setId(getId());
        mTextView.setBackgroundResource(R.drawable.spinner_background_holo_light);
        mTextView.setFont(getContext().getString(R.string.regular_font_name));
        mTextView.setLayoutParams(textViewParams);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.medium_text_size));

        addView(mTextView);
    }

    public void setText(CharSequence sequence) {
        if (mTextView != null && sequence != null) {
            mTextView.setText(sequence);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mTextView.setOnClickListener(listener);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        mTextView.setEnabled(isEnabled);
        setText(mHint);
    }
}
