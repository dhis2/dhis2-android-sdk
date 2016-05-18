package org.hisp.dhis.client.sdk.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ReportEntity;
import org.hisp.dhis.client.sdk.ui.views.CircleView;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ReportEntityAdapter extends RecyclerView.Adapter {
    private final List<ReportEntity> reportEntities;
    private final LayoutInflater layoutInflater;

    // click listener
    private OnReportEntityClickListener onReportEntityClickListener;

    public ReportEntityAdapter(Context context) {
        isNull(context, "context must not be null");

        this.layoutInflater = LayoutInflater.from(context);
        this.reportEntities = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReportEntityViewHolder(layoutInflater.inflate(
                R.layout.recyclerview_report_entity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReportEntity reportEntity = reportEntities.get(position);
        ((ReportEntityViewHolder) holder).update(reportEntity);
    }

    @Override
    public int getItemCount() {
        return reportEntities.size();
    }

    public void setOnReportEntityClickListener(OnReportEntityClickListener onClickListener) {
        this.onReportEntityClickListener = onClickListener;
    }

    public void swapData(@Nullable List<ReportEntity> reportEntities) {
        this.reportEntities.clear();

        if (reportEntities != null) {
            this.reportEntities.addAll(reportEntities);
        }

        notifyDataSetChanged();
    }

    public interface OnReportEntityClickListener {
        void onReportEntityClicked(ReportEntity reportEntity);
    }

    private final class ReportEntityViewHolder extends RecyclerView.ViewHolder {
        final CircleView statusBackground;
        final ImageView statusIcon;
        final TextView lineOne;
        final TextView lineTwo;
        final TextView lineThree;
        final OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

        final Drawable drawableSent;
        final Drawable drawableOffline;
        final Drawable drawableError;

        final int colorSent;
        final int colorOffline;
        final int colorError;

        public ReportEntityViewHolder(View itemView) {
            super(itemView);

            statusBackground = (CircleView) itemView
                    .findViewById(R.id.circleview_status_background);
            statusIcon = (ImageView) itemView
                    .findViewById(R.id.imageview_status_icon);
            lineOne = (TextView) itemView
                    .findViewById(R.id.textview_line_one);
            lineTwo = (TextView) itemView
                    .findViewById(R.id.textview_line_two);
            lineThree = (TextView) itemView
                    .findViewById(R.id.textview_line_three);

            onRecyclerViewItemClickListener = new OnRecyclerViewItemClickListener();
            itemView.setOnClickListener(onRecyclerViewItemClickListener);

            Context context = itemView.getContext();

            drawableSent = ContextCompat.getDrawable(context, R.drawable.ic_sent);
            drawableOffline = ContextCompat.getDrawable(context, R.drawable.ic_offline);
            drawableError = ContextCompat.getDrawable(context, R.drawable.ic_error);

            colorSent = ContextCompat.getColor(context, R.color.color_material_green_default);
            colorOffline = ContextCompat.getColor(context, R.color.color_accent_default);
            colorError = ContextCompat.getColor(context, R.color.color_material_red_default);
        }

        public void update(ReportEntity reportEntity) {
            onRecyclerViewItemClickListener.setReportEntity(reportEntity);

            switch (reportEntity.getStatus()) {
                case SENT: {
                    statusBackground.setFillColor(colorSent);
                    statusIcon.setImageDrawable(drawableSent);
                    break;
                }
                case OFFLINE: {
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

            lineOne.setText(reportEntity.getLineOne());
            lineTwo.setText(reportEntity.getLineTwo());
            lineThree.setText(reportEntity.getLineThree());
        }
    }

    private class OnRecyclerViewItemClickListener implements View.OnClickListener {
        private ReportEntity reportEntity;

        public void setReportEntity(ReportEntity reportEntity) {
            this.reportEntity = reportEntity;
        }

        @Override
        public void onClick(View view) {
            if (onReportEntityClickListener != null) {
                onReportEntityClickListener.onReportEntityClicked(reportEntity);
            }
        }
    }
}
