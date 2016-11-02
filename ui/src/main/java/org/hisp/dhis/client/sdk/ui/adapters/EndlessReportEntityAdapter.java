/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ReportEntity;
import org.hisp.dhis.client.sdk.ui.views.CircleView;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public abstract class EndlessReportEntityAdapter extends RecyclerView.Adapter {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private final List<ReportEntity> reportEntities;
    private final LayoutInflater layoutInflater;
    private final RecyclerView recyclerView;
    private final EndlessScrollListener endlessScrollListener;


    // interaction listener
    private OnReportEntityInteractionListener onReportEntityInteractionListener;
    private boolean loading;

    public EndlessReportEntityAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        isNull(recyclerView, "RecyclerView must not be null");
        Context context = recyclerView.getContext();
        isNull(context, "RecyclerView must have valid Context");

        layoutInflater = LayoutInflater.from(context);
        this.reportEntities = new ArrayList<>();
        endlessScrollListener = new EndlessScrollListener();
        recyclerView.addOnScrollListener(endlessScrollListener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return new ReportEntityViewHolder(layoutInflater.inflate(
                    R.layout.recyclerview_report_entity_item, parent, false));
        } else if (viewType == VIEW_TYPE_LOADING) {
            return new LoadingViewHolder(layoutInflater.inflate(R.layout.loading_view, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ReportEntityViewHolder) {
            ReportEntity reportEntity = reportEntities.get(position);
            ((ReportEntityViewHolder) holder).update(reportEntity);
        }

    }

    @Override
    public int getItemCount() {
        return reportEntities.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isLoadingView(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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


    private final class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    private final class ReportEntityViewHolder extends RecyclerView.ViewHolder {

        ReportEntity reportEntity;
        final View statusIconContainer;
        final CircleView statusBackground;
        final ImageView statusIcon;
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

            deleteButton = itemView.findViewById(R.id.delete_button);

            onRecyclerViewItemClickListener = new OnRecyclerViewItemClickListener();
            itemView.setOnClickListener(onRecyclerViewItemClickListener);
            deleteButton.setOnClickListener(new OnDeleteButtonClickListener());

            Context context = itemView.getContext();

            drawableSent = ContextCompat.getDrawable(context, R.drawable.ic_tick);
            drawableOffline = ContextCompat.getDrawable(context, R.drawable.ic_offline);
            drawableError = ContextCompat.getDrawable(context, R.drawable.ic_error);

            colorSent = ContextCompat.getColor(context, R.color.color_material_green_default);
            colorOffline = ContextCompat.getColor(context, R.color.color_accent_default);
            colorError = ContextCompat.getColor(context, R.color.color_material_red_default);
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

        public void update(ReportEntity reportEntity) {

            this.reportEntity = reportEntity;
            onRecyclerViewItemClickListener.setReportEntity(reportEntity);

            switch (reportEntity.getSyncStatus()) {
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

            /*lineOne.setText(reportEntity.getLineOne());
            lineTwo.setText(reportEntity.getLineTwo());
            lineThree.setText(reportEntity.getLineThree());*/
        }

        private void showStatusDialog(Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.drawer_item_status);

            switch (reportEntity.getSyncStatus()) {
                case SENT: {
                    Drawable mutableSentIcon = ContextCompat.getDrawable(context, R.drawable.ic_tick).mutate();
                    mutableSentIcon.setColorFilter(colorSent, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableSentIcon);
                    builder.setMessage(R.string.sync_status_ok_message);
                    break;
                }
                case TO_UPDATE:
                case TO_POST: {
                    Drawable mutableOfflineIcon = ContextCompat.getDrawable(context, R.drawable.ic_offline).mutate();
                    mutableOfflineIcon.setColorFilter(colorOffline, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableOfflineIcon);
                    builder.setMessage(R.string.sync_status_offline_message);
                    break;
                }
                case ERROR: {
                    Drawable mutableDrawableError = ContextCompat.getDrawable(context, R.drawable.ic_error).mutate();
                    mutableDrawableError.setColorFilter(colorError, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableDrawableError);
                    builder.setMessage(R.string.sync_status_error_message);
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

    private class EndlessScrollListener extends RecyclerView.OnScrollListener {

        // number of items to the bottom of the list where we start fetching new items
        public static final int BOTTOM_ITEM_TRESHOLD = 3;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!loading && bottomTresholdReached(recyclerView)) {

                loading = true;
                showLoadingView();
                onLoadData();
            }
        }

        private boolean bottomTresholdReached(RecyclerView recyclerView) {
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            return totalItemCount - visibleItemCount < (firstVisibleItem + BOTTOM_ITEM_TRESHOLD);
        }
    }

    private void showLoadingView() {

        if (!loadingViewIsShowing()) {
            reportEntities.add(null);
            notifyItemInserted(reportEntities.size() - 1);
        }
    }

    public void addLoadedItems(List<ReportEntity> newItems) {

        hideLoadingView();

        reportEntities.addAll(newItems);

        notifyDataSetChanged();

        loading = false;

    }

    private void hideLoadingView() {
        if (!reportEntities.isEmpty() && loadingViewIsShowing()) {
            reportEntities.remove(reportEntities.size() - 1);
            notifyItemRemoved(reportEntities.size());
        }
    }

    private boolean loadingViewIsShowing() {
        return isLoadingView(reportEntities.size() - 1);
    }

    private boolean isLoadingView(int position) {
        return reportEntities.get(position) == null;
    }

    /**
     * Do your fetching of data items
     * Add fetched items with {@link #addLoadedItems(List)}
     */
    public abstract void onLoadData();

    /**
     * Hides the loading view
     * Call when there are no more items to fetch
     */
    public void onLoadFinished() {
        recyclerView.removeOnScrollListener(endlessScrollListener);
        hideLoadingView();
    }


}
