package org.hisp.dhis.android.sdk.ui.dialogs;

import org.hisp.dhis.android.sdk.events.OnRowClick;
import org.hisp.dhis.android.sdk.persistence.models.BaseSerializableModel;

import java.util.List;

/**
 * Created by Simen S. Russnes on 9/7/15.
 */
class ItemStatusDialogFragmentForm
{
    private BaseSerializableModel item;
    private List<String> values;
    private OnRowClick.ITEM_STATUS status;
    private String type;

    public BaseSerializableModel getItem() {
        return item;
    }

    public void setItem(BaseSerializableModel item) {
        this.item = item;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public OnRowClick.ITEM_STATUS getStatus() {
        return status;
    }

    public void setStatus(OnRowClick.ITEM_STATUS status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
