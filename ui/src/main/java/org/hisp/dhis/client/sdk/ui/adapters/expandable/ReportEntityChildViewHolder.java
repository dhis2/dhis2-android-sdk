package org.hisp.dhis.client.sdk.ui.adapters.expandable;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ReportEntity;
import org.hisp.dhis.client.sdk.ui.views.CircleView;

public class ReportEntityChildViewHolder<C> extends ChildViewHolder<C> {

    // Event map keys:
    public static final String EVENT_DATE_KEY = "eventDate";
    public static final String STATUS_KEY = "status";
    public static final String EVENT_DATE_LABEL = "Event date";
    public static final String STATUS_LABEL = "Status";
    public static final String ORG_UNIT = "OrgUnit";

    final ImageView statusIcon;
    final CircleView statusBackground;
    final TextView label;
    final TextView date;
    final View errorView;
    final View refreshButton;

    final Drawable drawableSent;
    final Drawable drawableOffline;
    final Drawable drawableError;

    final int colorSent;
    final int colorOffline;
    final int colorError;

    public ReportEntityChildViewHolder(View itemView) {
        super(itemView);

        statusIcon = (ImageView) itemView.findViewById(R.id.status_icon);
        statusBackground = (CircleView) itemView.findViewById(R.id.circleview_status_background);
        errorView = itemView.findViewById(R.id.error_text);
        refreshButton = itemView.findViewById(R.id.refresh_button);
        label = (TextView) itemView.findViewById(R.id.event_name);
        date = (TextView) itemView.findViewById(R.id.date_text);

        drawableSent = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_tick);
        drawableOffline = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_offline);
        drawableError = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_error);

        colorSent = ContextCompat.getColor(itemView.getContext(), R.color.color_material_green_default);
        colorOffline = ContextCompat.getColor(itemView.getContext(), R.color.color_accent_default);
        colorError = ContextCompat.getColor(itemView.getContext(), R.color.color_material_red_default);
    }

    public void bind(ReportEntity reportEntity) {
        label.setText(reportEntity.getId());

        //Map<String, String> dataElementToValueMap =
        //dataElementToValueMap.put(Event.STATUS_KEY, event.getStatus().toString());

        //label.setText(reportEntity.getValueForDataElement(ORG_UNIT));
        date.setText(reportEntity.getValueForDataElement(EVENT_DATE_KEY));

        switch (reportEntity.getStatus()) {
            // TODO: show deleteButton for all statuses when deletion is supported in SDK
            case SENT: {
                statusBackground.setFillColor(colorSent);
                statusIcon.setImageDrawable(drawableSent);
                break;
            }
            case TO_POST: {
                statusBackground.setFillColor(colorOffline);
                statusIcon.setImageDrawable(drawableOffline);
                break;
            }
            case TO_UPDATE: {
                statusBackground.setFillColor(colorOffline);
                statusIcon.setImageDrawable(drawableOffline);
                break;
            }
            case ERROR: {
                statusBackground.setFillColor(colorError);
                statusIcon.setImageDrawable(drawableError);
                break;
            }
        }
        //updateDataElements(reportEntity);
        refreshButton.setVisibility(View.VISIBLE);
    }
}
