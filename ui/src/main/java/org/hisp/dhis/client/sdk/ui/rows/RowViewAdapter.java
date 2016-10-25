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

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityAction;
import org.hisp.dhis.client.sdk.ui.models.FormEntityAction.FormEntityActionType;
import org.hisp.dhis.client.sdk.ui.models.FormEntityCharSequence;
import org.hisp.dhis.client.sdk.ui.models.FormEntityEditText;
import org.hisp.dhis.client.sdk.ui.models.FormEntityFilter;
import org.hisp.dhis.client.sdk.ui.models.Picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class RowViewAdapter extends Adapter<ViewHolder> {
    private final FragmentManager fragmentManager;
    private final List<FormEntity> originalDataEntities;
    private final List<FormEntity> modifiedDataEntities;
    private final List<RowView> rowViews;

    public RowViewAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = isNull(fragmentManager, "fragmentManager must not be null");
        this.rowViews = new ArrayList<>();

        this.originalDataEntities = new ArrayList<>();
        this.modifiedDataEntities = new ArrayList<>();

        assignRowViewsToItemViewTypes();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return rowViews.get(viewType).onCreateViewHolder(
                LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        rowViews.get(holder.getItemViewType()).onBindViewHolder(holder, getItem(position));
    }

    @Override
    public int getItemCount() {
        return modifiedDataEntities.size();
    }

    @Override
    public int getItemViewType(int position) {
        FormEntity formEntity = getItem(position);
        return formEntity != null ? formEntity.getType().ordinal() : -1;
    }

    private void assignRowViewsToItemViewTypes() {
        for (int ordinal = 0; ordinal < FormEntity.Type.values().length; ordinal++) {
            FormEntity.Type dataEntityType = FormEntity.Type.values()[ordinal];
            switch (dataEntityType) {
                case TEXT: {
                    rowViews.add(ordinal, new TextRowView());
                    break;
                }
                case EDITTEXT: {
                    rowViews.add(ordinal, new EditTextRowView());
                    break;
                }
                case CHECKBOX: {
                    rowViews.add(ordinal, new CheckBoxRowView());
                    break;
                }
                case COORDINATES: {
                    rowViews.add(ordinal, new CoordinateRowView());
                    break;
                }
                case RADIO_BUTTONS: {
                    rowViews.add(ordinal, new RadioButtonRowView());
                    break;
                }
                case DATE: {
                    rowViews.add(ordinal, new DatePickerRowView(fragmentManager));
                    break;
                }
                case FILTER: {
                    rowViews.add(ordinal, new FilterableRowView(fragmentManager));
                    break;
                }
                case EXPANSION_PANEL: {
                    rowViews.add(ordinal, new ExpansionPanelRowView());
                    break;
                }
            }
        }
    }


    private void assignRowViewsToDataViewTypes() {
        for (int ordinal = 0; ordinal < FormEntity.Type.values().length; ordinal++) {
            FormEntity.Type dataEntityType = FormEntity.Type.values()[ordinal];
            switch (dataEntityType) {
                case TEXT: {
                    rowViews.add(ordinal, new TextRowView());
                    break;
                }
                case EDITTEXT: {
                    rowViews.add(ordinal, new EditTextRowView());
                    break;
                }
                case CHECKBOX: {
                    rowViews.add(ordinal, new CheckBoxRowView());
                    break;
                }
                case COORDINATES: {
                    rowViews.add(ordinal, new CoordinateRowView());
                    break;
                }
                case RADIO_BUTTONS: {
                    rowViews.add(ordinal, new RadioButtonRowView());
                    break;
                }
                case DATE: {
                    rowViews.add(ordinal, new DatePickerRowView(fragmentManager));
                    break;
                }
                case FILTER: {
                    rowViews.add(ordinal, new FilterableRowView(fragmentManager));
                    break;
                }
                case EXPANSION_PANEL: {
                    rowViews.add(ordinal, new ExpansionPanelRowView());
                    break;
                }
            }
        }
    }

    private FormEntity getItem(int position) {
        return modifiedDataEntities.size() > position ? modifiedDataEntities.get(position) : null;
    }

    public void update(List<FormEntityAction> actions) {
        applyFormEntityActions(actions, true);
    }

    public void swap(List<FormEntity> formEntities) {
        swapData(formEntities, null);
    }

    public void swap(List<FormEntity> formEntities, List<FormEntityAction> actions) {
        swapData(formEntities, actions);
    }

    private void swapData(List<FormEntity> dataEntities, List<FormEntityAction> actions) {
        this.originalDataEntities.clear();
        this.modifiedDataEntities.clear();

        if (dataEntities != null) {
            this.originalDataEntities.addAll(dataEntities);
        }

        // we don't want trigger ui updates during data changes
        applyFormEntityActions(actions, false);

        notifyDataSetChanged();
    }

    // if granularNotificationsEnabled is set to true, method will trigger changes in UI,
    // otherwise it will only modify underlying data
    private void applyFormEntityActions(
            List<FormEntityAction> formEntityActions, boolean granularUiUpdatesEnabled) {

        // apply rule effects before rendering list
        Map<String, FormEntityAction> actionMap = mapActions(formEntityActions);

        // applying FormEntityActions. Note, methods below will gradually
        // mutate list of form entities. So it is important to preserve, order of calls
        applyHideFormEntityActions(actionMap, granularUiUpdatesEnabled);
        applyAssignFormEntityActions(actionMap, granularUiUpdatesEnabled);
    }

    private void applyHideFormEntityActions(
            Map<String, FormEntityAction> actionMap, boolean granularUiUpdatesEnabled) {

        List<FormEntity> activeFormEntities = distinctActiveFormEntities(actionMap);
        List<FormEntity> previousDataEntities = new ArrayList<>(modifiedDataEntities);

        modifiedDataEntities.clear();
        modifiedDataEntities.addAll(activeFormEntities);

        // we should have at least one entity
        if (!previousDataEntities.isEmpty()) {
            int currentFormEntityPosition = 0;

            while (currentFormEntityPosition < previousDataEntities.size()) {
                FormEntity formEntity = previousDataEntities.get(currentFormEntityPosition);

                if (activeFormEntities.indexOf(formEntity) < 0) {

                    if (granularUiUpdatesEnabled) {
                        // updating recycler view
                        notifyItemRemoved(currentFormEntityPosition);
                    }

                    // removing corresponding model from list
                    previousDataEntities.remove(currentFormEntityPosition);

                    // nullifying value in entity
                    if (formEntity instanceof FormEntityCharSequence) {
                        ((FormEntityCharSequence) formEntity).setValue("", false);
                    } else if (formEntity instanceof FormEntityFilter) {
                        Picker picker = ((FormEntityFilter) formEntity).getPicker();

                        if (picker != null) {
                            picker.setSelectedChild(null);
                            ((FormEntityFilter) formEntity).setPicker(picker);
                        }
                    }
                } else {
                    currentFormEntityPosition = currentFormEntityPosition + 1;
                }
            }
        }

        for (int index = 0; index < activeFormEntities.size(); index++) {
            FormEntity formEntity = activeFormEntities.get(index);

            if (granularUiUpdatesEnabled &&
                    previousDataEntities.indexOf(formEntity) < 0) {
                notifyItemInserted(index);
            }
        }
    }

    private void applyAssignFormEntityActions(
            Map<String, FormEntityAction> actionMap, boolean granularUiUpdatesEnabled) {

        // go through all existing form entities
        for (FormEntity formEntity : originalDataEntities) {
            if (!(formEntity instanceof FormEntityEditText)) {
                continue;
            }

            FormEntityAction entityAction = actionMap.get(formEntity.getId());
            FormEntityEditText formEntityEditText = (FormEntityEditText) formEntity;

            if (entityAction == null ||
                    !FormEntityActionType.ASSIGN.equals(entityAction.getActionType())) {
                // if the field previously was
                // locked, we need to unlock it
                if (formEntityEditText.isLocked()) {
                    formEntityEditText.setLocked(false);

                    int indexOfVisibleEntity = modifiedDataEntities.indexOf(formEntity);
                    if (granularUiUpdatesEnabled && !(indexOfVisibleEntity < 0)) {
                        notifyItemChanged(indexOfVisibleEntity);
                    }
                }
            } else {
                // assigning new value
                formEntityEditText.setValue(entityAction.getValue(), false);

                // conditionally updating ui
                if (!formEntityEditText.isLocked()) {
                    formEntityEditText.setLocked(true);
                }

                int indexOfVisibleEntity = modifiedDataEntities.indexOf(formEntity);
                if (granularUiUpdatesEnabled && !(indexOfVisibleEntity < 0)) {
                    notifyItemChanged(indexOfVisibleEntity);
                }
            }
        }
    }

    // we need to filter out entities which should be hidden from user
    private List<FormEntity> distinctActiveFormEntities(Map<String, FormEntityAction> actionMap) {
        List<FormEntity> activeDataEntities = new ArrayList<>();

        for (FormEntity originalDataEntity : originalDataEntities) {
            FormEntityAction formEntityAction = actionMap.get(originalDataEntity.getId());

            if (formEntityAction == null || !FormEntityActionType.HIDE
                    .equals(formEntityAction.getActionType())) {

                // we need to show item
                activeDataEntities.add(originalDataEntity);
            }
        }

        return activeDataEntities;
    }

    private static Map<String, FormEntityAction> mapActions(List<FormEntityAction> actions) {
        Map<String, FormEntityAction> formEntityActionMap = new HashMap<>();

        if (actions != null && !actions.isEmpty()) {
            for (FormEntityAction action : actions) {
                formEntityActionMap.put(action.getId(), action);
            }
        }

        return formEntityActionMap;
    }
}
