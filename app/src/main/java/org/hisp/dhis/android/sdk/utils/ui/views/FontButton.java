package org.hisp.dhis.android.sdk.utils.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.utils.TypefaceManager;

public class FontButton extends Button {

    public FontButton(Context context) {
        super(context);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributeSet, R.styleable.ViewFont);
            setFont(attrs.getString(R.styleable.ViewFont_font));
            attrs.recycle();
        }
    }

    public void setFont(int resId) {
        String name = getResources().getString(resId);
        setFont(name);
    }

    private void setFont(final String fontName) {
        if (getContext() != null && getContext().getAssets() != null && fontName != null) {
            Typeface typeface = TypefaceManager.getTypeface(getContext().getAssets(), fontName);
            if (typeface != null) {
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
                setTypeface(typeface);
            }
        }
    }
}
