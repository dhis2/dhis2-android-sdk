package org.hisp.dhis.client.sdk.ui.adapters.expandable;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ExpansionPanel;

public class ExpansionPanelViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private final ImageButton expandCollapseButton;
    private final TextView title;
    private final ImageButton actionButton;
    private final View divider;

    public ExpansionPanelViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        actionButton = (ImageButton) itemView.findViewById(R.id.action_button);
        expandCollapseButton = (ImageButton) itemView.findViewById(R.id.expand_collapse_button);
        divider = itemView.findViewById(R.id.divider);

        expandCollapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionButton.performClick();
            }
        });
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Do action", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bind(ExpansionPanel expansionPanel) {
        title.setText(expansionPanel.getLabel());

        switch (expansionPanel.getType()) {
            case ACTION_ADD:
                actionButton.setImageResource(R.drawable.ic_add_black);
                break;
            case ACTION_EDIT:
                actionButton.setImageResource(R.drawable.ic_edit_black);
                break;
            case ACTION_NONE:
                actionButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        RotateAnimation rotateAnimation;
        if (expanded) { // rotate clockwise
            divider.setVisibility(View.VISIBLE);
            rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        } else { // rotate counterclockwise
            divider.setVisibility(View.GONE);
            rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        }

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        expandCollapseButton.startAnimation(rotateAnimation);
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
        return false;
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (expanded) {
            divider.setVisibility(View.GONE);
            expandCollapseButton.setRotation(ROTATED_POSITION);
        } else {
            divider.setVisibility(View.VISIBLE);
            expandCollapseButton.setRotation(INITIAL_POSITION);
        }
    }
}
