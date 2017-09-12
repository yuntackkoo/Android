package ssd.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import static ssd.app.DevlistActivity.lv_Adapter;

public class SettingsActivity extends Activity {

    static String input_ip;
    static String input_username;
    static String input_devname;
    EditText edit_ipadd;
    EditText edit_username;
    EditText edit_devname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
    }

    public void onClick_altercancle (View v) { finish(); }

    public void onClick_alterdevice (View v) {
        input_ip = edit_ipadd.getText().toString();
        input_username = edit_username.getText().toString();
        input_devname = edit_devname.getText().toString();
        finish();

        add_Listview(input_ip,input_username,input_devname);
    }

    public void add_Listview (String a, String b, String c) {
        lv_Adapter.addItem(c);
        lv_Adapter.notifyDataSetChanged();
    }
}
