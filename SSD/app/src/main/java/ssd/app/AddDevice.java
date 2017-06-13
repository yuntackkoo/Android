package ssd.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import static ssd.app.Devlist.lv_Adapter;

public class AddDevice extends Activity {

    static String input_ip;
    static String input_port;
    static String input_username;
    static String input_devname;
    EditText edit_ipadd;
    EditText edit_username;
    EditText edit_devname;
    EditText edit_port;
    SsdDB db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_device);
        db = new SsdDB(this.getApplicationContext(),SsdDB.DBNAME,null,1);

        edit_ipadd = (EditText) findViewById(R.id.input_ipadd);
        edit_username = (EditText) findViewById(R.id.input_username);
        edit_devname = (EditText) findViewById(R.id.input_devname);
    }

    public void onClick_addcancle (View v) { finish(); }

    public void onClick_adddevice (View v) {
        input_ip = edit_ipadd.getText().toString();
        input_username = edit_username.getText().toString();
        input_devname = edit_devname.getText().toString();

        db.addDevice(input_devname,input_ip);

        finish();

        add_Listview(input_ip,input_username,input_devname);
    }

    public void add_Listview (String a, String b, String c) {
        lv_Adapter.addItem(c);
        lv_Adapter.notifyDataSetChanged();
    }

}
