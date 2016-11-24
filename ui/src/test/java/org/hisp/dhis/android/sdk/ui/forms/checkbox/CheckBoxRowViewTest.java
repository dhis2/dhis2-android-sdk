/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.ui.forms.checkbox;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.ui.R;
import org.hisp.dhis.android.sdk.ui.forms.checkbox.CheckBoxRowView;
import org.hisp.dhis.android.sdk.ui.forms.checkbox.FormEntityCheckBox;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckBoxRowViewTest {

    private static final String TRUE = "true";
    private static final String EMPTY_FIELD = "";

    private CheckBoxRowView checkBoxRowView;

    @Mock
    private CheckBox checkBoxView;

    @Mock
    private TextView textViewLabel;

    private RecyclerView.ViewHolder viewHolder;

    @SuppressLint("InflateParams")
    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        checkBoxRowView = new CheckBoxRowView();

        LayoutInflater inflater = mock(LayoutInflater.class);
        View itemView = mock(View.class);

        when(inflater.inflate(
                R.layout.recyclerview_row_checkbox, null, false)).thenReturn(itemView);

        when(itemView.findViewById(R.id.checkbox_row_checkbox)).thenReturn(checkBoxView);
        when(itemView.findViewById(R.id.textview_row_label)).thenReturn(textViewLabel);

        viewHolder = checkBoxRowView.onCreateViewHolder(inflater, null);
    }

    @Test
    public void onBindViewHolder_valuesAreSet() throws Exception {

        String label = "test_label";
        FormEntityCheckBox formEntityCheckBox = new FormEntityCheckBox("test_id", label);
        formEntityCheckBox.setValue(TRUE); // value set to true

        checkBoxRowView.onBindViewHolder(viewHolder, formEntityCheckBox);

        verify(textViewLabel).setText(label);
        verify(checkBoxView).setChecked(true); // check that value is set to true
    }

    @Test
    public void onBindViewHolder_viewIsUpdated() throws Exception {

        String updatedLabel = "updated_test_label"; // new label

        FormEntityCheckBox formEntityCheckBox = new FormEntityCheckBox("test_id", updatedLabel);
        formEntityCheckBox.setValue(EMPTY_FIELD); // value set to EMPTY_FIELD (false)

        checkBoxRowView.onBindViewHolder(viewHolder, formEntityCheckBox);

        verify(textViewLabel).setText(updatedLabel);  // check that new label is set
        verify(checkBoxView).setChecked(false); // check that value is set to false
    }
}