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
import java.util.Map;

import protocol.Comunication;

public class Devlist extends AppCompatActivity {

    static ListView lv_Devlist;
    static ListViewAdapter lv_Adapter;
    MenuItem delete_btn;
    SsdDB db;
    ArrayList<String> dlist;

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

        lv_Devlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_settings = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent_settings);
                return true;
            }
        });

        lv_Devlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = lv_Adapter.getItemName(position);
                Map<String,String> tmp = db.deviceSerch(name);
                Comunication com = new Comunication(tmp.get("addr").toString(),tmp.get("port").toString(),Byte.parseByte(tmp.get("id").toString()));
                com.start();
            }
        });
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
                lv_Adapter.removeItem(lv_Devlist);
            case R.id.menu_setmenu:
                if (lv_Adapter.mCheckBoxState == false) {

                    lv_Adapter.setCheckBoxState(true);
                    delete_btn.setVisible(true);
                } else {

                    lv_Adapter.setCheckBoxState(false);
                    delete_btn.setVisible(false);
                }
        }
        return true;
    }

    public void onClick_add_device(View v) {
        Intent intent_adddevice = new Intent(this, AddDevice.class);
        startActivity(intent_adddevice);
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
}