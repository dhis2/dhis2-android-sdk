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
import org.hisp.dhis.client.sdk.ui.models.FormEntityCharSequence;
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
            }
        }
    }

    private FormEntity getItem(int position) {
        return modifiedDataEntities.size() > position ? modifiedDataEntities.get(position) : null;
    }

    public void update(List<FormEntityAction> actions) {
        Map<String, FormEntityAction> actionMap = mapActions(actions);
        List<FormEntity> updatedDataEntities = new ArrayList<>();

        for (FormEntity originalDataEntity : originalDataEntities) {
            FormEntityAction formEntityAction = actionMap.get(originalDataEntity.getId());

            if (formEntityAction == null) {
                // we need to show item
                updatedDataEntities.add(originalDataEntity);
                continue;
            }

            switch (formEntityAction.getActionType()) {
                case HIDE: {
                    // ignore field
                    continue;
                }
                case ASSIGN: {

                    // do something
                    break;
                }
            }

            updatedDataEntities.add(originalDataEntity);
        }

        List<FormEntity> oldDataEntities = new ArrayList<>(modifiedDataEntities);

        modifiedDataEntities.clear();
        modifiedDataEntities.addAll(updatedDataEntities);

        // we should have at least one entity
        if (!oldDataEntities.isEmpty()) {
            int currentFormEntityPosition = 0;

            while (currentFormEntityPosition < oldDataEntities.size()) {
                FormEntity formEntity = oldDataEntities.get(currentFormEntityPosition);

                if (updatedDataEntities.indexOf(formEntity) < 0) {
                    // updating recycler view
                    notifyItemRemoved(currentFormEntityPosition);

                    // removing corresponding model from lsit
                    oldDataEntities.remove(currentFormEntityPosition);

                    // nullifying value in entity
                    if (formEntity instanceof FormEntityCharSequence) {
                        ((FormEntityCharSequence) formEntity).setValue("");
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

        for (int index = 0; index < updatedDataEntities.size(); index++) {
            FormEntity formEntity = updatedDataEntities.get(index);

            if (oldDataEntities.indexOf(formEntity) < 0) {
                notifyItemInserted(index);
            }
        }
    }

    public void swap(List<FormEntity> formEntities) {
        swapData(formEntities, null);
    }

    public void swap(List<FormEntity> formEntities, List<FormEntityAction> actions) {
        swapData(formEntities, actions);
    }

    private void swapData(List<FormEntity> dataEntities, List<FormEntityAction> actions) {
        this.originalDataEntities.clear();

        if (dataEntities != null) {
            this.originalDataEntities.addAll(dataEntities);
        }

        // apply rule effects before rendering list
        Map<String, FormEntityAction> actionMap = mapActions(actions);
        Map<String, FormEntity> formEntityMap = mapFormEntities(originalDataEntities);
        for (FormEntity dataEntity : originalDataEntities) {
            FormEntityAction action = actionMap.get(dataEntity.getId());

            if (action != null) {
                switch (action.getActionType()) {
                    case HIDE: {
                        // we don't want to include form entity in this case
                        continue;
                    }
                    case ASSIGN: {
                        System.out.println("Assign action: " + action);

                        FormEntity formEntity = formEntityMap.get(action.getId());
                        if (formEntity instanceof FormEntityCharSequence) {
                            ((FormEntityCharSequence) formEntity).setValue(action.getValue());
                        }
                        break;
                    }
                }

                modifiedDataEntities.add(dataEntity);
            } else {
                modifiedDataEntities.add(dataEntity);
            }
        }

        notifyDataSetChanged();
    }

    private Map<String, FormEntityAction> mapActions(List<FormEntityAction> actions) {
        Map<String, FormEntityAction> formEntityActionMap = new HashMap<>();

        if (actions != null && !actions.isEmpty()) {
            for (FormEntityAction action : actions) {
                formEntityActionMap.put(action.getId(), action);
            }
        }

        return formEntityActionMap;
    }

    private Map<String, FormEntity> mapFormEntities(List<FormEntity> formEntities) {
        Map<String, FormEntity> formEntityMap = new HashMap<>();

        if (formEntities != null && !formEntities.isEmpty()) {
            for (FormEntity formEntity : formEntities) {
                formEntityMap.put(formEntity.getId(), formEntity);
            }
        }

        return formEntityMap;
    }
}
