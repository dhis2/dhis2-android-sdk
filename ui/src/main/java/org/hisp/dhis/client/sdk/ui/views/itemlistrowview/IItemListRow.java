package org.hisp.dhis.client.sdk.ui.views.itemlistrowview;

import android.support.v4.util.Pair;
import android.view.View;

import java.util.List;

public interface IItemListRow {
    String getStatus();

    void setStatus(String status);

    List<Pair<String, Integer>> getValuesPosition();

    void setValuesPosition(List<Pair<String, Integer>> valuesPosition);

    View.OnClickListener getOnRowClickListener();

    void setOnRowClickListener(View.OnClickListener onRowClickListener);

    View.OnClickListener getOnStatusClickListener();

    void setOnStatusClickListener(View.OnClickListener onStatusClickListener);

    View.OnLongClickListener getOnLongClickListener();

    void setOnLongClickListener(View.OnLongClickListener onLongClickListener);
}
