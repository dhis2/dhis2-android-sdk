package org.hisp.dhis.client.sdk.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ReportEntity;
import org.hisp.dhis.client.sdk.ui.views.CircleView;
import org.hisp.dhis.client.sdk.ui.views.FontTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ReportEntityAdapter extends RecyclerView.Adapter {

    public static final String REPORT_ENTITY_LIST_KEY = "REPORT_ENTITY_LIST_KEY";

    private ArrayList<ReportEntity> reportEntities;
    private final LayoutInflater layoutInflater;
    private HashMap<String, Pair<String, Boolean>> reportEntityDataElementFilters;

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

    public void onRestoreInstanceState(Bundle bundle) {
        reportEntities = bundle.getParcelableArrayList(REPORT_ENTITY_LIST_KEY);
        notifyDataSetChanged();
    }

    public void notifyFiltersChanged(HashMap<String, Pair<String, Boolean>> labelFilters) {
        this.reportEntityDataElementFilters = labelFilters;
        notifyDataSetChanged();
    }

    public interface OnReportEntityInteractionListener {
        void onReportEntityClicked(ReportEntity reportEntity);

        void onDeleteReportEntity(ReportEntity reportEntity);
    }

    private final class ReportEntityViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup dataElementLabelContainer;
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
            dataElementLabelContainer = (ViewGroup) itemView
                    .findViewById(R.id.data_element_label_container);
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
                    deleteButton.setVisibility(View.INVISIBLE);
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
                    deleteButton.setVisibility(View.INVISIBLE);
                    statusBackground.setFillColor(colorOffline);
                    statusIcon.setImageDrawable(drawableOffline);
                    break;
                }
                case ERROR: {
                    deleteButton.setVisibility(View.INVISIBLE);
                    statusBackground.setFillColor(colorError);
                    statusIcon.setImageDrawable(drawableError);
                    break;
                }
            }

            updateDataElements(reportEntity);
        }

        private void updateDataElements(ReportEntity reportEntity) {

            if (reportEntityDataElementFilters == null) {
                showPlaceholder();
            } else if (noDataElementsToShow(reportEntityDataElementFilters)) {
                showThreeFirstDataElements(reportEntity);
            } else {
                int viewIndex = 0;
                for (String key : reportEntityDataElementFilters.keySet()) {
                    boolean displayInList = reportEntityDataElementFilters.get(key).second;
                    if (displayInList) {
                        View dataElementLabelView = dataElementLabelContainer.getChildAt(viewIndex++);
                        if (dataElementLabelView == null) {
                            dataElementLabelView = layoutInflater.inflate(
                                    R.layout.data_element_label, dataElementLabelContainer, false);
                            dataElementLabelContainer.addView(dataElementLabelView);
                        }

                        String name = reportEntityDataElementFilters.get(key).first;
                        String value = reportEntity.getValueForDataElement(key);

                        String dataElementString = String.format("%s: %s", name, value);

                        SpannableString text = new SpannableString(dataElementString);

                        text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                                dataElementString.length() - value.length(),
                                dataElementString.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        ((FontTextView) dataElementLabelView).setText(text, TextView.BufferType.SPANNABLE);
                    }
                }
                while (dataElementLabelContainer.getChildCount() > viewIndex) {
                    // remove old views if they exist
                    dataElementLabelContainer.removeViewAt(dataElementLabelContainer.getChildCount() - 1);
                }
            }

        }

        private void showThreeFirstDataElements(ReportEntity reportEntity) {
            final int PLACEHOLDER_ITEMS = 3;
            int viewIndex = 0;

            String[] keySet = reportEntityDataElementFilters.keySet().toArray(new String[0]);

            for (int i = 0; i < reportEntityDataElementFilters.keySet().size(); i++) {

                if (i >= PLACEHOLDER_ITEMS) {
                    // only show PLACEHOLDER_ITEMS
                    break;
                }

                View dataElementLabelView = dataElementLabelContainer.getChildAt(viewIndex++);
                if (dataElementLabelView == null) {
                    dataElementLabelView = layoutInflater.inflate(
                            R.layout.data_element_label, dataElementLabelContainer, false);
                    dataElementLabelContainer.addView(dataElementLabelView);
                }

                String name = reportEntityDataElementFilters.get(keySet[i]).first;
                String value = reportEntity.getValueForDataElement(keySet[i]);

                String dataElementString = String.format("%s: %s", name, value);

                SpannableString text = new SpannableString(dataElementString);

                text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                        dataElementString.length() - value.length(),
                        dataElementString.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                ((FontTextView) dataElementLabelView).setText(text, TextView.BufferType.SPANNABLE);

            }

            while (dataElementLabelContainer.getChildCount() > viewIndex) {
                // remove old views if they exist
                dataElementLabelContainer.removeViewAt(dataElementLabelContainer.getChildCount() - 1);
            }
        }

        private void showPlaceholder() {
            View dataElementLabelView = dataElementLabelContainer.getChildAt(0);
            if (dataElementLabelView == null) {
                dataElementLabelView = layoutInflater.inflate(R.layout.data_element_label, dataElementLabelContainer, false);
                dataElementLabelContainer.addView(dataElementLabelView);
                ((FontTextView) dataElementLabelView).setText(dataElementLabelContainer.getContext().getString(R.string.report_entity));

                while (dataElementLabelContainer.getChildCount() > 1) {
                    // remove old views if they exist
                    dataElementLabelContainer.removeViewAt(dataElementLabelContainer.getChildCount() - 1);
                }
            }
        }

        private void showStatusDialog(Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            switch (reportEntity.getStatus()) {
                case SENT: {
                    builder.setTitle(R.string.sync_status_ok_title);
                    Drawable mutableSentIcon = ContextCompat.getDrawable(context, R.drawable.ic_tick).mutate();
                    mutableSentIcon.setColorFilter(colorSent, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableSentIcon);
                    builder.setMessage(R.string.sync_status_ok_message);
                    break;
                }
                case TO_UPDATE:
                case TO_POST: {
                    builder.setTitle(R.string.sync_status_offline_title);
                    Drawable mutableOfflineIcon = ContextCompat.getDrawable(context, R.drawable.ic_offline).mutate();
                    mutableOfflineIcon.setColorFilter(colorOffline, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableOfflineIcon);
                    builder.setMessage(R.string.sync_status_offline_message);
                    break;
                }
                case ERROR: {
                    builder.setTitle(R.string.sync_status_error_title);
                    Drawable mutableDrawableError = ContextCompat.getDrawable(context, R.drawable.ic_error).mutate();
                    mutableDrawableError.setColorFilter(colorError, PorterDuff.Mode.MULTIPLY);
                    builder.setIcon(mutableDrawableError);
                    builder.setMessage(R.string.sync_status_error_message);
                    break;
                }
            }

            /* TODO: sync individual report entities
            builder.setPositiveButton(R.string.sync_now, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO: syncing happens here
                    dialog.dismiss();
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }*/

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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

    private boolean noDataElementsToShow(Map<String, Pair<String, Boolean>> reportEntitDataElementFilters) {
        for (String s : reportEntitDataElementFilters.keySet()) {
            if (reportEntitDataElementFilters.get(s).second) {
                return false;
            }
        }
        return true;
    }

    public void addItem(ReportEntity reportEntity) {
        reportEntities.add(reportEntity);
        notifyItemInserted(reportEntities.size() - 1);
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(REPORT_ENTITY_LIST_KEY, reportEntities);
        return bundle;
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

    public HashMap<String, Pair<String, Boolean>> getReportEntityDataElementFilters() {
        return reportEntityDataElementFilters;
    }
}
