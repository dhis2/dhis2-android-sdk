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

package org.hisp.dhis.client.sdk.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.flexbox.FlexboxLayout;

import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.models.Picker;

public class QuickSelectionContainer extends FlexboxLayout implements View.OnClickListener {

    private FormEntityFilter formEntityFilter;
    private boolean locked;

    public QuickSelectionContainer(Context context) {
        super(context);
        init();
    }

    public QuickSelectionContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuickSelectionContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFlexWrap(FLEX_WRAP_WRAP);
    }

    public void setFormEntityFilter(FormEntityFilter formEntityFilter) {
        this.formEntityFilter = formEntityFilter;
        refresh();
    }

    public void refresh() {
        if (getVisibility() != VISIBLE) {
            setVisibility(View.VISIBLE);
        }

        drawQuickSelectionItems();
    }

    private void drawQuickSelectionItems() {

        if (formEntityFilter.getPicker() == null) {
            hide();
            return;
        }

        for (int i = 0; i < formEntityFilter.getPicker().getChildren().size(); i++) {
            Picker item = formEntityFilter.getPicker().getChildren().get(i);

            final QuickSelectionItemView quickSelectionItemView = getQuickSelectionItemView(i);
            quickSelectionItemView.setItem(item);
            boolean selectedState = isSelected(item);
            quickSelectionItemView.setSelected(selectedState);
        }

        hideUnusedViews();
    }

    private void hideUnusedViews() {
        if (formEntityFilter.getPicker().getChildren().size() < getChildCount()) {
            int numberOfViews = getChildCount();
            for (int i = numberOfViews - 1; i >= formEntityFilter.getPicker().getChildren().size(); i--) {
                getChildAt(i).setVisibility(GONE);
            }
        }
    }

    private boolean isSelected(Picker item) {
        return formEntityFilter.getPicker().getSelectedChild() != null && formEntityFilter.getPicker().getSelectedChild().equals(item);
    }

    private QuickSelectionItemView getQuickSelectionItemView(int i) {
        View view = getChildAt(i);
        if (view == null) {
            QuickSelectionItemView quickSelectionItemView = new QuickSelectionItemView(getContext());
            quickSelectionItemView.setOnClickListener(this);
            addView(quickSelectionItemView);
            return quickSelectionItemView;
        } else {
            view.setVisibility(VISIBLE);
            return (QuickSelectionItemView) view;
        }
    }

    private void deselectAllItems() {
        for (int i = 0; i < getChildCount(); i++) {
            QuickSelectionItemView unselectedView = (QuickSelectionItemView) getChildAt(i);
            unselectedView.setSelected(false);
        }
    }

    public void hide() {
        setVisibility(GONE);
    }

    /*
     *  Handles click events on quick selection Items
     * */
    @Override
    public void onClick(View view) {

        if (formEntityFilter.isLocked()) {
            return;
        }

        // TODO: animate view to selected state (and do not use selectable item background)
        // TODO: see https://jira.dhis2.org/browse/ANDRODHIS2-5
        QuickSelectionItemView quickSelectionItemView = (QuickSelectionItemView) view;

        if (quickSelectionItemView.isSelected()) {
            formEntityFilter.getPicker().setSelectedChild(null);
        } else {
            deselectAllItems();
            formEntityFilter.getPicker().setSelectedChild(quickSelectionItemView.getItem());
        }

        quickSelectionItemView.toggleSelectionState();

        // using this hack in order to trigger listener in formEntityFilter
        formEntityFilter.setPicker(formEntityFilter.getPicker());
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }
}
