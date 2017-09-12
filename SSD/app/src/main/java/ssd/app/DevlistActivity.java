package ssd.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import protocol.Comunication;

public class DevlistActivity extends AppCompatActivity {

    static ListView lv_Devlist;
    static ListViewAdapter lv_Adapter;
    private MenuItem delete_btn;
    private SsdDB db;
    private ArrayList<String> dlist;
    Comunication com = null;
    ListViewClick listViewClick = new ListViewClick();
    ListViewLongClick listViewLongClick = new ListViewLongClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devlist);

        lv_Devlist = (ListView) findViewById(R.id.lv_devlist);
        lv_Adapter = new ListViewAdapter();
        db = new SsdDB(this.getApplicationContext(),SsdDB.DBNAME,null,1);

        dlist = db.getDeviceList();

        //DB에서 기기목록을 불러와 출력
        for(int i=0;i<dlist.size();i++){
            lv_Adapter.addItem(dlist.get(i));
        }

        //리스트뷰에 저장될 텍스트
        lv_Devlist.setAdapter(lv_Adapter);

        lv_Devlist.setOnItemClickListener(listViewClick);
        lv_Devlist.setOnItemLongClickListener(listViewLongClick);
    }

    public void onClick_add_device(View v) {
        Intent intent_adddevice = new Intent(this, AddDeviceActivity.class);
        startActivity(intent_adddevice);
    }

    public void chageSta(String state, int position){
        lv_Adapter.chageState(state,position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        delete_btn = (MenuItem) menu.findItem(R.id.menu_delete);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        delete_btn.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                lv_Adapter.removeItem();
            case R.id.menu_setmenu:
                if (lv_Adapter.mCheckBoxState == false) {
                    lv_Devlist.setOnItemClickListener(null);
                    lv_Devlist.setOnItemLongClickListener(null);
                    lv_Adapter.setCheckBoxState(true);
                    lv_Adapter.uncheckAll();
                    delete_btn.setVisible(true);
                } else {
                    lv_Devlist.setOnItemClickListener(listViewClick);
                    lv_Devlist.setOnItemLongClickListener(listViewLongClick);
                    lv_Adapter.setCheckBoxState(false);
                    delete_btn.setVisible(false);
                }
        }
        return true;
    }

    private class ListViewClick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parentView, View clickedView, int position, long id) {
            String name = lv_Adapter.getItemName(position);
            Intent intent = new Intent("CONNECT").putExtra("name",name);
            sendBroadcast(intent);
        }
    }

    private class ListViewLongClick implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent_settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent_settings);
            return true;
        }
    }

    @Override
    protected void onResume() {
        lv_Adapter.removeAll();

        dlist = db.getDeviceList();

        //DB에서 기기목록을 불러와 출력
        for(int i=0;i<dlist.size();i++){
            lv_Adapter.addItem(dlist.get(i));
        }

        lv_Adapter.notifyDataSetChanged();
        dlist = null;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        lv_Adapter.setCheckBoxState(false);
        delete_btn.setVisible(false);

        super.onDestroy();
    }
}