package org.hisp.dhis.client.sdk.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private OnReportEntityInteractionListener onReportEntityInteractionListener;

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

    public void setOnReportEntityInteractionListener(OnReportEntityInteractionListener onInteractionListener) {
        this.onReportEntityInteractionListener = onInteractionListener;
    }

    public void swapData(@Nullable List<ReportEntity> reportEntities) {
        this.reportEntities.clear();

        if (reportEntities != null) {
            this.reportEntities.addAll(reportEntities);
        }

        notifyDataSetChanged();
    }

    public interface OnReportEntityInteractionListener {
        void onReportEntityClicked(ReportEntity reportEntity);

        void onDeleteReportEntity(ReportEntity reportEntity);
    }

    private final class ReportEntityViewHolder extends RecyclerView.ViewHolder {

        ReportEntity reportEntity;
        final View statusIconContainer;
        final CircleView statusBackground;
        final ImageView statusIcon;
        final TextView lineOne;
        final TextView lineTwo;
        final TextView lineThree;
        final OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
        final View deleteButton;

        final Drawable drawableSent;
        final Drawable drawableOffline;
        final Drawable drawableError;

        final int colorSent;
        final int colorOffline;
        final int colorError;

        public ReportEntityViewHolder(View itemView) {
            super(itemView);

            statusIconContainer = itemView.findViewById(R.id.status_icon_container);
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
            deleteButton = itemView.findViewById(R.id.delete_button);

            onRecyclerViewItemClickListener = new OnRecyclerViewItemClickListener();
            itemView.setOnClickListener(onRecyclerViewItemClickListener);
            deleteButton.setOnClickListener(new OnDeleteButtonClickListener());
            statusIconContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStatusDialog(v.getContext());
                }
            });

            Context context = itemView.getContext();

            drawableSent = ContextCompat.getDrawable(context, R.drawable.ic_tick);
            drawableOffline = ContextCompat.getDrawable(context, R.drawable.ic_offline);
            drawableError = ContextCompat.getDrawable(context, R.drawable.ic_error);

            colorSent = ContextCompat.getColor(context, R.color.color_material_green_default);
            colorOffline = ContextCompat.getColor(context, R.color.color_accent_default);
            colorError = ContextCompat.getColor(context, R.color.color_material_red_default);
        }

        public void update(ReportEntity reportEntity) {

            this.reportEntity = reportEntity;
            onRecyclerViewItemClickListener.setReportEntity(reportEntity);

            switch (reportEntity.getStatus()) {
                // TODO: show deleteButton for all statuses when deletion is supported in SDK
                case SENT: {
                    deleteButton.setVisibility(View.GONE);
                    statusBackground.setFillColor(colorSent);
                    statusIcon.setImageDrawable(drawableSent);
                    break;
                }
                case TO_POST: {
                    deleteButton.setVisibility(View.VISIBLE);
                    statusBackground.setFillColor(colorOffline);
                    statusIcon.setImageDrawable(drawableOffline);
                    break;
                }
                case TO_UPDATE: {
                    deleteButton.setVisibility(View.GONE);
                    statusBackground.setFillColor(colorOffline);
                    statusIcon.setImageDrawable(drawableOffline);
                    break;
                }
                case ERROR: {
                    deleteButton.setVisibility(View.GONE);
                    statusBackground.setFillColor(colorError);
                    statusIcon.setImageDrawable(drawableError);
                    break;
                }
            }

            lineOne.setText(reportEntity.getLineOne());
            lineTwo.setText(reportEntity.getLineTwo());
            lineThree.setText(reportEntity.getLineThree());
        }

        private void showStatusDialog(Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.drawer_item_status);

            switch (reportEntity.getStatus()) {
                case SENT: {
                    Drawable mutableSentIcon = ContextCompat.getDrawable(context, R.drawable.ic_tick).mutate();
                    mutableSentIcon.setColorFilter(colorSent, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableSentIcon);
                    builder.setMessage(R.string.sync_status_ok);
                    break;
                }
                case TO_UPDATE:
                case TO_POST: {
                    Drawable mutableOfflineIcon = ContextCompat.getDrawable(context, R.drawable.ic_offline).mutate();
                    mutableOfflineIcon.setColorFilter(colorOffline, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableOfflineIcon);
                    builder.setMessage(R.string.sync_status_offline);
                    break;
                }
                case ERROR: {
                    Drawable mutableDrawableError = ContextCompat.getDrawable(context, R.drawable.ic_error).mutate();
                    mutableDrawableError.setColorFilter(colorError, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableDrawableError);
                    builder.setMessage(R.string.sync_status_error);
                    break;
                }
            }

            builder.setPositiveButton(R.string.sync_now, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO: sync this report entity
                    dialog.dismiss();
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }

        private void showEntityDeletionConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(deleteButton.getContext());
            builder.setTitle(R.string.delete_report_entity_dialog_title).setMessage(R.string.delete_report_entity_dialog_message).setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    if (onReportEntityInteractionListener != null) {
                        int entityIndex = reportEntities.indexOf(reportEntity);
                        reportEntities.remove(reportEntity);
                        notifyItemRemoved(entityIndex);
                        onReportEntityInteractionListener.onDeleteReportEntity(reportEntity);
                    } else {
                        Toast.makeText(deleteButton.getContext(), R.string.report_entity_deletion_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }

        private class OnDeleteButtonClickListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {

                if (onReportEntityInteractionListener != null) {
                    showEntityDeletionConfirmationDialog();
                } else {
                    Toast.makeText(v.getContext(), R.string.report_entity_deletion_error, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void addItem(ReportEntity reportEntity) {
        reportEntities.add(reportEntity);
        notifyItemInserted(reportEntities.size() - 1);
    }

    private class OnRecyclerViewItemClickListener implements View.OnClickListener {
        private ReportEntity reportEntity;

        public void setReportEntity(ReportEntity reportEntity) {
            this.reportEntity = reportEntity;
        }

        @Override
        public void onClick(View view) {
            if (onReportEntityInteractionListener != null) {
                onReportEntityInteractionListener.onReportEntityClicked(reportEntity);
            }
        }
    }
}
