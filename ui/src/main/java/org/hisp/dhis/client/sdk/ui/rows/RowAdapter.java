package org.hisp.dhis.client.sdk.ui.rows;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.client.sdk.ui.R;

import java.util.List;

public class RowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Row> rows;
    private Context context;

    public RowAdapter(Context context, List<Row> rows) {
        this.context = context;
        this.rows = rows;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == RowType.TEXT.ordinal()
                || viewType == RowType.LONG_TEXT.ordinal()
                || viewType == RowType.NUMBER.ordinal()
                || viewType == RowType.INTEGER.ordinal()
                || viewType == RowType.INTEGER_NEGATIVE.ordinal()
                || viewType == RowType.INTEGER_POSITIVE.ordinal()
                || viewType == RowType.INTEGER_ZERO_OR_POSITIVE.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_edit_text, parent, false);
            return new EditTextRowViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof EditTextRowViewHolder) {
            if(holder.getItemViewType() == RowType.TEXT.ordinal()) {
                ((EditTextRowViewHolder) holder).textInputLayout.setHint(context.getString(R.string.enter_text));
                ((EditTextRowViewHolder) holder).editText.setInputType(InputType.TYPE_CLASS_TEXT);
                ((EditTextRowViewHolder) holder).editText.setSingleLine(true);
            } else if (RowType.LONG_TEXT.equals(holder.getItemViewType())) {
                ((EditTextRowViewHolder) holder).editText.setInputType(InputType.TYPE_CLASS_TEXT);
                ((EditTextRowViewHolder) holder).textInputLayout.setHint(context.getString(R.string.enter_long_text));
                ((EditTextRowViewHolder) holder).editText.setLines(EditTextRow.LONG_TEXT_LINE_COUNT);
            } else if (RowType.NUMBER.equals(holder.getItemViewType())) {
                ((EditTextRowViewHolder) holder).editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                ((EditTextRowViewHolder) holder).textInputLayout.setHint(context.getString(R.string.enter_number));
                ((EditTextRowViewHolder) holder).editText.setSingleLine(true);
            } else if (RowType.INTEGER.equals(holder.getItemViewType())) {
                ((EditTextRowViewHolder) holder).editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                ((EditTextRowViewHolder) holder).textInputLayout.setHint(context.getString(R.string.enter_integer));
                ((EditTextRowViewHolder) holder).editText.setSingleLine(true);
            } else if (RowType.INTEGER_NEGATIVE.equals(holder.getItemViewType())) {
                ((EditTextRowViewHolder) holder).editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                ((EditTextRowViewHolder) holder).textInputLayout.setHint(context.getString(R.string.enter_negative_integer));
                ((EditTextRowViewHolder) holder).editText.setFilters(new InputFilter[]{new EditTextRow.NegInpFilter()});
                ((EditTextRowViewHolder) holder).editText.setSingleLine(true);
            } else if (RowType.INTEGER_ZERO_OR_POSITIVE.equals(holder.getItemViewType())) {
                ((EditTextRowViewHolder) holder).editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                ((EditTextRowViewHolder) holder).textInputLayout.setHint(context.getString(R.string.enter_positive_integer_or_zero));
                ((EditTextRowViewHolder) holder).editText.setFilters(new InputFilter[]{new EditTextRow.PosOrZeroFilter()});
                ((EditTextRowViewHolder) holder).editText.setSingleLine(true);
            } else if (RowType.INTEGER_POSITIVE.equals(holder.getItemViewType())) {
                ((EditTextRowViewHolder) holder).editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                ((EditTextRowViewHolder) holder).textInputLayout.setHint(context.getString(R.string.enter_positive_integer));
                ((EditTextRowViewHolder) holder).editText.setFilters(new InputFilter[]{new EditTextRow.PosFilter()});
                ((EditTextRowViewHolder) holder).editText.setSingleLine(true);
            }

        }
//        else if (holder instanceof )

    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).getRowType().ordinal();
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
