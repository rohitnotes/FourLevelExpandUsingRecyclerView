package four.level.expand.using.recyclerview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;

import four.level.expand.using.recyclerview.adapter.TopMostExpandableAdapter;
import four.level.expand.using.recyclerview.levels.BaseLevel;
import four.level.expand.using.recyclerview.levels.FirstLevelRowData;
import four.level.expand.using.recyclerview.levels.FourthLevelRowData;
import four.level.expand.using.recyclerview.levels.SecondLevelRowData;
import four.level.expand.using.recyclerview.levels.ThirdLevelRowData;
import four.level.expand.using.recyclerview.list.ExpandableList;
import four.level.expand.using.recyclerview.list.ExpandableListOperation;
import four.level.expand.using.recyclerview.listener.FirstSecondThirdLevelClickListener;
import four.level.expand.using.recyclerview.listener.FourthLevelClickListener;

public class FourLevelExpandActivity extends AppCompatActivity implements FourthLevelClickListener, FirstSecondThirdLevelClickListener {

    private final ExpandableList expandableList = createList();
    private TopMostExpandableAdapter topMostExpandableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_level_expand);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        topMostExpandableAdapter = new TopMostExpandableAdapter(expandableList, this, this);
        recyclerView.setAdapter(topMostExpandableAdapter);
    }

    @Override
    public void onFirstSecondThirdLevelClick(int position)
    {
        ExpandableListOperation node = expandableList.get(position);
        if (node.isExpanded())
        {
            int collapsed = expandableList.collapse(node);
            if (collapsed > 0)
            {
                topMostExpandableAdapter.notifyItemRangeRemoved(position + 1, collapsed);
            }
        }
        else
        {
            int expanded = expandableList.expand(node);
            if (expanded > 0)
            {
                topMostExpandableAdapter.notifyItemRangeInserted(position + 1, expanded);
            }
        }
        topMostExpandableAdapter.notifyItemChanged(position);
    }

    @Override
    public void onFourthLevelClick(int position)
    {
        BaseLevel item = (BaseLevel) expandableList.get(position);
        StringBuilder desc = new StringBuilder(item.text);
        ExpandableListOperation node = item;
        while ((node = node.getParent()) != null)
        {
            if (node.getParent() == null) break;
            desc.insert(0, ((BaseLevel) node).text + " > ");
        }
        Toast.makeText(this, desc.toString(), Toast.LENGTH_SHORT).show();
    }

    private static ExpandableList createList()
    {
        ExpandableList list = new ExpandableList();
        for (int i = 0; i < 5; i++)
        {
            // first level (root nodes)
            FirstLevelRowData header1 = new FirstLevelRowData(i);
            for (int j = 0; j < 4; j++)
            {
                // second level
                SecondLevelRowData header2 = new SecondLevelRowData(j);
                for (int k = 0; k < 3; k++)
                {
                    // third level
                    ThirdLevelRowData header3 = new ThirdLevelRowData(k);
                    for (int n = 0, count = (int) (6 * Math.random() + 2); n < count; n++)
                    {
                        // fourth level
                        header3.insert(new FourthLevelRowData(n));
                    }
                    header2.insert(header3);
                }
                header1.insert(header2);
            }
            list.insert(header1);
        }
        return list;
    }
}
