package org.hisp.dhis.client.sdk.ui.adapters.expandable;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ReportEntity;
import org.hisp.dhis.client.sdk.ui.views.CircleView;

public class ReportEntityChildViewHolder<C> extends ChildViewHolder<C> {

    // Event map keys:
    public static final String EVENT_DATE_KEY = "eventDate";
    public static final String EVENT_STATUS = "status";
    public static final String EVENT_DATE_LABEL = "Event date";
    public static final String STATUS_LABEL = "SyncStatus";
    public static final String ORG_UNIT = "OrgUnit";

    final ImageView statusIcon;
    final CircleView statusBackground;
    final TextView label;
    final TextView date;
    final ImageButton syncButton;

    final Drawable drawableActive;
    final Drawable drawableCompleted;
    final Drawable drawableSkipped;
    final Drawable drawableSchedule;

    final int colorGreen;
    final int colorOrange;
    final int colorRed;

    public ReportEntityChildViewHolder(View itemView) {
        super(itemView);

        statusIcon = (ImageView) itemView.findViewById(R.id.status_icon);
        statusBackground = (CircleView) itemView.findViewById(R.id.circleview_status_background);
        syncButton = (ImageButton) itemView.findViewById(R.id.refresh_button);
        label = (TextView) itemView.findViewById(R.id.event_name);
        date = (TextView) itemView.findViewById(R.id.date_text);

        drawableActive = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_tick);
        drawableCompleted = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_double_tick);
        drawableSchedule = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_chevron_right);
        drawableSkipped = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_cancel_white);

        colorGreen = ContextCompat.getColor(itemView.getContext(), R.color.color_material_green_default);
        colorOrange = ContextCompat.getColor(itemView.getContext(), R.color.color_accent_default);
        colorRed = ContextCompat.getColor(itemView.getContext(), R.color.color_material_red_default);
    }

    public void bind(ReportEntity reportEntity) {
        label.setText(reportEntity.getId());

        //Map<String, String> dataElementToValueMap =
        //dataElementToValueMap.put(Event.EVENT_STATUS, event.getSyncStatus().toString());

        date.setText(reportEntity.getValueForDataElement(EVENT_DATE_KEY));

        switch (reportEntity.getSyncStatus()) {
            // TODO: show deleteButton for all statuses when deletion is supported in SDK
            case SENT: {
                break;
            }
            case TO_POST: {
                syncButton.setImageResource(R.drawable.ic_refresh_gray);
                syncButton.setVisibility(View.VISIBLE);
                syncButton.setClickable(true);
                break;
            }
            case TO_UPDATE: {
                syncButton.setImageResource(R.drawable.ic_refresh_gray);
                syncButton.setVisibility(View.VISIBLE);
                syncButton.setClickable(true);
                break;
            }
            case ERROR: {
                //errorView.setVisibility(View.VISIBLE);
                syncButton.setImageResource(R.drawable.ic_sync_problem_black);
                syncButton.setVisibility(View.VISIBLE);
                syncButton.setClickable(true);
                //syncButton.setBackgroundColor(colorRed);
                break;
            }
        }

        String status = reportEntity.getValueForDataElement(EVENT_STATUS);
        switch (status) {
            case "ACTIVE":
                statusBackground.setFillColor(colorGreen);
                statusIcon.setImageDrawable(drawableActive);
                break;
            case "COMPLETED":
                statusBackground.setFillColor(colorRed);
                statusIcon.setImageDrawable(drawableCompleted);
                break;
            case "SCHEDULE":
                statusBackground.setFillColor(colorOrange);
                statusIcon.setImageDrawable(drawableSchedule);
                break;
            case "SKIPPED":
                statusBackground.setFillColor(colorRed);
                statusIcon.setImageDrawable(drawableSkipped);
                break;
        }
        //label.setText(reportEntity.getValueForDataElement(ORG_UNIT));

    }
}
