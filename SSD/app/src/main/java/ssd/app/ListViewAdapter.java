package ssd.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

public class ListViewAdapter extends BaseAdapter {
    static ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    static boolean mCheckBoxState=false;

    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }

    //체크박스 Visible
    public void setCheckBoxState(boolean pState) {
        mCheckBoxState = pState;
        notifyDataSetChanged();
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleView = (TextView) convertView.findViewById(R.id.custom_text);
        TextView stateView = (TextView) convertView.findViewById(R.id.devstate);
        CheckBox checkboxView = (CheckBox) convertView.findViewById(R.id.custom_checkbox);
        //((ListView)parent).setItemChecked(position,false);
        //checkboxView.setChecked(false);
        //checkboxView.setChecked(((ListView) parent).isItemChecked(position));

        if (mCheckBoxState) { checkboxView.setVisibility(View.VISIBLE); }
        else if (!mCheckBoxState) { checkboxView.setVisibility(View.GONE); }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem listViewItem = listViewItemList.get(position);

        checkboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listViewItem.setChecked(isChecked);
            }
        });
        checkboxView.setChecked(listViewItem.isChecked());

        // 아이템 내 각 위젯에 데이터 반영
        titleView.setText(listViewItem.getTitle());
        stateView.setText(listViewItem.getState());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public String getItemName(int position) {return listViewItemList.get(position).getTitle();}


    // 아이템 데이터 추가를 위한 함수.
    public void addItem(String title) {
        ListViewItem item = new ListViewItem();

        item.setTitle(title);

        listViewItemList.add(item);
    }

    //삭제
    public void removeItem() {
        for(Iterator<ListViewItem> iter = listViewItemList.iterator(); iter.hasNext(); ) {
            ListViewItem o = iter.next();
            if (o.isChecked()) {
                iter.remove();
            }
        }
        notifyDataSetChanged();
    }

    //체크 전부 해제
    public void uncheckAll() {
        for(ListViewItem o : listViewItemList) {
            o.setChecked(false);
        }
        notifyDataSetChanged();
    }

    public void chageState(String state,int position){
        listViewItemList.get(position).setState(state);
        notifyDataSetChanged();
    }

    public void removeAll() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }

}
