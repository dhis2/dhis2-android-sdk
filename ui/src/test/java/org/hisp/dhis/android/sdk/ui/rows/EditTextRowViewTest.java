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

package org.hisp.dhis.android.sdk.ui.rows;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.ui.R;
import org.hisp.dhis.android.sdk.ui.models.edittext.FormEntityEditText;
import org.hisp.dhis.android.sdk.ui.models.edittext.FormEntityLongEditText;
import org.hisp.dhis.android.sdk.ui.models.edittext.FormEntityShortEditText;
import org.hisp.dhis.android.sdk.ui.views.FontTextInputEditText;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hisp.dhis.android.sdk.ui.models.edittext.FormEntityEditText.LONG_TEXT_LINE_COUNT;
import static org.hisp.dhis.android.sdk.ui.models.edittext.FormEntityEditText.SHORT_TEXT_LINE_COUNT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditTextRowViewTest {

    private EditTextRowView editTextRowView;

    @Mock
    private TextInputLayout textInputLayout;

    @Mock
    private FontTextInputEditText editText;

    @Mock
    private TextView textViewLabel;

    @Mock
    private Context context;

    private RecyclerView.ViewHolder viewHolder;

    @SuppressLint("InflateParams")
    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        editTextRowView = new EditTextRowView();

        LayoutInflater inflater = mock(LayoutInflater.class);
        View itemView = mock(View.class);

        when(inflater.inflate(
                R.layout.recyclerview_row_edittext, null, false)).thenReturn(itemView);

        when(itemView.findViewById(R.id.edittext_row_textinputlayout)).thenReturn(textInputLayout);
        when(itemView.findViewById(R.id.edittext_row_edittext)).thenReturn(editText);
        when(itemView.findViewById(R.id.textview_row_label)).thenReturn(textViewLabel);
        when(itemView.getContext()).thenReturn(context);

        when(context.getString(R.string.enter_text)).thenReturn("hint text");

        viewHolder = editTextRowView.onCreateViewHolder(inflater, null);
    }

    @Test
    public void onBindViewHolder_valuesAreSet() throws Exception {

        String label = "test_label";
        String value = "test_value";
        FormEntityEditText formEntity = new FormEntityShortEditText("test_id", label);
        formEntity.setValue(value, false);
        when(editText.getText()).thenReturn(new SpannableStringBuilder(value));

        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(textViewLabel).setText(label);
        verify(editText).setText(value);
        verify(editText).setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        verify(editText).setMaxLines(SHORT_TEXT_LINE_COUNT);

        formEntity = new FormEntityLongEditText("test_id", label);
        editTextRowView.onBindViewHolder(viewHolder, formEntity);
    }

    @Test
    public void onBindViewHolder_viewIsUpdated() throws Exception {

        String label = "updated_test_label";
        String value = "updated_test_value";
        FormEntityEditText formEntity = new FormEntityLongEditText("test_id", label);
        formEntity.setValue(value, false);
        when(editText.getText()).thenReturn(new SpannableStringBuilder(value));

        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(textViewLabel).setText(label);
        verify(editText).setText(value);
        verify(editText).setInputType(formEntity.getAndroidInputType());
        verify(editText).setMaxLines(LONG_TEXT_LINE_COUNT); // FormEntity is now a FormEntityLongEditText
    }

}