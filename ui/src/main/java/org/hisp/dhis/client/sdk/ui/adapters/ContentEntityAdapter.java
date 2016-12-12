package org.hisp.dhis.client.sdk.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ContentEntity;
import org.hisp.dhis.client.sdk.ui.views.FontTextView;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ContentEntityAdapter extends RecyclerView.Adapter {
    public static final String KEY_CONTENT_ENTITY_LIST = "key:ContentEntityList";
    private LayoutInflater layoutInflater;
    private List<ContentEntity> contentEntityList;
    private OnContentItemClicked onContentItemClickListener;


    public ContentEntityAdapter(Context context) {
        isNull(context, "context must not be null");

        this.layoutInflater = LayoutInflater.from(context);
        this.contentEntityList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentEntityViewHolder(layoutInflater.inflate(
                R.layout.recyclerview_row_content_entity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContentEntity contentEntity = contentEntityList.get(position);
        ((ContentEntityViewHolder) holder).update(contentEntity);
    }

    @Override
    public int getItemCount() {
        return contentEntityList.size();
    }

    public void swapData(@Nullable List<ContentEntity> contentEntities) {
        this.contentEntityList.clear();

        if (contentEntities != null) {
            this.contentEntityList.addAll(contentEntities);
        }

        notifyDataSetChanged();
    }

    public void setOnContentItemClickListener(OnContentItemClicked onContentItemClickListener) {
        this.onContentItemClickListener = onContentItemClickListener;
    }

    public void filter(String query) {
        if(contentEntityList != null && !contentEntityList.isEmpty()) {
            List<ContentEntity> filteredLists = new ArrayList<>();
            for (ContentEntity contentEntity : contentEntityList) {
                if(contentEntity.getTitle().contains(query)) {
                    filteredLists.add(contentEntity);
                }
            }
            swapData(filteredLists);
        }
    }

    public interface OnContentItemClicked {
        void onContentItemClicked(ContentEntity contentEntity);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        contentEntityList = bundle.getParcelableArrayList(KEY_CONTENT_ENTITY_LIST);
        notifyDataSetChanged();
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_CONTENT_ENTITY_LIST, (ArrayList<? extends Parcelable>) contentEntityList);
        return bundle;
    }

    private class OnContentItemClickListener implements View.OnClickListener {
        private ContentEntity contentEntity;

        public void setContentEntity(ContentEntity contentEntity) {
            this.contentEntity = contentEntity;
        }

        @Override
        public void onClick(View view) {
            if (onContentItemClickListener != null) {
                onContentItemClickListener.onContentItemClicked(contentEntity);
            }
        }
    }

    final class ContentEntityViewHolder extends RecyclerView.ViewHolder {
        final FontTextView title;
        final ImageView imageView;
        final OnContentItemClickListener onClickListener;

        ContentEntityViewHolder(View itemView) {
            super(itemView);
            title = (FontTextView) itemView.findViewById(R.id.content_entity_title);
            imageView = (ImageView) itemView.findViewById(R.id.content_entity_image_view);
            onClickListener = new OnContentItemClickListener();
            itemView.setOnClickListener(onClickListener);
        }

        void update(ContentEntity contentEntity) {
            title.setText(contentEntity.getTitle());
            onClickListener.setContentEntity(contentEntity);

            switch (contentEntity.getType()) {
                case ContentEntity.TYPE_TRACKED_ENTITY: {
                    imageView.setImageResource(R.drawable.ic_widgets_black);
                    break;
                }
                case ContentEntity.TYPE_PROGRAM: {
                    imageView.setImageResource(R.drawable.ic_border_all_black);
                    break;
                }
                case ContentEntity.TYPE_DATA_SET: {
                    imageView.setImageResource(R.drawable.ic_border_all_black);
                    break;
                }
            }
        }
    }
}
