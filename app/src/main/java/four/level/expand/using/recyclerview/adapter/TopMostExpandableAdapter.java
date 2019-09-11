package four.level.expand.using.recyclerview.adapter;

import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter;

import four.level.expand.using.recyclerview.list.ExpandableList;
import four.level.expand.using.recyclerview.listener.FirstSecondThirdLevelClickListener;
import four.level.expand.using.recyclerview.listener.FourthLevelClickListener;

public class TopMostExpandableAdapter extends AbsDelegationAdapter<ExpandableList>
{
    public TopMostExpandableAdapter(ExpandableList expandableList, FourthLevelClickListener fourthLevelClickListener, FirstSecondThirdLevelClickListener firstSecondThirdLevelClickListener)
    {
        setItems(expandableList);
        setHasStableIds(true);
        delegatesManager.addDelegate(new FourthLevelAdapterDelegate(fourthLevelClickListener));
        delegatesManager.addDelegate(new FirstLevelAdapterDelegate(firstSecondThirdLevelClickListener));
        delegatesManager.addDelegate(new SecondLevelAdapterDelegate(firstSecondThirdLevelClickListener));
        delegatesManager.addDelegate(new ThirdLevelAdapterDelegate(firstSecondThirdLevelClickListener));
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    @Override
    public long getItemId(int position)
    {
        return items.get(position).hashCode();
    }
}
