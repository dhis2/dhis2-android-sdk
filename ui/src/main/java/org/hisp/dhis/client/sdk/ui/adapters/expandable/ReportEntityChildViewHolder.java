package org.hisp.dhis.client.sdk.ui.adapters.expandable;

import android.content.Context;
import android.graphics.Color;
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

    final CircleView statusBackground;
    final TextView label;
    final TextView date;
    final TextView eventStatusText;
    final ImageButton syncButton;

    final int colorGreen;
    final int colorOrange;
    final int colorRed;

    final Context context;

    public ReportEntityChildViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext();
        statusBackground = (CircleView) itemView.findViewById(R.id.circleview_status_background);
        syncButton = (ImageButton) itemView.findViewById(R.id.refresh_button);
        label = (TextView) itemView.findViewById(R.id.event_name);
        date = (TextView) itemView.findViewById(R.id.date_text);
        eventStatusText = (TextView) itemView.findViewById(R.id.status_text);

        colorGreen = ContextCompat.getColor(itemView.getContext(), R.color.color_material_green_default);
        colorOrange = ContextCompat.getColor(itemView.getContext(), R.color.color_accent_default);
        colorRed = ContextCompat.getColor(itemView.getContext(), R.color.color_material_red_default);
    }

    public void bind(ReportEntity reportEntity) {
        label.setText(reportEntity.getId());

        //Map<String, String> dataElementToValueMap =
        //dataElementToValueMap.put(Event.EVENT_STATUS, event.getSyncStatus().toString());

        date.setText(reportEntity.getValueForDataElement(EVENT_DATE_KEY));

        //Display the EventSyncStatus:
        switch (reportEntity.getSyncStatus()) {
            case SENT: { //no-op.
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
                break;
            }
        }

        //Display the event status
        String status = reportEntity.getValueForDataElement(EVENT_STATUS);
        switch (status) {
            case "ACTIVE":
                eventStatusText.setText(context.getString(R.string.active));
                eventStatusText.setTextColor(Color.BLACK);
                break;
            case "COMPLETED":
                eventStatusText.setText(context.getString(R.string.completed));
                eventStatusText.setTextColor(colorGreen);
                break;
            case "SCHEDULED":
                eventStatusText.setText(context.getString(R.string.scheduled));
                eventStatusText.setTextColor(colorOrange);
                break;
            case "SKIPPED":
                eventStatusText.setText(context.getString(R.string.skipped));
                eventStatusText.setTextColor(colorRed);
                break;
        }
        //label.setText(reportEntity.getValueForDataElement(ORG_UNIT));

    }
}
