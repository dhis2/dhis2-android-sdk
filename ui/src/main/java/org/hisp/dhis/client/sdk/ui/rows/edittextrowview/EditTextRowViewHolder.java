package org.hisp.dhis.client.sdk.ui.rows.edittextrowview;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntityEditText;
import org.hisp.dhis.client.sdk.ui.utils.FormUtils;

import static android.text.TextUtils.isEmpty;

final class EditTextRowViewHolder extends RecyclerView.ViewHolder {
    /* number of lines for LONG_TEXT */
    private static final int LONG_TEXT_LINE_COUNT = 3;

    /* in order to improve performance, we pre-fetch
    all prompts from resources */
    private final String enterText;
    private final String enterLongText;
    private final String enterNumber;
    private final String enterInteger;
    private final String enterPositiveInteger;
    private final String enterPositiveOrZeroInteger;
    private final String enterNegativeInteger;

    final TextView textViewLabel;
    final TextInputLayout textInputLayout;
    final EditText editText;
    final LinearLayout rootLinearLayout;

    /* we use EditTextRowOnFocusChangeListener in order to hide
    hint from user when row is not focused */
    final EditTextRowOnFocusChangeListener onFocusChangeListener;

    /* callback which is triggered on value changes */
    final EditTextRowOnValueChangedListener onValueChangedListener;

    EditTextRowViewHolder(View itemView) {
        super(itemView);

        Context context = itemView.getContext();

        // fetching hint strings
        enterText = context.getString(R.string.enter_text);
        enterLongText = context.getString(R.string.enter_long_text);
        enterNumber = context.getString(R.string.enter_number);
        enterInteger = context.getString(R.string.enter_integer);
        enterPositiveInteger = context.getString(R.string.enter_positive_integer);
        enterPositiveOrZeroInteger = context.getString(R.string.enter_positive_integer_or_zero);
        enterNegativeInteger = context.getString(R.string.enter_negative_integer);

        textViewLabel = (TextView) itemView
                .findViewById(R.id.textview_row_label);
        textInputLayout = (TextInputLayout) itemView
                .findViewById(R.id.edittext_row_textinputlayout);
        editText = (EditText) itemView
                .findViewById(R.id.edittext_row_edittext);

        rootLinearLayout = (LinearLayout) itemView.findViewById(R.id.root_linear_layout);
        rootLinearLayout.setOnClickListener(new EditTextRowOnRowClickListener(editText, context));

        onFocusChangeListener = new EditTextRowOnFocusChangeListener(textInputLayout, editText);
        onValueChangedListener = new EditTextRowOnValueChangedListener();

        editText.setOnFocusChangeListener(onFocusChangeListener);
        editText.addTextChangedListener(onValueChangedListener);
    }

    public void update(FormEntityEditText entity) {
        // update callbacks with current entities
        onValueChangedListener.setDataEntity(entity);
        textViewLabel.setText(FormUtils.getFormEntityLabel(entity));
        editText.setText(entity.getValue());
        editText.setEnabled(!entity.isLocked());
        rootLinearLayout.setEnabled(!entity.isLocked());

        // configure edittext according to entity
        configureView(entity);


        if (entity.isLocked()) {
            textInputLayout.setHintEnabled(false);
            editText.setEnabled(false);
            editText.setClickable(false);
        } else {
            editText.setEnabled(true);
            editText.setClickable(true);
            textInputLayout.setHintEnabled(true);
        }
    }

    private boolean configureView(FormEntityEditText dataEntityText) {
        switch (dataEntityText.getInputType()) {
            case TEXT: {
                String hint = isEmpty(dataEntityText.getHint()) ?
                        enterText : dataEntityText.getHint();
                return configure(hint, InputType.TYPE_CLASS_TEXT, true);
            }
            case LONG_TEXT: {
                String hint = isEmpty(dataEntityText.getHint()) ?
                        enterLongText : dataEntityText.getHint();
                return configure(hint, InputType.TYPE_CLASS_TEXT, false);
            }
            case NUMBER: {
                String hint = isEmpty(dataEntityText.getHint()) ?
                        enterNumber : dataEntityText.getHint();
                return configure(hint,
                        InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                                InputType.TYPE_NUMBER_FLAG_SIGNED, true);
            }
            case INTEGER: {
                String hint = isEmpty(dataEntityText.getHint()) ?
                        enterInteger : dataEntityText.getHint();
                return configure(hint,
                        InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_SIGNED, true);
            }
            case INTEGER_NEGATIVE: {
                String hint = isEmpty(dataEntityText.getHint()) ?
                        enterNegativeInteger : dataEntityText.getHint();
                return configure(hint,
                        InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_SIGNED, true);
            }
            case INTEGER_ZERO_OR_POSITIVE: {
                String hint = isEmpty(dataEntityText.getHint()) ?
                        enterPositiveOrZeroInteger : dataEntityText.getHint();
                return configure(hint, InputType.TYPE_CLASS_NUMBER, true);
            }
            case INTEGER_POSITIVE: {
                String hint = isEmpty(dataEntityText.getHint()) ?
                        enterPositiveInteger : dataEntityText.getHint();
                return configure(hint, InputType.TYPE_CLASS_NUMBER, true);
            }
            default:
                return false;
        }
    }

    private boolean configure(String hint, int inputType, boolean line) {
        String textInputLayoutHint = isEmpty(editText.getText()) ? hint : null;

        onFocusChangeListener.setHint(hint);
        textInputLayout.setHint(textInputLayoutHint);

        editText.setInputType(inputType);
        editText.setSingleLine(line);

        if (!line) {
            editText.setLines(LONG_TEXT_LINE_COUNT);
        }

        return true;
    }
}
