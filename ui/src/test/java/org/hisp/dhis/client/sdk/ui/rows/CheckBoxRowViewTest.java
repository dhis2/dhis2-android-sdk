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

package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntityCheckBox;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckBoxRowViewTest {

    private static final String TRUE = "true";
    private static final String EMPTY_FIELD = "";

    private String label = "test_label";

    private CheckBoxRowView checkBoxRowView;

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private View itemView;

    @Mock
    private CheckBox checkBoxView;

    @Mock
    private TextView textViewLabel;

    private RecyclerView.ViewHolder viewHolder;
    private FormEntityCheckBox formEntityCheckBox;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        checkBoxRowView = new CheckBoxRowView();

        when(layoutInflater.inflate(
                R.layout.recyclerview_row_checkbox, null, false)).thenReturn(itemView);

        when(itemView.findViewById(R.id.checkbox_row_checkbox)).thenReturn(checkBoxView);
        when(itemView.findViewById(R.id.textview_row_label)).thenReturn(textViewLabel);

        viewHolder = checkBoxRowView.onCreateViewHolder(layoutInflater, null);

        String id = "test_id";
        formEntityCheckBox = new FormEntityCheckBox(id, label);
        formEntityCheckBox.setValue(TRUE, false);
    }

    @Test
    public void onBindViewHolder_valuesAreSet() throws Exception {
        checkBoxRowView.onBindViewHolder(viewHolder, formEntityCheckBox);
        verify(textViewLabel).setText(label);
        verify(checkBoxView).setChecked(true);
    }

    @Test
    public void onBindViewHolder_viewIsUpdated() throws Exception {
        formEntityCheckBox.setValue(EMPTY_FIELD, false);
        checkBoxRowView.onBindViewHolder(viewHolder, formEntityCheckBox);
        verify(checkBoxView).setChecked(false);
    }
}