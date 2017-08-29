package org.hisp.dhis.android.sdk.synchronization.data.faileditem;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem$Table;

public class FailedItemRepository implements IFailedItemRepository {

    @Override
    public void save(FailedItem failedItem) {
        failedItem.save();
    }

    @Override
    public void delete(String type, long id) {
        FailedItem item = getFailedItem(type, id);
        if (item != null) {
            item.async().delete();
        }
    }

    private FailedItem getFailedItem(String type, long id) {
        return new Select().from(FailedItem.class).where(
                Condition.column(FailedItem$Table.ITEMTYPE).is(type),
                Condition.column(FailedItem$Table.ITEMID).is(id)).querySingle();

    }


}
