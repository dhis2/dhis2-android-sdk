package org.hisp.dhis.client.sdk.ui.views.itemlistrowview;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.R;

import java.util.List;

public class ItemListRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemListRow> itemListRows;


    public ItemListRowAdapter(List<ItemListRow> itemListRows) {
        this.itemListRows = itemListRows;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_list, parent, false);
        return new ItemListRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemListRowViewHolder) {
            ItemListRowViewHolder itemListRowViewHolder = (ItemListRowViewHolder) holder;
            ItemListRow itemListRow= itemListRows.get(position);

            List<Pair<String, Integer>> valuesPosition = itemListRow.getValuesPosition();

            for (int i =0;i<valuesPosition.size();i++) {
                switch (valuesPosition.get(i).second) {
                    case 1: {
                        if(itemListRowViewHolder.firstItem.getText().equals("")) {
                            itemListRowViewHolder.firstItem.setText(valuesPosition.get(i).first + " ");
                        }
                        else {
                            itemListRowViewHolder.firstItem.append(valuesPosition.get(i).first);
                        }
                        break;
                    }
                    case 2: {
                        if(itemListRowViewHolder.secondItem.getText().equals("")) {
                            itemListRowViewHolder.secondItem.setText(valuesPosition.get(i).first + " ");
                        }
                        else {
                            itemListRowViewHolder.secondItem.append(valuesPosition.get(i).first);

                        }
                        break;
                    }
                    case 3: {
                        if(itemListRowViewHolder.thirdItem.getText().equals("")) {
                            itemListRowViewHolder.thirdItem.setText(valuesPosition.get(i).first + " ");
                        }
                        else {
                            itemListRowViewHolder.thirdItem.append(valuesPosition.get(i).first);
                        }
                        break;
                    }
                }

            }
            if(itemListRow.getStatus().equals(EItemListRowStatus.ERROR.toString())) {
                itemListRowViewHolder.statusItem.setImageResource(R.drawable.ic_report);
            }
            else if(itemListRow.getStatus().equals(EItemListRowStatus.OFFLINE.toString())) {
                itemListRowViewHolder.statusItem.setImageResource(R.drawable.ic_save);
            }
            else {
                itemListRowViewHolder.statusItem.setImageResource(R.drawable.ic_check);
            }

            itemListRowViewHolder.itemContainer.setOnClickListener(itemListRow.getOnRowClickListener());
            itemListRowViewHolder.itemContainer.setOnLongClickListener(itemListRow.getOnLongClickListener());

            itemListRowViewHolder.statusContainer.setOnClickListener(itemListRow.getOnStatusClickListener());
        }
    }

    @Override
    public int getItemCount() {
        return itemListRows.size();
    }

}
