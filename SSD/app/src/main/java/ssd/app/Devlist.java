package ssd.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Devlist extends AppCompatActivity {

    static ListView lv_Devlist;
    static ArrayList<String> lv_items;
    static ListViewAdapter lv_Adapter;
    SsdDB db;
    ArrayList<String> dlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devlist);

        lv_Devlist = (ListView) findViewById(R.id.lv_devlist);
        lv_items = new ArrayList<String>();
        lv_Adapter = new ListViewAdapter();
        db = new SsdDB(this.getApplicationContext(),SsdDB.DBNAME,null,1);
        dlist = db.getDeviceList();

        //DB에서 기기목록을 불러와 출력
        for(int i=0;i<dlist.size();i++){
            lv_Adapter.addItem(dlist.get(i));
        }

        //리스트뷰에 저장될 텍스트
        lv_Devlist.setAdapter(lv_Adapter);

        lv_Devlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_settings = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent_settings);
                return true;
            }
           });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                if (lv_Adapter.mCheckBoxState == false) {
                    lv_Adapter.setCheckBoxState(true);
                } else {
                    lv_Adapter.setCheckBoxState(false);
                }
        }
        return true;
    }

    public void onClick_add_device(View v) {
        Intent intent_adddevice = new Intent(this, AddDevice.class);
        startActivity(intent_adddevice);
    }

    /*
    Button lv_addBt = (Button) findViewById(R.id.lv_addBt);
    lv_addBt.setOnClickListener(new Button.OnClickListener() {
        @Override
            public void onClick(View v) {
                int count;
                count = lv_Adapter.getCount();
                lv_items.add("LIST" + Integer.toString(count+1));
                lv_Adapter.notifyDataSetChanged();

                SavePref("LIST" + Integer.toString(count+1), "LIST" + Integer.toString(count+1));
            }
    });

    Button lv_removeBt = (Button) findViewById(R.id.lv_removeBt);
    lv_removeBt.setOnClickListener(new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int count, checked;
            count = lv_Adapter.getCount();
            if(count> 0) {
                checked= lv_Devlist.getCheckedItemPosition();

                if(checked> -1 && checked<count) {
                    lv_items.remove(checked);
                    lv_Devlist.clearChoices();
                    lv_Adapter.notifyDataSetChanged();
                }

                SavePref("LIST" + Integer.toString(count+1), "LIST" + Integer.toString(count+1));
            }
        }
    });

    protected void SavePref(String key, String value) {
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = mPref.edit();
        int i;
        for (i=0; i<lv_Adapter.getCount(); i++) {
            edit.putString(key, value);
        }
        edit.putInt("count", i);
        edit.commit();
    }

    protected void LoadPref() {
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        int j=mPref.getInt("count", 0);

        for(int i=0;i<j;i++) {
            lv_Adapter.add(mPref.getString("LIST" + Integer.toString(i+1),
                    "LIST" + Integer.toString(i+1)));
        }
        lv_Adapter.notifyDataSetChanged();
    }
    */

}