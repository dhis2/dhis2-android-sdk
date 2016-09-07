package org.hisp.dhis.android.sdk.ui.dialogs;

import android.content.Context;

import org.hisp.dhis.android.sdk.persistence.loaders.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpcomingEventsDialogFilterQuery implements Query<List<AutoCompleteDialogAdapter.OptionAdapterValue>> {

    public UpcomingEventsDialogFilterQuery() {
    }

    @Override
    public List<AutoCompleteDialogAdapter.OptionAdapterValue> query(Context context) {
        List<AutoCompleteDialogAdapter.OptionAdapterValue> optionAdapterValues = new ArrayList<>();

        List<UpcomingEventsDialogFilter.Type> types = Arrays.asList(UpcomingEventsDialogFilter.Type.values());
        for (int i = 0; i < types.size(); i++) {
            optionAdapterValues.add(new AutoCompleteDialogAdapter.OptionAdapterValue(Integer.toString(i),types.get(i).name()));
        }

        return optionAdapterValues;
    }
}
