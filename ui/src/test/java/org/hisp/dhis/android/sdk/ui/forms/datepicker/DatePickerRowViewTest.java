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

package org.hisp.dhis.android.sdk.ui.forms.datepicker;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.ui.R;
import org.hisp.dhis.android.sdk.ui.common.RaisedButton;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DatePickerRowViewTest {

    private DatePickerRowView datePickerRowView;

    @Mock
    private EditText editText;

    @Mock
    private TextView textViewLabel;

    private RecyclerView.ViewHolder viewHolder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FragmentManager fragmentManager = mock(FragmentManager.class);
        datePickerRowView = new DatePickerRowView(fragmentManager);

        LayoutInflater inflater = mock(LayoutInflater.class);
        View itemView = mock(View.class);

        when(inflater.inflate(
                R.layout.recyclerview_row_datepicker, null, false)).thenReturn(itemView);

        when(itemView.findViewById(R.id.row_date_picker_edit_text)).thenReturn(editText);
        when(itemView.findViewById(R.id.textview_row_label)).thenReturn(textViewLabel);

        // returning mock buttons avoids null-pointer exceptions when setting onClickListeners in DatePickerRowView.onCreateViewHolder
        when(itemView
                .findViewById(R.id.button_clear)).thenReturn(mock(ImageButton.class));
        when(itemView
                .findViewById(R.id.row_date_picker_button_today)).thenReturn(mock(RaisedButton.class));
        when(itemView
                .findViewById(R.id.row_date_picker_button_pick)).thenReturn(mock(RaisedButton.class));

        viewHolder = datePickerRowView.onCreateViewHolder(inflater, null);
    }

    @Test
    public void onBindViewHolder_valuesAreSet() throws Exception {

        String label = "test_label";
        String value = "2016-01-01";

        FormEntityDate formEntityDate = new FormEntityDate("test_id", label);
        formEntityDate.setValue(value);

        datePickerRowView.onBindViewHolder(viewHolder, formEntityDate);

        verify(textViewLabel).setText(label);
        verify(editText).setText(value);

    }
}