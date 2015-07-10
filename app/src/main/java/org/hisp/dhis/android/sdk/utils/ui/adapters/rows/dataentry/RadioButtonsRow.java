/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.utils.ui.adapters.rows.dataentry;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.fragments.dataentry.RowValueChangedEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;

public class RadioButtonsRow implements DataEntryRow {
    private static final String EMPTY_FIELD = "";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    public static final String FEMALE = "gender_female";
    public static final String MALE = "gender_male";
    public static final String OTHER = "gender_other";

    private final String mLabel;
    private final BaseValue mValue;
    private final DataEntryRowTypes mType;

    private boolean hidden = false;
    private boolean editable = true;

    public RadioButtonsRow(String label, BaseValue baseValue, DataEntryRowTypes type) {
        if (!DataEntryRowTypes.GENDER.equals(type) && !DataEntryRowTypes.BOOLEAN.equals(type)) {
            throw new IllegalArgumentException("Unsupported row type");
        }

        mLabel = label;
        mValue = baseValue;
        mType = type;
    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        View view;
        BooleanRowHolder holder;

        if (convertView != null && convertView.getTag() instanceof BooleanRowHolder) {
            view = convertView;
            holder = (BooleanRowHolder) convertView.getTag();
        } else {
            View root = inflater.inflate(
                    R.layout.listview_row_radio_buttons, container, false);
            TextView label = (TextView)
                    root.findViewById(R.id.text_label);
            CompoundButton firstButton = (CompoundButton)
                    root.findViewById(R.id.first_radio_button);
            CompoundButton secondButton = (CompoundButton)
                    root.findViewById(R.id.second_radio_button);
            CompoundButton thirdButton = (CompoundButton)
                    root.findViewById(R.id.third_radio_button);

            if (DataEntryRowTypes.BOOLEAN.equals(mType)) {
                firstButton.setText(R.string.yes);
                secondButton.setText(R.string.no);
                thirdButton.setText(R.string.none);
            } else if (DataEntryRowTypes.GENDER.equals(mType)) {
                firstButton.setText(R.string.gender_male);
                secondButton.setText(R.string.gender_female);
                thirdButton.setText(R.string.gender_other);
            }

            CheckedChangeListener listener = new CheckedChangeListener();
            holder = new BooleanRowHolder(mType, label, firstButton,
                    secondButton, thirdButton, listener);

            holder.firstButton.setOnCheckedChangeListener(listener);
            holder.secondButton.setOnCheckedChangeListener(listener);
            holder.thirdButton.setOnCheckedChangeListener(listener);

            if(!isEditable())
            {
                holder.firstButton.setEnabled(false);
                holder.secondButton.setEnabled(false);
                holder.thirdButton.setEnabled(false);
            }
            else
            {
                holder.firstButton.setEnabled(true);
                holder.secondButton.setEnabled(true);
                holder.thirdButton.setEnabled(true);
            }


            root.setTag(holder);
            view = root;
        }

        holder.updateViews(mLabel, mValue);
        return view;
    }

    @Override
    public int getViewType() {
        return mType.ordinal();
    }

    @Override
    public BaseValue getBaseValue() {
        return mValue;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    private static class BooleanRowHolder {
        final TextView textLabel;
        final CompoundButton firstButton;
        final CompoundButton secondButton;
        final CompoundButton thirdButton;
        final CheckedChangeListener listener;
        final DataEntryRowTypes type;

        public BooleanRowHolder(DataEntryRowTypes type, TextView textLabel, CompoundButton firstButton,
                                CompoundButton secondButton, CompoundButton thirdButton,
                                CheckedChangeListener listener) {
            this.type = type;
            this.textLabel = textLabel;
            this.firstButton = firstButton;
            this.secondButton = secondButton;
            this.thirdButton = thirdButton;
            this.listener = listener;
        }

        public void updateViews(String label, BaseValue baseValue) {
            textLabel.setText(label);

            listener.setType(type);
            listener.setBaseValue(baseValue);

            String value = baseValue.getValue();
            if (DataEntryRowTypes.BOOLEAN.equals(type)) {
                if (TRUE.equalsIgnoreCase(value)) {
                    firstButton.setChecked(true);
                } else if (FALSE.equalsIgnoreCase(value)) {
                    secondButton.setChecked(true);
                } else if (EMPTY_FIELD.equalsIgnoreCase(value)) {
                    thirdButton.setChecked(true);
                }
            } else if (DataEntryRowTypes.GENDER.equals(type)) {
                if (MALE.equalsIgnoreCase(value)) {
                    firstButton.setChecked(true);
                } else if (FEMALE.equalsIgnoreCase(value)) {
                    secondButton.setChecked(true);
                } else if (OTHER.equalsIgnoreCase(value)) {
                    thirdButton.setChecked(true);
                }
            }
        }
    }

    private static class CheckedChangeListener implements OnCheckedChangeListener {
        private BaseValue value;
        private DataEntryRowTypes type;

        public void setBaseValue(BaseValue baseValue) {
            this.value = baseValue;
        }

        public void setType(DataEntryRowTypes type) {
            this.type = type;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // if one of buttons in group is unchecked, another one has to be checked
            // So we are not interested in events where button is being unchecked
            if (!isChecked) {
                return;
            }
            String newValue = "";
            if (DataEntryRowTypes.BOOLEAN.equals(type)) {
                if (buttonView.getId() == R.id.first_radio_button) {
                    newValue = TRUE;
                } else if (buttonView.getId() == R.id.second_radio_button) {
                    newValue = FALSE;
                } else if (buttonView.getId() == R.id.third_radio_button) {
                    newValue = EMPTY_FIELD;
                }
            }
            if (DataEntryRowTypes.GENDER.equals(type)) {
                if(buttonView.getId() == R.id.first_radio_button) {
                    newValue = MALE;
                } else if (buttonView.getId() == R.id.second_radio_button) {
                    newValue = FEMALE;
                } else if (buttonView.getId() == R.id.third_radio_button ) {
                    newValue = OTHER;
                }
            }

            if(!newValue.toString().equals(value.getValue()))
            {
                value.setValue(newValue);
                Dhis2Application.getEventBus().post(new RowValueChangedEvent(value));
            }

            if (DataEntryRowTypes.BOOLEAN.equals(type)) {
                if(buttonView.getId() == R.id.first_radio_button) {
                    value.setValue(TRUE);
                } else if (buttonView.getId() == R.id.second_radio_button ) {
                    value.setValue(FALSE);
                } else if (buttonView.getId() == R.id.third_radio_button ) {
                    value.setValue(EMPTY_FIELD);
                }
            }

            if (DataEntryRowTypes.GENDER.equals(type)) {
                if(buttonView.getId() == R.id.first_radio_button) {
                    value.setValue(MALE);
                } else if (buttonView.getId() == R.id.second_radio_button) {
                    value.setValue(FEMALE);
                } else if (buttonView.getId() == R.id.third_radio_button ) {
                    value.setValue(OTHER);
                }
            }
        }
    }
}





