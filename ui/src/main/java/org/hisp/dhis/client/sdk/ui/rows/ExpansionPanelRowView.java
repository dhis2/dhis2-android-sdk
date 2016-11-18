package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;

public class ExpansionPanelRowView implements RowView {

    public ExpansionPanelRowView() {
        // explicit empty constructor
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ExpansionPanelRowView.ExpansionPanelRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_expansion_panel, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, FormEntity formEntity) {
        //ExpansionPanel entity = (ExpansionPanel) formEntity;
        //((ExpansionPanelRowView.ExpansionPanelRowViewHolder) viewHolder).update(entity);
    }

    private static class ExpansionPanelRowViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewLabel;
        public final ImageButton actionButton;
        public final ImageButton expandCollapseButton;

        public ExpansionPanelRowViewHolder(View itemView) {
            super(itemView);

            textViewLabel = (TextView) itemView
                    .findViewById(R.id.title);
            actionButton = (ImageButton) itemView.findViewById(R.id.action_button);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), textViewLabel.getText(), Toast.LENGTH_SHORT).show();
                }
            });
            expandCollapseButton = (ImageButton) itemView.findViewById(R.id.expand_collapse_button);

            View.OnClickListener expandCollapseClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExpanded()) {
                        collapse(v);
                    } else {
                        expand(v);
                    }
                }

                private void expand(View v) {
                    expandCollapseButton.setTag(true);
                    expandCollapseButton.setImageResource(R.drawable.ic_expand);
                    Toast.makeText(v.getContext(), "Collapse " + textViewLabel.getText(), Toast.LENGTH_SHORT).show();
                }

                private void collapse(View v) {
                    expandCollapseButton.setTag(false);
                    expandCollapseButton.setImageResource(R.drawable.ic_collapse);
                    Toast.makeText(v.getContext(), "Expand " + textViewLabel.getText(), Toast.LENGTH_SHORT).show();
                }

                private boolean isExpanded() {
                    return expandCollapseButton.getTag() != null && expandCollapseButton.getTag() instanceof Boolean && (Boolean) expandCollapseButton.getTag();
                }
            };

            expandCollapseButton.setOnClickListener(expandCollapseClickListener);
            itemView.setOnClickListener(expandCollapseClickListener);
        }

        public void update(FormEntity entity) {
            textViewLabel.setText(entity.getLabel());
            configureView(entity);
        }

        private boolean configureView(FormEntity formEntity) {
            // todo check if repeatable or not and change action button icon from add to edit
            return true;
        }
    }
}
