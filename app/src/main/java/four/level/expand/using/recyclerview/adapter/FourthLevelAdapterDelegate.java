package four.level.expand.using.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;
import four.level.expand.using.recyclerview.R;
import four.level.expand.using.recyclerview.levels.FourthLevelRowData;
import four.level.expand.using.recyclerview.list.ExpandableList;
import four.level.expand.using.recyclerview.listener.FourthLevelClickListener;

public class FourthLevelAdapterDelegate extends AdapterDelegate<ExpandableList>
{
    private final FourthLevelClickListener fourthLevelClickListener;

    public FourthLevelAdapterDelegate(FourthLevelClickListener listener)
    {
        this.fourthLevelClickListener = listener;
    }

    @Override
    protected boolean isForViewType(@NonNull ExpandableList items, int position)
    {
        return items.get(position) instanceof FourthLevelRowData;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fourth_level_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v ->
        {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION)
            {
                fourthLevelClickListener.onFourthLevelClick(pos);
            }
        });
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ExpandableList items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads)
    {
        FourthLevelRowData item = (FourthLevelRowData) items.get(position);
        ((ViewHolder) holder).text.setText(item.text);
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
