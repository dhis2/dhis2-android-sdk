package org.hisp.dhis.android.sdk.utils.ui.dialogs;

import android.content.Context;

import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis.android.sdk.events.OnRowClick;
import org.hisp.dhis.android.sdk.persistence.loaders.Query;
import org.hisp.dhis.android.sdk.persistence.models.BaseSerializableModel;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;

/**
 * Created by Simen S. Russnes on 7/9/15.
 */
public class ItemStatusDialogFragmentQuery implements Query<ItemStatusDialogFragmentForm>
{
    public static final String TAG = ItemStatusDialogFragmentQuery.class.getSimpleName();
    private long id;
    private String type;


    public ItemStatusDialogFragmentQuery(long id, String type)
    {
        this.id = id;
        this.type = type;
    }

    @Override
    public ItemStatusDialogFragmentForm query(Context context)
    {
        BaseSerializableModel item = null;
        switch (type) {
            case FailedItem.TRACKEDENTITYINSTANCE: {
                item = DataValueController.getTrackedEntityInstance(id);
                break;
            }
            case FailedItem.ENROLLMENT: {
                item = DataValueController.getEnrollment(id);
                break;
            }
            case FailedItem.EVENT: {
                item = DataValueController.getEvent(id);
                break;
            }
        }
        ItemStatusDialogFragmentForm form = new ItemStatusDialogFragmentForm();
        form.setItem(item);
        form.setType(type);

        if(item == null) {
            return form;
        }

        boolean failed = false;
        if(DataValueController.getFailedItem(type, id) != null) {
            failed = true;
        }

        if (item.isFromServer()) {
            form.setStatus(OnRowClick.ITEM_STATUS.SENT);
        } else if (failed) {
            form.setStatus(OnRowClick.ITEM_STATUS.ERROR);
        } else {
            form.setStatus(OnRowClick.ITEM_STATUS.OFFLINE);
        }
        return form;
    }
}
