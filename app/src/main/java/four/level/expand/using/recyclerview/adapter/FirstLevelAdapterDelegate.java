package four.level.expand.using.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import four.level.expand.using.recyclerview.R;
import four.level.expand.using.recyclerview.levels.BaseLevel;
import four.level.expand.using.recyclerview.levels.FirstLevelRowData;
import four.level.expand.using.recyclerview.list.ExpandableList;
import four.level.expand.using.recyclerview.listener.FirstSecondThirdLevelClickListener;

public class FirstLevelAdapterDelegate extends AdapterDelegate<ExpandableList>
{
    private final FirstSecondThirdLevelClickListener firstSecondThirdLevelClickListener;

    public FirstLevelAdapterDelegate(FirstSecondThirdLevelClickListener listener)
    {
        this.firstSecondThirdLevelClickListener = listener;
    }

    @Override
    protected boolean isForViewType(@NonNull ExpandableList items, int position)
    {
        return items.get(position) instanceof FirstLevelRowData;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.first_level_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v ->
        {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION)
            {
                firstSecondThirdLevelClickListener.onFirstSecondThirdLevelClick(pos);
            }
        });
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ExpandableList items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads)
    {
        BaseLevel item = (BaseLevel) items.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.text.setText(item.text);
        int end = item.isExpanded() ? R.drawable.ic_expand_less_black_24dp : R.drawable.ic_expand_more_black_24dp;
        viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, end, 0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView text;

        ViewHolder(View itemView)
        {
            super(itemView);
            text = (TextView) itemView;
        }
    }
}
