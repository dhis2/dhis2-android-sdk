package org.hisp.dhis.client.sdk.ui.views.itemlistrowview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.FontTextView;

public class ItemListRowViewHolder extends RecyclerView.ViewHolder {
    LinearLayout itemContainer;
    LinearLayout statusContainer;
    FontTextView firstItem;
    FontTextView secondItem;
    FontTextView thirdItem;
    ImageView statusItem;

    public ItemListRowViewHolder(View itemView) {
        super(itemView);
        itemContainer = (LinearLayout) itemView.findViewById(R.id.llItemContainer);
        statusContainer = (LinearLayout) itemView.findViewById(R.id.llStatusContainer);
        firstItem = (FontTextView) itemView.findViewById(R.id.tvFirstItem);
        secondItem = (FontTextView) itemView.findViewById(R.id.tvSecondItem);
        thirdItem = (FontTextView) itemView.findViewById(R.id.tvThirdItem);
        statusItem = (ImageView) itemView.findViewById(R.id.ivStatus);
    }
}
