package org.hisp.dhis.client.sdk.ui.adapters.expandable;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ExpansionPanel;

public class ExpansionPanelViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private final ImageButton expandCollapseButton;
    private final TextView title;
    private final View divider;

    public ExpansionPanelViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
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
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            }
        });

    }

    public void bind(ExpansionPanel expansionPanel) {
        title.setText(expansionPanel.getLabel());
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
